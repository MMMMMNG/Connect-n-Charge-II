package ch.ladestation.connectncharge.model.game.gamelogic;

import com.github.mbelling.ws281x.Color;

public class Node extends Segment {
    public Node(int index, int startIndex, int endIndex) {
        super(index, startIndex, endIndex, Color.BLUE);

    }
}
