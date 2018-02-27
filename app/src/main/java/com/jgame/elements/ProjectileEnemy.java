package com.jgame.elements;

import com.jgame.util.CollisionObject;
import com.jgame.util.Decoration;
import com.jgame.util.FrameCounter;
import com.jgame.util.Square;
import com.jgame.util.TextureDrawer;
import com.jgame.util.Vector2;

import java.util.ArrayDeque;

/**
 * Created by jose on 27/02/18.
 */

public class ProjectileEnemy extends GameCharacter {

    private final static TextureDrawer.TextureData IDLE_SPRITE = new TextureDrawer.TextureData(0.4375f, 0, 0.46875f, 0.09375f);
    private final static float IDLE_SIZE_X = 50;
    private final static float IDLE_SIZE_Y = 100;
    private final byte ID_TOOL = 0;
    private final byte ID_ENEMY = 1;
    private final byte ID_PROJECTILE = 2;
    private final byte ID_USER_PROJECTILE = 3;
    private final Vector2 HIDE_POSITION = new Vector2();
    private final Vector2 PROJECTILE_SPEED = new Vector2(-2,0);
    private final Vector2 USER_PROJECTILE_SPEED = new Vector2(2,0);
    private final Vector2 INITIAL_POSITION = new Vector2(450,0);
    private final Vector2 ARTIFACT_INITIAL_OFFSET = new Vector2(300,0);
    private final Vector2 artifactPosition = new Vector2();
    private final Vector2 projectilePosition = new Vector2();
    private final Vector2 userProjectilePosition = new Vector2();
    private final CollisionObject.IdCollisionObject coArtifact = new CollisionObject.IdCollisionObject(new Square(artifactPosition, 65, 50),
            CollisionObject.TYPE_HITTABLE, ID_TOOL);
    private final CollisionObject.IdCollisionObject coProjectile = new CollisionObject.IdCollisionObject(new Square(projectilePosition, 50, 50),
            CollisionObject.TYPE_MIXED, ID_PROJECTILE);
    private final CollisionObject.IdCollisionObject coUserProjectile = new CollisionObject.IdCollisionObject(new Square(userProjectilePosition, 50, 50),
            CollisionObject.TYPE_ATTACK, ID_USER_PROJECTILE);
    private boolean projectileLaunched;
    private boolean userProjectileLaunched;
    private FrameCounter projectileInterval;

    public ProjectileEnemy(){
        super(new Square(new Vector2(), IDLE_SIZE_X, IDLE_SIZE_Y));
        projectileInterval = new FrameCounter(500);
        collisionObjects = new CollisionObject[]{coArtifact, coProjectile, coUserProjectile};
        baseX.x = -1;
    }

    @Override
    public void update(GameCharacter foe, ArrayDeque<Decoration> decorationData) {
        projectileInterval.updateFrame();
        if(projectileInterval.completed()){
            projectilePosition.set(position);
            projectileLaunched = true;
        }

        if(userProjectileLaunched) {
            userProjectilePosition.add(USER_PROJECTILE_SPEED);
            if(userProjectilePosition.x >= projectilePosition.x){
                userProjectilePosition.set(HIDE_POSITION);
                projectilePosition.set(HIDE_POSITION);
                userProjectileLaunched = false;
                projectileLaunched = false;
                projectileInterval.reset();
            }

            else if(userProjectilePosition.x > position.x) {
                color.a = 0;
                userProjectilePosition.set(HIDE_POSITION);
                userProjectileLaunched = false;
            }
        }
        if(projectileLaunched) {
            projectilePosition.add(PROJECTILE_SPEED);
            if(projectilePosition.x < foe.position.x){
                projectileInterval.reset();
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
        projectileInterval.reset();
        moveTo(positionOffset, INITIAL_POSITION);
        artifactPosition.set(position);
        artifactPosition.add(ARTIFACT_INITIAL_OFFSET);
    }

    @Override
    public boolean hittable() {
        return true;
    }

    @Override
    public boolean alive() {
        return true;
    }

    @Override
    public TextureDrawer.TextureData getCurrentTexture() {
        return IDLE_SPRITE;
    }

    @Override
    public void hit(CollisionObject target) {
        if(coArtifact.equals(target) && !userProjectileLaunched) {
            userProjectileLaunched = true;
            projectilePosition.set(artifactPosition);
        }
    }
}
