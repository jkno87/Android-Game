package com.jgame.elements;

import com.jgame.game.GameActivity;
import com.jgame.game.GameFlow;
import com.jgame.util.TextureDrawer.TextureData;
import com.jgame.util.TextureDrawer;
import com.jgame.util.TimeCounter;
import com.jgame.util.Vector2;

/**
 * Created by jose on 27/09/16.
 */
public class RobotEnemy extends GameCharacter {

    enum EnemyState {
        WAITING, EXPLODING, ATTACKING, DEAD
    }

    public final static TextureData TELEPORT_TEXTURE = new TextureDrawer.TextureData(0,0.625f,0.125f,0.75f);
    public final static TextureData IDLE_TEXTURE = new TextureDrawer.TextureData(0.5f,0,0.75f,0.25f);
    public final static TextureData STARTUP_ATTACK = new TextureDrawer.TextureData(0.5f,0.25f,0.75f,0.5f);
    public final static TextureData ATTACK_TEXTURE = new TextureDrawer.TextureData(0.5f,0.5f,0.75f,0.75f);
    public final static float DISTANCE_FROM_MAIN_CHARACTER = 150;
    public final static float ATTACK_DISTANCE = 90;
    public final static float TIME_TO_SELF_DESTRUCT = 5;
    //private final EnemyAction[] actions;
    private final MainCharacter mainCharacter;
    private final TimeCounter timeToSelfDestruct;
    private EnemyState currentState;
    private float attackRange;
    private final AttackData explosionAttack;
    private final AttackData regularAttack;


    public RobotEnemy(float spriteSizeX, float spriteSizeY, float idleSizeX, float idleSizeY, float positionY, int id, final MainCharacter mainCharacter) {
        super(spriteSizeX, spriteSizeY, idleSizeX, idleSizeY, new Vector2(0, positionY), id);
        //EnemyAction checkAttackDistance = new EnemyAction() {
         //   @Override
        //    public void act() {

        //    }
        //};

        //actions = new EnemyAction[]{checkAttackDistance};
        this.mainCharacter = mainCharacter;
        timeToSelfDestruct = new TimeCounter(TIME_TO_SELF_DESTRUCT);
        attackRange = ATTACK_DISTANCE + idleSizeX;
        CollisionObject[] explosionBoxes = new CollisionObject[]{new CollisionObject(new Vector2(57,55),0,GameActivity.PLAYING_WIDTH,35,this, CollisionObject.TYPE_ATTACK)};
        explosionAttack = new AttackData(explosionBoxes, explosionBoxes, explosionBoxes);
        CollisionObject[] startupBoxes = new CollisionObject[]{new CollisionObject(new Vector2(0,50),0,125,55,this, CollisionObject.TYPE_HITTABLE)};
        CollisionObject[] attackBoxes = new CollisionObject[]{new CollisionObject(new Vector2(0,50),0,125,55,this, CollisionObject.TYPE_HITTABLE),
        new CollisionObject(new Vector2(100, 50),0,20,20, this, CollisionObject.TYPE_ATTACK)};
        regularAttack = new AttackData(1f, 0.1f, 0.5f, startupBoxes, attackBoxes, attackBoxes);
    }

    @Override
    public void reset(float x, float y) {
        timeToSelfDestruct.reset();
        regularAttack.reset();
        currentState = EnemyState.WAITING;
        setPosition(mainCharacter, DISTANCE_FROM_MAIN_CHARACTER);

    }

    @Override
    public boolean hittable() {
        return currentState != EnemyState.WAITING;
    }

    @Override
    public boolean alive() {
        return currentState != EnemyState.DEAD;
    }

    @Override
    public boolean attacking() {
        return currentState != EnemyState.WAITING;
    }

    @Override
    public TextureDrawer.TextureData getCurrentTexture() {
        if(currentState == EnemyState.ATTACKING) {
            if(activeAttack.currentState == AttackData.CollisionState.ACTIVE)
                return ATTACK_TEXTURE;
            else
                return STARTUP_ATTACK;
        } else
            return IDLE_TEXTURE;
    }

    @Override
    public void hit() {
        currentState = EnemyState.DEAD;
    }

    @Override
    public void update(GameCharacter foe, GameFlow.UpdateInterval interval, GameActivity.WorldData worldData) {
        adjustToFoePosition(foe);
        if(currentState == EnemyState.WAITING) {
            if (position.x > foe.position.x && (position.x - foe.position.x) < attackRange) {
                currentState = EnemyState.ATTACKING;
                activeAttack = regularAttack;
                for(CollisionObject co : activeAttack.active)
                    co.updatePosition();
            } else if(position.x < foe.position.x && (position.x - foe.position.x) * -1 < attackRange) {
                currentState = EnemyState.ATTACKING;
                activeAttack = regularAttack;
                for(CollisionObject co : activeAttack.active)
                    co.updatePosition();
            } else
                timeToSelfDestruct.accum(interval);

            if(timeToSelfDestruct.completed()) {
                currentState = EnemyState.EXPLODING;
                activeAttack = explosionAttack;
            }
        }

        if(currentState == EnemyState.ATTACKING){
            activeAttack.update(interval);
        }

        if(currentState != EnemyState.WAITING){
            super.update(foe, interval, worldData);
        }

    }

}
