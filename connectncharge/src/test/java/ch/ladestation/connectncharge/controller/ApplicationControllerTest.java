package ch.ladestation.connectncharge.controller;

import ch.ladestation.connectncharge.model.game.gamelogic.Edge;
import ch.ladestation.connectncharge.model.game.gamelogic.Game;
import ch.ladestation.connectncharge.model.game.gamelogic.Hint;
import ch.ladestation.connectncharge.model.game.gamelogic.Node;
import ch.ladestation.connectncharge.pui.GamePUI;
import ch.ladestation.connectncharge.pui.LEDAnimator;
import ch.ladestation.connectncharge.util.Pi4JContext;
import ch.ladestation.connectncharge.util.mvcbase.ObservableArray;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApplicationControllerTest {
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationControllerTest.class);
    private Game model;
    private GamePUI pui;
    private ApplicationController controller;

    public static Stream<Arguments> sourceTestHasCycle() {
        int[][] trivialCycle = {{1, 2}, {2, 3}, {3, 1}};
        int[][] trivialNoCycle = {{1, 2}, {2, 3}};
        int[][] graph1noCycle = {{1, 2}, {2, 3}, {3, 4}, {4, 5}, {5, 80}, {80, 81}, {81, 82}, {5, 6}, {6, 7}, {8, 9}};
        int[][] graph2yesCycle = {{1, 2}, {2, 3}, {3, 4}, {4, 5}, {5, 6}, {6, 77}, {77, 88}, {6, 7}, {7, 8}, {4, 1}};
        int[][] graph3multipleDisconectedCycles = {{1, 2}, {2, 3}, {3, 4}, {4, 1}, {11, 22}, {22, 33}, {33, 11}};
        int[][] graph4treeAndDisconnectedCycle =
            {{1, 11}, {11, 10}, {1, 12}, {12, 13}, {13, 14},
                {14, 15}, {1, 3}, {3, 2}, {2, 7}, {2, 5}, {5, 8},
                {8, 4}, {8, 6}, {17, 18}, {18, 19}, {19, 17}};
        int[][] graph5bigCycle =
            {{1, 2}, {2, 3}, {3, 4}, {4, 5}, {5, 6}, {6, 7}, {7, 8}, {8, 10}, {10, 11}, {11, 12}, {12, 1}};


        return Stream.of(
            Arguments.of(trivialNoCycle, false),
            Arguments.of(trivialCycle, true),
            Arguments.of(graph1noCycle, false),
            Arguments.of(graph2yesCycle, true),
            Arguments.of(graph3multipleDisconectedCycles, true),
            Arguments.of(graph4treeAndDisconnectedCycle, true),
            Arguments.of(graph5bigCycle, true)
        );
    }

    private LEDAnimator mockAnimator() {
        return mock(LEDAnimator.class);
    }

    @Test
    @DisplayName("Verify that edges are being toggled")
    public void verifyTogglingOfEdges() {
        var edge = mock(Edge.class);
        when(edge.isOn()).thenReturn(false);
        ObservableArray.ValueChangeListener<Edge> mockedListener = mock(ObservableArray.ValueChangeListener.class);
        var model = new Game();
        model.activatedEdges.onChange(mockedListener);
        var cut = new ApplicationController(model);
        cut.setGameStarted(true);

        //when
        cut.edgePressed(edge);
        cut.awaitCompletion();
        //then
        assertArrayEquals(new Edge[] {edge}, model.activatedEdges.getValues());
        verify(mockedListener).update(new Edge[0], new Edge[] {edge});
    }

    @ParameterizedTest
    @DisplayName("Test if the hasCycle() method reliably computes whether the graph has a cycle or not")
    @MethodSource("sourceTestHasCycle")
    public void testHasCycle(int[][] fromAndToNodes, boolean result) {
        var mockedGraph = mockGraph(fromAndToNodes);

        assertEquals(result, ApplicationController.hasCycle(mockedGraph));
        //verify that all the nodes were visited
        for (var edge : mockedGraph) {
            verify(edge, atLeast(1)).getFromNode();
            verify(edge, atLeast(1)).getToNode();
        }
    }

    private static Edge[] mockGraph(int[][] fromAndToNodes) {
        var ret = new ArrayList<Edge>();
        var nodes = new HashMap<Integer, Node>();

        for (int i = 0; i < fromAndToNodes.length; i++) {
            int fromNode = fromAndToNodes[i][0];
            int toNode = fromAndToNodes[i][1];
            var mockEdge = mock(Edge.class);

            Function<Integer, Node> computeFunc = nodeIndex -> {
                var mockNode = mock(Node.class);
                when(mockNode.getSegmentIndex()).thenReturn(nodeIndex);
                return mockNode;
            };


            var fNode = nodes.computeIfAbsent(fromNode, computeFunc);
            var tNode = nodes.computeIfAbsent(toNode, computeFunc);

            when(mockEdge.getFromNodeId()).thenReturn(fromNode);
            when(mockEdge.getToNodeId()).thenReturn(toNode);
            when(mockEdge.getFromNode()).thenReturn(fNode);
            when(mockEdge.getToNode()).thenReturn(tNode);
            //offset index so it doesn't overlap with nodes
            when(mockEdge.getSegmentIndex()).thenReturn(i + 696969);

            ret.add(mockEdge);
        }
        return ret.toArray(Edge[]::new);
    }

    private ApplicationController setupGameTrioSkipBlinkingEdgeAndCountDown() {
        //given
        model = new Game();
        controller = new ApplicationController(model);

        pui = new GamePUI(controller, Pi4JContext.createMockContext(), mockAnimator());
        controller.setGPUI(pui);
        assertArrayEquals(new Edge[0], model.solution.getValues());

        controller.startRound();
        controller.edgePressed(model.blinkingEdge);
        controller.awaitCompletion();
        controller.setCountdownFinished();

        assertTrue(model.gameStarted.getValue());
        assertTrue(model.isCountdownFinished.getValue());

        var sol = model.solution.getValues();
        assertTrue(sol.length > 0);
        assertFalse(model.ignoringInputs);
        return controller;
    }

    @ParameterizedTest
    @DisplayName("Test if accepting a hint edge (add edge) removes the hint")
    @ValueSource(ints = {0, 1, 10, -1, -2, -10}) //which index of the solution array to omit
    public void testHintGoesAway(int omit) {
        //given
        ApplicationController controller = hintTestingSetup(omit);
        var sol = model.solution.getValues();
        int omittedIndex = omit < 0 ? sol.length + omit : omit;
        pickEdgeTippHandling(controller, sol[omittedIndex]);
        assertEquals(Hint.HINT_PICK_EDGE.getColor(), sol[omittedIndex].getColor());
        //when
        controller.edgePressed(sol[omittedIndex]);
        controller.awaitCompletion();
        //then
        assertEquals(Hint.HINT_EMPTY_HINT, model.activeHint.getValue());
        assertArrayEquals(new Hint[0], model.activeHints.getValues());
    }

    @ParameterizedTest
    @DisplayName("Test that allTerminalsConnected() works")
    @MethodSource("sourceAllTerminalsConnected")
    public void allTerminalsConnected(Edge[] arr, Node[] term, boolean wantedRes) {
        assertEquals(wantedRes, ApplicationController.allTerminalsConnected(arr, term));
    }

    public static Stream<Arguments> sourceAllTerminalsConnected() {
        var simpleHappy = mockGraph(new int[][] {{1, 2}});
        var simpleHappyTerms = new Node[] {simpleHappy[0].getFromNode(), simpleHappy[0].getToNode()};
        var forked = mockGraph(new int[][] {{1, 2}, {3, 2}, {2, 4}, {4, 5}, {5, 6}, {6, 7}, {6, 8}});
        var forkedTerms = new Node[] {forked[0].getFromNode(), forked[1].getFromNode(), forked[5].getToNode(),
            forked[6].getToNode()};
        var twoCyc = mockGraph(
            new int[][] {{1, 2}, {2, 3}, {3, 4}, {4, 1}, {1, 20}, {20, 30}, {30, 81}, {81, 82}, {82, 83}, {83, 84},
                {84, 81}});
        var twoCycTerms =
            new Node[] {twoCyc[0].getFromNode(), twoCyc[3].getToNode(), twoCyc[6].getToNode(), twoCyc[9].getToNode()};

        var simpleUnhappy = mockGraph(new int[][] {{1, 2}, {99, 100}});
        var simpleUnhappyTerms = new Node[] {simpleUnhappy[0].getFromNode(), simpleUnhappy[1].getToNode()};
        var twoCycUnhappy = mockGraph(
            new int[][] {{1, 2}, {2, 3}, {3, 4}, {4, 1}, {1, 20}, {20, 999}, {30, 81}, {81, 82}, {82, 83}, {83, 84},
                {84, 81}});
        var twoCycUnhappyTerms =
            new Node[] {twoCyc[0].getFromNode(), twoCyc[3].getToNode(), twoCyc[6].getToNode(), twoCyc[9].getToNode()};
        return Stream.of(
            Arguments.of(simpleHappy, simpleHappyTerms, true),
            Arguments.of(forked, forkedTerms, true),
            Arguments.of(twoCyc, twoCycTerms, true),
            Arguments.of(simpleUnhappy, simpleUnhappyTerms, false),
            Arguments.of(twoCycUnhappy, twoCycUnhappyTerms, false)
        );
    }

    @Test
    @DisplayName("Test that the remove edge hint disappears when an edge is pressed")
    public void testRemoveEdgeTipp() {
        //given
        var cut = hintTestingSetup(0);
        var sol = model.solution.getValues();
        var tooMuch = pui.lookUpEdge(18, 91);
        removeEdgeTippHandling(cut, sol[0], tooMuch); //hardcoded edge known not to be in solution 1. Not so flexible 🤐

        assertEquals(Hint.HINT_REMOVE_EDGE.getColor(), tooMuch.getColor());

        //when
        cut.edgePressed(sol[0]);
        cut.awaitCompletion();

        assertTrue(tooMuch.isOn());
        assertFalse(sol[0].isOn());

        LOG.warn("critical assert that always fails with HINT_EMPTY expected");
        assertEquals(Hint.HINT_EMPTY_HINT, model.activeHint.getValue());
        assertFalse(model.isTippOn.getValue());
        assertArrayEquals(new Hint[0], model.activeHints.getValues());
    }

    @Test
    @DisplayName("Test that the hint still disappears if a different edge than the tipp edge is clicked")
    public void testHintGoesAwayEvenIfDifferentEdgeClicked() {
        //given
        var cut = hintTestingSetup(0);
        var sol = model.solution.getValues();
        pickEdgeTippHandling(cut, sol[0]);
        //when
        cut.edgePressed(sol[2]); //hardcoded edge, not so well designed
        cut.awaitCompletion();
        //then
        assertEquals(Hint.HINT_EMPTY_HINT, model.activeHint.getValue());
        assertArrayEquals(new Hint[0], model.activeHints.getValues());
    }

    private ApplicationController hintTestingSetup(int omit) {
        var controller = setupGameTrioSkipBlinkingEdgeAndCountDown();
        var sol = model.solution.getValues();
        var almost = new Edge[sol.length - 1];
        var j = 0;
        int ommittedIndex = 0;
        for (int i = 0; i < sol.length; i++) {
            if (i == omit || i == sol.length + omit) {
                ommittedIndex = i;
                continue;
            }
            controller.edgePressed(sol[i]);
            almost[j++] = sol[i];
        }
        controller.awaitCompletion();
        assertArrayEquals(almost, model.activatedEdges.getValues());
        return controller;
    }

    private void pickEdgeTippHandling(ApplicationController controller, Edge sol) {
        controller.handleTipp();

        assertEquals(sol, model.tippEdge);
        assertTrue(model.isTippOn.getValue());
        assertEquals(Hint.HINT_PICK_EDGE, model.activeHint.getValue());
        assertArrayEquals(new Hint[] {Hint.HINT_PICK_EDGE}, model.activeHints.getValues());
    }

    private void removeEdgeTippHandling(ApplicationController ctr, Edge sol, Edge tooMuch) {
        ctr.edgePressed(tooMuch);
        ctr.edgePressed(sol);
        ctr.awaitCompletion();

        ctr.handleTipp();

        assertEquals(tooMuch, model.tippEdge);
        assertTrue(model.isTippOn.getValue());
        assertEquals(Hint.HINT_REMOVE_EDGE, model.activeHint.getValue());
        assertArrayEquals(new Hint[] {Hint.HINT_SOLUTION_NOT_FOUND, Hint.HINT_REMOVE_EDGE},
            model.activeHints.getValues());
    }

    @Test
    @DisplayName("Check if next level is being loaded")
    public void testNextLevelIsLoaded() {
        //given
        var cut = setupGameTrioSkipBlinkingEdgeAndCountDown();
        finishLevel(cut);
        //when
        cut.playAgain();
        //then
        assertFalse(model.gameStarted.getValue());
        assertFalse(model.isCountdownFinished.getValue());
        assertFalse(model.ignoringInputs);
        assertArrayEquals(new Edge[0], model.activatedEdges.getValues());
    }

    private void finishLevel(ApplicationController cut) {
        var sol = model.solution.getValues();
        assertArrayEquals(new Edge[0], model.activatedEdges.getValues());
        for (var edge : sol) {
            cut.edgePressed(edge);
        }
        cut.awaitCompletion();
        assertTrue(model.isFinished.getValue());
        assertTrue(model.gameStarted.getValue());
        assertTrue(model.ignoringInputs);
        assertTrue(model.activatedEdges.getValues().length > 0);
    }

    @Test
    public void testSetEndTime() {
        final var str = "HI there!";
        var model = new Game();
        var cut = new ApplicationController(model);
        //when
        cut.setEndTime(str);
        //then
        assertEquals(str, model.endTime.get());
    }

    @Test
    @DisplayName("Test the controller.quitGame() method")
    public void testQuitGame() {
        var cut = setupGameTrioSkipBlinkingEdgeAndCountDown();
        var sol = model.solution.getValues()[0];
        cut.edgePressed(sol);
        cut.awaitCompletion();
        assertArrayEquals(new Edge[] {sol}, model.activatedEdges.getValues());
        assertTrue(sol.isOn());
        assertTrue(model.terminals.getValues().length > 0);

        //when
        cut.quitGame();
        //then
        assertFalse(model.gameStarted.getValue());
        assertArrayEquals(new Edge[0], model.activatedEdges.getValues());
        assertArrayEquals(new Node[0], model.terminals.getValues());
    }

    @Test
    @DisplayName("Test cycle hint gets added")
    public void testCycleHintGetsAdded() {
        var cut = setupGameTrioSkipBlinkingEdgeAndCountDown();

        Edge one = (Edge) pui.lookUpSegmentIdToSegment(61);
        Edge two = (Edge) pui.lookUpSegmentIdToSegment(37);   //hard coded segment ids, maybe better to
        Edge three = (Edge) pui.lookUpSegmentIdToSegment(17); //parametrize this test, or better yet use mocks
        assertArrayEquals(new Hint[0], model.activeHints.getValues());

        //when
        cut.edgePressed(one);
        cut.edgePressed(two);
        cut.edgePressed(three);
        cut.awaitCompletion();

        //then
        assertArrayEquals(new Hint[] {Hint.HINT_CYCLE}, model.activeHints.getValues());
        assertEquals(Hint.HINT_CYCLE, model.activeHint.getValue());
    }

    @Test
    @DisplayName("Test that the levels repeat after every level has been played")
    public void testLevelRepeat() {
        var cut = setupGameTrioSkipBlinkingEdgeAndCountDown();
        var sol1 = model.solution.getValues();

        //when
        for (int i = 0; i < Game.MAX_LEVEL; ++i) {
            finishLevel(cut);
            cut.playAgain();
            cut.edgePressed(model.blinkingEdge);
            cut.awaitCompletion();
            cut.setCountdownFinished();
        }

        var sol2 = model.solution.getValues();
        //then
        assertArrayEquals(sol1, sol2); //solutions are the same means it wrapped around
    }

    @Test
    @DisplayName("test that the two sources of truth, Edge.isOn and model.activatedEdges are synced")
    public void testDeactivateAllEdges() {
        var cut = setupGameTrioSkipBlinkingEdgeAndCountDown();
        finishLevel(cut);
        var edges = model.activatedEdges.getValues();
        for (var edge : edges) {
            assertTrue(edge.isOn());
        }
        //when
        cut.playAgain(); //calling deactivateAllEdges under the hood
        assertArrayEquals(new Edge[0], model.activatedEdges.getValues());
        for (var edge : pui.getAllEdges()) {
            assertFalse(edge.isOn());
        }
    }


}
