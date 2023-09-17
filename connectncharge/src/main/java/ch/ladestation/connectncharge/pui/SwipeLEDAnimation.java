package ch.ladestation.connectncharge.pui;

import ch.ladestation.connectncharge.model.game.gamelogic.Edge;
import com.github.mbelling.ws281x.Color;

public class SwipeLEDAnimation extends LEDAnimation {
    public SwipeLEDAnimation(Edge associatedEdge, boolean dissappear) {
        super(associatedEdge, dissappear);
    }

    @Override
    boolean tickForwards(Color[] ledStates, int progress, int from, int to, Color color) {
        ledStates[from + progress] = color;
        return from + progress <= to;
    }

    @Override
    boolean tickBackwards(Color[] ledStates, int progress, int from, int to, Color color) {
        ledStates[from + progress] = Color.BLACK;
        return progress >= 0;
    }

    @Override
    int endProgress(int from, int to, Color color) {
        return to - from + 1;
    }
}
