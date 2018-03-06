package com.jgame.elements;

import com.jgame.util.CollisionObject;
import com.jgame.util.Decoration;
import com.jgame.util.FrameCounter;
import com.jgame.util.Square;
import com.jgame.util.TextureDrawer;
import com.jgame.util.Vector2;

import java.util.ArrayDeque;
import java.util.Random;

/**
 * Created by jose on 27/02/18.
 */

public class ProjectileEnemy extends GameCharacter {

    private final static TextureDrawer.TextureData IDLE_SPRITE = new TextureDrawer.TextureData(0.4375f, 0, 0.46875f, 0.09375f);
    private final static float IDLE_SIZE_X = 50;
    private final static float IDLE_SIZE_Y = 100;
    private final float PROJECTILE_WIDTH = 50;
    private final byte ID_TOOL = 0;
    private final byte ID_ENEMY = 1;
    private final byte ID_PROJECTILE = 2;
    private final byte ID_USER_PROJECTILE = 3;
    private final byte INITIAL_HP = 5;
    private final Vector2 HIDE_POSITION = new Vector2();
    private final Vector2 PROJECTILE_SPEED = new Vector2(-4.75f,10);
    private final Vector2 USER_PROJECTILE_SPEED = new Vector2(2,0);
    private final Vector2 INITIAL_POSITION = new Vector2(450,0);
    private final Vector2 GRAVITY_MAGNITUDE = new Vector2(0, -0.25f);
    private final Vector2 ARTIFACT_INITIAL_OFFSET = new Vector2(-300,0);
    private final Vector2 artifactPosition = new Vector2();
    private final Vector2 projectilePosition = new Vector2();
    private final Vector2 userProjectilePosition = new Vector2();
    private final CollisionObject.IdCollisionObject coArtifact = new CollisionObject.IdCollisionObject(new Square(artifactPosition, 65, 50),
            CollisionObject.TYPE_HITTABLE, ID_TOOL);
    private final CollisionObject.IdCollisionObject coProjectile = new CollisionObject.IdCollisionObject(new Square(projectilePosition, PROJECTILE_WIDTH, 50),
            CollisionObject.TYPE_MIXED, ID_PROJECTILE);
    private final CollisionObject.IdCollisionObject coUserProjectile = new CollisionObject.IdCollisionObject(new Square(userProjectilePosition, 50, 50),
            CollisionObject.TYPE_ATTACK, ID_USER_PROJECTILE);
    private final Vector2 currentProjectileSpeed = new Vector2();
    private byte hp;
    private boolean projectileLaunched;
    private boolean userProjectileLaunched;
    private FrameCounter idleInterval;
    private FrameCounter attackStartup;
    private State currentState;
    private Random random;

    private enum State {
        ATTACK_A, ATTACK_B, IDLE, DEAD
    }

    public ProjectileEnemy(){
        super(new Square(new Vector2(), IDLE_SIZE_X, IDLE_SIZE_Y));
        attackStartup = new FrameCounter(20);
        idleInterval = new FrameCounter(78);
        collisionObjects = new CollisionObject[]{coArtifact, coProjectile, coUserProjectile};
        baseX.x = -1;
        random = new Random();
    }

    @Override
    public void update(GameCharacter foe, ArrayDeque<Decoration> decorationData) {

        if(!projectileLaunched && currentState == State.IDLE) {
            idleInterval.updateFrame();
            if(idleInterval.completed()){
                currentState = random.nextInt(5) > 3 ? State.ATTACK_B : State.ATTACK_A;
                if(currentState == State.ATTACK_A){
                    color.g = 0;
                    color.b = 0;
                } else {
                    color.r = 0;
                    color.g = 0;
                }
                attackStartup.reset();
            }
        } else if(currentState == State.ATTACK_A){
            attackStartup.updateFrame();
            if(attackStartup.completed()){
                color.g = 1;
                color.b = 1;
                projectilePosition.set(position);
                projectileLaunched = true;
                currentProjectileSpeed.set(PROJECTILE_SPEED);
                currentState = State.IDLE;
                idleInterval.reset();
            }
        } else if(currentState == State.ATTACK_B){
            attackStartup.updateFrame();
            if(attackStartup.completed()){
                color.r = 1;
                color.g = 1;
                currentState = State.IDLE;
                idleInterval.reset();
            }
        }

        if(userProjectileLaunched) {
            userProjectilePosition.add(USER_PROJECTILE_SPEED);
            if(projectileLaunched && userProjectilePosition.x >= projectilePosition.x - PROJECTILE_WIDTH){
                userProjectilePosition.set(HIDE_POSITION);
                projectilePosition.set(HIDE_POSITION);
                userProjectileLaunched = false;
                projectileLaunched = false;
            }

            else if(userProjectilePosition.x > position.x) {
                userProjectilePosition.set(HIDE_POSITION);
                userProjectileLaunched = false;
                hp--;
                if(hp == 0)
                    currentState = State.DEAD;
            }
        }

        if(projectileLaunched) {
            currentProjectileSpeed.add(GRAVITY_MAGNITUDE);
            projectilePosition.add(currentProjectileSpeed);
            if(projectilePosition.x - PROJECTILE_WIDTH < foe.position.x){
                projectilePosition.set(HIDE_POSITION);
                projectileLaunched = false;
                foe.hit(coProjectile);
            }
        }
    }

    @Override
    public boolean completedTransition() {
        return false;
    }

    @Override
    public void reset(Vector2 positionOffset) {
        projectileLaunched = false;
        userProjectileLaunched = false;
        idleInterval.reset();
        moveTo(positionOffset, INITIAL_POSITION);
        artifactPosition.set(position);
        artifactPosition.add(ARTIFACT_INITIAL_OFFSET);
        userProjectilePosition.set(HIDE_POSITION);
        currentState = State.IDLE;
        hp = INITIAL_HP;
    }

    @Override
    public boolean hittable() {
        return true;
    }

    @Override
    public boolean alive() {
        return currentState != State.DEAD;
    }

    @Override
    public TextureDrawer.TextureData getCurrentTexture() {
        return IDLE_SPRITE;
    }

    @Override
    public void hit(CollisionObject target) {
        if(coArtifact.equals(target) && !userProjectileLaunched) {
            userProjectileLaunched = true;
            userProjectilePosition.set(artifactPosition);
        }
    }
}
