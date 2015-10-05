package com.jgame.elements;

import com.jgame.util.Circle;
import com.jgame.util.Vector2;

import java.util.List;

/**
 * Created by ej-jose on 5/10/15.
 */
public class Trap implements GameElement {

    private enum State {
        COUNTING, EXPLODING
    }

    private static float TIME_TO_EXPLODE = 3;
    private static float EXPLOSION_TIME = 0.15f;
    private float timeToExplode;
    private float remExplosionTime;
    private Circle locationInfo;
    private State currentState;

    public Trap(Vector2 position, float size){
        locationInfo = new Circle(position, size);
        timeToExplode = TIME_TO_EXPLODE;
        remExplosionTime = EXPLOSION_TIME;
        currentState = State.COUNTING;
    }

    @Override
    public boolean vivo() {
        return remExplosionTime > 0;
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public Vector2 getPosition() {
        return locationInfo.position;
    }

    @Override
    public float getPctAlive() {
        return remExplosionTime / EXPLOSION_TIME;
    }

    @Override
    public void interact(GameElement other) {

    }

    @Override
    public float getSize() {
        return locationInfo.radius;
    }

    @Override
    public void update(List<GameElement> otherElements, float timeDifference) {
        if(currentState == State.COUNTING) {
            timeToExplode -= timeDifference;
            if(timeToExplode <= 0)
                currentState = State.EXPLODING;
        } else {
            remExplosionTime -= timeDifference;
            locationInfo.radius *= 1.12f;
        }
    }
}
