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
        IDLE, ATTACKING, CHARGING, DEAD
    }

    public final static TextureData IDLE_TEXTURE = new TextureData(0.25f,0,0.5f,0.25f);
    public final static TextureData CHARGING_TEXTURE = new TextureData(0.25f,0.25f,0.5f,0.5f);
    public final static TextureData ATTACKING_TEXTURE = new TextureData(0.25f,0.5f,0.5f,0.75f);
    public final static float ATTACK_SPEED = 2;
    public final static float DISTANCE_FROM_CHARACTER = 150;
    private final CollisionObject[] activeBoxes;
    private final float CHARGE_TIME = 0.25f;
    private static final float IDLE_TIME = 0.35f;
    private final MainCharacter mainCharacter;
    private State currentState;
    private TimeCounter chargeTimer;
    private TimeCounter idleTimer;

    public ChargingEnemy(float sizeX, float sizeY, float idleSizeX, float idleSizeY, float yPosition, int id, final MainCharacter mainCharacter){
        super(sizeX, sizeY, idleSizeX, idleSizeY, new Vector2(0, yPosition), id);
        activeBoxes = new CollisionObject[]{new CollisionObject(new Vector2(),0, idleSizeX + 25, idleSizeY,this, CollisionObject.TYPE_HITTABLE),
                new CollisionObject(new Vector2(57,55),0,10,15,this, CollisionObject.TYPE_ATTACK)};
        currentState = State.IDLE;
        chargeTimer = new TimeCounter(CHARGE_TIME);
        idleTimer = new TimeCounter(IDLE_TIME);
        this.mainCharacter = mainCharacter;
        activeAttack = new AttackData(activeBoxes, activeBoxes, activeBoxes);
        activeAttack.currentState = AttackData.CollisionState.ACTIVE;
    }

    @Override
    public void reset(float x, float y) {
        chargeTimer.reset();
        idleTimer.reset();
        currentState = State.IDLE;
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
        if(currentState == State.IDLE){
            idleTimer.accum(interval);
            if(idleTimer.completed())
                currentState = State.CHARGING;
        } else if(currentState == State.CHARGING){
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
        if(currentState == State.CHARGING)
            return CHARGING_TEXTURE;

        if(currentState == State.ATTACKING)
            return ATTACKING_TEXTURE;

        return IDLE_TEXTURE;
    }

    @Override
    public void hit() {
        currentState = State.DEAD;
    }
}
