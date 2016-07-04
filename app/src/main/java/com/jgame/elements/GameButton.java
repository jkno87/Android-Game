package com.jgame.elements;

import com.jgame.util.Square;

/**
 * Clase que representa un boton. Tiene dos estados FREE Y PRESSED.
 * Created by jose on 14/04/16.
 */
public class GameButton {

    enum State {
        FREE, PRESSED
    }

    private State state;
    public final Square bounds;

    public GameButton(Square bounds){
        state = State.FREE;
        this.bounds = bounds;
    }

    /**
     * Cambia el estado del boton a PRESSED
     */
    public void press(){
        this.state = State.PRESSED;
    }

    /**
     * Cambia el estado del boton a FREE
     */
    public void release(){
        this.state = State.FREE;
    }

    /**
     *
     * @return boolean que representa true si el boton esta presionado.
     */
    public boolean pressed(){
       return this.state == State.PRESSED;
    }

}
