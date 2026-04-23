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
    private int maxDepthReached;

    public ExampleAgentA() {
        super(PlayerTurn.PLAYER_ONE);
    }

    public ExampleAgentA(PlayerTurn playerTurn) {
        super(playerTurn);
    }

    @Override
    public AgentMove getMove(GameBoardState gameState) {
        nodesExamined = 0;
        maxDepthReached = 0;
        return minimaxDecision(gameState);
    }

    private AgentMove minimaxDecision(GameBoardState state) {

        int bestValue = Integer.MIN_VALUE;
        ObjectiveWrapper bestMove = null;

        List<ObjectiveWrapper> moves =
                AgentController.getAvailableMoves(state, playerTurn);

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

        if (bestMove == null) {
            return null;
        }
        setNodesExamined(nodesExamined);
        setSearchDepth(MAX_DEPTH - maxDepthReached);
        return new MoveWrapper(bestMove);
    }

    private int maxValue(GameBoardState state, int depth, int alpha, int beta) {
        nodesExamined++;
        maxDepthReached = Math.max(maxDepthReached, depth);
        if (depth == 0 || AgentController.isTerminal(state, playerTurn)) {
            return evaluate(state);
        }

        int value = Integer.MIN_VALUE;

        List<ObjectiveWrapper> moves =
                AgentController.getAvailableMoves(state, playerTurn);

        if (moves.isEmpty()) {
            return evaluate(state);
        }

        for (ObjectiveWrapper move : moves) {

            GameBoardState newState =
                    AgentController.getNewState(state, move);

            value = Math.max(value,
                    minValue(newState, depth - 1, alpha, beta));

            // Pruning
            if (value >= beta) {
                return value;
            }

            alpha = Math.max(alpha, value);
        }

        return value;
    }

    private int minValue(GameBoardState state, int depth, int alpha, int beta) {
        nodesExamined++;
        maxDepthReached = Math.max(maxDepthReached, depth);
        if (depth == 0 || AgentController.isTerminal(state, playerTurn)) {
            return evaluate(state);
        }

        int value = Integer.MAX_VALUE;

        List<ObjectiveWrapper> moves =
                AgentController.getAvailableMoves(state, GameTreeUtility.getCounterPlayer(playerTurn));

        if (moves.isEmpty()) {
            return evaluate(state);
        }

        for (ObjectiveWrapper move : moves) {

            GameBoardState newState =
                    AgentController.getNewState(state, move);

            value = Math.min(value,
                    maxValue(newState, depth - 1, alpha, beta));

            // Pruning
            if (value <= alpha) {
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