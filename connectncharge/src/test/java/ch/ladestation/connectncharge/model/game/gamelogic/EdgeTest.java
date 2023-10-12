package ch.ladestation.connectncharge.model.game.gamelogic;

import com.github.mbelling.ws281x.Color;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class EdgeTest {

    @Test
    public void testConstructorWithValues() {
        var cut = new Edge(1, 2, 3, 4, 5, 6);

        assertEquals(1, cut.getSegmentIndex());
        assertEquals(2, cut.getStartIndex());
        assertEquals(3, cut.getEndIndex());
        assertEquals(4, cut.getCost());
        assertEquals(5, cut.getFromNodeId());
        assertEquals(6, cut.getToNodeId());
    }

    @Test
    public void testSetFromAndToNode() {
        var mockNode1 = mock(Node.class);
        var mockNode2 = mock(Node.class);

        var cut = new Edge(1, 2, 3, 4, 5, 6);

        cut.setFromNode(mockNode1);
        cut.setToNode(mockNode2);

        assertEquals(mockNode1, cut.getFromNode());
        assertEquals(mockNode2, cut.getToNode());
    }

    @Test
    @DisplayName("Test remaining Edge setters and getters")
    void testSettersAndGetters() {
        var mockedColor = mock(Color.class);

        var cut = new Edge(1, 2, 3, 4, 5, 6);

        cut.setColor(mockedColor);

        assertEquals(mockedColor, cut.getColor());
    }
}
