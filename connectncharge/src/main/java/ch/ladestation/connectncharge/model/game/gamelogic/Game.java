package ch.ladestation.connectncharge.model.game.gamelogic;

import ch.ladestation.connectncharge.util.mvcbase.ObservableArray;
import ch.ladestation.connectncharge.util.mvcbase.ObservableValue;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class Game {
    public static final String HOUSE_FLAG = "H";
    public final ObservableArray<Edge> solution = new ObservableArray<>(new Edge[0]);
    public final ObservableArray<Edge> activatedEdges = new ObservableArray<>(new Edge[0]);
    public final ObservableArray<Node> terminals = new ObservableArray<>(new Node[0]);
    public final ObservableValue<Integer> currentScore = new ObservableValue<>(0);
    public final ObservableValue<Boolean> gameStarted = new ObservableValue<>(false);
    public final ObservableValue<Boolean> isFinished = new ObservableValue<>(false);
    public final ObservableValue<Boolean> isCountdownFinished = new ObservableValue<>(false);
    public final ObservableValue<Boolean> isEdgeBlinking = new ObservableValue<>(true);
    public final ObservableValue<Boolean> isTippOn = new ObservableValue<>(false);
    public final ObservableValue<Boolean> hasCycle = new ObservableValue<>(false);
    public final ObservableValue<Hint> activeHint = new ObservableValue<>(Hint.HINT_EMPTY_HINT);
    public final ObservableArray<Hint> activeHints = new ObservableArray<>(new Hint[0]);
    public Edge tippEdge = null;
    public Edge blinkingEdge = null;
    public boolean ignoringInputs = false;
    public StringProperty endTime = new SimpleStringProperty("");

    public Game() {
        //inherently syncing "two sources of truth":
        //this way the instance state is dependent on the model state
        //and therefore weird bugs where the two are contradictory are
        //eradicated (I hope)
        activatedEdges.onChange((oldValues, newValues) -> {
            for (var ed : oldValues) {
                ed.off();
            }
            for (var ed : newValues) {
                ed.on();
            }
        });
    }
}
