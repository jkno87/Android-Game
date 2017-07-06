package com.jgame.elements;

import com.jgame.game.GameActivity;
import com.jgame.game.GameActivity.Difficulty;
import com.jgame.game.GameRenderer;
import com.jgame.util.Decoration;
import com.jgame.util.SimpleDrawer;
import com.jgame.util.Square;
import com.jgame.util.TextureDrawer.TextureData;
import com.jgame.util.TextureDrawer;
import com.jgame.util.Vector2;
import com.jgame.util.Decoration.StaticDecoration;
import com.jgame.game.GameData.Event;
import java.util.ArrayDeque;

/**
 * Enemigo que tiene el proposito de ensenar whiff punish al jugador. Esto significa que esta a una distancia lejana al jugador, el jugador
 * debe de acercarse para provocar un ataque del robot, reaccionar al ataque y castigar su ataque. En caso de que el jugador no se acerque,
 * el robot explota y el juego se termina.
 * Created by jose on 27/09/16.
 */
public class RobotEnemy extends GameCharacter {

    enum EnemyState {
        WAITING, EXPLODING, ATTACKING, DYING, DEAD, RECOVERING
    }

    private final int[] EASY_FRAME_DATA = new int[]{3,2,20};
    private final int[] MEDIUM_FRAME_DATA = new int[]{2,3,15};
    private final int[] HARD_FRAME_DATA = new int[]{2,3,10};
    private final static int INITIAL_BEEP_INTERVAL = 30;
    public final static TextureData IDLE_TEXTURE = new TextureData(0.375f,0.125f,0.5f,0.25f);
    public final static TextureData[] STARTUP_TEXTURES = {
            IDLE_TEXTURE, new TextureData(0.375f,0f,0.5f,0.125f),
            new TextureData(0.375f,0.25f,0.5f,0.375f),
            new TextureData(0.5f,0.25f,0.625f,0.375f),
            new TextureData(0.5f,0.125f,0.625f,0.25f),
            new TextureData(0.5f,0f,0.625f,0.125f),
            new TextureData(0.625f,0f,0.75f,0.125f),
            new TextureData(0.625f,0.125f,0.75f,0.25f),
            new TextureData(0.625f,0.25f,0.75f,0.375f)
    };
    private final static AnimationData BEEP_ANIMATION = new AnimationData(15, false,
            new TextureData[]{IDLE_TEXTURE});
    private final static TextureData EXPLOSION = TextureDrawer.genTextureData(6,2,8);
    //private final static AnimationData DESTROY_ANIMATION = new AnimationData(2, false,
    //        new TextureData[] {STARTUP_TEXTURES[2], STARTUP_TEXTURES[1], STARTUP_TEXTURES[0], EXPLOSION});
    public final static TextureData DISAPPEAR_TEXTURE = new TextureData(0.75f,0f,0.875f,0.125f);
    public final static TextureData[] RECOVERY_TEXTURES = {new TextureData(0.75f,0.125f,0.875f,0.25f)};
    public final static TextureData ATTACK_TEXTURE = new TextureData(0.75f,0.25f,0.875f,0.375f);
    public final static float DISTANCE_FROM_MAIN_CHARACTER = 260;
    public final static float ATTACK_DISTANCE = 95;
    //private final EnemyAction[] actions;
    private final MainCharacter mainCharacter;
    private final int FRAMES_TO_SELFDESTRUCT = 600;
    private final int FRAMES_TO_RECOVER = 20;
    private EnemyState currentState;
    private int beepInterval;
    private int currentIdleFrame;
    private float attackRange;
    private final AttackData explosionAttack;
    private final AttackData regularAttack;
    private int[] currentFrameDataSet;


    public RobotEnemy(float spriteSizeX, float spriteSizeY, float idleSizeX, float idleSizeY, float positionY, int id, final MainCharacter mainCharacter) {
        super(spriteSizeX, spriteSizeY, idleSizeX, idleSizeY, new Vector2(0, positionY), id);
        //EnemyAction checkAttackDistance = new EnemyAction() {
         //   @Override
        //    public void act() {

        //    }
        //};

        //actions = new EnemyAction[]{checkAttackDistance};
        this.mainCharacter = mainCharacter;
        currentFrameDataSet = EASY_FRAME_DATA;
        attackRange = ATTACK_DISTANCE + idleSizeX;
        CollisionObject[] explosionBoxes = new CollisionObject[]{new CollisionObject(new Vector2(57,55),0,GameActivity.PLAYING_WIDTH,35,this, CollisionObject.TYPE_ATTACK)};
        CollisionObject[] startupBoxes = new CollisionObject[]{new CollisionObject(new Vector2(115,100), 0, 15, 10, this, CollisionObject.TYPE_HITTABLE)};
        CollisionObject[] attackBoxes = new CollisionObject[]{new CollisionObject(new Vector2(0,50),0,140,55,this, CollisionObject.TYPE_HITTABLE),
        new CollisionObject(new Vector2(100, 50),0,15,20, this, CollisionObject.TYPE_ATTACK)};
        explosionAttack = new AttackData(explosionBoxes, explosionBoxes, explosionBoxes);
        regularAttack = new AttackData(startupBoxes, attackBoxes, startupBoxes);
        regularAttack.setStartupAnimation(new AnimationData(currentFrameDataSet[0], false, STARTUP_TEXTURES));
        regularAttack.setActiveAnimation(new AnimationData(currentFrameDataSet[1], false, ATTACK_TEXTURE));
        regularAttack.setRecoveryAnimation(new AnimationData(currentFrameDataSet[2], false, RECOVERY_TEXTURES));
    }

    @Override
    public void trip(){

    }

    @Override
    public void reset(float x, float y) {
        beepInterval = 0;
        currentIdleFrame = 0;
        currentState = EnemyState.WAITING;
        setPosition(mainCharacter, DISTANCE_FROM_MAIN_CHARACTER);
        regularAttack.reset();
        //DESTROY_ANIMATION.reset();
        regularAttack.updateFrameData(currentFrameDataSet);
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
            return RECOVERY_TEXTURES[0];
        else if(currentState == EnemyState.RECOVERING)
            return DISAPPEAR_TEXTURE;
        else
            return IDLE_TEXTURE;
    }

    @Override
    public void hit() {
        currentState = EnemyState.DYING;
    }

    @Override
    public Event update(GameCharacter foe, ArrayDeque<Decoration> decorationData) {
        adjustToFoePosition(foe);
        if(currentState == EnemyState.WAITING) {
            if(currentIdleFrame >= FRAMES_TO_SELFDESTRUCT) {
                currentState = EnemyState.EXPLODING;
                activeAttack = explosionAttack;
                decorationData.add(new StaticDecoration(EXPLOSION, new Square(new Vector2(position), 200, 90, 0),
                        baseX.x == -1, 0, 15, true));
                decorationData.add(new StaticDecoration(EXPLOSION, new Square(new Vector2(position), 300, 75, 0),
                        baseX.x == -1, 13, 10, true));
            }

            //Se verifica que el enemigo se encuentre en rango del ataque.
            if ((position.x > foe.position.x && (position.x - foe.position.x) < attackRange) ||
                    (position.x < foe.position.x && (position.x - foe.position.x) * -1 < attackRange)) {
                decorationData.add(new StaticDecoration(IDLE_TEXTURE,
                        new Square(new Vector2(position), spriteContainer.lenX, spriteContainer.lenY, 0),
                        new SimpleDrawer.ColorData(1,1,1,1),
                        baseX.x == -1, 0, 75, true));
                currentState = EnemyState.ATTACKING;
                activeAttack = regularAttack;
                for(CollisionObject co : activeAttack.active)
                    co.updatePosition();
            } else if(beepInterval == INITIAL_BEEP_INTERVAL){
                decorationData.add(new StaticDecoration(IDLE_TEXTURE,
                        new Square(new Vector2(position), spriteContainer.lenX, spriteContainer.lenY, 0),
                        new SimpleDrawer.ColorData(1,0,0,0.25f),
                        baseX.x == -1, 0, 10, false));
                beepInterval = 0;
            }

            currentIdleFrame += 1;
            beepInterval += 1;

        }

        if(currentState == EnemyState.ATTACKING){
            activeAttack.update();
            if(activeAttack.completed()) {
                currentIdleFrame = 0;
                beepInterval = 0;
                currentState = EnemyState.RECOVERING;
                regularAttack.reset();
                foe.trip();

                return Event.QUAKE;
            }
        }

        if(currentState == EnemyState.RECOVERING){
            currentIdleFrame += 1;
            if(currentIdleFrame > FRAMES_TO_RECOVER) {
                currentState = EnemyState.WAITING;
                currentIdleFrame = 0;
            }
        }

        if(currentState != EnemyState.WAITING){
            super.update(foe, decorationData);
        }

        if(currentState == EnemyState.DYING){
            StaticDecoration s = new StaticDecoration(GameRenderer.DISAPPEAR_TEXTURE,
                    new Square(new Vector2(position).add(spriteContainer.lenX * baseX.x, 0)
                            , spriteContainer.lenX, spriteContainer.lenY, 0),
                    new SimpleDrawer.ColorData(1,1,1,1),
                    baseX.x == 1, 0, 3, false);
            s.shrinkRateX = 0.9f;
            decorationData.add(s);
            currentState = EnemyState.DEAD;
        }

        if(currentState == EnemyState.EXPLODING)
            currentState = EnemyState.DEAD;

        return Event.NONE;
    }

    @Override
    public void setCurrentDifficulty(Difficulty newDifficulty){
        if(this.currentDifficulty == newDifficulty)
            return;
        else {
            if(newDifficulty == Difficulty.EASY)
                currentFrameDataSet = EASY_FRAME_DATA;
            else if(newDifficulty == Difficulty.MEDIUM)
                currentFrameDataSet = MEDIUM_FRAME_DATA;
            else
                currentFrameDataSet = HARD_FRAME_DATA;

            currentDifficulty = newDifficulty;
        }

    }

}
