package com.jgame.elements;

import com.jgame.game.FightingGameFlow;
import com.jgame.game.GameFlow;
import com.jgame.util.TextureDrawer.TextureData;
import com.jgame.util.TimeCounter;
import com.jgame.util.Vector2;

import java.sql.Time;

/**
 * Clase que representa un enemigo basico que se mantiene en su lugar y ataca
 * Created by jose on 24/05/16.
 */
public class Enemy extends Character {

    enum EnemyState {
        IDLE, DEAD, ATTACKING, TELEPORTING
    }

    abstract class EnemyAction {
        public abstract void act();
    }

    public final static TextureData TELEPORT_TEXTURE = new TextureData(0,0.625f,0.125f,0.75f);
    private final int DISTANCE_FROM_CHARACTER = 35;
    private TimeCounter idleTimer = new TimeCounter(1.5f);
    private CollisionObject[] startupBoxes = new CollisionObject[]{idleCollisionBoxes[0]};
    private CollisionObject[] activeBoxes = new CollisionObject[]{idleCollisionBoxes[0],
            new CollisionObject(new Vector2(55,55),0,10,5,this, CollisionObject.TYPE_ATTACK)};
    private CollisionObject[] recoveryBoxes = new CollisionObject[]{idleCollisionBoxes[0]};
    private AttackData [] attacks =
            new AttackData[] {new AttackData(0.33f,0.1f,0.45f, startupBoxes, activeBoxes, recoveryBoxes)};
    protected EnemyState currentState;
    private final TimeCounter teleportInterval;
    private int currentAction;
    private final EnemyAction[] actions;

    public Enemy(float sizeX, float sizeY, float idleSizeX, float idleSizeY, float yPosition,int id, final MainCharacter mainCharacter) {
        super(sizeX, sizeY, idleSizeX, idleSizeY, new Vector2(0, yPosition), id);
        teleportInterval = new TimeCounter(0.5f);
        currentState = EnemyState.TELEPORTING;
        EnemyAction move  = new EnemyAction() {
            @Override
            public void act() {
                moveTo(mainCharacter.position.x + mainCharacter.baseX.x* -1 * DISTANCE_FROM_CHARACTER, position.y);
                adjustToFoePosition(mainCharacter);
            }
        };
        EnemyAction attack = new EnemyAction(){
            @Override
            public void act(){
                currentState = EnemyState.IDLE;
            }
        };
        actions = new EnemyAction[]{move, attack};
        currentAction = 0;
    }

    private void toggleCurrentAction(){
        currentAction = currentAction + 1 < actions.length ? currentAction + 1 : 0;
    }

    public void reset(){
        idleTimer.reset();
        baseX.x = 1;
        currentState = EnemyState.TELEPORTING;
    }

    @Override
    public void update(Character foe, GameFlow.UpdateInterval interval, FightingGameFlow.WorldData worldData) {

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
            idleTimer.accum(interval);
            if (idleTimer.completed()) {
                currentState = EnemyState.ATTACKING;
                activeAttack = attacks[0];
                activeAttack.reset();
            }
            return;
        }

        if(currentState == EnemyState.ATTACKING) {
            activeAttack.update(interval);
            if(activeAttack.completed()){
                currentState = EnemyState.TELEPORTING;
                idleTimer.reset();
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
