package main;

import com.eudycontreras.othello.capsules.AgentMove;
import com.eudycontreras.othello.capsules.MoveWrapper;
import com.eudycontreras.othello.capsules.ObjectiveWrapper;
import com.eudycontreras.othello.controllers.AgentController;
import com.eudycontreras.othello.controllers.Agent;
import com.eudycontreras.othello.enumerations.PlayerTurn;
import com.eudycontreras.othello.models.GameBoardState;
import com.eudycontreras.othello.utilities.GameTreeUtility;

import java.util.List;

public class ExampleAgentA extends Agent {

    private static final int MAX_DEPTH = 4;

    private int nodesExamined;
    private int prunedNodes;

    public ExampleAgentA() {
        super(PlayerTurn.PLAYER_ONE);
    }

    public ExampleAgentA(PlayerTurn playerTurn) {
        super(playerTurn);
    }

    @Override
    public AgentMove getMove(GameBoardState gameState) {
        nodesExamined = 0;
        prunedNodes = 0;

        AgentManager.printBoard(gameState.getGameBoard().getCells(), true);

        return minimaxDecision(gameState);
    }

    private AgentMove minimaxDecision(GameBoardState state) {

        int bestValue = Integer.MIN_VALUE;
        ObjectiveWrapper bestMove = null;

        List<ObjectiveWrapper> moves =
                AgentController.getAvailableMoves(state, playerTurn);

        if (moves.isEmpty()) {
            return null;
        }

        for (ObjectiveWrapper move : moves) {

            GameBoardState newState =
                    AgentController.getNewState(state, move);

            int value = minValue(newState, MAX_DEPTH - 1,
                    Integer.MIN_VALUE, Integer.MAX_VALUE);

            if (value > bestValue) {
                bestValue = value;
                bestMove = move;
            }
        }

        setNodesExamined(nodesExamined);
        setSearchDepth(MAX_DEPTH);
        setPrunedCounter(prunedNodes);

        System.out.println(
                "Move done | Nodes: " + nodesExamined +
                        " | Depth: " + MAX_DEPTH +
                        " | Pruned: " + prunedNodes
        );

        return new MoveWrapper(bestMove);
    }

    private int maxValue(GameBoardState state, int depth, int alpha, int beta) {
        nodesExamined++;

        // Hämta moves för båda spelare
        List<ObjectiveWrapper> myMoves =
                AgentController.getAvailableMoves(state, playerTurn);

        List<ObjectiveWrapper> opponentMoves =
                AgentController.getAvailableMoves(state,
                        GameTreeUtility.getCounterPlayer(playerTurn));

        // Terminal: ingen kan spela
        if ((myMoves.isEmpty() && opponentMoves.isEmpty()) || depth == 0) {
            return evaluate(state);
        }

        int value = Integer.MIN_VALUE;

        // Pass move
        if (myMoves.isEmpty()) {
            return minValue(state, depth - 1, alpha, beta);
        }

        for (ObjectiveWrapper move : myMoves) {

            GameBoardState newState =
                    AgentController.getNewState(state, move);

            value = Math.max(value,
                    minValue(newState, depth - 1, alpha, beta));

            // Alpha-Beta pruning
            if (value >= beta) {
                prunedNodes++;
                return value;
            }

            alpha = Math.max(alpha, value);
        }

        return value;
    }

    private int minValue(GameBoardState state, int depth, int alpha, int beta) {
        nodesExamined++;

        List<ObjectiveWrapper> myMoves =
                AgentController.getAvailableMoves(state,
                        GameTreeUtility.getCounterPlayer(playerTurn));

        List<ObjectiveWrapper> opponentMoves =
                AgentController.getAvailableMoves(state, playerTurn);

        // Terminal
        if ((myMoves.isEmpty() && opponentMoves.isEmpty()) || depth == 0) {
            return evaluate(state);
        }

        int value = Integer.MAX_VALUE;

        // Pass move
        if (myMoves.isEmpty()) {
            return maxValue(state, depth - 1, alpha, beta);
        }

        for (ObjectiveWrapper move : myMoves) {

            GameBoardState newState =
                    AgentController.getNewState(state, move);

            value = Math.min(value,
                    maxValue(newState, depth - 1, alpha, beta));

            // Alpha-Beta pruning
            if (value <= alpha) {
                prunedNodes++;
                return value;
            }

            beta = Math.min(beta, value);
        }

        return value;
    }

    private int evaluate(GameBoardState state) {
        return (int) AgentController.getGameEvaluation(state, playerTurn);
    }
}