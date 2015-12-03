package com.jgame.elements;

import com.jgame.util.Circle;
import com.jgame.util.GeometricElement;
import com.jgame.util.Vector2;

/**
 * Created by jose on 24/11/15.
 */
public class InputOrganism extends Organism {

    public static final int HP = 1;
    public static final int FOOD_POINTS = 0;
    private final Vector2 direction;
    public final Vector2 position;

    public InputOrganism(float timeToLive, final Vector2 position, float interactionDistance){
        direction = new Vector2();
        this.position = position;

        OrganismBehavior behavior = new OrganismBehavior(timeToLive, new Circle(position, interactionDistance), HP, FOOD_POINTS, true) {
            @Override
            public void age(float timeDifference) {
                timeRemaining.accum(timeDifference);
                position.add(direction);
            }

            @Override
            public void evaluateCollision(GameElement e) {
                //Implementar la interaccion con otros elementos del juego
            }
        };
    }

    /**
     * Actualiza la direccion en la que se movera el organismo
     * @param nDistance Vector con la distancia a la que se movera el organismo
     */
    public void changeDistance(Vector2 nDistance){
        direction.set(nDistance);
    }

    /**
     * Detiene al organismo en el lugar donde se encuentre
     */
    public void stop(){
        direction.set(0,0);
    }

}
