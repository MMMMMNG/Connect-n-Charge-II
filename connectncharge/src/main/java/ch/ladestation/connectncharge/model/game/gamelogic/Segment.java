package ch.ladestation.connectncharge.model.game.gamelogic;

import ch.ladestation.connectncharge.pui.Component;
import ch.ladestation.connectncharge.util.mvcbase.ObservableArray;
import com.github.mbelling.ws281x.Color;

import java.util.Arrays;

public abstract class Segment extends Component {
    private static ObservableArray<Edge> modelActiveEdgesReference;
    private static ObservableArray<Node> modelActiveTerminalsReference;
    /**
     * the segment index according to LEDSegments.csv
     */
    private final int segmentIndex;
    /**
     * The start pixel of this edge
     */
    private final int startIndex;
    /**
     * The end pixel of this edge
     */
    private final int endIndex;
    /**
     * The color this segment should display
     */
    private Color color = Color.GREEN;

    /**
     * Basic constructor for the {@code Edge} class
     *
     * @param segmentIndex
     * @param startIndex   the start pixel of the edge.
     * @param endIndex     the end pixel of the edge
     * @param color        the color this segment will be visualized with
     */
    public Segment(int segmentIndex, int startIndex, int endIndex, Color color) {
        this.segmentIndex = segmentIndex;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.color = color;
    }

    public static void setActiveEdgesRef(ObservableArray<Edge> ref) {
        modelActiveEdgesReference = ref;
    }

    public static void setActiveTerminalsRef(ObservableArray<Node> ref) {
        modelActiveTerminalsReference = ref;
    }

    protected static boolean activeTerminalsContains(Node node) {
        return Arrays.asList(modelActiveTerminalsReference.getValues()).contains(node);
    }

    protected static boolean activeEdgesContains(Edge edge) {
        return Arrays.asList(modelActiveEdgesReference.getValues()).contains(edge);
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public abstract boolean isOn();

    public int getSegmentIndex() {
        return segmentIndex;
    }
}
