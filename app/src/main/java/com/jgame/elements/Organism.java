package com.jgame.elements;

import com.jgame.definitions.GameIds;
import com.jgame.util.Circle;
import com.jgame.util.GeometricElement;
import com.jgame.util.TimeCounter;
import com.jgame.util.Vector2;

import java.util.List;

/**
 * Created by jose on 3/09/15.
 */
public class Organism implements GameElement {

    public OrganismBehavior behavior;

    public Organism(){

    }

    public Organism (OrganismBehavior behavior){
        this.behavior = behavior;
    }

    public void setBehavior(OrganismBehavior behavior){
        this.behavior = behavior;
    }

    @Override
    public void update(List<GameElement> others, float timeDifference){
        behavior.age(timeDifference);
        if(!behavior.active)
            return;

        for(GameElement e: others)
            behavior.evaluateCollision(e);
    }

    @Override
    public int getId(){
        return GameIds.FOOD_ORGANISM_ID;
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
