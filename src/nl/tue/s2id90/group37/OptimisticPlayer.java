package nl.tue.s2id90.group37;

import java.util.List;
import nl.tue.s2id90.draughts.DraughtsState;
import nl.tue.s2id90.draughts.player.DraughtsPlayer;
import nl.tue.s2id90.game.GameState;
import org10x10.dam.game.Move;

/**
 * A simple draughts player that plays the first moves that comes to mind and
 * values all moves with value 0.
 *
 * @author huub
 */
public class OptimisticPlayer extends DraughtsPlayer {

    public OptimisticPlayer() {
        super(UninformedPlayer.class.getResource("resources/optimist.png"));
    }
    private boolean stopped;

    /*
     Method for interrupting
     */
    @Override
    public void stop() {
        stopped = true;
    }

    int value = 0;

    public int evaluate(DraughtsState ds) {
        int[] pieces = ds.getPieces();
        int computedValue = 0;
        for (int k = 1; k < pieces.length; k++) {
            if (pieces[k] == 1) {
                computedValue += 20;
            }
            if (pieces[k] == 2) {
                computedValue -= 20;
            }
            if (pieces[k] == 3) {
                computedValue += 50;
            }
            if (pieces[k] == 4) {
                computedValue -= 50;
            }
        }
         if (!isWhite) {
            computedValue = -computedValue;
        }
        return computedValue;
    }

    int alphaBeta(GameNode node, int alpha, int beta, int depth, int limit, boolean maximize)
            throws AIStoppedException {
        if (stopped) {  //interrupt
            stopped = false;
            throw new AIStoppedException();
        }
        GameState state = node.getGameState();      //get state

        if (state.isEndState() || depth > limit) {  //if it is the last state or the depth is over the limit return last evaluate 
            return evaluate((DraughtsState) state);
        }

        List<Move> moves = state.getMoves();        //get all moves
        Move bestMove = moves.get(0);               //set best move random

//        if (moves.size() == 1) {
//            return evaluate((DraughtsState) state);
//        }
        if (maximize) {
            int temp = Integer.MIN_VALUE;
            for (Move move : moves) {
                state.doMove(move);
                GameNode newNode = new GameNode(state);
                temp = Math.max(temp, alphaBeta(newNode, alpha, beta, depth + 1, limit, false)); //recursive call
                if (temp > alpha) {
                    alpha = temp;
                    bestMove = move;
                }
                state.undoMove(move);
                if (beta <= alpha) {
                    node.setBestMove(bestMove);
                    return beta;
                }
            }
            node.setBestMove(bestMove);
            return alpha;
        } else {
            int temp = Integer.MAX_VALUE;
            for (Move move : moves) {
                state.doMove(move);
                GameNode newNode = new GameNode(state);
                temp = Math.min(temp, alphaBeta(newNode, alpha, beta, depth + 1, limit, true));        //recursive call
                beta = Math.min(beta, temp);
                state.undoMove(move);
                if (beta <= alpha) {
                    node.setBestMove(bestMove);
                    return alpha;
                }
            }
            node.setBestMove(bestMove);
            return beta;
        }
    }
    
    Move bestMove;
    Boolean isWhite;

    @Override
    /**
     * @return a random move *
     */
    public Move getMove(DraughtsState s) {
      GameNode node = new GameNode(s.clone());
        isWhite = s.isWhiteToMove();
        try {
            int limit = 2;
            while (true) {
                //System.out.println("nu alphabeta");
                value = alphaBeta(node, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, limit, true); //find best move
                //System.out.println("value: " + value);
                limit++;
                //System.out.println("limit: " + limit);
                if (node.getBestMove() == null) {           //never get here
                    System.out.println("null move");
                    List<Move> moves = node.getGameState().getMoves();
                    Move temp = moves.get(0);
                    node.setBestMove(temp);
                }
                bestMove = node.getBestMove();
            }
        } catch (AIStoppedException e) {
            //do nothing
        }
        return bestMove;
    }

    @Override
    public Integer getValue() {
        return value;
    }
}
