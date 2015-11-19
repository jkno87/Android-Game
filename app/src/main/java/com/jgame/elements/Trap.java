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

    private static float TIME_TO_EXPLODE = 3;
    private static float EXPLOSION_TIME = 0.15f;
    private static final float EXPLOSION_GROWTH_RATE = 1.15f;
    private float remExplosionTime;
    private OrganismBehavior timerBehavior;
    public List<GameElement> capturedElements;

    public Trap(Vector2 position, float size){
        timerBehavior = new OrganismBehavior(TIME_TO_EXPLODE, new Circle(position, size), 0, 0, false) {
            @Override
            public void age(float timeDifference) {
                timeRemaining.accum(timeDifference);
                if(timeRemaining.completed())
                    setBehavior(null);
            }

            @Override
            public void evaluateCollision(GameElement e) {
                //Cuando se encuentra con este comportamiento no interactua con el mundo
            }

            @Override
            /**
             * Se cambia la funcion is Alive porque tiene un comportamiento diferente a un organismo normal
             */
            public boolean isAlive(){
                return true;
            }

        };

        remExplosionTime = EXPLOSION_TIME;
        capturedElements = new ArrayList<>();
    }

    @Override
    public int getId() {
        return GameIds.TRAP_ID;
    }

    /*@Override
    public void interact(GameElement other) {
        if(other instanceof MovingOrganism) {
            MovingOrganism mOther = (MovingOrganism) other;
            if(locationInfo.containsCircle(mOther.interaction))
                capturedElements.add(mOther);
        }
    }

    /*@Override
    public void update(List<GameElement> otherElements, float timeDifference) {
            remExplosionTime -= timeDifference;
            locationInfo.radius *= EXPLOSION_GROWTH_RATE;
            for(GameElement e : otherElements)
                interact(e);

    }*/

}
