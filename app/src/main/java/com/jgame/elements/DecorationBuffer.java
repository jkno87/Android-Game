package com.jgame.elements;

import com.jgame.game.GameActivity.Decoration;

import java.util.ArrayDeque;
import java.util.ArrayList;

/**
 * Created by jose on 8/11/16.
 */
public class DecorationBuffer {

    private final Decoration[] decorations;
    private byte availableDecorations;
    private byte recycledDecorations;

    public DecorationBuffer(int size){
        decorations = new Decoration[size];
        availableDecorations = 0;
        recycledDecorations = 0;
    }

    public void updateDecorations(){
        if(availableDecorations == 0)
            return;

    }

    public void addDecoration(Decoration d){

    }

}
