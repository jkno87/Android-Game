package com.jgame.elements;

import com.jgame.definitions.GameIds;
import com.jgame.definitions.GameLevels;
import com.jgame.util.Circle;
import com.jgame.util.TimeCounter;
import com.jgame.util.Vector2;
import java.util.List;
import java.util.Random;

/**
 * Created by jose on 10/09/15.
 */
public class MovingOrganism implements GameElement {

    public enum State {
        NORMAL, GROWING ,EVOLVED
    };

    public static final float POINTS_SCALE = 0.1f;
    public static final float POINTS_SPEED = 0.05f;
    public static final int FOOD_TO_EVOLVE = 10;
    public static final int DEFAULT_MOVES = 10;
    public State currentState;
    private final TimeCounter timeToLive;
    private int movesLeft;
    private Vector2 direction;
    private Vector2 position;
    private Random random;
    private final Circle sight;
    public final Circle interaction;
    private float speedModifier;
    private int foodConsumed;
    private float size;

    public MovingOrganism (float timeToLive, Vector2 position, float sightDistance, float interactionDistance, float initialSize){
        this.timeToLive = new TimeCounter(timeToLive);
        this.position = position;
        this.direction = new Vector2();
        random = new Random();
        movesLeft = DEFAULT_MOVES;
        setDirection();
        this.sight = new Circle(position, sightDistance);
        this.interaction = new Circle(position, interactionDistance);
        speedModifier = 1.0f;
        currentState = State.NORMAL;
        size = initialSize;
    }

    private void setDirection(){
        direction.set(random.nextInt(3) - 1, random.nextInt(3) - 1).nor();
    }

    @Override
    public void update(List<GameElement> others, float timeDifference){

        if(currentState == State.GROWING){
            movesLeft--;
            size *= 1.15f;

            if(movesLeft <= 0) {
                currentState = State.EVOLVED;
                size *= 0.75f;
            }

            return;
        }

        if(movesLeft <= 0){
            movesLeft = DEFAULT_MOVES;
            setDirection();
            direction.mul(speedModifier);
        }

        for(GameElement e : others) {
            if(e instanceof MovingOrganism || e instanceof Trap)
                continue;

            //TODO: Aqui se hace un cast porque en este momento solo existe otro tipo de organismo en el juego
            Organism o = (Organism) e;

            if(interaction.containsCircle(o.interactionBox)){
                interact(e);
                break;
            }


            if (sight.containsCircle(o.interactionBox)) {
                direction.set(new Vector2(o.interactionBox.position).sub(position).nor()).mul(speedModifier);
                break;
            }
        }

        position.add(direction);

        if(position.x <= 0 || position.x >= GameLevels.FRUSTUM_WIDTH)
            direction.x *= -1;
        if(position.y <= 0 || position.y >= GameLevels.MAX_PLAYING_HEIGHT)
            direction.y *= -1;

        movesLeft--;
        timeToLive.accum(timeDifference);
    }


    @Override
    public float getSize(){
        return size;
    }

    /**
     * Modifica al organismo recibiendo un numero de foodPoints
     * @param foodPoints que se agregaran a la instancia de organismo
     */
    private void consumeFood(int foodPoints){
        if(foodPoints == 0)
            return;
        timeToLive.accum(- foodPoints * POINTS_SCALE);
        speedModifier *= foodPoints * POINTS_SPEED;
        foodConsumed += foodPoints;
    }

    @Override
    public void interact(GameElement other){
        if(other instanceof Organism){
            Organism o = (Organism) other;
            consumeFood(o.takeFood());

            if(currentState == State.NORMAL && foodConsumed > FOOD_TO_EVOLVE) {
                currentState = State.GROWING;
                movesLeft = DEFAULT_MOVES;
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

    @Override
    public boolean vivo() {
        return !timeToLive.completed();
    }

    @Override
    public Vector2 getPosition() {
        return position;
    }

    @Override
    public float getPctAlive() {
        return 1 - timeToLive.pctCharged();
    }
}
