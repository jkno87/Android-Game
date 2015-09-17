package com.jgame.elements;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import com.jgame.game.GameLogic;
import com.jgame.util.AnimationData;
import com.jgame.util.TextureData;
import com.jgame.util.TimeCounter;
import com.jgame.util.Vector2;

public class SimpleEnemy extends Enemy {


    private List<EnemyParticle> subordinates;

    public static class SnakeBody extends Enemy {

        //SnakeBody descendant;
        SnakeBody parent;
        boolean positionChanged;
        float speed;

        public SnakeBody(int hp, int damage, Vector2 position, float size,AnimationData textData, int points, SnakeBody parent){
            super(hp, damage, position, new Vector2(), size, textData, points);
            this.parent = parent;
            if(this.parent != null) {
                this.speed = parent.speed;
                directTowardsParent();
            }
        }

        private void directTowardsParent(){
            direction = new Vector2(parent.position).sub(position).nor().mul(speed);
        }

        public void update(GameLogic gameInstance, float timeDifference){
            super.updateDeprecated(gameInstance, timeDifference);
            if(parent != null && parent.positionChanged) {
                directTowardsParent();
                positionChanged = true;
            }
            position.add(direction);
        }
    }

    public static class SnakeHead extends SnakeBody {
        private float xDelta = 0.75f;
        private TimeCounter counter;

        public SnakeHead(int hp, int damage, Vector2 position, Vector2 direction, float speed,float size,AnimationData textData, int points){
            super(hp, damage, position, size, textData, points, null);
            counter = new TimeCounter(0.5f);
            this.speed = speed;
            this.direction = direction.mul(this.speed);
        }


        public void update(GameLogic gameInstance, float timeDifference){
            super.update(gameInstance, timeDifference);
            counter.accum(timeDifference);
            direction.rotate(xDelta);

            if(Math.abs(direction.x) > 2 * Math.abs(direction.y))
                xDelta *= -1;

            if(counter.completed()) {
                this.positionChanged = true;
                counter.reset();
            } else
                this.positionChanged = false;

        }

        @Override
        public void hit(){
            super.hit();
            if(!vivo())
                this.positionChanged = false;
        }
    }


    public SimpleEnemy(int hp, int damage, Vector2 position, float size, Vector2 speed, int points) {
        super(hp, damage, position, speed, size, new AnimationData(1, 0.5f, 0, 0.5f, 0), points);
        subordinates = new ArrayList<EnemyParticle>();
    }

    @Override
    public void updateDeprecated(GameLogic gameInstance, float timeDiff){
        super.updateDeprecated(gameInstance, timeDiff);
        position.add(direction);
        for(EnemyParticle e : subordinates){
            e.updateParent(position);
        }
    }

    @Override
    public void update(List<GameElement> others, float timeDifference){

    }

    public void addSubordinate(EnemyParticle sub){
        subordinates.add(sub);
    }

    public int getPoints(){
        int total = points;
        for(EnemyParticle e : subordinates)
            total += e.getPoints();
        return total;
    }

}