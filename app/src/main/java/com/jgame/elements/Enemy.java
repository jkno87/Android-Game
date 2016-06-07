package com.jgame.elements;

import com.jgame.game.FightingGameFlow;
import com.jgame.game.GameFlow;
import com.jgame.util.TextureDrawer;
import com.jgame.util.TimeCounter;
import com.jgame.util.Vector2;

/**
 * Clase que representa un enemigo basico que se mantiene en su lugar y ataca
 * Created by jose on 24/05/16.
 */
public class Enemy extends Character {

    enum EnemyState {
        IDLE, DEAD, ATTACKING
    }

    public Enemy(float sizeX, float sizeY, float idleSizeX, float idleSizeY, Vector2 position, int id) {
        super(sizeX, sizeY, idleSizeX, idleSizeY, position, id);
    }

    private TimeCounter idleTimer = new TimeCounter(1.5f);
    private CollisionObject[] startupBoxes = new CollisionObject[]{idleCollisionBoxes[0]};
    private CollisionObject[] activeBoxes = new CollisionObject[]{idleCollisionBoxes[0],
            new CollisionObject(new Vector2(55,55),0,10,5,this, CollisionObject.TYPE_ATTACK)};
    private CollisionObject[] recoveryBoxes = new CollisionObject[]{idleCollisionBoxes[0]};
    private AttackData [] attacks =
            new AttackData[] {new AttackData(0.33f,0.1f,0.45f, startupBoxes, activeBoxes, recoveryBoxes)};
    protected EnemyState currentState = EnemyState.IDLE;

    public void reset(){
        idleTimer.reset();
        currentState = EnemyState.IDLE;
    }

    @Override
    public void update(Character foe, GameFlow.UpdateInterval interval, FightingGameFlow.WorldData worldData) {

        adjustToFoePosition(foe);

        if(currentState == EnemyState.DEAD)
            return;

        if(currentState == EnemyState.IDLE) {
            idleTimer.accum(interval);
            if (idleTimer.completed()) {
                currentState = EnemyState.ATTACKING;
                activeAttack = attacks[0];
                activeAttack.reset();
            }
        }

        if(currentState == EnemyState.ATTACKING) {
            activeAttack.update(interval);
            if(activeAttack.completed()){
                currentState = EnemyState.IDLE;
                idleTimer.reset();
            }

            for(CollisionObject co : getActiveCollisionBoxes())
                if(co.checkCollision(foe))
                    foe.hit();

        }
    }

    @Override
    public void hit(){
        currentState = EnemyState.DEAD;
    }

    @Override
    public TextureDrawer.TextureData getCurrentTexture() {
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
