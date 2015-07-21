package com.jgame.elements;

import java.util.List;

/**
 * Created by jose on 27/01/15.
 */
public interface EnemyModifier {

    public void detectCollision(List<Enemy> enemies);
}
