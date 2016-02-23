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

    int value = 0;

    @Override
    public Integer getValue() {
        return value; //To change body of generated methods, choose Tools | Templates.
    }

    int alphaBeta(GameNode node, int alpha, int beta, int depth, int limit, boolean maximize)
            throws AIStoppedException {
        if (stopped) {
            stopped = false;
            throw new AIStoppedException();
        }
        GameState state = node.getGameState();

        if (state.isEndState() || depth > limit) {
            return evaluate((DraughtsState) state);
        }

        List<Move> moves = state.getMoves();
        Move bestMove = moves.get(0);

        for (Move move : moves) {
            state.doMove(move);
            GameNode newNode = new GameNode(state);
            int temp = alphaBeta(newNode, alpha, beta, depth + 1, limit, !maximize);
            state.undoMove(move);

            if (maximize) {
                if (temp > alpha) {
                    alpha = temp;
                    bestMove = move;
                }
            } else {
                if (temp < beta) {
                    beta = temp;
                }
            }
            if (alpha >= beta) {
                node.setBestMove(bestMove);
                if (maximize) {
                    return beta;
                } else {
                    return alpha;
                }
            }
        }

        node.setBestMove(bestMove);
        if (maximize) {
            return alpha;
        } else {
            return beta;
        }

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
                    computedValue += 5;
                }
            }
            if (!(ds.isWhiteToMove())) {        //count black pieces
                if (pieces[k] == 2) {
                    computedValue++;
                }
                if (pieces[k] == 4) {       //count black kings
                    computedValue += 5;
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

        try {
            while (true) {
                int limit = 2;
                while (true) {
                    System.out.println("nu alphabeta");
                    value = alphaBeta(node, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, limit, true); //find best move
                    limit++;
                    System.out.println("klaar alphabeta");
                    bestMove = node.getBestMove();
                }
            }
        } catch (AIStoppedException e) {
            //do nothing
        }
        return bestMove;
    }
}
