package ch.ladestation.connectncharge.pui;

import ch.ladestation.connectncharge.model.game.gamelogic.Edge;
import com.github.mbelling.ws281x.Color;

public class WipeFromCenterAnimation extends LEDAnimation {
    public WipeFromCenterAnimation(Edge associatedEdge,
                                   boolean disappear) {
        super(associatedEdge, disappear);
    }

    @Override
    boolean tickForwards(Color[] ledStates, int progress, int from, int to, Color color) {
        int mid = (to - from) / 2;
        ledStates[from + mid + progress] = color;
        ledStates[from + mid - progress] = color;
        return progress <= mid;
    }

    @Override
    boolean tickBackwards(Color[] ledStates, int progress, int from, int to, Color color) {
        int mid = (to - from) / 2;
        ledStates[from + mid + progress] = Color.BLACK;
        ledStates[from + mid - progress] = Color.BLACK;
        return progress >= 0;
    }

    @Override
    int endProgress(int from, int to, Color color) {
        return (to - from) / 2 + 1;
    }
}
