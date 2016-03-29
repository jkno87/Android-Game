package com.jgame.elements;

import android.util.Log;

import com.jgame.util.GeometricElement;
import com.jgame.util.Vector2;

import java.util.List;

/**
 * Created by jose on 29/03/16.
 */
public class GameObject implements GameElement {

    public OrganismBehavior behavior;
    public int id;
    private final GameObject parent;
    final Vector2 relativePosition;
    public final Vector2 base;

    public GameObject(int id){
        this.id = id;
        parent = null;
        this.relativePosition = new Vector2();
        this.base = new Vector2(1,0);
    }

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

    public void setBehavior(OrganismBehavior behavior){
        this.behavior = behavior;
    }

    @Override
    public void update(List<GameElement> others, float timeDifference){
        if(parent != null) {
            behavior.bounds.setPosition(relativePosition);
            behavior.bounds.getPosition().changeBase(base).add(parent.getBounds().getPosition());
        }
        behavior.age(timeDifference);
        if(!behavior.active)
            return;

        //for(GameElement e: others)
        //    behavior.evaluateCollision(e);
    }

    @Override
    public int getId(){
        return id;
    }

    @Override
    public GeometricElement getBounds(){
        return behavior.bounds;
    }

    @Override
    public boolean alive(){
        return behavior.isAlive();
    }
}
