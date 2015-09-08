package com.jgame.elements;

import com.jgame.game.GameLogic;
import com.jgame.util.Vector2;

public interface GameElement {
    public static final float[] DEFAULT_COLOR = new float[]{1,1,1,1};
    public static final float[] HIT_COLOR = new float[]{1,0,0,1};


    public void update(GameLogic gameInstance, float timeDifference);
    public boolean vivo();
    public Vector2 getPosition();
    public float getPctAlive();
}