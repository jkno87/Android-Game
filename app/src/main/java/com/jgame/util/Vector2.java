package com.jgame.util;

import android.util.FloatMath;

public class Vector2 {

    public static float TO_RADIANS = (1 / 180.0f) * (float) Math.PI;
    public static float TO_DEGREES = (1 / (float) Math.PI) * 180;
    public float x,y;

    public static class RotationMatrix {
        public final float cos;
        public final float sin;

        public RotationMatrix(float angle){
            float rad = angle * TO_RADIANS;
            cos = FloatMath.cos(rad);
            sin = FloatMath.sin(rad);
        }

    }

    public Vector2(){

    }

    public Vector2(float x, float y){
        this.x = x;
        this.y = y;
    }

    public Vector2(Vector2 other){
        this.x = other.x;
        this.y = other.y;
    }

    public Vector2 cpy(){
        return new Vector2(x, y);
    }

    public Vector2 set(float x, float y){
        this.x = x;
        this.y = y;
        return this;
    }

    public Vector2 set(Vector2 other){
        this.x = other.x;
        this.y = other.y;
        return this;
    }

    public Vector2 add(float x, float y){
        this.x += x;
        this.y += y;
        return this;
    }

    public Vector2 add(Vector2 other){
        this.x += other.x;
        this.y += other.y;
        return this;
    }

    public Vector2 sub(float x, float y){
        this.x -= x;
        this.y -= y;
        return this;
    }

    public Vector2 sub(Vector2 other){
        this.x -= other.x;
        this.y -= other.y;
        return this;
    }

    public Vector2 mul(float scalar){
        this.x *= scalar;
        this.y *= scalar;
        return this;
    }

    public float len(){
        return FloatMath.sqrt(x * x + y * y);
    }

    public Vector2 nor(){
        float len = len();
        if(len != 0){
            this.x /= len;
            this.y /= len;
        }
        return this;
    }

    public float angle(){
        float angle = (float) Math.atan2(y, x) * TO_DEGREES;
        if (angle < 0)
            angle += 360;
        return angle;
    }

    public Vector2 rotate(RotationMatrix rm){
        float newX = this.x * rm.cos - this.y * rm.sin;
        float newY = this.x * rm.sin + this.y * rm.cos;
        this.x = newX;
        this.y = newY;

        return this;
    }

    /**
     * Cambia la base del vector utilizando b. Genera un vector ortogonal en base a b.
     * @param b
     * @return regresa el mismo vector con la base diferente.
     */
    public Vector2 changeBase(Vector2 b){
        float nX = x * b.x - y * b.y;
        float nY = x * b.y + y * b.x;
        this.x = nX;
        this.y = nY;

        return this;
    }

    /**
     * Cambia la base de los vector utilizando las bases a, b. Si los vectores no son ortogonales, buena suerte ;)
     * @param a
     * @param b
     * @return Regresa el vector con la base ab.
     */
    public Vector2 changeBase(Vector2 a, Vector2 b){
        float nX = x * a.x + y * b.x;
        float nY = x * a.y + y * b.y;

        this.x = nX;
        this.y = nY;

        return this;
    }

    /**
     * Multiplica el vector con la matriz de rotacion rm el numero de veces times.
     * @param rm matriz de rotacion
     * @param times veces que se va a rotar
     * @return vector con la transformacion aplicada
     */
    public Vector2 rotate(RotationMatrix rm, int times){
        for(int i = 0; i < times; i++)
            this.rotate(rm);
        return this;
    }

    public float dist(float x, float y){
        float distX = this.x - x;
        float distY = this.y - y;
        return FloatMath.sqrt(distX * distX + distY * distY);
    }

    public float dist(Vector2 other){
        float distX = this.x - other.x;
        float distY = this.y - other.y;
        return FloatMath.sqrt(distX * distX + distY * distY);
    }

    public static float angleBetween(Vector2 v1, Vector2 v2){
        float dot = v1.x * v2.x + v1.y * v2.y;
        float mags = v1.len() * v2.len();

        return (float) Math.acos(dot / mags);
    }

    @Override
    public String toString(){
        return "[" + x + "," + y + "]";
    }

}