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

    /*
     Method to evaluate the draughts
     */
    int evaluate(DraughtsState ds) {
//obtain pieces array
        int[] pieces = ds.getPieces();
        int computedValue = 0;

        computedValue = countPieces(ds);

        //computedValue += endState(ds);
        for (int k = 1; k < pieces.length; k++) {       //go to the middle
            if (22 <= k && k <= 24 || 27 <= k && k <= 29) {
                if (pieces[k] == 1) {
                    computedValue++;
                }
                if (pieces[k] == 2) {
                    computedValue--;
                }
            }

            if (k % 10 == 1 || k % 10 == 2 || k % 10 == 3 || k % 10 == 4) {    //check if you are covered from white side
                if (pieces[k] == 1) {
                    if (pieces[k + 5] == 1 && pieces[k + 6] == 1) {
                        computedValue += 2;
                    }
                }
                if (k > 5) {
                    if (pieces[k] == 2) {
                        if (pieces[k - 4] == 2 && pieces[k - 5] == 2) {
                            computedValue -= 2;
                        }
                    }
                }
            }

            if (k % 10 == 7 || k % 10 == 8 || k % 10 == 9 || k % 10 == 0) {    //check if you are covered from black side
                if (pieces[k] == 2) {
                    if (pieces[k - 5] == 2 && pieces[k - 6] == 2) {
                        computedValue -= 2;
                    }
                }
                if (k < 47) {
                    if (pieces[k] == 1) {
                        if (pieces[k + 4] == 1 && pieces[k + 5] == 1) {
                            computedValue += 2;
                        }
                    }
                }
            }

//            if (pieces[k] == 1) {
//                computedValue += 20;
//            }
//            if (pieces[k] == 2) {
//                computedValue -= 20;
//            }
//            if (pieces[k] == 3) {
//                computedValue += 50;
//            }
//            if (pieces[k] == 4) {
//                computedValue -= 50;
//            }
        }
        if (!isWhite) {
            computedValue = -computedValue;
        }

        //System.out.println("computedValue = " + computedValue);
        return computedValue;
    }

    public int countPieces(DraughtsState ds) {
        int score = 0;
        int[] pieces = ds.getPieces();
        for (int k = 1; k < pieces.length; k++) {
            if (pieces[k] == 1) {
                score += 40;
            }
            if (pieces[k] == 2) {
                score -= 40;
            }
            if (pieces[k] == 3) {
                if (k < 6) {
                    score += 40;
                } else {
                    score += 100;
                }
            }
            if (pieces[k] == 4) {
                if (k > 45) {
                    score -= 40;
                } else {
                    score -= 100;
                }
            }
        }
        return score;
    }

    public int endState(DraughtsState ds) {
        int win = 0;
        if ((isWhite != ds.isWhiteToMove()) && ds.isEndState()) {
            win += 100000;
        }
        if ((isWhite == ds.isWhiteToMove()) && ds.isEndState()) {
            win -= 100000;
        }
        return win;
    }

    Move bestMove;          //best move
    Boolean isWhite;        //player is white or black

    @Override
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
}
