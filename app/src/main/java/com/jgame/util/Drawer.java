package com.jgame.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by jose on 3/02/15.
 */
public class Drawer {

    public static class TextureData {
        public final float v1;
        public final float u1;
        public final float v2;
        public final float u2;

        public TextureData(float v1, float u1, float v2, float u2){
            this.v1 = v1;
            this.u1 = u1;
            this.v2 = v2;
            this.u2 = u2;
        }

    }

    public static class ColorData {
        public float r;
        public float g;
        public float b;
        public float a;

        public ColorData(float r, float g, float b, float a){
            this.r = r;
            this.g = g;
            this.b = b;
            this.a = a;
        }
    }

    public final static ColorData DEFAULT_COLOR = new ColorData(1,1,1,1);

    public static TextureData genTextureData(float x, float y, float total){
        return new TextureData((x - 1) / total, (y - 1) / total, x / total, y / total);
    }

    public static TextureData generarTextureData(float x1, float y1, float x2, float y2, float scale){
        return new TextureData(x1/scale, y1/scale, x2/scale, y2/scale);
    }

    private final static int COORD_ELEMS = 2;
    private final static int TEX_ELEMS = 6; //Se utilizan 6 porque todas las texturas incluyen los elementos de color
    private final static int VERTEX_PER_RECTANGLE = 4;
    private final static int VERTEX_PER_TRIANGLE = 3;
    private final static int INDICES_PER_RECTANGLE = 6;
    private final static int INDICES_PER_TRIANGLE = 3;
    private final int texRectElements;
    private final int triangleElements;
    private final FloatBuffer vertices;
    private final ShortBuffer indices;
    float[] verticesBuffer;
    int rectanglesAdded;
    int trianglesAdded;
    int currentIndex;
    int rectangleIndex;
    int triangleIndex;

    public Drawer(int maxTexRectangles, int maxTriangles){
        texRectElements = (COORD_ELEMS + TEX_ELEMS) * VERTEX_PER_RECTANGLE;
        triangleElements = (COORD_ELEMS + TEX_ELEMS) * VERTEX_PER_TRIANGLE;
        verticesBuffer = new float[maxTexRectangles*texRectElements + triangleElements*maxTriangles];
        short[] indicesBuffer = new short[maxTexRectangles * INDICES_PER_RECTANGLE + maxTriangles * INDICES_PER_TRIANGLE];

        rectangleIndex = 0;
        triangleIndex = maxTexRectangles * INDICES_PER_RECTANGLE;
        short j = 0;
        int i = 0;
        for(; i < triangleIndex; i+= INDICES_PER_RECTANGLE, j+= VERTEX_PER_RECTANGLE){
            indicesBuffer[i + 0] = (short)(j + 0);
            indicesBuffer[i + 1] = (short)(j + 1);
            indicesBuffer[i + 2] = (short)(j + 2);
            indicesBuffer[i + 3] = (short)(j + 2);
            indicesBuffer[i + 4] = (short)(j + 3);
            indicesBuffer[i + 5] = (short)(j + 0);
        }

        for(; i < indicesBuffer.length; i+= INDICES_PER_TRIANGLE, j+= VERTEX_PER_TRIANGLE){
            indicesBuffer[i + 0] = (short)(j + 0);
            indicesBuffer[i + 1] = (short)(j + 1);
            indicesBuffer[i + 2] = (short)(j + 2);
        }


        ByteBuffer buffer = ByteBuffer.allocateDirect(verticesBuffer.length * Float.SIZE);
        buffer.order(ByteOrder.nativeOrder());
        vertices = buffer.asFloatBuffer();

        buffer = ByteBuffer.allocateDirect(indicesBuffer.length * Short.SIZE / 8);
        buffer.order(ByteOrder.nativeOrder());
        indices = buffer.asShortBuffer();

        indices.clear();
        indices.put(indicesBuffer, 0, indicesBuffer.length);
        indices.flip();

    }


    public void reset(){
        rectanglesAdded = 0;
        trianglesAdded = 0;
        currentIndex = 0;
    }

    public void draw(GL10 gl){
        vertices.clear();
        vertices.put(verticesBuffer, 0, verticesBuffer.length);
        vertices.flip();

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        vertices.position(0);
        gl.glVertexPointer(2, GL10.GL_FLOAT, texRectElements, vertices);

        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        vertices.position(2);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, texRectElements, vertices);

        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        vertices.position(4);
        gl.glColorPointer(4, GL10.GL_FLOAT, texRectElements, vertices);

        indices.position(0);
        gl.glDrawElements(GL10.GL_TRIANGLES, rectanglesAdded * INDICES_PER_RECTANGLE, GL10.GL_UNSIGNED_SHORT, indices);

        if(trianglesAdded > 0) {
            indices.position(texRectElements);
            gl.glDrawElements(GL10.GL_TRIANGLES, trianglesAdded * INDICES_PER_TRIANGLE, GL10.GL_UNSIGNED_SHORT, indices);
        }


        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }


    public void addColoredSquare(Square s, TextureData tData, ColorData cData){
        rectanglesAdded++;

        verticesBuffer[currentIndex++] = s.position.x;
        verticesBuffer[currentIndex++] = s.position.y;
        verticesBuffer[currentIndex++] = tData.v1;
        verticesBuffer[currentIndex++] = tData.u2;
        verticesBuffer[currentIndex++] = cData.r;
        verticesBuffer[currentIndex++] = cData.g;
        verticesBuffer[currentIndex++] = cData.b;
        verticesBuffer[currentIndex++] = cData.a;

        verticesBuffer[currentIndex++] = s.position.x + s.lenX;
        verticesBuffer[currentIndex++] = s.position.y;
        verticesBuffer[currentIndex++] = tData.v2;
        verticesBuffer[currentIndex++] = tData.u2;
        verticesBuffer[currentIndex++] = cData.r;
        verticesBuffer[currentIndex++] = cData.g;
        verticesBuffer[currentIndex++] = cData.b;
        verticesBuffer[currentIndex++] = cData.a;

        verticesBuffer[currentIndex++] = s.position.x + s.lenX;
        verticesBuffer[currentIndex++] = s.position.y + s.lenY;
        verticesBuffer[currentIndex++] = tData.v2;
        verticesBuffer[currentIndex++] = tData.u1;
        verticesBuffer[currentIndex++] = cData.r;
        verticesBuffer[currentIndex++] = cData.g;
        verticesBuffer[currentIndex++] = cData.b;
        verticesBuffer[currentIndex++] = cData.a;

        verticesBuffer[currentIndex++] = s.position.x;
        verticesBuffer[currentIndex++] = s.position.y + s.lenY;
        verticesBuffer[currentIndex++] = tData.v1;
        verticesBuffer[currentIndex++] = tData.u1;
        verticesBuffer[currentIndex++] = cData.r;
        verticesBuffer[currentIndex++] = cData.g;
        verticesBuffer[currentIndex++] = cData.b;
        verticesBuffer[currentIndex++] = cData.a;
    }

    public void addTexturedSquare(Square s, TextureData tData){
        rectanglesAdded++;

        verticesBuffer[currentIndex++] = s.position.x;
        verticesBuffer[currentIndex++] = s.position.y;
        verticesBuffer[currentIndex++] = tData.v1;
        verticesBuffer[currentIndex++] = tData.u2;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;

        verticesBuffer[currentIndex++] = s.position.x + s.lenX;
        verticesBuffer[currentIndex++] = s.position.y;
        verticesBuffer[currentIndex++] = tData.v2;
        verticesBuffer[currentIndex++] = tData.u2;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;

        verticesBuffer[currentIndex++] = s.position.x + s.lenX;
        verticesBuffer[currentIndex++] = s.position.y + s.lenY;
        verticesBuffer[currentIndex++] = tData.v2;
        verticesBuffer[currentIndex++] = tData.u1;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;

        verticesBuffer[currentIndex++] = s.position.x;
        verticesBuffer[currentIndex++] = s.position.y + s.lenY;
        verticesBuffer[currentIndex++] = tData.v1;
        verticesBuffer[currentIndex++] = tData.u1;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;
    }

    /**
     * Agrega los vertices de un cuadrado con textura y color
     * @param tData Informacion de textura que se utilizara
     * @return Drawer que contiene los vertices del cuadrado
     */
    public void addInvertedColoredSquare(Square s, TextureData tData, ColorData cData){
        rectanglesAdded++;

        verticesBuffer[currentIndex++] = s.position.x - s.lenX;
        verticesBuffer[currentIndex++] = s.position.y;
        verticesBuffer[currentIndex++] = tData.v2;
        verticesBuffer[currentIndex++] = tData.u2;
        verticesBuffer[currentIndex++] = cData.r;
        verticesBuffer[currentIndex++] = cData.g;
        verticesBuffer[currentIndex++] = cData.b;
        verticesBuffer[currentIndex++] = cData.a;

        verticesBuffer[currentIndex++] = s.position.x;
        verticesBuffer[currentIndex++] = s.position.y;
        verticesBuffer[currentIndex++] = tData.v1;
        verticesBuffer[currentIndex++] = tData.u2;
        verticesBuffer[currentIndex++] = cData.r;
        verticesBuffer[currentIndex++] = cData.g;
        verticesBuffer[currentIndex++] = cData.b;
        verticesBuffer[currentIndex++] = cData.a;

        verticesBuffer[currentIndex++] = s.position.x;
        verticesBuffer[currentIndex++] = s.position.y + s.lenY;
        verticesBuffer[currentIndex++] = tData.v1;
        verticesBuffer[currentIndex++] = tData.u1;
        verticesBuffer[currentIndex++] = cData.r;
        verticesBuffer[currentIndex++] = cData.g;
        verticesBuffer[currentIndex++] = cData.b;
        verticesBuffer[currentIndex++] = cData.a;

        verticesBuffer[currentIndex++] = s.position.x - s.lenX;
        verticesBuffer[currentIndex++] = s.position.y + s.lenY;
        verticesBuffer[currentIndex++] = tData.v2;
        verticesBuffer[currentIndex++] = tData.u1;
        verticesBuffer[currentIndex++] = cData.r;
        verticesBuffer[currentIndex++] = cData.g;
        verticesBuffer[currentIndex++] = cData.b;
        verticesBuffer[currentIndex++] = cData.a;
    }

    public void addInvertedTexturedSquare(Square s, TextureData tData){
        rectanglesAdded++;

        verticesBuffer[currentIndex++] = s.position.x - s.lenX;
        verticesBuffer[currentIndex++] = s.position.y;
        verticesBuffer[currentIndex++] = tData.v2;
        verticesBuffer[currentIndex++] = tData.u2;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;

        verticesBuffer[currentIndex++] = s.position.x;
        verticesBuffer[currentIndex++] = s.position.y;
        verticesBuffer[currentIndex++] = tData.v1;
        verticesBuffer[currentIndex++] = tData.u2;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;

        verticesBuffer[currentIndex++] = s.position.x;
        verticesBuffer[currentIndex++] = s.position.y + s.lenY;
        verticesBuffer[currentIndex++] = tData.v1;
        verticesBuffer[currentIndex++] = tData.u1;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;

        verticesBuffer[currentIndex++] = s.position.x - s.lenX;
        verticesBuffer[currentIndex++] = s.position.y + s.lenY;
        verticesBuffer[currentIndex++] = tData.v2;
        verticesBuffer[currentIndex++] = tData.u1;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;
    }


/**
 * Agrega los vertices de un cuadrado con textura y color
 * @param x coordenada X del centro del cuadrado
 * @param y coordenada Y del centro de cuadrado
 * @param lenX longitud X del rectangulo
 * @param lenY longitud Y del rectangulo
 * @param tdata Informacion de la textura que se utilizara
 * @return Drawer que contiene los vertices del cuadrado
 */
    public void addTexturedSquare(float x, float y, float lenX, float lenY, TextureData tdata){
        rectanglesAdded++;

        float x2 = x + lenX;
        float y2 = y + lenY;

        verticesBuffer[currentIndex++] = x;
        verticesBuffer[currentIndex++] = y;
        verticesBuffer[currentIndex++] = tdata.v1;
        verticesBuffer[currentIndex++] = tdata.u2;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;

        verticesBuffer[currentIndex++] = x2;
        verticesBuffer[currentIndex++] = y;
        verticesBuffer[currentIndex++] = tdata.v2;
        verticesBuffer[currentIndex++] = tdata.u2;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;

        verticesBuffer[currentIndex++] = x2;
        verticesBuffer[currentIndex++] = y2;
        verticesBuffer[currentIndex++] = tdata.v2;
        verticesBuffer[currentIndex++] = tdata.u1;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;

        verticesBuffer[currentIndex++] = x;
        verticesBuffer[currentIndex++] = y2;
        verticesBuffer[currentIndex++] = tdata.v1;
        verticesBuffer[currentIndex++] = tdata.u1;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;

    }
}
