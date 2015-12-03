package com.jgame.elements;

import com.jgame.util.GeometricElement;
import com.jgame.util.Vector2;

import java.util.List;

public interface GameElement {
    float[] DEFAULT_COLOR = new float[]{1,1,1,1};

    int getId();
    GeometricElement getBounds();
    void update(List<GameElement> others, float timeDifference);
    boolean alive();

}