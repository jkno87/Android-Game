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
    private final static float IDLE_SIZE = 50;
    private final static TextureDrawer.TextureData IDLE_SPRITE = new TextureDrawer.TextureData(0.4375f, 0, 0.46875f, 0.09375f);
    private final static Vector2 INITIAL_POSITION = new Vector2(425, 0);

    public EatingEnemy() {
        super(new Square(new Vector2(), IDLE_SIZE, IDLE_SIZE));
        idleSizeX = IDLE_SIZE;
        baseX.x = -1;
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
        moveTo(positionOffset, INITIAL_POSITION);
    }

    @Override
    public boolean hittable() {
        return false;
    }

    @Override
    public boolean alive() {
        return true;
    }

    @Override
    public TextureDrawer.TextureData getCurrentTexture() {
        return IDLE_SPRITE;
    }

    @Override
    public void hit(CollisionObject target) {

    }
}
