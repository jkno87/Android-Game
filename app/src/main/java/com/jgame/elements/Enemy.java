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
    public Enemy(float sizeX, float sizeY, Vector2 position, int id) {
        super(sizeX, sizeY, position, id);
        changeDirection();
    }

    private TimeCounter idleTimer = new TimeCounter(1.5f);
    private CollisionObject[] startupBoxes = new CollisionObject[]{idleCollisionBoxes[0]};
    private CollisionObject[] activeBoxes = new CollisionObject[]{idleCollisionBoxes[0],
            new CollisionObject(new Vector2(15,55),0,10,5,this, CollisionObject.TYPE_ATTACK)};
    private CollisionObject[] recoveryBoxes = new CollisionObject[]{idleCollisionBoxes[0]};
    private AttackData [] attacks =
            new AttackData[] {new AttackData(0.33f,0.1f,0.45f, startupBoxes, activeBoxes, recoveryBoxes)};

    public void reset(){
        idleTimer.reset();
        currentState = CharacterState.IDLE;
    }

    @Override
    public void update(Character foe, GameFlow.UpdateInterval interval, FightingGameFlow.WorldData worldData) {

        adjustToFoePosition(foe);

        if(currentState == CharacterState.DEAD)
            return;

        if(currentState == CharacterState.IDLE) {
            idleTimer.accum(interval);
            if (idleTimer.completed()) {
                currentState = CharacterState.ATTACKING;
                activeAttack = attacks[0];
                activeAttack.reset();
            }
        }

        if(currentState == CharacterState.ATTACKING) {
            activeAttack.update(interval);
            if(activeAttack.completed()){
                currentState = CharacterState.IDLE;
                idleTimer.reset();
            }

            for(CollisionObject co : getActiveCollisionBoxes())
                if(co.checkCollision(foe))
                    foe.hit();

        }
    }

    @Override
    public void hit(){
        currentState = CharacterState.DEAD;
    }

    @Override
    public TextureDrawer.TextureData getCurrentTexture() {
        if(currentState != CharacterState.ATTACKING)
            return Character.TEXTURE;

        if(activeAttack.currentState == AttackData.CollisionState.ACTIVE)
            return MainCharacter.ACTIVE_MOV_A;
        else
            return MainCharacter.INIT_MOV_A;

    }
}
