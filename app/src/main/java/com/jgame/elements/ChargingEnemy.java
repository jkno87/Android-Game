package com.jgame.elements;

import com.jgame.util.Decoration;
import com.jgame.util.Square;
import com.jgame.util.TextureDrawer;
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

    private final static Vector2 INITIAL_POSITION = new Vector2(425,0);
    public final static TextureData IDLE_TEXTURE = TextureDrawer.generarTextureData(20,0,22,2,32);
    private final static Vector2 ATTACK_SPEED = new Vector2(-5f, 0);
    public final static float DISTANCE_FROM_CHARACTER = 150;
    private final static int IDLE_FRAMES = 120;
    private final static int CHARGE_FRAMES = 20;
    private State currentState;
    private int idleFrame;
    private int chargeFrame;

    public ChargingEnemy(float yPosition, int id){
        super(new Square(new Vector2(0, yPosition), 85,85,0), id);
        this.baseX.x = -1;
        currentState = State.IDLE;
        idleFrame = IDLE_FRAMES;
        chargeFrame = CHARGE_FRAMES;
        CollisionObject[] a = new CollisionObject[]{};
        activeAttack = new AttackData(a, a, a);
    }

    @Override
    public void reset(Vector2 positionOffset) {
        idleFrame = IDLE_FRAMES;
        chargeFrame = CHARGE_FRAMES;
        currentState = State.IDLE;
        moveTo(positionOffset, INITIAL_POSITION);
        color.b = 0;
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
        return position.x < INITIAL_POSITION.x;
    }

    @Override
    public Event update(GameCharacter foe, ArrayDeque<Decoration> decorationData) {
        if(!completedTransition())
            return Event.NONE;

        if(currentState == State.IDLE){
            idleFrame -= 1;
            if(idleFrame <= 0) {
                currentState = State.CHARGING;
                color.b = 1;
            }
        } else if(currentState == State.CHARGING){
            chargeFrame -= 1;
            if(chargeFrame == 0) {
                currentState = State.ATTACKING;
                color.g = 0;
                color.b = 0;
                color.r = 1;
            }
        } else if (currentState == State.ATTACKING){
            move(ATTACK_SPEED);
        }

        return Event.NONE;
    }

    @Override
    public TextureData getCurrentTexture() {

        return IDLE_TEXTURE;
    }

    @Override
    public void hit() {
        currentState = State.DEAD;
    }
}
