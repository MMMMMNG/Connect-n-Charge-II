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
        int length = (to - from);
        boolean uneven = (length % 2) == 1;
        float mid = length / 2.0f;
        ledStates[(int) Math.ceil(from + mid + progress)] = color;
        ledStates[(int) Math.floor(from + mid - progress)] = color;
        return progress < mid - (uneven ? 1 : 0);
    }

    @Override
    boolean tickBackwards(Color[] ledStates, int progress, int from, int to, Color color) {
        float mid = (to - from) / 2.0f;
        ledStates[(int) Math.ceil(from + mid + progress)] = Color.BLACK;
        ledStates[(int) Math.floor(from + mid - progress)] = Color.BLACK;
        return progress > 0;
    }

    @Override
    int endProgress(int from, int to, Color color) {
        return (to - from) / 2;
    }
}
