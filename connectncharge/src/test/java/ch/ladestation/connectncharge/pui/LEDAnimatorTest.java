package ch.ladestation.connectncharge.pui;

import ch.ladestation.connectncharge.model.game.gamelogic.Edge;
import com.github.mbelling.ws281x.Color;
import com.github.mbelling.ws281x.LedStrip;
import com.github.mbelling.ws281x.Ws281xLedStrip;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class LEDAnimatorTest {
    public static final int AMOUNT_LEDS = 845;

    private LedStrip mockLedStrip() {
        var mock = mock(Ws281xLedStrip.class);
        when(mock.getLedsCount()).thenReturn(AMOUNT_LEDS);
        return mock;
    }

    //this test sucks massively. Needs to be decoupled from the actual animation used.
    @Test
    @DisplayName("Test that one single animation triggers the wanted amount of renders")
    public void testAddAnimation() {
        final int lEDAMOUNTINTEST = 4; //has to be even
        final int aNIMATIONCALLS = 3;
        //given
        var mockStrip = mockLedStrip();
        var mockEdge = mock(Edge.class);
        var mockColor = mock(Color.class);
        when(mockEdge.isOn()).thenReturn(true);
        when(mockEdge.getStartIndex()).thenReturn(0);
        when(mockEdge.getEndIndex()).thenReturn(lEDAMOUNTINTEST);
        when(mockEdge.getColor()).thenReturn(mockColor);

        var cut = new LEDAnimator(mockStrip);
        //when
        cut.scheduleEdgesToBeAnimated(new Edge[0], new Edge[] {mockEdge});
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //then
        verify(mockStrip, times(aNIMATIONCALLS)).render();
        verify(mockStrip, times(AMOUNT_LEDS * aNIMATIONCALLS)).setPixel(anyInt(), any(Color.class));

    }
}
