package com.jgame.elements;

import com.jgame.util.SimpleDrawer;
import com.jgame.util.Square;

/**
 * Created by jose on 14/04/16.
 */
public class GameButton {

    enum State {
        FREE, PRESSED
    }

    public static final SimpleDrawer.ColorData REGULAR_COLOR = new SimpleDrawer.ColorData(0.25f,0.5f,0.85f,1);
    public static final SimpleDrawer.ColorData SELECTED_COLOR = new SimpleDrawer.ColorData(1,0.9f,1,0.85f);
    private State state;
    public final Square bounds;

    public GameButton(Square bounds){
        this.bounds = bounds;
        state = State.FREE;
    }


    /**
     * Cambia el estado del boton a PRESSED
     */
    public synchronized void press(){
        this.state = State.PRESSED;
    }

    /**
     * Cambia el estado del boton a FREE
     */
    public synchronized void release(){
        this.state = State.FREE;
    }

    /**
     *
     * @return boolean que representa true si el boton esta presionado.
     */
    public synchronized boolean pressed(){
       return this.state == State.PRESSED;
    }

    /**
     * Regresa el color que debe dibujarse dependiendo del estado del boton.
     * @return
     */
    public synchronized SimpleDrawer.ColorData getCurrentColor(){
        if(state == State.FREE)
            return REGULAR_COLOR;
        else
            return SELECTED_COLOR;
    }


}
