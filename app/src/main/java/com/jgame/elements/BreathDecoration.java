package com.jgame.elements;

import com.jgame.util.Decoration.AnimatedDecoration;
import com.jgame.util.Square;
import com.jgame.util.TextureDrawer.TextureData;

/**
 * Created by jose on 12/09/17.
 */

public class BreathDecoration extends AnimatedDecoration {

    private final static TextureData[] sprites = {new TextureData(0.375f,0.25f,0.5f,0.375f),
            new TextureData(0.5f,0.25f,0.625f,0.375f)};

    public BreathDecoration(int frames, Square boundaries, boolean inverted){
        super(new AnimationData(frames, false, sprites), boundaries, inverted);
    }

    @Override
    public void update() {
        animation.updateFrame();
    }
}
