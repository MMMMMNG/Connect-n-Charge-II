package ch.ladestation.connectncharge.pui;

import ch.ladestation.connectncharge.controller.ApplicationController;
import ch.ladestation.connectncharge.model.game.gamelogic.Edge;
import ch.ladestation.connectncharge.model.game.gamelogic.Game;
import ch.ladestation.connectncharge.model.game.gamelogic.Node;
import ch.ladestation.connectncharge.model.game.gamelogic.Segment;
import ch.ladestation.connectncharge.services.file.CSVReader;
import ch.ladestation.connectncharge.util.mvcbase.PuiBase;
import com.github.mbelling.ws281x.LedStrip;
import com.github.mbelling.ws281x.LedStripType;
import com.github.mbelling.ws281x.Ws281xLedStrip;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.PullResistance;
import com.pi4j.io.spi.Spi;
import com.pi4j.io.spi.SpiBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

import java.io.IOException;
import java.util.*;

public class GamePUI extends PuiBase<Game, ApplicationController> {

    public static final String DEBUG_MSG_REACT_TO_ARR_CHANGE =
        "reacting to change of {}; oldValue.length={} newValue.length={}";
    public static final String DEBUG_MSG_REACT_TO_CHANGE =
        "reacting to change of {}; oldValue={} newValue={}";
    /**
     * Logger instance
     */
    private static final Logger LOG = LoggerFactory.getLogger(GamePUI.class);
    private final String hOUSEFLAG = "H";
    private LEDAnimator ledAnimator;
    private List<MCP23S17> chips;
    private List<Edge> edges;
    private List<Node> nodes;
    private Map<Integer, Map<Integer, Edge>> pinToEdgeLUT;
    private Map<Integer, Segment> segmentIdLUT;
    private DigitalInput[] interruptPins;
    private Spi spiInterface;
    private Game modelInstance;
    public GamePUI(ApplicationController controller, Context pi4J, LEDAnimator animator) {
        super(controller, pi4J);
        this.ledAnimator = animator;
        setupOwnModelToUiBindings(this.modelInstance);
    }

    /**
     * Will set up and initialise the LED-Strip
     *
     * @return the {@link LedStrip} object
     */
    public static LedStrip setupLEDStrip() {
        LedStrip ledStrip = new Ws281xLedStrip(845, 10, 800000, 10, false, LedStripType.WS2811_STRIP_GRB, true);
        return ledStrip;
    }

    @Override
    public void initializeParts() {
        this.chips = setupGPIOExtensionICs(pi4J);
        this.edges = new ArrayList<>();
        this.nodes = new ArrayList<>();
        this.segmentIdLUT = new HashMap<>();
        this.pinToEdgeLUT = new HashMap<>();
        instanceSegments();
    }

    @Override
    public void setupUiToActionBindings(ApplicationController controller) {
        addInterruptsToPinViews(controller);
    }

    /**
     * this is only used to store the model instance to call
     * {@link GamePUI#setupOwnModelToUiBindings(Game)} later, because
     * otherwise it is impossible to mock the LED-strip class.
     * <p>
     * If the mock is passed to the Ctor
     * it cannot be assigned to {@code this.ledstrip} in time before super calls
     * setupModelToUiBindings(). So instead call it later but store the model until then.
     *
     * @param model
     */
    @Override
    public void setupModelToUiBindings(Game model) {
        this.modelInstance = model;
    }

    /**
     * This method binds the model reactively to the ledstrips.
     *
     * @param model
     */
    public void setupOwnModelToUiBindings(Game model) {
        onChangeOf(model.activatedEdges).execute(((oldValue, newValue) -> {
            LOG.debug(DEBUG_MSG_REACT_TO_ARR_CHANGE, "Game.activatedEdges", oldValue.length, newValue.length);
            ledAnimator.scheduleEdgesToBeAnimated(oldValue, newValue);
        }));

        onChangeOf(model.terminals).execute(((oldValue, newValue) -> {
            LOG.debug(DEBUG_MSG_REACT_TO_ARR_CHANGE, "Game.terminals", oldValue.length, newValue.length);
            ledAnimator.simplyToggleMultipleSegments(oldValue, false);
            ledAnimator.simplyToggleMultipleSegments(newValue, true);
        }));

        onChangeOf(model.isEdgeBlinking).execute((oldValue, newValue) -> {
            LOG.debug(DEBUG_MSG_REACT_TO_CHANGE, "Game.isEdgeBlinking", oldValue, newValue);
            ledAnimator.simplyToggleSegment(model.blinkingEdge, newValue);
        });

        onChangeOf(model.isTippOn).execute((oldValue, newValue) -> {
            LOG.debug(DEBUG_MSG_REACT_TO_CHANGE, "Game.isTippOn", oldValue, newValue);
            ledAnimator.simplyToggleSegment(model.tippEdge, newValue);
        });
    }

    /**
     * Configures pins of the MCP23S17 ICs to listen for interrupts and
     * adds a listener to every single one of them that calls
     * handleEdgePressed with the correct chip no. & pin no.
     */
    private void addInterruptsToPinViews(ApplicationController controller) {
        try {
            for (int i = 0; i < chips.size(); i++) {
                var pinViews = chips.get(i).getAllPinsAsPulledUpInterruptInput();
                for (var pinView : pinViews) {
                    addEdgePressListenerToPinView(i, pinView, controller);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error when trying to configure MCP23S17 pins: " + e.getMessage());
        }
    }

    /**
     * given a chip index and a {@link MCP23S17.PinView} object, this method will add a listener to the latter
     * that calls the {@link GamePUI#handleEdgePressed} method with the corresponding {@link Edge} instance.
     *
     * @param indexOfIC the index into the {@link GamePUI#chips} list where the {@link MCP23S17} instance
     *                  that the {@link MCP23S17.PinView} argument belongs to is stored.
     * @param pinView   the {@link MCP23S17.PinView} object the interrupt originated from.
     */
    private void addEdgePressListenerToPinView(int indexOfIC, MCP23S17.PinView pinView,
                                               ApplicationController controller) {
        pinView.addListener((state, pin) -> {
            LOG.debug("Interrupt triggered for Chip={}, Pin={}. state={}", indexOfIC, pin.getPinNumber(), state);
            var edge = lookUpChipAndPinNumberToEdge(indexOfIC, pin.getPinNumber());
            if (edge == null) {
                var msg = MessageFormatter.arrayFormat("No Edge registered for Chip {}, Pin {}. "
                            + "Please revise src/main/resources{} and/or the elctrical connections.",
                        new Object[] {indexOfIC, pin.getPinNumber(), CSVReader.LEDSEGMENTS_CSV})
                    .getMessage();
                LOG.error(msg);
                throw new NoSuchElementException(msg);
            }
            if (state) {
                handleEdgePressed(edge, controller);
            }
        });
    }

    /**
     * method that gets called every time someone tries to toggle an edge by pushing it down.
     * NOTE: it is only called on release of the edge.
     *
     * @param edge the instance that represents the pressed edge
     */
    private void handleEdgePressed(Edge edge, ApplicationController controller) {
        LOG.info("edge {} between {} & {} was pressed",
            edge.getSegmentIndex(),
            edge.getFromNodeId(),
            edge.getToNodeId());
        controller.edgePressed(edge);
    }

    /**
     * get the spi interface of the MCP23S17 chips
     *
     * @return the pi4j {@link Spi} object
     */
    public Spi getSpiInterface() {
        return spiInterface;
    }

    /**
     * Will set up and initialise the MCP23S17 GPIO-Extension ICs
     *
     * @return two fully configured lists of {@link MCP23S17.PinView} objects.
     * that means 2 * 16 extra GPIO Pins set as input, pulled up and interrupt enabled
     * @throws IOException when the creation of the {@link MCP23S17} objects or
     *                     gathering of the {@link MCP23S17.PinView} objects fail
     */
    private List<MCP23S17> setupGPIOExtensionICs(Context pi4J) {
        var interruptPinConfig =
            DigitalInput.newConfigBuilder(pi4J).id("interrupt0").name("a MCP interrupt").address(22)
                .pull(PullResistance.PULL_UP);

        var interruptPinChip0 = pi4J.create(interruptPinConfig);
        var interruptPinChip1 = pi4J.create(interruptPinConfig.address(23).id("interrupt1"));
        var interruptPinChip2 = pi4J.create(interruptPinConfig.address(24).id("interrupt2"));
        var interruptPinChip3 = pi4J.create(interruptPinConfig.address(25).id("interrupt3"));
        var interruptPinChip4 = pi4J.create(interruptPinConfig.address(27).id("interrupt4"));

        interruptPins = new DigitalInput[] {interruptPinChip0, interruptPinChip1, interruptPinChip2, interruptPinChip3,
            interruptPinChip4};
        List<MCP23S17> interruptChips;
        try {
            interruptChips = MCP23S17.multipleNewOnSameBusWithTiedInterrupts(
                pi4J, SpiBus.BUS_1, interruptPins, 5, true);
        } catch (IOException e) {
            throw new RuntimeException("Fatal error when instantiating MCP23S17 chips: " + e.getMessage());
        }
        spiInterface = interruptChips.get(0).getSpi();
        return interruptChips;
    }

    /**
     * Get the pins to which the chips are connected
     *
     * @return an array of {@link DigitalInput} objects
     */
    public DigitalInput[] getInterruptPins() {
        return interruptPins;
    }

    public void instanceSegments() {
        var records = CSVReader.readCSV();

        int runningTotal = 0;
        var retSegments = new ArrayList<Segment>();
        for (int i = 1; i < records.size() - 1; i++) {
            var record = records.get(i);
            int startIndex = runningTotal;
            runningTotal += Integer.parseInt(record.get(1));
            int endIndex = runningTotal - 1;


            if (record.get(2).equals(hOUSEFLAG)) {
                int segmentId = Integer.parseInt(record.get(0));
                var segment = new Node(segmentId, startIndex, endIndex);
                nodes.add(segment);
                segmentIdLUT.put(segmentId, segment);
            } else {
                int segmentId = Integer.parseInt(record.get(0));
                int chip = Integer.parseInt(record.get(2));
                int pin = Integer.parseInt(record.get(3));
                int cost = Integer.parseInt(record.get(4));
                int fromNode = Integer.parseInt(record.get(5));
                int toNode = Integer.parseInt(record.get(6));
                var segment = new Edge(segmentId, startIndex, endIndex, cost, fromNode, toNode);
                edges.add(segment);

                segmentIdLUT.put(segmentId, segment);

                populateLUT(chip, pin, segment);

            }
        }

        linkNodeReferencesInAllEdges();
    }

    private void linkNodeReferencesInAllEdges() {
        for (var edge : edges) {
            edge.setFromNode((Node) lookUpSegmentIdToSegment(edge.getFromNodeId()));
            edge.setToNode((Node) lookUpSegmentIdToSegment(edge.getToNodeId()));
        }
    }

    private void populateLUT(int chip, int pin, Edge segment) {
        pinToEdgeLUT.putIfAbsent(chip, new HashMap<>());
        pinToEdgeLUT.computeIfPresent(chip, (key, oldValue) -> {
            oldValue.put(pin, segment);
            return oldValue;
        });
    }

    public Edge lookUpChipAndPinNumberToEdge(int chipNo, int pinNo) {
        return pinToEdgeLUT.get(chipNo).get(pinNo);
    }

    public Segment lookUpSegmentIdToSegment(int segmentId) {
        return segmentIdLUT.get(segmentId);
    }

    public Edge lookUpEdge(int fromIndex, int toIndex) {
        return edges.stream().filter(e -> (e.getFromNodeId() == fromIndex && e.getToNodeId() == toIndex)
            || (e.getFromNodeId() == toIndex && e.getToNodeId() == fromIndex)).findFirst().orElseThrow();
    }

    public List<Edge> getAllEdges() {
        return edges;
    }
}

