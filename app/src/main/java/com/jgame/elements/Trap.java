package com.jgame.elements;

import android.util.Log;

import com.jgame.definitions.GameIds;
import com.jgame.util.Circle;
import com.jgame.util.GeometricElement;
import com.jgame.util.Vector2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ej-jose on 5/10/15.
 */
public class Trap extends Organism {

    private static final int HP = 1;
    private static final int FOOD_POINTS = 0;
    private static float TIME_TO_EXPLODE = 3;
    private static float EXPLOSION_TIME = 0.15f;
    private static final float EXPLOSION_GROWTH_RATE = 1.15f;
    private OrganismBehavior timerBehavior;
    private OrganismBehavior explodingBehavior;
    public List<GameElement> capturedElements;

    public Trap(Vector2 position, float size){
        final Circle organismBounds = new Circle(position,size);
        timerBehavior = new OrganismBehavior(TIME_TO_EXPLODE, organismBounds, HP,FOOD_POINTS, false) {
            @Override
            public void age(float timeDifference) {
                timeRemaining.accum(timeDifference);
                if(timeRemaining.completed())
                    setBehavior(explodingBehavior);
            }

            @Override
            public void evaluateCollision(GameElement e) {
                //Cuando se encuentra con este comportamiento no interactua con el mundo
            }
        };

        explodingBehavior = new OrganismBehavior(EXPLOSION_TIME, organismBounds,HP,FOOD_POINTS, true){

            @Override
            public void age(float timeDifference) {
                timeRemaining.accum(timeDifference);
                organismBounds.radius *= EXPLOSION_GROWTH_RATE;
            }

            @Override
            public void evaluateCollision(GameElement e) {
                if(!(e instanceof Organism))
                    return;

                Organism o = (Organism) e;
                if(bounds.collides(o.getBounds()))
                    capturedElements.add(o);

            }
        };

        setBehavior(timerBehavior);
        capturedElements = new ArrayList<>(5);
    }

    @Override
    public int getId() {
        return GameIds.TRAP_ID;
    }

}
