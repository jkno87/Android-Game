package com.jgame.elements;

import com.jgame.game.GameActivity;
import com.jgame.game.GameFlow;
import com.jgame.util.TextureDrawer.TextureData;
import com.jgame.util.TimeCounter;
import com.jgame.util.Vector2;

/**
 * Enemigo que tiene el objetivo de lanzarse hacia el personaje principal despues de realizar un periodo de carga.
 * Created by jose on 13/07/16.
 */
public class ChargingEnemy extends GameCharacter {

    enum State {
        ATTACKING, CHARGING, DEAD
    }

    public final static TextureData CHARGING_TEXTURE = new TextureData(0,0.625f,0.125f,0.75f);
    public final static float ATTACK_SPEED = 2;
    public final static float DISTANCE_FROM_CHARACTER = 150;
    private CollisionObject[] activeBoxes = new CollisionObject[]{idleCollisionBoxes[0],
            new CollisionObject(new Vector2(30,55),0,10,5,this, CollisionObject.TYPE_ATTACK)};
    private final float CHARGE_TIME = 1.25f;
    private final MainCharacter mainCharacter;
    private State currentState;
    private TimeCounter chargeTimer;

    public ChargingEnemy(float sizeX, float sizeY, float idleSizeX, float idleSizeY, float yPosition, int id, final MainCharacter mainCharacter){
        super(sizeX, sizeY, idleSizeX, idleSizeY, new Vector2(0, yPosition), id);
        currentState = State.CHARGING;
        chargeTimer = new TimeCounter(CHARGE_TIME);
        this.mainCharacter = mainCharacter;
        activeAttack = new AttackData(activeBoxes, activeBoxes, activeBoxes);
        activeAttack.currentState = AttackData.CollisionState.ACTIVE;
    }

    @Override
    public void reset(float x, float y) {
        chargeTimer.reset();
        currentState = State.CHARGING;
        setPosition(mainCharacter, DISTANCE_FROM_CHARACTER);
    }

    @Override
    public boolean hittable() {
        return currentState == State.ATTACKING;
    }

    @Override
    public boolean alive() {
        return currentState != State.DEAD;
    }

    @Override
    public boolean attacking() {
        return currentState == State.ATTACKING;
    }

    @Override
    public void update(GameCharacter foe, GameFlow.UpdateInterval interval, GameActivity.WorldData worldData) {
        if(currentState == State.CHARGING){
            chargeTimer.accum(interval);
            if(chargeTimer.completed())
                currentState = State.ATTACKING;
        } else if (currentState == State.ATTACKING){
            if(foe.hittable())
                super.update(foe, interval, worldData);
            moveX(baseX.x * ATTACK_SPEED);
        }

        for(CollisionObject co : activeAttack.active)
            co.updatePosition();
    }

    @Override
    public TextureData getCurrentTexture() {
        return CHARGING_TEXTURE;
    }

    @Override
    public void hit() {
        currentState = State.DEAD;
    }
}
