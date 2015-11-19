package com.jgame.elements;

import com.jgame.definitions.GameIds;
import com.jgame.definitions.GameLevels;
import com.jgame.util.Circle;
import com.jgame.util.GeometricElement;
import com.jgame.util.TimeCounter;
import com.jgame.util.Vector2;
import java.util.List;
import java.util.Random;

/**
 * Created by jose on 10/09/15.
 */
public class MovingOrganism extends Organism {

    public enum State {
        NORMAL, GROWING ,EVOLVED
    };

    private static final int HP = 1;
    private static final int FOOD_POINTS = 0;
    public static final float POINTS_SCALE = 0.1f;
    public static final float POINTS_SPEED = 0.05f;
    public static final int FOOD_TO_EVOLVE = 10;
    public static final int DEFAULT_MOVES = 10;
    public State currentState;
    private float speedModifier;
    private int foodConsumed;
    private OrganismBehavior initialBehavior;

    public MovingOrganism(float timeToLive, final Vector2 position, float sightDistance, final float interactionDistance){

        initialBehavior = new OrganismBehavior(timeToLive, new Circle(position, sightDistance), HP, FOOD_POINTS, true) {
            private int movesLeft = DEFAULT_MOVES;
            private Vector2 direction = new Vector2();
            private Random random = new Random();
            private Circle organismBounds = new Circle(position, interactionDistance);
            @Override
            public void age(float timeDifference) {
                if(movesLeft <= 0) {
                    movesLeft = DEFAULT_MOVES;
                    direction.set(random.nextInt(3) - 1, random.nextInt(3) - 1).nor();
                    direction.mul(speedModifier);
                }

                movesLeft--;

                position.add(direction);

                if(position.x <= 0 || position.x >= GameLevels.FRUSTUM_WIDTH)
                    direction.x *= -1;
                if(position.y <= 0 || position.y >= GameLevels.MAX_PLAYING_HEIGHT)
                    direction.y *= -1;

                timeRemaining.accum(timeDifference);
            }

            @Override
            public void collide(GameElement e) {
                if(e instanceof Organism){
                    Organism o = (Organism) e;
                    //Aqui falta hacer algo para que represente que consume comida

                    if(foodConsumed > FOOD_TO_EVOLVE) {
                        currentState = State.GROWING;
                        movesLeft = DEFAULT_MOVES;
                    }
                }
            }
        };

        setBehavior(initialBehavior);

    }

    @Override
    public void update(List<GameElement> others, float timeDifference){

        behavior.age(timeDifference);

        for(GameElement e : others) {
            if(e instanceof MovingOrganism || e instanceof Trap)
                continue;

            //TODO: Aqui se hace un cast porque en este momento solo existe otro tipo de organismo en el juego
            Organism o = (Organism) e;

            if(behavior.bounds.collides(o.getBounds()))
                behavior.collide(o);


            if (sight.collides(o.getBounds())) {
                direction.set(new Vector2(o.interactionBox.position).sub(position).nor()).mul(speedModifier);
                break;
            }
        }
    }

    @Override
    public int getId(){
        if(currentState == State.EVOLVED)
            return GameIds.EVOLVED_ORGANISM_ID;
        else
            return GameIds.MOVING_ORGANISM_ID;
    }
}
