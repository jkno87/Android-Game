package com.jgame.elements;

import android.util.Log;

import com.jgame.game.GameActivity;
import com.jgame.util.SimpleDrawer;
import com.jgame.util.Square;
import com.jgame.util.TextureDrawer.TextureData;
import com.jgame.util.TextureDrawer;
import com.jgame.util.Vector2;
import com.jgame.game.GameActivity.Decoration;

/**
 * Enemigo que tiene el proposito de ensenar whiff punish al jugador. Esto significa que esta a una distancia lejana al jugador, el jugador
 * debe de acercarse para provocar un ataque del robot, reaccionar al ataque y castigar su ataque. En caso de que el jugador no se acerque,
 * el robot explota y el juego se termina.
 * Created by jose on 27/09/16.
 */
public class RobotEnemy extends GameCharacter {

    enum EnemyState {
        WAITING, EXPLODING, ATTACKING, DYING, DEAD
    }

    private final static int INITIAL_BEEP_INTERVAL = 30;
    public final static TextureData IDLE_TEXTURE = new TextureData(0.375f,0.125f,0.5f,0.25f);
    public final static TextureData[] STARTUP_TEXTURES = {new TextureData(0.375f,0,0.5f,0.125f),
            new TextureData(0.375f,0.25f,0.5f,0.375f),
            new TextureData(0.5f,0.25f,0.625f,0.375f)};
    private final static AnimationData BEEP_ANIMATION = new AnimationData(15, false,
            new TextureData[]{IDLE_TEXTURE});
    private final static AnimationData DESTROY_ANIMATION = new AnimationData(2, false,
            new TextureData[] {STARTUP_TEXTURES[2], STARTUP_TEXTURES[1], STARTUP_TEXTURES[0], TextureDrawer.genTextureData(6,2,8)});
    public final static TextureData[] RECOVERY_TEXTURES = {TextureDrawer.genTextureData(5,1,8),
    TextureDrawer.genTextureData(6,1,8)};
    public final static TextureData ATTACK_TEXTURE = new TextureData(0.5f,0.125f,0.625f,0.25f);
    public final static float DISTANCE_FROM_MAIN_CHARACTER = 150;
    public final static float ATTACK_DISTANCE = 100;
    //private final EnemyAction[] actions;
    private final MainCharacter mainCharacter;
    private final int FRAMES_TO_SELFDESTRUCT = 300;
    private EnemyState currentState;
    private int beepInterval;
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
        CollisionObject[] startupBoxes = new CollisionObject[]{};
        CollisionObject[] attackBoxes = new CollisionObject[]{new CollisionObject(new Vector2(0,50),0,125,55,this, CollisionObject.TYPE_HITTABLE),
        new CollisionObject(new Vector2(100, 50),0,20,20, this, CollisionObject.TYPE_ATTACK)};
        explosionAttack = new AttackData(explosionBoxes, explosionBoxes, explosionBoxes);
        regularAttack = new AttackData(startupBoxes, attackBoxes, attackBoxes);
        regularAttack.setStartupAnimation(new AnimationData(10, false, STARTUP_TEXTURES));
        regularAttack.setActiveAnimation(new AnimationData(40, false, ATTACK_TEXTURE));
        regularAttack.setRecoveryAnimation(new AnimationData(10, false, RECOVERY_TEXTURES));
    }

    @Override
    public void reset(float x, float y) {
        beepInterval = 0;
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
            if(selfDestructFrame >= FRAMES_TO_SELFDESTRUCT) {
                currentState = EnemyState.EXPLODING;
                activeAttack = explosionAttack;
                worldData.dBuffer.add(new Decoration(new AnimationData(DESTROY_ANIMATION),
                        new Square(new Vector2(position), 50, 100, 0), baseX.x == -1){
                    public void update(){
                        animation.updateFrame();
                        size.lenX += 10;
                    }
                });
            }

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
            } else if(beepInterval == INITIAL_BEEP_INTERVAL){
                worldData.dBuffer.add(new Decoration(new AnimationData(BEEP_ANIMATION),
                        new Square(new Vector2(position), spriteContainer.lenX, spriteContainer.lenY,0),
                        new SimpleDrawer.ColorData(1,0,0,0.25f), baseX.x == -1){
                        public void update(){
                            animation.updateFrame();
                        }
                });
                beepInterval = 0;
            }

            selfDestructFrame += 1;
            beepInterval += 1;

        }

        if(currentState == EnemyState.ATTACKING){
            activeAttack.update();
            if(activeAttack.completed()) {
                selfDestructFrame = 0;
                beepInterval = 0;
                currentState = EnemyState.WAITING;
                regularAttack.reset();
            }
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
