package com.jgame.elements;

import com.jgame.game.GameActivity;
import com.jgame.util.Decoration;
import com.jgame.util.FrameCounter;
import com.jgame.util.Square;
import com.jgame.util.TextureDrawer;
import com.jgame.util.Vector2;

import java.util.ArrayDeque;

/**
 * Created by jose on 19/01/18.
 */

public class FireSpaceEnemy extends GameCharacter {

    private final static float IDLE_SIZE_X = 37;
    private final static float IDLE_SIZE_Y = 160;
    private final static float SPRITE_SIZE_X = 37;
    private final static float SPRITE_SIZE_Y = 160;
    private final static Vector2 INITIAL_POSITION = new Vector2(350, 0);
    private final static TextureDrawer.TextureData IDLE_SPRITE = new TextureDrawer.TextureData(0.4375f, 0, 0.46875f, 0.09375f);
    private final TextureDrawer.ColorData ATTACK_A_COLOR = new TextureDrawer.ColorData(0,0,1,1);
    private final TextureDrawer.ColorData ATTACK_B_COLOR = new TextureDrawer.ColorData(1,0,0,1);
    private int lastAttack = 0;
    private Vector2 fireballPosition;
    private Vector2 fireballOrigin;
    private Vector2 groundFirePosition;
    private FrameCounter idleFrames;
    private FrameCounter preAttackFrames;
    private State currentState;

    private enum State {
        IDLE, STARTING_ATTACK_A, STARTING_ATTACK_B, ATTACK_A, ATTACK_B
    }

    public FireSpaceEnemy(int id) {
        super(SPRITE_SIZE_X, SPRITE_SIZE_Y, IDLE_SIZE_X, IDLE_SIZE_Y, new Vector2(0, GameActivity.ELEMENTS_HEIGHT), id);
        baseX.x = -1;
        fireballPosition = new Vector2();
        fireballOrigin = new Vector2();
        groundFirePosition = new Vector2();
        idleFrames = new FrameCounter(15);
        preAttackFrames = new FrameCounter(10);
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
        currentState = State.IDLE;
        moveTo(positionOffset, INITIAL_POSITION);
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
    public boolean attacking() {
        return false;
    }

    @Override
    public TextureDrawer.TextureData getCurrentTexture() {
        return IDLE_SPRITE;
    }

    @Override
    public void hit() {

    }
}
