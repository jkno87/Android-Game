package com.jgame.elements;

import android.widget.Button;

import com.jgame.util.SimpleDrawer;
import com.jgame.util.Square;

/**
 * Created by jose on 14/04/16.
 */
public class GameButton {

    public abstract static class ButtonListener {
        public abstract void pressAction();
        public abstract void releaseAction();
    }

    enum State {
        FREE, PRESSED
    }

    public static final SimpleDrawer.ColorData REGULAR_COLOR = new SimpleDrawer.ColorData(0.25f,0.5f,0.85f,1);
    public static final SimpleDrawer.ColorData SELECTED_COLOR = new SimpleDrawer.ColorData(1,0.9f,1,0.85f);
    private State state;
    public final Square bounds;
    private ButtonListener buttonListener;

    public GameButton(Square bounds){
        this.bounds = bounds;
        state = State.FREE;
    }

    /**
     * Agrega el buttonListener al boton
     * @param buttonListener Listener que se utilizara en el boton
     */
    public void setButtonListener(ButtonListener buttonListener){
        this.buttonListener = buttonListener;
    }

    /**
     * Cambia el estado del boton a PRESSED
     */
    public synchronized void press(){
        buttonListener.pressAction();
        this.state = State.PRESSED;
    }

    /**
     * Cambia el estado del boton a FREE
     */
    public synchronized void release(){
        buttonListener.releaseAction();
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
