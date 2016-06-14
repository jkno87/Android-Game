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

    class EnemyParameters {
        float distanceFromCharacter;
        float startInterval;
        float activeInterval;
        float recoveryInterval;
        TimeCounter teleportInterval;
        TimeCounter idleTimer;
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
    private final EnemyParameters[] parameters;
    private int currentDifficulty;
    private EnemyParameters currentParameters;

    public Enemy(float sizeX, float sizeY, float idleSizeX, float idleSizeY, float yPosition,int id, final MainCharacter mainCharacter) {
        super(sizeX, sizeY, idleSizeX, idleSizeY, new Vector2(0, yPosition), id);
        currentState = EnemyState.TELEPORTING;
        this.mainCharacter = mainCharacter;
        EnemyAction move  = new EnemyAction() {
            @Override
            public void act() {
                setPosition();
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
        parameters = setupEnemy();
        currentParameters = parameters[currentDifficulty];
        this.attack = new AttackData(startupBoxes, activeBoxes, recoveryBoxes);
        this.attack.startupCounter = new TimeCounter(currentParameters.startInterval);
        this.attack.activeCounter = new TimeCounter(currentParameters.activeInterval);
        this.attack.recoveryCounter = new TimeCounter(currentParameters.recoveryInterval);
        activeAttack = this.attack;
    }

    private EnemyParameters[] setupEnemy(){
        EnemyParameters easy = new EnemyParameters();
        easy.distanceFromCharacter = 35;
        easy.activeInterval = 0.1f;
        easy.recoveryInterval = 0.45f;
        easy.startInterval = 0.33f;
        easy.teleportInterval = new TimeCounter(0.5f);
        easy.idleTimer = new TimeCounter(1.5f);

        EnemyParameters medium = new EnemyParameters();
        medium.activeInterval = 0.1f;
        medium.distanceFromCharacter = 20;
        medium.recoveryInterval = 0.3f;
        medium.startInterval = 0.2f;
        medium.teleportInterval = new TimeCounter(0.3f);
        medium.idleTimer = new TimeCounter(0.4f);

        EnemyParameters hard = new EnemyParameters();
        hard.activeInterval = 0;
        hard.distanceFromCharacter = 0;
        hard.recoveryInterval = 0;
        hard.startInterval = 0;
        hard.teleportInterval = new TimeCounter();
        hard.idleTimer = new TimeCounter();

        return new EnemyParameters[]{easy, medium, hard};
    }

    private void toggleCurrentAction(){
        currentAction = currentAction + 1 < actions.length ? currentAction + 1 : 0;
    }

    public void increaseDifficulty(){
        if(currentDifficulty == 1)
            return;
        else {
            currentDifficulty = 1;
            currentParameters = parameters[1];
        }

    }

    private void setPosition(){
        moveTo(mainCharacter.position.x + mainCharacter.baseX.x* -1 * currentParameters.distanceFromCharacter, position.y);
        adjustToFoePosition(mainCharacter);
    }

    public void reset(){
        currentParameters.teleportInterval.reset();
        baseX.x = 1;
        currentState = EnemyState.TELEPORTING;
        currentAction = 0;
        setPosition();
    }

    @Override
    public void update(Character foe, GameFlow.UpdateInterval interval, FightingGameFlow.WorldData worldData) {

        adjustToFoePosition(foe);

        if(currentState == EnemyState.DEAD)
            return;

        if(currentState == EnemyState.TELEPORTING){
            currentParameters.teleportInterval.accum(interval);
            if(!currentParameters.teleportInterval.completed())
                return;
            currentParameters.teleportInterval.reset();
            actions[currentAction].act();
            toggleCurrentAction();

            return;
        }

        if(currentState == EnemyState.IDLE) {
            currentParameters.idleTimer.accum(interval);
            if (currentParameters.idleTimer.completed()) {
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
                currentParameters.idleTimer.reset();
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
