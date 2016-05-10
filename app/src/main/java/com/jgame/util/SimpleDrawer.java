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

        float x2 = x + lenX;
        float y2 = y + lenY;

        verticesBuffer[currentIndex++] = x;
        verticesBuffer[currentIndex++] = y;
        verticesBuffer[currentIndex++] = cData.r;
        verticesBuffer[currentIndex++] = cData.g;
        verticesBuffer[currentIndex++] = cData.b;
        verticesBuffer[currentIndex++] = cData.a;

        verticesBuffer[currentIndex++] = x2;
        verticesBuffer[currentIndex++] = y;
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

        verticesBuffer[currentIndex++] = x;
        verticesBuffer[currentIndex++] = y2;
        verticesBuffer[currentIndex++] = cData.r;
        verticesBuffer[currentIndex++] = cData.g;
        verticesBuffer[currentIndex++] = cData.b;
        verticesBuffer[currentIndex++] = cData.a;

    }

    /**
     * Agrega los vertices de un cuadrado con textura y color
     * @param cData Informacion de color que se utilizara
     * @param offset Es un vector que modifica la posicion de Square s.
     *               Esto se utiliza porque normalmente la posicion en el juego es diferente de la posicion en la que se dibuja en pantalla
     * @return Drawer que contiene los vertices del cuadrado
     */
    public void addSquare(Square s, ColorData cData, Vector2 offset){
        elementsAdded++;

        verticesBuffer[currentIndex++] = s.position.x - offset.x;
        verticesBuffer[currentIndex++] = s.position.y - offset.y;
        verticesBuffer[currentIndex++] = cData.r;
        verticesBuffer[currentIndex++] = cData.g;
        verticesBuffer[currentIndex++] = cData.b;
        verticesBuffer[currentIndex++] = cData.a;

        verticesBuffer[currentIndex++] = (s.position.x + s.lenX) - offset.x;
        verticesBuffer[currentIndex++] = s.position.y - offset.y;
        verticesBuffer[currentIndex++] = cData.r;
        verticesBuffer[currentIndex++] = cData.g;
        verticesBuffer[currentIndex++] = cData.b;
        verticesBuffer[currentIndex++] = cData.a;

        verticesBuffer[currentIndex++] = (s.position.x + s.lenX) - offset.x;
        verticesBuffer[currentIndex++] = (s.position.y + s.lenY) - offset.y;
        verticesBuffer[currentIndex++] = cData.r;
        verticesBuffer[currentIndex++] = cData.g;
        verticesBuffer[currentIndex++] = cData.b;
        verticesBuffer[currentIndex++] = cData.a;

        verticesBuffer[currentIndex++] = s.position.x - offset.x;
        verticesBuffer[currentIndex++] = (s.position.y + s.lenY) - offset.y;
        verticesBuffer[currentIndex++] = cData.r;
        verticesBuffer[currentIndex++] = cData.g;
        verticesBuffer[currentIndex++] = cData.b;
        verticesBuffer[currentIndex++] = cData.a;
    }
}
