package com.jgame.elements;

import com.jgame.util.GeometricElement;
import com.jgame.util.Vector2;

import java.util.List;

public interface GameElement {
    public static final float[] DEFAULT_COLOR = new float[]{1,1,1,1};
    public static final float[] HIT_COLOR = new float[]{1,0,0,1};

    public int getId();
    public GeometricElement getBounds();
    public void update(List<GameElement> others, float timeDifference);
    public boolean alive();

}