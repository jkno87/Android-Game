package com.jgame.elements;

import com.jgame.game.GameActivity;
import com.jgame.util.Square;
import com.jgame.util.TextureDrawer.TextureData;
import com.jgame.util.TextureDrawer;
import com.jgame.util.Vector2;

/**
 * Created by jose on 27/09/16.
 */
public class RobotEnemy extends GameCharacter {

    enum EnemyState {
        WAITING, EXPLODING, ATTACKING, DYING, DEAD
    }

    public final static TextureData IDLE_TEXTURE = new TextureData(0.375f,0.125f,0.5f,0.25f);
    public final static TextureData[] STARTUP_TEXTURES = {new TextureData(0.375f,0,0.5f,0.125f),
            new TextureData(0.375f,0.25f,0.5f,0.375f),
            new TextureData(0.5f,0.25f,0.625f,0.375f)};
    private final static AnimationData DECORATION_SAMPLE = new AnimationData(50, false,
            new TextureData[]{new TextureData(0.125f, 0.625f, 0.1875f,0.75f)});
    private final static AnimationData DESTROY_ANIMATION = new AnimationData(2, false,
            new TextureData[] {STARTUP_TEXTURES[2], STARTUP_TEXTURES[1], STARTUP_TEXTURES[0], TextureDrawer.genTextureData(6,2,8)});
    public final static TextureData[] RECOVERY_TEXTURES = {TextureDrawer.genTextureData(5,1,8),
    TextureDrawer.genTextureData(6,1,8)};
    public final static TextureData ATTACK_TEXTURE = new TextureData(0.5f,0.125f,0.625f,0.25f);
    public final static float DISTANCE_FROM_MAIN_CHARACTER = 150;
    public final static float ATTACK_DISTANCE = 100;
    //private final EnemyAction[] actions;
    private final MainCharacter mainCharacter;
    private final int FRAMES_TO_SELFDESTRUCT = 200;
    private EnemyState currentState;
    private int selfDestructFrame;
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
        attackRange = ATTACK_DISTANCE + idleSizeX;
        CollisionObject[] explosionBoxes = new CollisionObject[]{new CollisionObject(new Vector2(57,55),0,GameActivity.PLAYING_WIDTH,35,this, CollisionObject.TYPE_ATTACK)};
        explosionAttack = new AttackData(explosionBoxes, explosionBoxes, explosionBoxes);
        CollisionObject[] startupBoxes = new CollisionObject[]{};
        CollisionObject[] attackBoxes = new CollisionObject[]{new CollisionObject(new Vector2(0,50),0,125,55,this, CollisionObject.TYPE_HITTABLE),
        new CollisionObject(new Vector2(100, 50),0,20,20, this, CollisionObject.TYPE_ATTACK)};
        regularAttack = new AttackData(startupBoxes, attackBoxes, attackBoxes);
        regularAttack.setStartupAnimation(new AnimationData(10, false, STARTUP_TEXTURES));
        regularAttack.setActiveAnimation(new AnimationData(40, false, ATTACK_TEXTURE));
        regularAttack.setRecoveryAnimation(new AnimationData(10, false, RECOVERY_TEXTURES));
    }

    @Override
    public void reset(float x, float y) {
        selfDestructFrame = 0;
        regularAttack.reset();
        currentState = EnemyState.WAITING;
        setPosition(mainCharacter, DISTANCE_FROM_MAIN_CHARACTER);
        DESTROY_ANIMATION.reset();
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
    public TextureData getCurrentTexture() {
        if(currentState == EnemyState.ATTACKING)
            return activeAttack.getCurrentAnimation().getCurrentSprite();
        else if(currentState == EnemyState.DYING)
            return DESTROY_ANIMATION.getCurrentSprite();
        else
            return IDLE_TEXTURE;
    }

    @Override
    public void hit() {
        currentState = EnemyState.DYING;
    }

    @Override
    public void update(GameCharacter foe, GameActivity.WorldData worldData) {
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
                selfDestructFrame += 1;

            if(selfDestructFrame >= FRAMES_TO_SELFDESTRUCT) {
                currentState = EnemyState.EXPLODING;
                activeAttack = explosionAttack;
                worldData.dBuffer.add(new GameActivity.Decoration(new AnimationData(DECORATION_SAMPLE),
                        new Square(position, 50,100,0)));
            }
        }

        if(currentState == EnemyState.ATTACKING){
            activeAttack.update();
            if(activeAttack.completed())
                currentState = EnemyState.WAITING;
        }

        if(currentState != EnemyState.WAITING){
            super.update(foe, worldData);
        }

        if(currentState == EnemyState.DYING){
            DESTROY_ANIMATION.updateFrame();
            if(DESTROY_ANIMATION.completed())
                currentState = EnemyState.DEAD;
        }

    }

}