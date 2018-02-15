package com.jgame.elements;

import com.jgame.util.CollisionObject;
import com.jgame.util.Decoration;
import com.jgame.util.Square;
import com.jgame.util.TextureDrawer;
import com.jgame.util.Vector2;

import java.util.ArrayDeque;

/**
 * Created by Jose on 13/02/2018.
 */

public class EatingEnemy extends GameCharacter {

    private final byte ID_TOOL = 0;
    private final byte ID_ENEMY = 1;
    private final byte ID_PROJECTILE = 2;
    private final static float IDLE_SIZE = 50;
    private final static TextureDrawer.TextureData IDLE_SPRITE = new TextureDrawer.TextureData(0.4375f, 0, 0.46875f, 0.09375f);
    private final static Vector2 INITIAL_POSITION = new Vector2(425, 0);
    private final Vector2 artifactPosition = new Vector2();
    private final Vector2 hitboxPosition = new Vector2();
    private final Vector2 projectilePosition = new Vector2();
    private final CollisionObject.IdCollisionObject coArtifact = new CollisionObject.IdCollisionObject(new Square(artifactPosition, 50, 50),
            CollisionObject.TYPE_HITTABLE, ID_TOOL);
    private final CollisionObject.IdCollisionObject coCharacter = new CollisionObject.IdCollisionObject(new Square(hitboxPosition, 50, 50),
            CollisionObject.TYPE_HITTABLE, ID_ENEMY);
    private final CollisionObject.IdCollisionObject coProjectile = new CollisionObject.IdCollisionObject(new Square(projectilePosition, 50, 50),
            CollisionObject.TYPE_MIXED, ID_PROJECTILE);

    public EatingEnemy() {
        super(new Square(new Vector2(), IDLE_SIZE, IDLE_SIZE));
        idleSizeX = IDLE_SIZE;
        baseX.x = -1;
        collisionObjects = new CollisionObject[]{coArtifact, coCharacter, coProjectile};
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
        moveTo(positionOffset, INITIAL_POSITION);
        hitboxPosition.set(position);
        artifactPosition.set(position);
        artifactPosition.add(-200, 0);
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
        if(coArtifact.equals(target)){
            color.b = 0.5f;
        } else if(coCharacter.equals(target)){
            color.b = 1;
        }
    }
}
