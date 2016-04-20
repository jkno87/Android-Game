package com.jgame.elements;

import com.jgame.util.GeometricElement;
import com.jgame.util.Vector2;
import java.util.List;

/**
 * Objeto que representa el elemento mas basico dentro del juego.
 * Created by jose on 29/03/16.
 */
public class GameObject {

    public OrganismBehavior behavior;
    public int id;
    private final GameObject parent;
    final Vector2 relativePosition;
    public final Vector2 base;

    public GameObject(OrganismBehavior behavior, int id){
        this.behavior = behavior;
        this.id = id;
        this.parent = null;
        this.relativePosition = new Vector2();
        this.base = new Vector2(1,0);
        behavior.setBase(base);
    }

    public GameObject(OrganismBehavior behavior, int id, GameObject parent, Vector2 relativePosition){
        this.behavior = behavior;
        this.id = id;
        this.parent = parent;
        this.relativePosition = relativePosition;
        this.base = parent.base;
        behavior.setBase(base);
    }

    public void update(List<GameElement> others, float timeDifference){
        if(parent != null) {
            behavior.bounds.setPosition(relativePosition);
            behavior.bounds.getPosition().changeBase(base).add(parent.getBounds().getPosition());
        }
        behavior.age(timeDifference);
        if(!behavior.active)
            return;
    }

    public int getId(){
        return id;
    }

    public GeometricElement getBounds(){
        return behavior.bounds;
    }

    /**
     * Funcion que determina si el GameObject puede ser dibujado en el renderer.
     * @return
     */
    public boolean isRenderable(){
        return false;
    }
}
