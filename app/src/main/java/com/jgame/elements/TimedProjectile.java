package com.jgame.elements;

import com.jgame.game.GameLogic;
import com.jgame.util.Vector2;

/**
 * Created by jose on 28/05/15.
 */
public class TimedProjectile extends SimpleProjectile {

    private float activeFrames;

    public TimedProjectile(Vector2 position, Vector2 direction, float speed, float size, float activeFrames) {
        super(position, direction, speed, size);
        this.activeFrames = activeFrames;
    }

    @Override
    public void updateDeprecated(GameLogic gameInstance, float timeDiff){
        super.updateDeprecated(gameInstance, timeDiff);
        activeFrames -= timeDiff;
    }

    @Override
    public boolean vivo(){
        return vivo && activeFrames > 0;
    }
}
