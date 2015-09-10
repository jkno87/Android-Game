package com.jgame.elements;

import com.jgame.game.GameLogic;
import com.jgame.util.TimeCounter;
import com.jgame.util.Vector2;

import java.util.Random;

/**
 * Created by jose on 10/09/15.
 */
public class MovingOrganism implements GameElement {

    public static final int DEFAULT_MOVES = 10;
    private final TimeCounter timeToLive;
    private int movesLeft;
    private Vector2 direction;
    private Vector2 position;
    private Random random;

    public MovingOrganism (float timeToLive, Vector2 position){
        this.timeToLive = new TimeCounter(timeToLive);
        this.position = position;
        this.direction = new Vector2();
        random = new Random();
        movesLeft = DEFAULT_MOVES;
        setDirection();
    }

    private void setDirection(){
        direction.set(random.nextInt(3) - 1, random.nextInt(3) - 1);
    }

    @Override
    public void update(GameLogic gameInstance, float timeDifference) {
        if(movesLeft <= 0){
            movesLeft = DEFAULT_MOVES;
            setDirection();
        }
        position.add(direction);
        movesLeft--;
        timeToLive.accum(timeDifference);
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
