package com.jgame.elements;

import java.util.List;

import com.jgame.game.GameLogic;
import com.jgame.util.AnimationData;
import com.jgame.util.TimeCounter;
import com.jgame.util.Vector2;

public class EnemyAccelerator extends Enemy {

    private static final int DAMAGE = 0;
    private static final float SHOOTING_DISTANCE = 55f;
    private final float speed;
    private boolean availableAmmo;
    //private final TimeCounter animationFrame;

    public EnemyAccelerator(int hp, Vector2 position, float size, float speed, int points) {
        super(hp, DAMAGE, position, new Vector2(1,0), size, new AnimationData(2, 0.5f, 0, 0, 0.5f), points);
        this.speed = speed;
        availableAmmo = true;
        //animationFrame = new TimeCounter(0.166f);
    }

    @Override
    public void updateDeprecated(GameLogic gameInstance, float timeDiff) {
        //animationFrame.accum(timeDiff);
        int i = 0;
        while(i < gameInstance.enemies.size()){
            Enemy e = gameInstance.enemies.get(i);
            if(!e.equals(this)){
                direction.nor().add(new Vector2(e.position).sub(position).nor().mul(0.075f)).nor();
                direction.mul(speed);
					if(availableAmmo && position.dist(e.position) < SHOOTING_DISTANCE){
                        //gameInstance.addProjectile(new );
						//gameInstance.createSpeederProjectile(new Vector2(position).sub(new Vector2(0, size * 2)), new Vector2(e.position).sub(position).nor());
                        availableAmmo = false;
					}

                break;
            }
            i++;
        }
        position.add(direction);
        textData.updateFrame(timeDiff);
    }
}