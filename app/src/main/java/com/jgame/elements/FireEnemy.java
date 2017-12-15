package com.jgame.elements;

import com.jgame.util.Decoration;
import com.jgame.util.Square;
import com.jgame.util.TextureDrawer;
import com.jgame.util.Vector2;
import com.jgame.util.TextureDrawer.TextureData;
import java.util.ArrayDeque;

/**
 * Created by Jose on 15/12/2017.
 */

public class FireEnemy extends GameCharacter {

    public static class FireDecoration extends Decoration {

        private boolean finished;
        private final Vector2 origin;
        private final Vector2 parentTransformation;

        public FireDecoration (Square size, Vector2 parentTransformation, Vector2 origin){
            this.size = size;
            this.parentTransformation = parentTransformation;
            this.origin = origin;
        }

        @Override
        public void terminate() {
            finished = false;
        }

        @Override
        public boolean drawable() {
            return !finished;
        }

        @Override
        public void update(Vector2 backgroundMoveDelta) {

        }

        @Override
        public boolean completed() {
            return finished;
        }

        @Override
        public TextureData getSprite() {
            return FIRE_SPRITE;
        }
    }


    private final static float IDLE_SIZE_X = 37;
    private final static float IDLE_SIZE_Y = 160;
    private final static float SPRITE_SIZE_X = 37;
    private final static float SPRITE_SIZE_Y = 160;
    private final static Vector2 INITIAL_POSITION = new Vector2(350, 0);
    private final static TextureData FIRE_SPRITE = TextureDrawer.generarTextureData(12,0,14,2,32);
    private final static TextureData IDLE_SPRITE = new TextureData(0.4375f, 0, 0.46875f, 0.09375f);

    public FireEnemy(float positionY, int id){
        super(SPRITE_SIZE_X, SPRITE_SIZE_Y, IDLE_SIZE_X, IDLE_SIZE_Y, new Vector2(0, positionY), id);
        baseX.x = -1;
    }

    @Override
    public void reset(Vector2 positionOffset) {
        activeCollisionBoxes = idleCollisionBoxes;
        moveTo(positionOffset, INITIAL_POSITION);
    }


    @Override
    public void update(GameCharacter foe, ArrayDeque<Decoration> decorationData) {

    }

    @Override
    public boolean completedTransition() {
        return false;
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
    public boolean attacking() {
        return false;
    }

    @Override
    public TextureData getCurrentTexture() {
        return IDLE_SPRITE;
    }

    @Override
    public void hit() {

    }
}
