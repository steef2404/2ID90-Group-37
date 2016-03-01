/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.tue.s2id90.group37;

import java.util.Arrays;
import java.util.List;
import nl.tue.s2id90.draughts.DraughtsState;
import nl.tue.s2id90.draughts.player.DraughtsPlayer;
import nl.tue.s2id90.game.GameState;
import org10x10.dam.game.Move;

/**
 *
 * @author huub
 */
public class OptimisticPlayer extends DraughtsPlayer {

    public OptimisticPlayer() {
        super(OptimisticPlayer.class.getResource("resources/smiley.png"));
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

    public int evaluate(DraughtsState ds, int depth) {
        int[] pieces = ds.getPieces();
        int computedValue = 0;
        int count = 0;
        for (int k = 1; k < pieces.length; k++) {       //loop over alles
            if (!isEndgame) {           //niet endgame betekent dus meer dan 10 stukken in totaal
                if (pieces[k] == 1) {
                    count++;
                    computedValue += 40;
                }
                if (pieces[k] == 2) {
                    count++;
                    computedValue -= 40;
                }
                if (pieces[k] == 3) {
                    count++;
                    if (k < 6) {                // op achterlijn is koning minder waard zodat hij koning wilt maken
                        computedValue += 80;
                    } else {
                        computedValue += 150;
                    }
                }
                if (pieces[k] == 4) {
                    count++;
                    if (k > 45) {
                        computedValue -= 80;    // op achterlijn is koning minder waard zodat hij koning wilt maken
                    } else {
                        computedValue -= 150;
                    }
                }
            } else {                            //dus wel endgame is andere heuristics.    waarom? uhm minder stukken dus koning belangrijker
                if (pieces[k] == 1) {
                    computedValue += 49;
                }
                if (pieces[k] == 2) {
                    computedValue -= 49;
                }
                if (pieces[k] == 3) {
                    if (k < 6) {
                        computedValue += 100;   // op achterlijn is koning minder waard zodat hij koning wilt maken
                    } else {
                        computedValue += 200;
                    }
                }
                if (pieces[k] == 4) {
                    if (k > 45) {
                        computedValue -= 100;   // op achterlijn is koning minder waard zodat hij koning wilt maken
                    } else {
                        computedValue -= 200;
                    }
                }
                if (isWhite) {
                    if (pieces[k] == 1) {
                        if (k < 11) {//verder naar voren is beter    want je wilt koning                
                            computedValue += 8;
                        } else if (k < 16) {
                            computedValue += 6;
                        } else if (k < 21) {
                            computedValue += 4;
                        }
                    }
                } else {
                    if (pieces[k] == 2) {
                        if (k > 45) {//verder naar voren is beter          want je wilt koning                   
                            computedValue -= 8;
                        } else if (k > 35) {
                            computedValue -= 6;
                        } else if (k > 25) {
                            computedValue -= 4;
                        }
                    }
                }
            }
        }
        if (depth == 3) {               // depth == 3 omdat je dan in een niet te diepe iteratie zit zodat je niet al 10 zetten van te voren denkt dat het endgame is 
            if (count <= 10) {
                //System.out.println(count + " count endgame true");
                isEndgame = true;
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
            return evaluate((DraughtsState) state, depth);
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
    Boolean isEndgame = false;

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
                //System.out.println("limit bowlingbal: " + limit);
                if (node.getBestMove() == null) {           //never get here
                    //System.out.println("null move");
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
