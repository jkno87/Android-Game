package com.jgame.elements;

import com.jgame.util.Circle;
import com.jgame.util.Vector2;

/**
 * Created by jose on 12/11/15.
 */
public class FoodOrganism extends Organism {

    public FoodOrganism (float timeToLive, Vector2 position, float size, int hp, int foodPoints){
        super(new OrganismBehavior(timeToLive, new Circle(position, size), hp, foodPoints, false) {
            @Override
            public void age(float timeDifference) {
                timeRemaining.accum(timeDifference);
            }

            @Override
            public void evaluateCollision(GameElement e) {
                //Implementar que cuando colisione con un elemento que lo puede comer, tome la comida
            }
        });
    }
}
