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

    @Override
    public String getName() {
        isEndgame = false;
        return super.getName(); //To change body of generated methods, choose Tools | Templates.
    }

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
            return evaluate((DraughtsState) state, depth);
        }

        List<Move> moves = state.getMoves();        //get all moves
        Move bestMove = moves.get(0);               //set best move random

//        if (moves.size() == 1) {                    //if only 1 move is available do it
//            System.out.println("1 zet");
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
    int evaluate(DraughtsState ds, int depth) {
//obtain pieces array
        int[] pieces = ds.getPieces();
        int computedValue = 0;
        int count = 0;

        // computedValue = countPieces(ds);
        //      computedValue += endState(ds);
        for (int k = 1; k < pieces.length; k++) {       //go to the middle

            if (!isEndgame) {
                // System.out.println("niet endgame");
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
                    if (k < 6) {
                        computedValue += 80;
                    } else {
                        computedValue += 150;
                    }
                }
                if (pieces[k] == 4) {
                    count++;
                    if (k > 45) {
                        computedValue -= 80;
                    } else {
                        computedValue -= 150;
                    }
                }
                if (k % 10 == 5 || k % 10 == 6) {
                    if (pieces[k] == 1) {
                        computedValue -= 4;
                    }
                    if (pieces[k] == 2) {
                        computedValue += 4;
                    }
                }
                if (22 <= k && k <= 24 || 27 <= k && k <= 29) {
                    if (pieces[k] == 1) {
                        computedValue++;
                    }
                    if (pieces[k] == 2) {
                        computedValue--;
                    }
                }

                if (k % 10 == 1 || k % 10 == 2 || k % 10 == 3 || k % 10 == 4) {    //check if you are covered from white side
                    if (isWhite) {
                        if (pieces[k] == 1) {
                            if (pieces[k + 5] == 1 && pieces[k + 6] == 1) {
                                computedValue += 2;
                            }
                        }
                    } else if (k > 5) {
                        if (pieces[k] == 2) {
                            if (pieces[k - 4] == 2 && pieces[k - 5] == 2) {
                                computedValue -= 2;
                            }
                        }
                    }
                }
                if (k % 10 == 7 || k % 10 == 8 || k % 10 == 9 || k % 10 == 0) {    //check if you are covered from black side
                    if (!isWhite) {
                        if (pieces[k] == 2) {
                            if (pieces[k - 5] == 2 && pieces[k - 6] == 2) {
                                computedValue -= 2;
                            }
                        }
                    } else if (k < 47) {
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
            } else {
                int kingsWhite = 0;
                int kingsBlack = 0;
                if (!multipleKings) {
                    if (pieces[k] == 1) {
                        computedValue += 50;
                    }
                    if (pieces[k] == 2) {
                        computedValue -= 50;
                    }
                    if (pieces[k] == 3) {
                        kingsWhite++;
                        if (k < 6) {
                            computedValue += 101;
                        } else {
                            computedValue += 150;
                        }
                    }
                    if (pieces[k] == 4) {
                        kingsBlack++;
                        if (k > 45) {
                            computedValue -= 101;
                        } else {
                            computedValue -= 150;
                        }
                    }
                } else {                        //meerdere koningen dus andere heuristics
                    System.out.println("meerdere konings");
                    if (pieces[k] == 1) {
                        computedValue += 50;
                    }
                    if (pieces[k] == 2) {
                        computedValue -= 50;
                    }
                    if (pieces[k] == 3) {
                        kingsWhite++;
                        if (stupidKings.contains(k)) {
                            computedValue--;
                        }
                        else {
                        computedValue += 51;
                        }
                    }
                    if (pieces[k] == 4) {
                        kingsBlack++;
                        if (stupidKings.contains(k)) {
                            computedValue++;
                        }
                        else {
                        computedValue -= 51;
                        }
                    }
                }
                if (depth == 3) {
                    if (kingsWhite >= 1 && kingsBlack >= 1) {
                        multipleKings = true;
                    } else {
                        multipleKings = false;
                    }
                }
                if (isWhite) {
                    if (pieces[k] == 1) {
                        if (k < 11) {//verder naar voren is beter                    
                            computedValue += 30;
                        } else if (k < 16) {
                            computedValue += 26;
                        } else if (k < 21) {
                            computedValue += 24;
                        }
                        boolean noEnemies = true;
                        for (int j = k; j > 5; j -= 10) {         //alle velden in die rijen, niet een veld
                            System.out.println("j = " + j + "k = " + k);
                            if (!noEnemies) {
                                System.out.println("hier komt ie niet, toch?");
                                break;
                            }
                            if (c0.contains(k)) {
                                if (pieces[j] == 2 || pieces[j + 1] == 2 || pieces[j - 5] == 2) {
                                    noEnemies = false;
                                    break;
                                }
                            } else if (c1.contains(k)) {
                                if (pieces[j - 10] == 2 || pieces[j - 4] == 2 || pieces[j - 5] == 2) {
                                    noEnemies = false;
                                    break;
                                }
                            } else if (c2.contains(k)) {
                                if (pieces[j] == 2 || pieces[j - 6] == 2 || pieces[j - 5] == 2) {
                                    noEnemies = false;
                                    break;
                                }
                            } else if (c3.contains(k)) {
                                if (pieces[j - 10] == 2 || pieces[j - 5] == 2 || pieces[j - 4] == 2) {
                                    noEnemies = false;
                                    break;
                                }
                            } else if (c4.contains(k)) {
                                if (pieces[j] == 2 || pieces[j - 6] == 2 || pieces[j - 5] == 2) {
                                    noEnemies = false;
                                    break;
                                }
                            } else if (c5.contains(k)) {
                                if (pieces[j - 10] == 2 || pieces[j - 4] == 2 || pieces[j - 5] == 2) {
                                    noEnemies = false;
                                    break;
                                }
                            } else if (c6.contains(k)) {
                                if (pieces[j] == 2 || pieces[j - 6] == 2 || pieces[j - 5] == 2) {
                                    noEnemies = false;
                                    break;
                                }
                            } else if (c7.contains(k)) {
                                if (pieces[j - 10] == 2 || pieces[j - 4] == 2 || pieces[j - 5] == 2) {
                                    noEnemies = false;
                                    break;
                                }
                            } else if (c8.contains(k)) {
                                if (pieces[j] == 2 || pieces[j - 6] == 2 || pieces[j - 5] == 2) {
                                    noEnemies = false;
                                    break;
                                }
                            } else if (c9.contains(k)) {
                                if (pieces[j - 10] == 2 || pieces[j - 1] == 2 || pieces[j - 5] == 2 || pieces[4] == 2) {
                                    noEnemies = false;
                                    break;
                                }
                            }
                        }
                        if (noEnemies) {
                            computedValue += 25;
                        }
                    }
                    if (pieces[k] == 3) {
                        if (diagonals.contains(k)) {
                            computedValue += 2;
                        }
                        if (center.contains(k)) {
                            System.out.println("koning midden");
                            computedValue += 14;
                        }
                    }
                } else {
                    if (pieces[k] == 2) {
                        if (k < 11) {//verder naar voren is beter                    
                            computedValue -= 30;
                        } else if (k < 16) {
                            computedValue -= 26;
                        } else if (k < 21) {
                            computedValue -= 24;
                        }
                        Boolean noEnemies = true;
                        for (int j = k; j < 46; j += 10) {
                            if (!noEnemies) {
                                break;
                            }
                            if (c0.contains(k)) {
                                if (pieces[j + 10] == 1 || pieces[j + 1] == 1 || pieces[j + 5] == 1 || pieces[47] == 1) {
                                    noEnemies = false;
                                    break;
                                }
                            } else if (c1.contains(k)) {
                                if (pieces[j] == 1 || pieces[j + 5] == 1 || pieces[j + 6] == 1) {
                                    noEnemies = false;
                                    break;
                                }
                            } else if (c2.contains(k)) {
                                if (pieces[j + 10] == 1 || pieces[j + 4] == 1 || pieces[j + 5] == 1) {
                                    noEnemies = false;
                                    break;
                                }
                            } else if (c3.contains(k)) {
                                if (pieces[j] == 1 || pieces[j + 5] == 1 || pieces[j + 6] == 1) {
                                    noEnemies = false;
                                    break;
                                }
                            } else if (c4.contains(k)) {
                                if (pieces[j + 10] == 1 || pieces[j + 4] == 1 || pieces[j + 5] == 1) {
                                    noEnemies = false;
                                    break;
                                }
                            } else if (c5.contains(k)) {
                                if (pieces[j] == 1 || pieces[j + 5] == 1 || pieces[j + 6] == 1) {
                                    noEnemies = false;
                                    break;
                                }
                            } else if (c6.contains(k)) {
                                if (pieces[j + 10] == 1 || pieces[j + 4] == 1 || pieces[j + 5] == 1) {
                                    noEnemies = false;
                                    break;
                                }
                            } else if (c7.contains(k)) {
                                if (pieces[j] == 1 || pieces[j + 5] == 1 || pieces[j + 6] == 1) {
                                    noEnemies = false;
                                    break;
                                }
                            } else if (c8.contains(k)) {
                                if (pieces[j + 10] == 1 || pieces[j + 4] == 1 || pieces[j + 5] == 1) {
                                    noEnemies = false;
                                    break;
                                }
                            } else if (c9.contains(k)) {
                                if (pieces[j] == 1 || pieces[j + 5] == 1 || pieces[j - 1] == 1) {
                                    noEnemies = false;
                                    break;
                                }
                            }
                        }
                        if (noEnemies) {
                            //System.out.println("bonus lege rij");
                            computedValue -= 25;
                        }
                    }
                    if (pieces[k] == 4) {
                        if (diagonals.contains(k)) {
                            computedValue -= 2;
                        }
                        if (center.contains(k)) {
                            System.out.println("koning midden");
                            computedValue -= 14;
                        }
                    }
                }
            }
        }
        if (depth == 3) {
            if (count <= 10) {
                //System.out.println(count + " count endgame true");
                isEndgame = true;
            }
        }
        if (!isWhite) {
            computedValue = -computedValue;
        }

        //System.out.println("computedValue = " + computedValue);
        return computedValue;
    }
//    public int countPieces(DraughtsState ds) {
//        int score = 0;
//        
//        int[] pieces = ds.getPieces();
//        for (int k = 1; k < pieces.length; k++) {
//
//        return score;
//    }

    public int endState(DraughtsState ds) {
        int win = 0;
        if ((isWhite != ds.isWhiteToMove()) && ds.isEndState()) {
            win += 100000;
        }
//        if ((isWhite == ds.isWhiteToMove()) && ds.isEndState()) {
//            win -= 100000;
//        }
        return win;
    }

    Move bestMove;          //best move
    Boolean isWhite;        //player is white or black
    Boolean isEndgame = false;       //see if it is endgame
    Boolean multipleKings = false;
    List diagonals = Arrays.asList(1, 6, 7, 11, 12, 17, 18, 22, 23, 28, 29, 33, 34, 39, 40, 44, 45, 50, 5, 10, 14, 19, 23, 28, 32, 37, 41, 46);
    List center = Arrays.asList(22, 23, 27, 28, 29, 32, 33);
    List stupidKings = Arrays.asList(2, 7, 11, 16, 1, 6);
    List c0 = Arrays.asList(6, 16, 26, 36, 46);
    List c1 = Arrays.asList(1, 11, 21, 31, 41);
    List c2 = Arrays.asList(7, 17, 27, 37, 47);
    List c3 = Arrays.asList(2, 12, 22, 32, 42);
    List c4 = Arrays.asList(8, 18, 28, 38, 48);
    List c5 = Arrays.asList(3, 13, 23, 33, 43);
    List c6 = Arrays.asList(9, 19, 29, 39, 49);
    List c7 = Arrays.asList(4, 14, 24, 34, 44);
    List c8 = Arrays.asList(10, 20, 30, 40, 50);
    List c9 = Arrays.asList(5, 15, 25, 35, 45);

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
                System.out.println("limit optimistic: " + limit);
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
