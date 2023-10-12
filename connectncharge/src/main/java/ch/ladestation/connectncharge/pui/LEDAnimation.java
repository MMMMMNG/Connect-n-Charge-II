package ch.ladestation.connectncharge.pui;

import ch.ladestation.connectncharge.model.game.gamelogic.Edge;
import com.github.mbelling.ws281x.Color;

public abstract class LEDAnimation {
    private Edge associatedEdge;
    private int progress;

    public LEDAnimation(Edge associatedEdge, boolean disappear) {
        this.associatedEdge = associatedEdge;
        if (disappear) {
            this.progress = endProgress(
                associatedEdge.getStartIndex(),
                associatedEdge.getEndIndex(),
                associatedEdge.getColor());
        }
    }

    public boolean tick(Color[] ledStates) {
        if (isReversing()) {
            return tickBackwards(ledStates, progress--, associatedEdge.getStartIndex(), associatedEdge.getEndIndex(),
                associatedEdge.getColor());
        } else {
            return tickForwards(ledStates, progress++, associatedEdge.getStartIndex(), associatedEdge.getEndIndex(),
                associatedEdge.getColor());
        }
    }

    abstract boolean tickForwards(Color[] ledStates, int progress, int from, int to, Color color);

    abstract boolean tickBackwards(Color[] ledStates, int progress, int from, int to, Color color);

    abstract int endProgress(int from, int to, Color color);

    public Edge getAssociatedEdge() {
        return associatedEdge;
    }

    public int getProgress() {
        return progress;
    }

    public boolean isReversing() {
        return !associatedEdge.isOn();
    }

}
