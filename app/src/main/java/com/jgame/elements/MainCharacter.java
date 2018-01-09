package com.jgame.elements;

import com.jgame.game.ControllerManager;
import com.jgame.game.GameData.Event;
import com.jgame.util.Decoration;
import com.jgame.util.FrameCounter;
import com.jgame.util.Square;
import com.jgame.util.TextureDrawer;
import com.jgame.util.TextureDrawer.ColorData;
import com.jgame.util.TextureDrawer.TextureData;
import com.jgame.util.Vector2;
import com.jgame.util.CollisionObject;
import java.util.ArrayDeque;

/**
 * Objeto que representa al personaje del jugador.
 * Created by jose on 14/01/16.
 */
public class MainCharacter extends GameCharacter {

    public enum CharacterState {
        ABSORBING {
            @Override
            public boolean isCancellable() {
                return false;
            }
        }, IDLE {
            @Override
            public boolean isCancellable() {
                return true;
            }
        }, MOVING_FORWARD {
            @Override
            public boolean isCancellable() {
                return true;
            }
        }, MOVING_BACKWARDS {
            @Override
            public boolean isCancellable() {
                return true;
            }
        }, INPUT_A {
            @Override
            public boolean isCancellable() {
                return false;
            }
        }, DYING {
            @Override
            public boolean isCancellable() {
                return false;
            }
        }, DEAD {
            @Override
            public boolean isCancellable() {
                return false;
            }
        }, ADVANCING {
            @Override
            public boolean isCancellable() {
                return false;
            }
        }, STUNNED {
            @Override
            public boolean isCancellable() {
                return false;
            }
        }, ATTACKING {
            @Override
            public boolean isCancellable() {return false; }
        };

        public abstract boolean isCancellable();
    }

    public static class AbsorbingDecoration extends Decoration {

        private int remainingFrames;

        public AbsorbingDecoration(Square size, int frames){
            this.size = size;
            this.remainingFrames = frames;
        }

        @Override
        public void terminate() {
            remainingFrames = 0;
        }

        @Override
        public boolean drawable() {
            return !completed();
        }

        @Override
        public void update(Vector2 backgroundMoveDelta) {
            remainingFrames--;
            size.lenX -= 2;
            size.lenY -= 3;
            size.position.y += 1.5f;
        }

        @Override
        public boolean completed() {
            return remainingFrames <= 0;
        }

        @Override
        public TextureData getSprite() {
            return ABSORBING_SPRITES;
        }
    }


    public final static int FRAMES_TO_GAME_OVER = 65;
    public final static TextureData IDLE_TEXTURE = new TextureData(0.4375f, 0, 0.46875f, 0.09375f);
    //Frames de animacion para caminar
    //0.03125
    public final static TextureData MOVING_A = new TextureData(0.46875f, 0, 0.5f, 0.09375f);
    public final static TextureData MOVING_B = new TextureData(0.5f, 0, 0.53125f, 0.09375f);
    public final static TextureData MOVING_C = new TextureData(0.53125f, 0, 0.5625f, 0.09375f);
    public final static TextureData MOVING_D = new TextureData(0.5625f, 0, 0.59375f, 0.09375f);
    public final static TextureData MOVING_E = new TextureData(0.59375f, 0, 0.625f, 0.09375f);
    //Frames de animacion para el movimiento de absorber
    public final static TextureData STARTUP_MOV_A = new TextureData(0.625f,0.09375f, 0.6875f, 0.1875f);
    public final static TextureData ACTIVE_MOV_A = new TextureData(0.5f,0.09375f, 0.5625f,0.1875f);
    //Frames para el personaje cuando se encuentra stunned
    public final static TextureData STUNNED_SPRITE = TextureDrawer.generarTextureData(14,6,16,9,32);

    private final static TextureData ABSORBING_SPRITES = TextureDrawer.generarTextureData(12,0,14,2,32);
    public static final float INITIAL_POSITION_X = 85;
    public static final int SPRITE_LENGTH = 75;
    public static final int SPRITE_LENGTH_SMALL = 37;
    public static final int CHARACTER_LENGTH = 40;
    public static final int CHARACTER_HEIGHT = 160;
    public final int LENGTH_MOVE_A = CHARACTER_LENGTH + 2;
    public final int HEIGHT_MOVE_A = CHARACTER_HEIGHT;
    private final int STUN_FRAMES = 18;
    private final int INITIAL_HP = 1000;
    private final AnimationData WALKING_ANIMATION = new AnimationData(15, true, new TextureData[]{MOVING_A, MOVING_B, MOVING_C, MOVING_D, MOVING_E});
    private final float MOVING_SPEED = 0.75f;
    private final Vector2 RIGHT_MOVE_SPEED = new Vector2(MOVING_SPEED, 0);
    private final Vector2 LEFT_MOVE_SPEED = new Vector2(-MOVING_SPEED, 0);
    private final Vector2 STUN_SPEED = new Vector2(-2f,0);
    private final FrameCounter absorbingFrames = new FrameCounter(26);
    private final FrameCounter attackStartup = new FrameCounter(10);
    private final FrameCounter attackFrames = new FrameCounter(10);
    public CharacterState state;
    public ColorData colorModifier;
    private int hp;
    private int framesToGameOver;
    private int stunVal;
    private final float playingHeight;
    private final float maxX;
    private final float minX;
    private final CollisionObject[] IDLE_COLLISION_BOXES = {new CollisionObject(new Square(position, LENGTH_MOVE_A, HEIGHT_MOVE_A), CollisionObject.TYPE_HITTABLE)};
    private final CollisionObject[] ATTACK_COLLISION_BOXES = {new CollisionObject(new Square(position, LENGTH_MOVE_A, HEIGHT_MOVE_A), CollisionObject.TYPE_HITTABLE),
            new CollisionObject(new Square(position, LENGTH_MOVE_A, HEIGHT_MOVE_A - 65), CollisionObject.TYPE_ATTACK)};

    public MainCharacter(int id, float playingHeight,float minX, float maxX){
        super(SPRITE_LENGTH_SMALL, CHARACTER_HEIGHT, CHARACTER_LENGTH, CHARACTER_HEIGHT, new Vector2(), id);
        this.state = CharacterState.IDLE;
        this.playingHeight = playingHeight;
        framesToGameOver = FRAMES_TO_GAME_OVER;
        this.color.a = 0;
        this.colorModifier = new ColorData(0.78f,1,0,1);
        this.maxX = maxX;
        this.minX = minX;
        this.hp = INITIAL_HP;
        collisionObjects = IDLE_COLLISION_BOXES;
    }

    /**
     * Funcion que se encarga de recibir el ultimo input del ControllerManager.
     * Solo realiza una accion si el estado en el que se encuentra el personaje
     * puede ser cancelable
     * @param input Ultimo GameInput recibido
     */
    public void receiveInput(ControllerManager.GameInput input){
        if (!state.isCancellable())
            return;

        if(input == ControllerManager.GameInput.INPUT_OFF)
            this.state = CharacterState.IDLE;
        else if(input == ControllerManager.GameInput.RIGHT)
            this.state = CharacterState.MOVING_FORWARD;
        else if(input == ControllerManager.GameInput.LEFT)
            this.state = CharacterState.MOVING_BACKWARDS;
        else if(input == ControllerManager.GameInput.INPUT_A) {
            attackStartup.reset();
            this.state = CharacterState.INPUT_A;
        }

    }

    @Override
    public TextureDrawer.TextureData getCurrentTexture(){
        if(state == CharacterState.MOVING_FORWARD || state == CharacterState.MOVING_BACKWARDS || state == CharacterState.ADVANCING)
            return WALKING_ANIMATION.getCurrentSprite();
        else if (state == CharacterState.STUNNED || state == CharacterState.DYING)
            return STUNNED_SPRITE;
        else if(state == CharacterState.ABSORBING || state == CharacterState.ATTACKING)
            return ACTIVE_MOV_A;
        else if(state == CharacterState.INPUT_A)
            return STARTUP_MOV_A;
        else
            return IDLE_TEXTURE;
    }

    @Override
    public void update(GameCharacter foe, ArrayDeque<Decoration> decorationData) {
        Event e = detectCollision(foe, collisionObjects);

        if (state == CharacterState.IDLE) {
            WALKING_ANIMATION.reset();
        } else if (state == CharacterState.DYING) {
            spriteContainer.lenX = SPRITE_LENGTH;
            if (framesToGameOver == 0)
                state = CharacterState.DEAD;
            else
                framesToGameOver--;

            return;

        } else if (state == CharacterState.STUNNED) {
            spriteContainer.lenX = SPRITE_LENGTH;
            move(STUN_SPEED);
            if (stunVal > 0)
                stunVal--;
            else {
                state = CharacterState.IDLE;
                spriteContainer.lenX = SPRITE_LENGTH_SMALL;
                collisionObjects = IDLE_COLLISION_BOXES;
            }
            //Se sale de la funcion para que esto no disminuya el HP
            return;

        } else if (state == CharacterState.MOVING_FORWARD) {
            if (position.x + MOVING_SPEED < maxX) {
                move(RIGHT_MOVE_SPEED);
                WALKING_ANIMATION.updateFrame();
            }
        } else if (state == CharacterState.MOVING_BACKWARDS) {
            if (position.x - MOVING_SPEED > minX) {
                move(LEFT_MOVE_SPEED);
                WALKING_ANIMATION.updateFrame();
            }
        } else if (state == CharacterState.INPUT_A) {
            spriteContainer.lenX = SPRITE_LENGTH;
            if (attackStartup.completed()) {
                state = CharacterState.ATTACKING;
                attackFrames.reset();
                collisionObjects = ATTACK_COLLISION_BOXES;
            } else
                attackStartup.updateFrame();
        } else if (state == CharacterState.ATTACKING) {
            //Si se detecta colision con el input, significa que absorbio energia
            if (e == Event.HIT) {
                hp = INITIAL_HP;
                state = CharacterState.ABSORBING;
                absorbingFrames.reset();
                decorationData.add(new AbsorbingDecoration(new Square(new Vector2(position).add(45, 29)
                        , spriteContainer.lenX, spriteContainer.lenY), absorbingFrames.totalFrames));
            }

            attackFrames.updateFrame();
            if(attackFrames.completed()){
                this.state = CharacterState.IDLE;
                spriteContainer.lenX = SPRITE_LENGTH_SMALL;
                collisionObjects = IDLE_COLLISION_BOXES;
            }

        } else if (state == CharacterState.ABSORBING){
            absorbingFrames.updateFrame();
            if(absorbingFrames.completed()) {
                state = CharacterState.ADVANCING;
                baseX.set(1,0);
                WALKING_ANIMATION.reset();
                spriteContainer.lenX = SPRITE_LENGTH_SMALL;
                collisionObjects = IDLE_COLLISION_BOXES;
            }
        } else if (state == CharacterState.ADVANCING) {
            //Este estado no debe de provocar que el personaje pierda hp
            WALKING_ANIMATION.updateFrame();
            return;
        } else if (state == CharacterState.DEAD) {
            return;
        }

        if(hp < 0)
            state = CharacterState.DYING;
        else {
            hp -= 1;
            color.a = 1 - (float) hp / INITIAL_HP;
        }

        return;
    }


    public void reset(Vector2 positionOffset){
        relativePosition.set(INITIAL_POSITION_X, playingHeight);
        spriteContainer.lenX = SPRITE_LENGTH_SMALL;
        baseX.set(1,0);
        updatePosition();
        IDLE_COLLISION_BOXES[0].bounds.position.set(position);
        state = CharacterState.IDLE;
        hp = INITIAL_HP;
        framesToGameOver = FRAMES_TO_GAME_OVER;
    }

    @Override
    public boolean completedTransition(){
        return this.state == CharacterState.ADVANCING && this.position.x < INITIAL_POSITION_X;
    }

    /**
     * Accion que sirve para que el personaje principal se caiga. Esto tiene la funcion de crear espacio entre el jugador y
     * el enemigo, tambien para controlar el ritmo de juego.
     */
    /*public void trip(){
        moveBackwards(25);
    }*/


    @Override
    public boolean hittable(){
        //en este momento el personaje principal siempre puede ser golpeado
        return true;
    }

    @Override
    public boolean attacking(){
        return state == CharacterState.INPUT_A;
    }

    @Override
    public boolean alive(){
        return state != CharacterState.DEAD;
    }

    @Override
    public void hit(){
        state = CharacterState.STUNNED;
        stunVal = STUN_FRAMES;
    }
}