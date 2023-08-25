package ch.ladestation.connectncharge.model.game.gamelogic;

import com.github.mbelling.ws281x.Color;

public enum Hint {
    HINT_EMPTY_HINT("EMPTY_HINT", Color.BLACK, 0),

    HINT_NOT_ALL_NODES_CONNECTED("Du hast noch nicht alle Häuser miteinander verbunden.", Color.GRAY, 0),

    HINT_PICK_EDGE("Diese orange leuchtende Kante ist in der Lösung enthalten.", Color.ORANGE, 1),
    HINT_REMOVE_EDGE("Diese rote leuchtende Kante ist nicht in der Lösung enthalten.", Color.RED, 1),
    HINT_CYCLE("Du hast ein Kreis gebildet.\n"
        + "Das heisst, dass dadurch mindestens eine dieser Kanten überflüssig ist und du diese entfernen kannst.",
        Color.RED, 2),
    HINT_SOLUTION_NOT_FOUND("Du hast zwar alle Häuser miteinander verbunden."
        + " Jedoch ist dies noch nicht das kostengünstigste Verteilungsnetz.", Color.GRAY, 3);

    private String text;
    private Color color;
    private int priority;

    Hint(String text, Color color, int priority) {
        this.text = text;
        this.color = color;
        this.priority = priority;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
