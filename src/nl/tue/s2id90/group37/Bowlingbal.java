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

    /*
     Method for interrupting
     */
    @Override
    public void stop() {
        stopped = true;
    }

    int value = 0;  //value in GUI

    /*
     Method to set value in GUI
     */
    @Override
    public Integer getValue() {
        return value; //To change body of generated methods, choose Tools | Templates.
    }

    /*
     AlphaBeta method for recursive searching
     */
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

        if (moves.size() == 1) {
            return evaluate((DraughtsState) state);
        }

        for (Move move : moves) {
            state.doMove(move);
            GameNode newNode = new GameNode(state);
            int temp = alphaBeta(newNode, alpha, beta, depth + 1, limit, !maximize);        //recursive call
            state.undoMove(move);

            if (maximize) {         //if we are maximizing then see if temp is greater than alpha and set bestMove
                if (temp > alpha) {
                    alpha = temp;
                    bestMove = move;
                }
            } else {                //if we are minimizing see if temp is smaller than beta
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

    /*
     Method to evaluate the draughts
     */
    int evaluate(DraughtsState ds) {
//obtain pieces array
        int[] pieces = ds.getPieces();
        int computedValue = 0;
// compute a value for this state
        for (int k = 0; k < pieces.length; k++) {
            if (ds.isWhiteToMove()) {
                System.out.println("wit");
                if (k % 10 == 5 || k % 10 == 6) {       //edges of board
                if (pieces[k] == 1) {   //count white pieces
                    computedValue += 4;
                }
                if (pieces[k] == 3) {       //count white kings
                    computedValue += 7;
                }
                if (pieces[k] == 2) {
                    computedValue -= 4;        //count black pieces
                }
                if (pieces[k] == 4) {       //count black kings
                    computedValue -= 7;
                }
                }
                else {
                                    if (pieces[k] == 1) {   //count white pieces
                    computedValue++;
                }
                if (pieces[k] == 3) {       //count white kings
                    computedValue += 5;
                }
                if (pieces[k] == 2) {
                    computedValue--;        //count black pieces
                }
                if (pieces[k] == 4) {       //count black kings
                    computedValue -= 5;
                }
                }
            }
            if (!(ds.isWhiteToMove())) {
                System.out.println("zwart");
                if (k % 10 == 5 || k % 10 == 6) {       //edges of board
                    if (pieces[k] == 2) {       //count black pieces
                        computedValue += 4;
                    }
                    if (pieces[k] == 4) {       //count black kings
                        computedValue += 7;
                    }
                    if (pieces[k] == 1) {       //count white pieces
                        computedValue -= 4;
                    }
                    if (pieces[k] == 3) {       //count white kings
                        computedValue -= 7;
                    }
                }
                else {
                    if (pieces[k] == 2) {       //count black pieces
                        computedValue += 1;
                    }
                    if (pieces[k] == 4) {       //count black kings
                        computedValue += 5;
                    }
                    if (pieces[k] == 1) {       //count white pieces
                        computedValue -= 1;
                    }
                    if (pieces[k] == 3) {       //count white kings
                        computedValue -= 5;
                    }
                }
            }
        }
        //System.out.println("computedValue = " + computedValue);
        return computedValue;
    }

    Move bestMove;

    @Override
    public Move getMove(DraughtsState s) {
        GameNode node = new GameNode(s);

        try {
            int limit = 2;
            while (true) {
                //System.out.println("nu alphabeta");
                value = alphaBeta(node, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, limit, true); //find best move
                limit++;
                //System.out.println("limit= " + limit);
                bestMove = node.getBestMove();
            }
        } catch (AIStoppedException e) {
            //do nothing
        }
        return bestMove;
    }
}
