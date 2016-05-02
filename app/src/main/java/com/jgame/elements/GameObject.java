package com.jgame.elements;

import android.util.Log;

import com.jgame.util.Vector2;
import java.util.List;

/**
 * Objeto que representa el elemento mas basico dentro del juego.
 * Created by jose on 29/03/16.
 */
public class GameObject {

    public int id;
    private GameObject parent;
    final Vector2 relativePosition;
    public Vector2 baseX;
    public Vector2 baseB;
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

    public void update(List<GameElement> others, float timeDifference){
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
    }

    /**
     * Funcion que determina si el GameObject puede ser dibujado en el renderer.
     * @return
     */
    public boolean isDrawable(){
        return false;
    }
}