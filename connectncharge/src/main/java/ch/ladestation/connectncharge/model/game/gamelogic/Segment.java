package ch.ladestation.connectncharge.model.game.gamelogic;

import com.github.mbelling.ws281x.Color;

import ch.ladestation.connectncharge.pui.Component;

public abstract class Segment extends Component {

    public static final String HOUSE_FLAG = "H";

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
     * Whether this edge is shining
     */
    private boolean isOn = false;

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


    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public boolean isOn() {
        return isOn;
    }

    public void setOn(boolean on) {
        isOn = on;
    }

    public void on() {
        setOn(true);
    }

    public void off() {
        setOn(false);
    }

    public int getSegmentIndex() {
        return segmentIndex;
    }

    public interface SegmentStateChange {
        void onStateChange(int deltaCost);
    }
}
