package com.jgame.elements;

import java.util.ArrayList;
import java.util.List;

import com.jgame.game.GameLogic;
import com.jgame.util.AnimationData;
import com.jgame.util.TextureData;
import com.jgame.util.Vector2;

public class Enemy implements GameElement {

    public static class StunInfo {
        public final float time;
        public final float force;

        public StunInfo(float time, float force){
            this.time = time;
            this.force = force;
        }
    }

    private final static float DEFAULT_STUN_TIME = 0.1f;
    private final static float DEFAULT_PUSH = 8;
    public static StunInfo DEFAULT_STUN = new StunInfo(DEFAULT_STUN_TIME, DEFAULT_PUSH);
    private final static int HIT_FRAMES = 4;


    public int hp;
    public int damage;
    public Vector2 position;
    public Vector2 direction;
    public float size;
    public AnimationData textData;
    public float[] color;
    public StunInfo stunInfo;
    //TODO: Son los frames en los que se cambia el color cuando se golpea al enemigo
    public int hitFrames;
    public int points;

    public Enemy(int hp, int damage, Vector2 position, Vector2 direction, float size, AnimationData textData, int points){
        this.hp = hp;
        this.damage = damage;
        this.position = position;
        this.direction = direction;
        this.size = size;
        this.textData = textData;
        this.color = DEFAULT_COLOR;
        this.stunInfo = DEFAULT_STUN;
        this.points = points;
    }

    public void hit(){
        hitFrames = HIT_FRAMES;
    }

    public void update(GameLogic gameInstance, float timeDifference){
        if(hitFrames > 0)
            hitFrames--;
    }

    public boolean vivo(){
        return hp > 0;
    }

    public Vector2 getPosition(){
        return position;
    }

    public int getPoints(){
        return points;
    }

}