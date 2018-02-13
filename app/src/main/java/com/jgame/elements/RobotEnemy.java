package com.jgame.elements;

import com.jgame.game.GameActivity.Difficulty;
import com.jgame.util.CollisionObject;
import com.jgame.util.Decoration;
import com.jgame.util.Square;
import com.jgame.util.TextureDrawer;
import com.jgame.util.TextureDrawer.TextureData;
import com.jgame.util.TextureDrawer.ColorData;
import com.jgame.util.Vector2;
import com.jgame.util.Decoration.AnimatedDecoration;
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

    private final static Vector2 INITIAL_POSITION = new Vector2(410,0);
    private final static TextureData[] BREATH_SPRITES = {new TextureData(0.375f,0.25f,0.5f,0.375f),
            new TextureData(0.5f,0.25f,0.625f,0.375f)};
    private final int[] EASY_FRAME_DATA = new int[]{20,45,45};
    private final int[] MEDIUM_FRAME_DATA = new int[]{17,28,28};
    private final int[] HARD_FRAME_DATA = new int[]{2,3,10};
    public final static TextureData BREATH_DECORATION = TextureDrawer.generarTextureData(4,4,5,6,32);
    public final static TextureData IDLE_TEXTURE = TextureDrawer.generarTextureData(0,0,4,4,32);
    public final static TextureData[] STARTUP_TEXTURES = {
            IDLE_TEXTURE, TextureDrawer.generarTextureData(4,0,8,4,32)
    };
    public final static TextureData[] RECOVERY_TEXTURES = {TextureDrawer.generarTextureData(4,0,8,4,32)};
    public final static TextureData ATTACK_TEXTURE = TextureDrawer.generarTextureData(8,0,12,4,32);
    public final static float ATTACK_DISTANCE = 56;
    public final static float WARNING_DISTANCE = 45;
    private EnemyState currentState;
    //private final AttackData regularAttack;
    private int[] currentFrameDataSet;
    private float distanceFromCharacter;


    public RobotEnemy(float spriteSizeX, float spriteSizeY, float idleSizeX, float idleSizeY, float positionY) {
        super(spriteSizeX, spriteSizeY, idleSizeX, idleSizeY, new Vector2(0, positionY));
        //Este personaje siempre va a ver hacia la izquierda
        this.baseX.x = -1;
        currentFrameDataSet = EASY_FRAME_DATA;
        /*CollisionObject[] startupBoxes = new CollisionObject[]{};
        CollisionObject[] attackBoxes = new CollisionObject[]{new CollisionObject(new Vector2(100, 50),0,58,20, this, CollisionObject.TYPE_ATTACK)};
        CollisionObject[] recoveryBoxes = new CollisionObject[]{new CollisionObject(new Vector2(0,50),0,165,55,this, CollisionObject.TYPE_HITTABLE)};
        regularAttack = new AttackData(startupBoxes, attackBoxes, recoveryBoxes);
        regularAttack.setStartupAnimation(new AnimationData(currentFrameDataSet[0], false, STARTUP_TEXTURES));
        regularAttack.setActiveAnimation(new AnimationData(currentFrameDataSet[1], false, ATTACK_TEXTURE));
        regularAttack.setRecoveryAnimation(new AnimationData(currentFrameDataSet[2], false, RECOVERY_TEXTURES));*/
    }

    @Override
    public void reset(Vector2 positionOffset) {
        //activeCollisionBoxes = idleCollisionBoxes;
        currentState = EnemyState.WAITING;
        moveTo(positionOffset, INITIAL_POSITION);
        //regularAttack.reset();
        //regularAttack.updateFrameData(currentFrameDataSet);
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
    public TextureData getCurrentTexture() {
        /*if(currentState == EnemyState.ATTACKING)
            return regularAttack.getCurrentAnimation().getCurrentSprite();
        else*/
            return IDLE_TEXTURE;
    }

    @Override
    public void hit(CollisionObject o) {
        currentState = EnemyState.DYING;
    }

    @Override
    public boolean completedTransition(){
        return position.x < INITIAL_POSITION.x;
    }

    @Override
    public void update(GameCharacter foe, ArrayDeque<Decoration> decorationData) {

        if(currentState == EnemyState.WAITING) {
            distanceFromCharacter = position.x - foe.position.x - idleSizeX - ATTACK_DISTANCE;

            //Se verifica que el enemigo se encuentre en rango del ataque.
            if (distanceFromCharacter < 0) {
                /*decorationData.add(new Decoration.StaticDecoration(BREATH_DECORATION, new Square(new Vector2(position).add(-120,65),40,85),
                                true, currentFrameDataSet[0],
                        currentFrameDataSet[1] + currentFrameDataSet[2], new ColorData(0,1,1,1),
                        1, currentFrameDataSet[1]));*/
                currentState = EnemyState.ATTACKING;
                //regularAttack.reset();
                /*for(CollisionObject co : activeAttack.recovery)
                    co.updatePosition();
                for(CollisionObject co : activeAttack.active)
                    co.updatePosition();*/

                return;
            } else if(distanceFromCharacter < WARNING_DISTANCE) {
                color.r = distanceFromCharacter / WARNING_DISTANCE;
                color.g = distanceFromCharacter / WARNING_DISTANCE;
                color.b = distanceFromCharacter / WARNING_DISTANCE;
            } else {
                color.g = 1;
                color.r = 1;
                color.b = 1;
            }
        }

        if(currentState == EnemyState.ATTACKING){
            /*regularAttack.update();
            updateCollisionObjects(regularAttack);
            if(regularAttack.completed()) {
                regularAttack.reset();
                currentState = EnemyState.WAITING;
                activeCollisionBoxes = idleCollisionBoxes;
            }

            detectCollision(foe, activeCollisionBoxes);*/

            return;
        }


        if(currentState == EnemyState.DYING){
            currentState = EnemyState.DEAD;
            //decorationData.add(new Decoration.IdleDecoration(IDLE_TEXTURE,
            //        new Square(this.spriteContainer), true));
        }

        return;
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
