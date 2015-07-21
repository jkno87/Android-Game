package com.jgame.elements;

import java.util.ArrayList;
import java.util.List;
import com.jgame.elements.Enemy.StunInfo;
import com.jgame.util.TimeCounter;
import com.jgame.util.Vector2;
import com.jgame.definitions.GameLevels.EnemyFactory;

public class EnemySpawner {

    public static abstract class PositionGenerator {
        public abstract List<Vector2> getPositions();
    }

    public static class SpawnElement {
        private int remEnemies;
        private EnemyFactory factory;
        private final TimeCounter counter;
        private PositionGenerator genPosition;

        public SpawnElement(PositionGenerator genPosition, EnemyFactory factory, float interval, int numEnemies){
            this.remEnemies = numEnemies;
            this.genPosition = genPosition;
            this.factory = factory;
            counter = new TimeCounter(interval);
        }

        public List<Enemy> spawn(float interval){
            ArrayList<Enemy> enemies = new ArrayList<Enemy>();
            if(remEnemies <= 0)
                return enemies;

            counter.accum(interval);
            if(counter.completed()){
                remEnemies--;
                counter.reset();
                for(Vector2 position : genPosition.getPositions()) {
                    enemies.addAll(factory.produce(position));
                }
            }
            return enemies;
        }
    }

    private SpawnElement[] fragments;

    public EnemySpawner(SpawnElement[] fragments){
       this.fragments = fragments;
    }

    public List<Enemy> spawnWave(float interval){
        List<Enemy> enemies = new ArrayList<Enemy>();
        for(SpawnElement s : fragments)
            enemies.addAll(s.spawn(interval));

        return enemies;
    }

    /**
     * Boolean que determina si el elemento del nivel tiene enemigos por generar.
     * @return
     */
    public boolean enemiesRemaining() {
        for(SpawnElement s : fragments)
            if(s.remEnemies > 0)
                return true;

        return false;
    }
}