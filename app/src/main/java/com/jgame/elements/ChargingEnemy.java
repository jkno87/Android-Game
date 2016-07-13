package com.jgame.elements;

import com.jgame.game.GameActivity;
import com.jgame.game.GameFlow;
import com.jgame.util.TextureDrawer.TextureData;
import com.jgame.util.TimeCounter;
import com.jgame.util.Vector2;

/**
 * Created by jose on 13/07/16.
 */
public class ChargingEnemy extends GameCharacter {

    enum State {
        ATTACKING, CHARGING
    }

    public final static TextureData CHARGING_TEXTURE = new TextureData(0,0.625f,0.125f,0.75f);
    private final float CHARGE_TIME = 3;
    private final MainCharacter mainCharacter;
    private final EnemyAction[] actions;
    private State currentState;
    private TimeCounter chargeTimer;
    private EnemyParameters currentParameters;

    public ChargingEnemy(float sizeX, float sizeY, float idleSizeX, float idleSizeY, float yPosition, int id, final MainCharacter mainCharacter){
        super(sizeX, sizeY, idleSizeX, idleSizeY, new Vector2(0, yPosition), id);
        actions = new EnemyAction[]{};
        currentState = State.CHARGING;
        chargeTimer = new TimeCounter(CHARGE_TIME);
        this.mainCharacter = mainCharacter;
        this.currentParameters = new EnemyParameters();
        currentParameters.distanceFromCharacter = 50;
    }

    @Override
    public void reset(float x, float y) {
        chargeTimer.reset();
        currentState = State.CHARGING;
        setPosition(mainCharacter, currentParameters);
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
    public void update(GameCharacter foe, GameFlow.UpdateInterval interval, GameActivity.WorldData worldData) {

    }

    @Override
    public TextureData getCurrentTexture() {
        return null;
    }

    @Override
    public void hit() {

    }
}
