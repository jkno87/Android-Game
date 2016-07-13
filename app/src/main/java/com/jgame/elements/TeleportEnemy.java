package com.jgame.elements;

import android.util.Log;

import com.jgame.game.GameFlow;
import com.jgame.util.TextureDrawer.TextureData;
import com.jgame.util.TimeCounter;
import com.jgame.util.Vector2;
import com.jgame.game.GameActivity.WorldData;

/**
 * Clase que representa un enemigo basico que se mantiene en su lugar y ataca
 * Created by jose on 24/05/16.
 */
public class TeleportEnemy extends GameCharacter {

    enum EnemyState {
        IDLE, DEAD, ATTACKING, TELEPORTING
    }

    public final static TextureData TELEPORT_TEXTURE = new TextureData(0,0.625f,0.125f,0.75f);
    private CollisionObject[] startupBoxes = new CollisionObject[]{idleCollisionBoxes[0]};
    private CollisionObject[] activeBoxes = new CollisionObject[]{idleCollisionBoxes[0],
            new CollisionObject(new Vector2(55,55),0,10,5,this, CollisionObject.TYPE_ATTACK)};
    private CollisionObject[] recoveryBoxes = new CollisionObject[]{idleCollisionBoxes[0]};
    private final AttackData attack;
    protected EnemyState currentState;
    private int currentAction;
    private final EnemyAction[] actions;
    private final MainCharacter mainCharacter;
    private int currentDifficulty;
    private EnemyParameters currentParameters;
    private final TimeCounter teleportInterval;
    private final TimeCounter idleInterval;

    public TeleportEnemy(float sizeX, float sizeY, float idleSizeX, float idleSizeY, float yPosition, int id, final MainCharacter mainCharacter) {
        super(sizeX, sizeY, idleSizeX, idleSizeY, new Vector2(0, yPosition), id);
        currentState = EnemyState.TELEPORTING;
        this.mainCharacter = mainCharacter;
        EnemyAction move  = new EnemyAction() {
            @Override
            public void act() {
                setPosition(mainCharacter, currentParameters);
            }
        };
        EnemyAction attack = new EnemyAction(){
            @Override
            public void act(){
                currentState = EnemyState.IDLE;
            }
        };
        actions = new EnemyAction[]{attack, move};
        currentAction = 0;
        currentDifficulty = 0;
        currentParameters = new EnemyParameters();
        currentParameters.distanceFromCharacter = 35;
        this.attack = new AttackData(startupBoxes, activeBoxes, recoveryBoxes);
        this.attack.startupCounter = new TimeCounter(0.33f);
        this.attack.activeCounter = new TimeCounter(0.1f);
        this.attack.recoveryCounter = new TimeCounter(0.45f);
        activeAttack = this.attack;
        teleportInterval = new TimeCounter(0.5f);
        idleInterval = new TimeCounter(1.5f);
    }

    private void toggleCurrentAction(){
        currentAction = currentAction + 1 < actions.length ? currentAction + 1 : 0;
    }

    @Override
    public void reset(float x, float y){
        teleportInterval.reset();
        currentState = EnemyState.TELEPORTING;
        currentAction = 0;
        idleInterval.reset();
        setPosition(mainCharacter, currentParameters);
    }

    @Override
    public void update(GameCharacter foe, GameFlow.UpdateInterval interval, WorldData worldData) {

        adjustToFoePosition(foe);

        if(currentState == EnemyState.DEAD)
            return;

        if(currentState == EnemyState.TELEPORTING){
            teleportInterval.accum(interval);
            if(!teleportInterval.completed())
                return;
            teleportInterval.reset();
            actions[currentAction].act();
            toggleCurrentAction();

            return;
        }

        if(currentState == EnemyState.IDLE) {
            idleInterval.accum(interval);
            if (idleInterval.completed()) {
                currentState = EnemyState.ATTACKING;
                //activeAttack = attacks[0];
                activeAttack.reset();
            }
            return;
        }

        if(currentState == EnemyState.ATTACKING) {
            activeAttack.update(interval);
            if(activeAttack.completed()){
                currentState = EnemyState.TELEPORTING;
                idleInterval.reset();
            }

            if(foe.hittable()) {
                for (CollisionObject co : getActiveCollisionBoxes())
                    if (co.checkCollision(foe))
                        foe.hit();
            }

        }
    }

    @Override
    public boolean hittable(){
        return currentState != EnemyState.TELEPORTING;
    }

    @Override
    public void hit(){
        currentState = EnemyState.DEAD;
    }

    @Override
    public TextureData getCurrentTexture() {
        if(currentState == EnemyState.TELEPORTING)
            return TELEPORT_TEXTURE;

        if(currentState != EnemyState.ATTACKING)
            return MainCharacter.IDLE_TEXTURE;

        if(activeAttack.currentState == AttackData.CollisionState.ACTIVE)
            return MainCharacter.ACTIVE_MOV_A;
        else
            return MainCharacter.INIT_MOV_A;
    }

    @Override
    public boolean attacking(){
        return currentState == EnemyState.ATTACKING;
    }

    @Override
    public boolean alive(){
        return currentState != EnemyState.DEAD;
    }


}
