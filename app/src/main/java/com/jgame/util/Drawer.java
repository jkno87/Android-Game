package com.jgame.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by jose on 3/02/15.
 */
public class Drawer {

    private GL10 gl;
    public static final int BYTES_ON_FLOAT = 4;
    public static final int VERTEX_PER_ELEMENT = 4;
    public static final int INDICES_PER_ELEMENT = 6;

    private ArrayList<Float> vertexQueue;
    private int elementsAdded;
    private final boolean withTexture;
    private final boolean withColor;

    public Drawer(boolean withTexture, boolean withColor){
        this.withColor = withColor;
        this.withTexture = withTexture;
    }

    /**
     * Agrega un indice que representa una coordenada de un cuadrado
     * @param x coordenada X del indice
     * @param y coordenada Y del indice
     * @return Drawer con el indice
     */
    public Drawer addSquareIndex(float x, float y){
        return addVertex(x).addVertex(y);
    }

    /**
     * Agrega un indice que representa una coordenada de un cuadrado con textura
     * @param x coordenada X del indice
     * @param y coordenada Y del indice
     * @param textX coordenada X de la textura del indice
     * @param textY coordenada Y de la textura del indice
     * @return Drawer que contiene el indice
     */
    public Drawer addSquareIndex(float x, float y, float textX, float textY){
        return addVertex(x).addVertex(y).addVertex(textX).addVertex(textY);
    }

    /**
     * Agrega un indice que representa una coordenada de un cuadrado con textura y color
     * @param x coordenada X del indice
     * @param y coordenada Y del indice
     * @param textX coordenada X de la textura del indice
     * @param textY coordenada Y de la textura del indice
     * @param color arreglo con los colores del indice
     * @return Drawer que contiene el indice
     */
    public Drawer addSquareIndex(float x, float y, float textX, float textY, float[] color){
        return addSquareIndex(x,y,textX, textY).addVertex(color[0])
                .addVertex(color[1]).addVertex(color[2]).addVertex(color[3]);
    }

    /**
     * Agrega un indice que representa una coordenada con un cuadrado con color
     * @param x coordenada X del indice
     * @param y coordenada Y del indice
     * @param color arreglo con el color del cuadrado
     * @return Drawer con los indices agregados
     */
    public Drawer addSquareIndex(float x, float y, float[] color){
        return addSquareIndex(x, y).addVertex(color[0])
                .addVertex(color[1]).addVertex(color[2]).addVertex(color[3]);
    }


    /**
     * Agrega los vertices de un rectangulo
     * @param x coordenada x del centro del rectangulo
     * @param y coordenada y del centro del rectangulo
     * @param lenX longitud X del rectangulo
     * @param lenY longitud Y del rectangulo
     * @return Drawer con los vertices del rectangulo agregado
     */
    public Drawer addRectangle(float x, float y, float lenX, float lenY){
        return addSquareIndex(x - lenX, y - lenY).addSquareIndex(x + lenX, y + lenY)
                .addSquareIndex(x + lenX, y + lenY).addSquareIndex(x - lenX, y + lenY);
    }

    /**
     * Agrega los vertices de un cuadrado
     * @param x coordenada X
     * @param y coordenada Y
     * @param len longitud de los lados
     * @return Drawer que ya incluye los indices del cuadrado
     */
    public Drawer addSquare(float x, float y, float len){
        return addRectangle(x, y, len, len);
    }


    /**
     * Agrega los vertices de un cuadrado con textura y color
     * @param x coordenada X del centro del cuadrado
     * @param y coordenada Y del centro de cuadrado
     * @param len longitud del cuadrado
     * @param textIndices arreglo con los indices de las texturas
     * @param colors arreglo con los indices del color
     * @return Drawer que contiene los vertices del cuadrado
     */
    public Drawer addTexturedSquare(float x, float y, float len, float[] textIndices, float[] colors){
        return addSquareIndex(x - len, y - len, textIndices[0], textIndices[1], colors)
                .addSquareIndex(x + len, y - len, textIndices[2], textIndices[3], colors)
                .addSquareIndex(x + len, y + len, textIndices[4], textIndices[5], colors)
                .addSquareIndex(x - len, y + len, textIndices[6], textIndices[7], colors);
    }

    /**
     * Agrega los vertices de un rectangulo con color
     * @param x coordenada X del centro del rectangulo
     * @param y coordenada Y del centro del rectangulo
     * @param lenX longitud en el eje X del rectangulo
     * @param lenY longitud en el eje Y del rectangulo
     * @param colors arreglo con los colores del rectangulo
     * @return Drawer que contiene los vertices del rectangulo
     */
    public Drawer addColoredRectangle(float x, float y, float lenX, float lenY, float[] colors){
        return addSquareIndex(x - lenX, y - lenY, colors).addSquareIndex(x + lenX, y - lenY, colors)
                .addSquareIndex(x + lenX, y + lenY, colors).addSquareIndex(x - lenX, y + lenY, colors);
    }


    /**
     * Agrega un float a los vertices del Drawer
     * @param e float que se agrega al Drawer
     * @return Drawer que contiene el float
     */
    public Drawer addVertex(float e){
        vertexQueue.add(e);
        elementsAdded++;

        return this;
    }

    public void draw(GL10 gl){
        if(elementsAdded == 0)
            return;

        int arraySize = (2 + (withTexture?2:0) + (withColor?4:0)) * VERTEX_PER_ELEMENT;
        ByteBuffer buffer = ByteBuffer.allocateDirect(elementsAdded * arraySize * VERTEX_PER_ELEMENT);
        buffer.order(ByteOrder.nativeOrder());
        FloatBuffer vertices = buffer.asFloatBuffer();

        buffer = ByteBuffer.allocateDirect(elementsAdded * INDICES_PER_ELEMENT * Short.SIZE / 8);
        buffer.order(ByteOrder.nativeOrder());
        ShortBuffer ind = buffer.asShortBuffer();

        short[] indices = new short[elementsAdded * INDICES_PER_ELEMENT];
        int len = indices.length;
        short j = 0;
        for(int i = 0; i < len; i+= 6, j+= 4){
            indices[i + 0] = (short)(j + 0);
            indices[i + 1] = (short)(j + 1);
            indices[i + 2] = (short)(j + 2);
            indices[i + 3] = (short)(j + 2);
            indices[i + 4] = (short)(j + 3);
            indices[i + 5] = (short)(j + 0);
        }

        ind.clear();
        ind.put(indices, 0, indices.length);
        ind.flip();

        float[] finalVertices = new float[elementsAdded * arraySize];

        for(int n = 0; n < elementsAdded; n++)
            finalVertices[n] = vertexQueue.get(n);

        vertices.clear();
        vertices.put(finalVertices, 0, elementsAdded);
        vertices.flip();

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        vertices.position(0);
        gl.glVertexPointer(2, GL10.GL_FLOAT, arraySize, vertices);

        if(withTexture){
            gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
            vertices.position(2);
            gl.glTexCoordPointer(2, GL10.GL_FLOAT, arraySize, vertices);
        }

        if(withColor){
            gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
            vertices.position(withTexture?4:2);
            gl.glColorPointer(4, GL10.GL_FLOAT, arraySize, vertices);
        }

        ind.position(0);
        gl.glDrawElements(GL10.GL_TRIANGLES, elementsAdded * INDICES_PER_ELEMENT, GL10.GL_UNSIGNED_SHORT, ind);

        if(withTexture)
            gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        if(withColor)
            gl.glDisableClientState(GL10.GL_COLOR_ARRAY);

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

    }
}
