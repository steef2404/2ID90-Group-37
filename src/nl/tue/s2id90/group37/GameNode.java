/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.tue.s2id90.group37;

import nl.tue.s2id90.draughts.DraughtsState;
import nl.tue.s2id90.game.GameState;
import org10x10.dam.game.Move;
import static nl.tue.s2id90.draughts.DraughtsState.*;

/**
 *
 * @author s130973
 */
public class GameNode {

    final GameState gameState;

    public GameNode(GameState gameState) {
        this.gameState = gameState;
    }

    public GameState getGameState() {
        return gameState;
    }

    private Move bestMove;

    public Move getBestMove() {
        return bestMove;
    }

    public void setBestMove(Move bestMove) {
        this.bestMove = bestMove;
    }

    int evaluate(DraughtsState ds) {
//obtain pieces array
        int[] pieces = ds.getPieces();
        int computedValue = 0;
// compute a value for this state, e.g.
// by compareing p[i] to WHITEPIECE, WHITEKING, etc
        for (int k = 0; k < pieces.length; k++) {
            if (pieces[k] == 1) {
                computedValue ++;
            }
            if(pieces[k] == 3){
                computedValue =+ 5;
            }
        }
        return computedValue;
    }

}
