package com.jgame.elements;

import com.jgame.util.Vector2;
import java.util.List;
import com.jgame.game.GameFlow.UpdateInterval;

/**
 * Objeto que representa el elemento mas basico dentro del juego.
 * Created by jose on 29/03/16.
 */
public class GameObject {

    public int id;
    protected GameObject parent;
    final Vector2 relativePosition;
    public final Vector2 baseX;
    public final Vector2 baseB;
    public final Vector2 position;

    public GameObject(Vector2 position, int id){
        this.parent = null;
        this.id = id;
        this.relativePosition = position;
        this.baseX = new Vector2(1,0);
        this.baseB = new Vector2(0,1);
        this.position = new Vector2(position);
    }

    /**
     * Actualiza la posicion del GameObject. Si tiene un padre, actualiza su posicion utilizando la posicion relativa.
     */
    public void updatePosition(){
        if(parent != null){
            position.set(relativePosition);
            position.changeBase(parent.baseX, parent.baseB);
            position.add(parent.position);
        } else {
            position.set(relativePosition);
        }
    }

    public void setParent(GameObject parent){
        this.parent = parent;
        updatePosition();
    }

    public int getId(){
        return id;
    }

    /**
     * Suma el vector direction a position.
     * @param direction direccion en la que se movera el GameObject
     */
    public void move(Vector2 direction){
        relativePosition.add(direction);
        updatePosition();
    }

    /**
     * Mueve a GameObject a la posicion target. Se incluye modifier para ser mas flexible con los posibles cambios en la posicion, tipicamente
     * se quiere mover al objeto y moverlo cerca del target.
      * @param target
     * @param modifier
     */
    public void moveTo(Vector2 target, Vector2 modifier){
        relativePosition.set(target).add(modifier);
        updatePosition();
    }

    /**
     * Funcion que determina si el GameObject puede ser dibujado en el renderer.
     * @return
     */
    public boolean isDrawable(){
        return false;
    }
}