package com.jgame.util;

/**
 * Created by jose on 7/01/16.
 */
public class SimpleDrawer extends Drawer {

    public static class ColorData {
        public final float r;
        public final float g;
        public final float b;
        public final float a;

        public ColorData(float r, float g, float b, float a){
            this.r = r;
            this.g = g;
            this.b = b;
            this.a = a;
        }
    }

    private final static int MAX_ELEMENTS = 100;

    public SimpleDrawer(boolean withColor){
        super(false, withColor, MAX_ELEMENTS);
    }

    /**
     * Agrega los vertices de un cuadrado con textura y color
     * @param x coordenada X del centro del cuadrado
     * @param y coordenada Y del centro de cuadrado
     * @param lenX longitud del rect en el eje X
     * @param lenY longitud del rect en el eje Y
     * @param cData Informacion de color que se utilizara
     * @return Drawer que contiene los vertices del cuadrado
     */
    public void addColoredRectangle(float x, float y, float lenX, float lenY, ColorData cData){
        elementsAdded++;

        float x1 = x - lenX;
        float x2 = x + lenX;
        float y1 = y - lenY;
        float y2 = y + lenY;

        verticesBuffer[currentIndex++] = x1;
        verticesBuffer[currentIndex++] = y1;
        verticesBuffer[currentIndex++] = cData.r;
        verticesBuffer[currentIndex++] = cData.g;
        verticesBuffer[currentIndex++] = cData.b;
        verticesBuffer[currentIndex++] = cData.a;

        verticesBuffer[currentIndex++] = x2;
        verticesBuffer[currentIndex++] = y1;
        verticesBuffer[currentIndex++] = cData.r;
        verticesBuffer[currentIndex++] = cData.g;
        verticesBuffer[currentIndex++] = cData.b;
        verticesBuffer[currentIndex++] = cData.a;

        verticesBuffer[currentIndex++] = x2;
        verticesBuffer[currentIndex++] = y2;
        verticesBuffer[currentIndex++] = cData.r;
        verticesBuffer[currentIndex++] = cData.g;
        verticesBuffer[currentIndex++] = cData.b;
        verticesBuffer[currentIndex++] = cData.a;

        verticesBuffer[currentIndex++] = x1;
        verticesBuffer[currentIndex++] = y2;
        verticesBuffer[currentIndex++] = cData.r;
        verticesBuffer[currentIndex++] = cData.g;
        verticesBuffer[currentIndex++] = cData.b;
        verticesBuffer[currentIndex++] = cData.a;

    }
}
