/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.tue.s2id90.group37;

import java.util.List;
import nl.tue.s2id90.draughts.DraughtsState;
import nl.tue.s2id90.draughts.player.DraughtsPlayer;
import nl.tue.s2id90.game.GameState;
import org10x10.dam.game.Move;

/**
 *
 * @author s130973
 */
public class Bowlingbal extends DraughtsPlayer {

    public Bowlingbal() {
        super(Bowlingbal.class.getResource("resources/smiley.png"));
    }

    private boolean stopped;

    int alphaBeta(GameNode node, int alpha, int beta, int depth)
            throws AIStoppedException {
        if (stopped) {
            stopped = false;
            throw new AIStoppedException();
        }
        GameState state = node.getGameState();
        List<Move> moves = state.getMoves();
        Move bestMove = null;
        int bestScore = Integer.MIN_VALUE;
        if (depth == 0) {
            return node.evaluate();
        }
        if (moves.size() == 1) {                //if only 1 move is possible do it
            bestMove = moves.get(0);
        } else {
            for (Move move : moves) {
                state.doMove(move);
//recursivecall     
                if (depth % 2 == 0) {              //if even depth, max alpha
                    alpha = Math.max(alpha, alphaBeta(node, alpha, beta, depth - 1));
                    if (alpha >= beta) {
                        state.undoMove(move);
                        return beta;
                    }
                } else {                            // else minimize beta
                    beta = Math.min(beta, alphaBeta(node, alpha, beta, depth - 1));
                    if (alpha >= beta) {
                        state.undoMove(move);
                        return alpha;
                    }
                }
                if (alpha > bestScore) {            //only set move as best if the alpha is better
                    bestMove = move;
                    bestScore = alpha;
                }
                state.undoMove(move);
            }
        }
        node.setBestMove(bestMove);
        if (depth % 2 == 0) {
            return alpha;
        }
        return beta;

    }

    @Override
    public Move getMove(DraughtsState s) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
