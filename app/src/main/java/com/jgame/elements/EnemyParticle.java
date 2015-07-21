package com.jgame.elements;

import com.jgame.game.GameLogic;
import com.jgame.util.AnimationData;
import com.jgame.util.Vector2;

/**
 * Created by jose on 21/04/15.
 */
public class EnemyParticle extends Enemy {

    private Vector2 distance;
    private Enemy parent;

    public EnemyParticle(Enemy parent, int hp, int damage, float size, AnimationData textData, int points, float distance, float rotation){
        super(hp, damage, new Vector2(parent.position), new Vector2(), size, textData, points);
        this.distance = new Vector2(distance, 0);
        position.add(this.distance.rotate(rotation));
        this.parent = parent;
    }

    public void update(GameLogic gameInstance, float timeDifference){
        super.update(gameInstance, timeDifference);
    }

    public void updateParent(Vector2 parentPosition){
        distance.rotate(0.75f);
        position = new Vector2(parentPosition).add(distance);
    }

    public boolean vivo(){
        return hp > 0 && parent.vivo();
    }

}
