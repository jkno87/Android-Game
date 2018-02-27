package com.jgame.elements;

import com.jgame.util.CollisionObject;
import com.jgame.util.Decoration;
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
    private final Vector2 PROJECTILE_SPEED = new Vector2(2,0);
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


    public ProjectileEnemy(){
        super(new Square(new Vector2(), IDLE_SIZE_X, IDLE_SIZE_Y));
    }

    @Override
    public void update(GameCharacter foe, ArrayDeque<Decoration> decorationData) {

    }

    @Override
    public boolean completedTransition() {
        return false;
    }

    @Override
    public void reset(Vector2 positionOffset) {

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
        if(coArtifact.equals(target) && !projectileLaunched) {
            projectileLaunched = true;
            projectilePosition.set(artifactPosition);
        }
    }
}
