package com.jgame.util;

import android.util.FloatMath;

public class Square {
    public static float TO_RADIANS = (1 / 180.0f) * (float) Math.PI;

    public Vector2 position;
    public final float lenX;
    public final float lenY;
    public float angle;

    public Square(float x, float y, float lenX, float lenY){
        position = new Vector2(x, y);
        this.lenX = lenX;
        this.lenY = lenY;
        angle = 0;
    }

    public Square(float x, float y, float lenX, float lenY, float angle){
        position = new Vector2(x, y);
        this.lenX = lenX;
        this.lenY = lenY;
        this.angle = angle;
    }

    public boolean within(float x, float y){
        return x <= position.x + lenX && x >= position.x - lenX
                && y <= position.y + lenY && y >= position.y - lenY;
    }


    public static float[] getSimpleCoords(float x, float y, float sizeX, float sizeY, float[] colors){
        return new float[]{x - sizeX, y - sizeY, colors[0], colors[1], colors[2], colors[3],
                           x + sizeX, y - sizeY, colors[0], colors[1], colors[2], colors[3],
                           x + sizeX, y + sizeY, colors[0], colors[1], colors[2], colors[3],
                           x - sizeX, y + sizeY, colors[0], colors[1], colors[2], colors[3]};
    }

    public float[] getSimpleCoords(float [] colors){
        return new float[]{position.x - lenX, position.y - lenY, colors[0], colors[1], colors[2], colors[3],
                position.x + lenX, position.y - lenY, colors[0], colors[1], colors[2], colors[3],
                position.x + lenX, position.y + lenY, colors[0], colors[1], colors[2], colors[3],
                position.x - lenX, position.y + lenY, colors[0], colors[1], colors[2], colors[3]};
    }

    public float[] getDrawCoords(){
        float cos = FloatMath.cos(angle * TO_RADIANS);
        float sin = FloatMath.sin(angle * TO_RADIANS);

        float oX = lenX;
        float oY = lenY;

        float x1 = position.x + (-oX * cos + oY * sin);
        float y1 = position.y + (-oX * sin - oY * cos);
        float x2 = position.x + (oX * cos + oY * sin);
        float y2 = position.y + (oX * sin - oY * cos);
        float x3 = position.x + (oX * cos - oY * sin);
        float y3 = position.y + (oX * sin + oY * cos);
        float x4 = position.x + (-oX * cos - oY * sin);
        float y4 = position.y + (-oX * sin + oY * cos);

        return new float[] {x1, y1, x2, y2, x3, y3, x4, y4};
    }

    public float[] getTextureCoords(TextureData tData){
        return getTextureCoords(tData.getTextCoords());
    }

    public float[] getTextureCoords(float[] textureIndexes){
        float cos = FloatMath.cos(angle * TO_RADIANS);
        float sin = FloatMath.sin(angle * TO_RADIANS);

        float oX = lenX;
        float oY = lenY;

        float x1 = position.x + (-oX * cos + oY * sin);
        float y1 = position.y + (-oX * sin - oY * cos);
        float x2 = position.x + (oX * cos + oY * sin);
        float y2 = position.y + (oX * sin - oY * cos);
        float x3 = position.x + (oX * cos - oY * sin);
        float y3 = position.y + (oX * sin + oY * cos);
        float x4 = position.x + (-oX * cos - oY * sin);
        float y4 = position.y + (-oX * sin + oY * cos);

        return new float[] { x1, y1, textureIndexes[0], textureIndexes[1],
                x2, y2, textureIndexes[2], textureIndexes[3],
                x3, y3, textureIndexes[4], textureIndexes[5],
                x4, y4, textureIndexes[6], textureIndexes[7]};
    }


    public float[] getTextureColorCoords(TextureData tData, float[] colors){
        float[] coords = getTextureCoords(tData);

        return new float[] { coords[0], coords[1], coords[2], coords[3], colors[0], colors[1], colors[2], colors[3],
                coords[4], coords[5], coords[6], coords[7], colors[0], colors[1], colors[2], colors[3],
                coords[8], coords[9], coords[10], coords[11], colors[0], colors[1], colors[2], colors[3],
                coords[12], coords[13], coords[14], coords[15], colors[0], colors[1], colors[2], colors[3]};
    }

    public float[] getTextureColorCoords(float[] tData, float[] colors){
        float[] coords = getTextureCoords(tData);

        return new float[] { coords[0], coords[1], coords[2], coords[3], colors[0], colors[1], colors[2], colors[3],
                coords[4], coords[5], coords[6], coords[7], colors[0], colors[1], colors[2], colors[3],
                coords[8], coords[9], coords[10], coords[11], colors[0], colors[1], colors[2], colors[3],
                coords[12], coords[13], coords[14], coords[15], colors[0], colors[1], colors[2], colors[3]};
    }


}