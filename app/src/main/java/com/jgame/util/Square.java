package com.jgame.util;

public class Square extends GeometricElement {
    public static float TO_RADIANS = (1 / 180.0f) * (float) Math.PI;

    public Vector2 position;
    public float lenX;
    public float lenY;
    public float angle;

    public Square(float x, float y, float lenX, float lenY){
        position = new Vector2(x, y);
        this.lenX = lenX;
        this.lenY = lenY;
        angle = 0;
    }

    public Square(Vector2 position, float lenX, float lenY, float angle){
        this.position = position;
        this.lenX = lenX;
        this.lenY = lenY;
        this.angle = angle;
    }

    public void scale(float m){
        this.lenX *= m;
        this.lenY *= m;
    }

    @Override
    public Vector2 getPosition() {
        return position;
    }

    @Override
    public void setPosition(Vector2 v){
        this.position.set(v);
    }

    @Override
    public boolean contains(float x, float y){
        return x < position.x + lenX && x > position.x
                && y < position.y + lenY && y > position.y;
    }

    @Override
    public boolean collides(GeometricElement e) {
        if(e instanceof Square){
            Square s2 = (Square) e;
            return position.x < s2.position.x + s2.lenX && position.x + lenX > s2.position.x
                    && position.y < s2.position.y + s2.lenY && position.y + lenY > s2.position.y;

        }
        return false;
    }
}