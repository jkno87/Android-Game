package com.jgame.elements;

import com.jgame.util.Square;
import com.jgame.util.TextureData;
import com.jgame.util.Vector2;

import java.util.List;

/**
 * Created by jose on 24/02/15.
 */
public class Decoration {

    private float x;
    private float y;
    private float size;
    private float timeAlive;
    private float angle;

    public TextureData textureData;
    public final static float[] colorData = new float[]{1,1,1,0.65f};

    public Decoration(Vector2 position, float size, float timeAlive){
        this.x = position.x;
        this.y = position.y;
        this.size = size;
        this.timeAlive = timeAlive;
        textureData = TextureData.USE_WHOLE_IMAGE;
        angle = 0;
    }

    public Vector2 getPosition(){
        return new Vector2();
    }

    public float getSize(){
        return 0.0f;
    }

    public void update(List<GameElement> others, float timeDifference){

    }

    public void interact(GameElement e){

    }

    public boolean vivo() {
        return timeAlive > 0;
    }

    public int getId(){
        return 0;
    }

    /*public Square getDrawSquare(){
        return new Square(x, y, size, size, angle);
    }*/

    public float getPctAlive(){
        return 0;
    }
}
