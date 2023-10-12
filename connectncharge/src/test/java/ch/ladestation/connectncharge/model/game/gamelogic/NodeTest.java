package ch.ladestation.connectncharge.model.game.gamelogic;

import com.github.mbelling.ws281x.Color;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

// import ch.ladestation.connectncharge.model.Node;

public class NodeTest {

    @Test
    @DisplayName("Test Node constructor and getters")
    void testNodeConstructorAndGetters() {
        var cut = new Node(1, 2, 3);

        assertEquals(1, cut.getSegmentIndex());
        assertEquals(2, cut.getStartIndex());
        assertEquals(3, cut.getEndIndex());
    }

    @Test
    @DisplayName("Test remaining Node setters and getters")
    void testSettersAndGetters() {
        var mockedColor = mock(Color.class);

        var cut = new Node(1, 2, 3);

        cut.setColor(mockedColor);

        assertEquals(mockedColor, cut.getColor());
    }
}
