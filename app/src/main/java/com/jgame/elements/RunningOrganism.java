package com.jgame.elements;

import com.jgame.definitions.GameLevels;
import com.jgame.util.Circle;
import com.jgame.util.Vector2;

import java.util.Random;

/**
 * Created by jose on 24/11/15.
 */
public class RunningOrganism extends Organism {

    public static final int HP = 1;
    public static final int FOOD_POINTS = 2;
    public static final int MOVES = 5;

    public RunningOrganism(float timeToLive, final Vector2 position, final float sightDistance, float interactionDistance){
        OrganismBehavior behavior = new OrganismBehavior(timeToLive, new Circle(position, interactionDistance), HP, FOOD_POINTS, true) {

            private int movesLeft = MOVES;
            private Vector2 direction = new Vector2();
            private Random random = new Random();
            private Circle sightBounds = new Circle(position, sightDistance);

            @Override
            public void age(float timeDifference) {
                if(movesLeft <= 0) {
                    movesLeft = MOVES;
                    direction.set(random.nextInt(3) - 1, random.nextInt(3) - 1).nor();
                } else
                    movesLeft--;

                position.add(direction);

                /*if(position.x <= 0 || position.x >= GameLevels.FRUSTUM_WIDTH)
                    direction.x *= -1;
                if(position.y <= 0 || position.y >= GameLevels.MAX_PLAYING_HEIGHT)
                    direction.y *= -1;*/

                timeRemaining.accum(timeDifference);
            }

            @Override
            public void evaluateCollision(GameElement e) {
                if(e instanceof Organism){
                    Organism o = (Organism) e;
                    if(bounds.collides(o.getBounds())){
                        //Aqui falta hacer algo para que represente que consume comida
                    }

                    if(sightBounds.collides(o.getBounds()))
                        direction.set(new Vector2(position).sub(o.getBounds().getPosition()).nor());

                }
            }
        };

        setBehavior(behavior);

    }
}
