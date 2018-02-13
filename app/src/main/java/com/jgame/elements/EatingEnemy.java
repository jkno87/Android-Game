package com.jgame.elements;

import com.jgame.util.CollisionObject;
import com.jgame.util.Decoration;
import com.jgame.util.Square;
import com.jgame.util.TextureDrawer;
import com.jgame.util.Vector2;

import java.util.ArrayDeque;

/**
 * Created by Jose on 13/02/2018.
 */

public class EatingEnemy extends GameCharacter {

    private final byte ID_TOOL = 0;
    private final byte ID_ENEMY = 1;
    private final byte ID_PROJECTILE = 2;

    public EatingEnemy(Square spriteContainer) {
        super(spriteContainer);
    }

    @Override
    public void update(GameCharacter foe, ArrayDeque<Decoration> decorationData) {

    }

    @Override
    public boolean completedTransition() {
        return false;
    }

    @Override
    public void reset(Vector2 positionOffset) {

    }

    @Override
    public boolean hittable() {
        return false;
    }

    @Override
    public boolean alive() {
        return false;
    }

    @Override
    public TextureDrawer.TextureData getCurrentTexture() {
        return null;
    }

    @Override
    public void hit(CollisionObject target) {

    }
}
