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
import com.jgame.util.Decoration.AnimatedDecoration;
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
        WAITING, ATTACKING, DEAD, DYING
    }

    private final static TextureData[] BREATH_SPRITES = {new TextureData(0.375f,0.25f,0.5f,0.375f),
            new TextureData(0.5f,0.25f,0.625f,0.375f)};
    private final int[] EASY_FRAME_DATA = new int[]{20,35,28};
    private final int[] MEDIUM_FRAME_DATA = new int[]{2,3,15};
    private final int[] HARD_FRAME_DATA = new int[]{2,3,10};
    public final static TextureData IDLE_TEXTURE = new TextureData(0,0,0.25f,0.25f);
    public final static TextureData[] STARTUP_TEXTURES = {
            IDLE_TEXTURE, new TextureData(0.25f, 0, 0.50f, 0.25f)
    };
    public final static TextureData[] RECOVERY_TEXTURES = {new TextureData(0.25f, 0, 0.5f, 0.25f)};
    public final static TextureData ATTACK_TEXTURE = new TextureData(0.50f, 0, 0.75f, 0.25f);
    public final static float DISTANCE_FROM_MAIN_CHARACTER = 200;
    public final static float ATTACK_DISTANCE = 56;
    public final static int BREATH_FRAMES = 10;
    private final MainCharacter mainCharacter;
    private final int FRAMES_TO_RECOVER = 20;
    private EnemyState currentState;
    private int currentIdleFrame;
    private float attackRange;
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
        //Este personaje siempre va a ver hacia la izquierda
        this.baseX.x = -1;
        currentFrameDataSet = EASY_FRAME_DATA;
        attackRange = idleSizeX + ATTACK_DISTANCE;
        CollisionObject[] startupBoxes = new CollisionObject[]{new CollisionObject(new Vector2(143,100), 0, 15, 25, this, CollisionObject.TYPE_HITTABLE)};
        CollisionObject[] attackBoxes = new CollisionObject[]{new CollisionObject(new Vector2(0,50),0,162,55,this, CollisionObject.TYPE_HITTABLE),
        new CollisionObject(new Vector2(100, 50),0,58,20, this, CollisionObject.TYPE_ATTACK)};
        regularAttack = new AttackData(startupBoxes, attackBoxes, attackBoxes);
        regularAttack.setStartupAnimation(new AnimationData(currentFrameDataSet[0], false, STARTUP_TEXTURES));
        regularAttack.setActiveAnimation(new AnimationData(currentFrameDataSet[1], false, ATTACK_TEXTURE));
        regularAttack.setRecoveryAnimation(new AnimationData(currentFrameDataSet[2], false, RECOVERY_TEXTURES));
    }

    @Override
    public void reset(float x, float y) {
        currentIdleFrame = 0;
        currentState = EnemyState.WAITING;
        moveX(mainCharacter.idleSizeX + mainCharacter.position.x + idleSizeX + DISTANCE_FROM_MAIN_CHARACTER);
        regularAttack.reset();
        //DESTROY_ANIMATION.reset();
        regularAttack.updateFrameData(currentFrameDataSet);
    }

    @Override
    public boolean hittable() {
        return true;
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

            //Se verifica que el enemigo se encuentre en rango del ataque.
            if ((position.x > foe.position.x && (position.x - foe.position.x) < attackRange) ||
                    (position.x < foe.position.x && (position.x - foe.position.x) * -1 < attackRange)) {
                decorationData.add(new AnimatedDecoration(currentFrameDataSet[0],
                        new AnimationData(BREATH_FRAMES, false, BREATH_SPRITES),
                        new Square(new Vector2(position).add(-120, 82), 50, 50, 0), true));
                currentState = EnemyState.ATTACKING;
                activeAttack = regularAttack;
                for(CollisionObject co : activeAttack.active)
                    co.updatePosition();
            }

            currentIdleFrame += 1;
        }

        if(currentState == EnemyState.ATTACKING){
            activeAttack.update();
            if(activeAttack.completed()) {
                currentIdleFrame = 0;
                regularAttack.reset();
                currentState = EnemyState.WAITING;
            }
        }

        if(currentState != EnemyState.WAITING){
            super.update(foe, decorationData);
        }

        if(currentState == EnemyState.DYING){
            currentState = EnemyState.DEAD;
            decorationData.add(new Decoration.IdleDecoration(IDLE_TEXTURE,
                    new Square(this.spriteContainer), true));
        }

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
