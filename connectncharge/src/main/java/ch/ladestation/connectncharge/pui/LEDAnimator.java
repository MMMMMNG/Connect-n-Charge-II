package ch.ladestation.connectncharge.pui;

import ch.ladestation.connectncharge.model.game.gamelogic.Edge;
import ch.ladestation.connectncharge.model.game.gamelogic.Segment;
import com.github.mbelling.ws281x.Color;
import com.github.mbelling.ws281x.LedStrip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LEDAnimator {

    public static final Logger LOGGER = LoggerFactory.getLogger(LEDAnimator.class);
    private Color[] ledStates;
    private List<LEDAnimation> activeAnims;

    private LedStrip stripRef;

    private ExecutorService executorService;

    public LEDAnimator(LedStrip ledStrip) {
        this.stripRef = ledStrip;
        ledStates = new Color[ledStrip.getLedsCount()];
        for (int i = 0; i < ledStates.length; i++) {
            ledStates[i] = Color.BLACK;
        }
        executorService = Executors.newSingleThreadExecutor();
        activeAnims = new ArrayList<>();
    }

    public void frameTick() {
        synchronized (ledStates) {
            var iter = activeAnims.iterator();
            while (iter.hasNext()) {
                var current = iter.next();
                try {
                    if (!current.tick(ledStates)) {
                        iter.remove();
                    }
                } catch (Exception e) {
                    LOGGER.warn("faulty animation code, removing offender", e);
                    iter.remove();
                }
            }
            render(ledStates, stripRef);

            if (!activeAnims.isEmpty()) {
                LOGGER.trace("scheduled new frame");
                executorService.submit(this::frameTick);
            }
        }
    }

    private void render(Color[] ledStates, LedStrip strip) {
        for (int i = 0; i < ledStates.length; i++) {
            strip.setPixel(i, ledStates[i]);
        }
        strip.render();
    }

    public void simplyToggleSegment(Segment seg, boolean state) {
        if (seg == null) {
            return;
        }
        int from = seg.getStartIndex();
        int to = seg.getEndIndex();
        synchronized (ledStates) {
            for (var i = from; i <= to; ++i) {
                ledStates[i] = state ? seg.getColor() : Color.BLACK;
            }
            executorService.submit(this::frameTick);
        }
    }

    public void simplyToggleMultipleSegments(Segment[] newValue, boolean state) {
        for (var seg : newValue) {
            simplyToggleSegment(seg, state);
        }
    }

    public void scheduleEdgesToBeAnimated(Edge[] oldValues, Edge[] newValues) {
        var oldList = new ArrayList<>(Arrays.asList(oldValues));
        var newList = new ArrayList<>(Arrays.asList(newValues));
        synchronized (ledStates) {
            if (oldValues.length > newValues.length) {
                oldList.removeAll(newList);
                instantiateAnims(oldList, true);
            } else if (oldValues.length < newValues.length) {
                newList.removeAll(oldList);
                instantiateAnims(newList, false);
            }
            if (!activeAnims.isEmpty()) {
                executorService.submit(this::frameTick);
            }
        }
    }

    public void instantiateAnims(List<Edge> newList, boolean dissappear) {
        for (var edg : newList) {
            if (activeAnims.stream().anyMatch(a -> a.getAssociatedEdge() == edg)) {
                continue;
            }
            activeAnims.add(new WipeFromCenterAnimation(edg, dissappear));
        }
    }

}
