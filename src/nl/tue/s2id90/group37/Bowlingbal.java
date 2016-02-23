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

    @Override
    public void stop() {
        stopped = true;
    }

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
            System.out.println("diepte 0");
            return evaluate((DraughtsState) node.getGameState());
        }
        if (moves.size() == 1) {                //if only 1 move is possible do it
            bestMove = moves.get(0);
        } else {
            for (Move move : moves) {
                state.doMove(move);
//recursivecall     
                if (depth % 2 == 0) {              //if even depth, max alpha
                    alpha = Math.max(alpha, alphaBeta(node, alpha, beta, depth - 1));
                    System.out.println("max alpha, alpha = " + alpha);
                    if (alpha >= beta) {
                        state.undoMove(move);
                        return beta;
                    }
                } else {                            // else minimize beta
                    beta = Math.min(beta, alphaBeta(node, alpha, beta, depth - 1));
                    System.out.println("min beta, beta = " + beta);
                    if (alpha >= beta) {
                        state.undoMove(move);
                        return alpha;
                    }
                }
                System.out.println(alpha + " = alpha" + bestScore + " = bestScore");
                if (alpha > bestScore) {            //only set move as best if the alpha is better
                    bestMove = move;
                    bestScore = alpha;
                }
                state.undoMove(move);
            }
        }
        System.out.println("set best move");
        System.out.println(bestMove.toString() + "move");
        node.setBestMove(bestMove);
        if (depth % 2 == 0) {
            return alpha;
        }
        return beta;
    }

    int evaluate(DraughtsState ds) {
//obtain pieces array
        int[] pieces = ds.getPieces();
        int computedValue = 0;
// compute a value for this state, e.g.
// by compareing p[i] to WHITEPIECE, WHITEKING, etc
        for (int k = 0; k < pieces.length; k++) {
            if (ds.isWhiteToMove()) {
                if (pieces[k] == 1) {   //count white pieces
                    computedValue++;
                }
                if (pieces[k] == 3) {       //count white kings
                    computedValue = +5;
                }
            }
            if (!(ds.isWhiteToMove())) {        //count black pieces
                if (pieces[k] == 2) {
                    computedValue++;
                }
                if (pieces[k] == 4) {       //count black kings
                    computedValue = +5;
                }
            }
        }
        System.out.println("computedValue = " + computedValue);
        return computedValue;
    }

    Move bestMove;

    @Override
    public Move getMove(DraughtsState s) {
        GameNode node = new GameNode(s);
        while (true) {
            try {
                System.out.println("nu alphabeta");
                alphaBeta(node, Integer.MIN_VALUE, Integer.MAX_VALUE, 2); //find best move
                System.out.println("klaar alphabeta");
                bestMove = node.getBestMove();
                System.out.println("stop");
                stop();
            } catch (AIStoppedException e) {
                return bestMove;
            }
        }
    }
}
