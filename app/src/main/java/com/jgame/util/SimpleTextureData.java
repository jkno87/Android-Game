package com.jgame.util;

/**
 * Created by ej-jose on 13/08/15.
 */
public class SimpleTextureData {

    private final float horizontalFrames;
    private final float verticalFrames;

    public SimpleTextureData(float horizontalFrames, float verticalFrames){
        this.horizontalFrames = horizontalFrames;
        this.verticalFrames = verticalFrames;
    }

    /**
     * Genera las coordenadas de textura utilizadas por OpenGL.
     * (X y Y) deben de ser forzosamente menores que horizontal y vertical frames establecidos en el constructor.
     * @param x
     * @param y
     * @return
     */
    public float[] getTextureCoordinates(int x, int y){
        float x1 = x / horizontalFrames;
        float y1 = y / verticalFrames;
        float x2 = (x + 1) / horizontalFrames;
        float y2 = (y + 2) / verticalFrames;

        return new float[]{x1, y2, x2, y2, x2, y1, x1, y1};
    }

}
