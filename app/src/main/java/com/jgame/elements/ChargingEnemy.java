package com.jgame.elements;

import com.jgame.util.Decoration;
import com.jgame.util.TextureDrawer.TextureData;
import com.jgame.game.GameData.Event;
import com.jgame.util.Vector2;

import java.util.ArrayDeque;

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
    public final static float[] ATTACK_SPEED = new float[]{2, 5f};
    public final static float DISTANCE_FROM_CHARACTER = 150;
    private final static int IDLE_FRAMES = 21;
    private final static int CHARGE_FRAMES = 16;
    private final CollisionObject[] activeBoxes;
    private final MainCharacter mainCharacter;
    private State currentState;
    private int idleFrame;
    private int chargeFrame;

    public ChargingEnemy(float sizeX, float sizeY, float idleSizeX, float idleSizeY, float yPosition, int id, final MainCharacter mainCharacter){
        super(sizeX, sizeY, idleSizeX, idleSizeY, new Vector2(0, yPosition), id);
        activeBoxes = new CollisionObject[]{new CollisionObject(new Vector2(),0, idleSizeX + 25, idleSizeY,this, CollisionObject.TYPE_HITTABLE),
                new CollisionObject(new Vector2(57,55),0,10,15,this, CollisionObject.TYPE_ATTACK)};
        currentState = State.IDLE;
        idleFrame = IDLE_FRAMES;
        chargeFrame = CHARGE_FRAMES;
        this.mainCharacter = mainCharacter;
        activeAttack = new AttackData(activeBoxes, activeBoxes, activeBoxes);
        activeAttack.currentState = AttackData.CollisionState.ACTIVE;
    }

    @Override
    public void reset() {
        idleFrame = IDLE_FRAMES;
        chargeFrame = CHARGE_FRAMES;
        currentState = State.IDLE;
        //setPosition(mainCharacter, DISTANCE_FROM_CHARACTER);
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
    public boolean completedTransition(){
        return false;
    }

    @Override
    public Event update(GameCharacter foe, ArrayDeque<Decoration> decorationData) {
        if(currentState == State.IDLE){
            idleFrame -= 1;
            if(idleFrame <= 0)
                currentState = State.CHARGING;
        } else if(currentState == State.CHARGING){
            chargeFrame -= 1;
            if(chargeFrame <= 0)
                currentState = State.ATTACKING;
        } else if (currentState == State.ATTACKING){
            if(foe.hittable())
                super.update(foe, decorationData);
            //moveX(baseX.x * ATTACK_SPEED[0]);
        }

        for(CollisionObject co : activeAttack.active)
            co.updatePosition();

        return Event.NONE;
    }

    //@Override
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
