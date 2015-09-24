package com.jgame.elements;

import com.jgame.game.GameLogic;
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
        NORMAL, EVOLVED
    };

    public static final int DEFAULT_MOVES = 10;
    public State currentState;
    private final TimeCounter timeToLive;
    private int movesLeft;
    private Vector2 direction;
    private Vector2 position;
    private Random random;
    private final Circle sight;
    private final Circle interaction;
    private float modifier;
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
        modifier = 1.0f;
        currentState = State.NORMAL;
        size = initialSize;
    }

    private void setDirection(){
        direction.set(random.nextInt(3) - 1, random.nextInt(3) - 1);
    }

    @Override
    public void update(List<GameElement> others, float timeDifference){
        if(movesLeft <= 0){
            movesLeft = DEFAULT_MOVES;
            setDirection();
            direction.mul(modifier);
        }

        for(GameElement e : others) {
            Vector2 otherPosition = e.getPosition();

            if(e instanceof MovingOrganism)
                continue;

            if(interaction.contains(otherPosition.x, otherPosition.y)){
                interact(e);
                break;
            }


            if (sight.contains(otherPosition.x, otherPosition.y)) {
                direction.set(new Vector2(otherPosition).sub(position).nor());
                break;
            }
        }

        position.add(direction);
        movesLeft--;
        timeToLive.accum(timeDifference);
    }


    @Override
    public float getSize(){
        return size;
    }

    @Override
    public void updateDeprecated(GameLogic gameInstance, float timeDifference) {

    }

    @Override
    public void interact(GameElement other){
        if(other instanceof Organism){
            Organism o = (Organism) other;
            timeToLive.accum(-0.03f);
            foodConsumed++;
            o.decreaseLife(0.03f);
            modifier = (float)Math.log(foodConsumed * -1.0);
            if(foodConsumed > 10) {
                currentState = State.EVOLVED;
                size *= 1.3f;
            }
        }
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
