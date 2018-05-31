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

    private final static int COORD_ELEMS = 2;
    private final static int TEX_ELEMS = 6; //Se utilizan 6 porque todas las texturas incluyen los elementos de color
    private final static int COLOR_ELEMS = 4;
    private final static int VERTEX_PER_RECTANGLE = 4;
    private final static int VERTEX_PER_TRIANGLE = 3;
    private final static int INDICES_PER_RECTANGLE = 6;
    private final static int INDICES_PER_TRIANGLE = 3;
    private final int texRectElements;
    private final int colRectElements;
    private final int triangleElements;
    private final int elementSize;
    private final boolean withColor;
    private final boolean withTexture;
    private final FloatBuffer vertices;
    private final ShortBuffer indices;
    float[] rectVerticesBuffer;
    float[] verticesBuffer;
    int elementsAdded;
    int currentIndex;

    public Drawer(int maxColoredRectangles, int maxTexRectangles, int maxTriangles){
        texRectElements = (COORD_ELEMS + TEX_ELEMS) * VERTEX_PER_RECTANGLE;
        colRectElements = (COORD_ELEMS + COLOR_ELEMS) * VERTEX_PER_RECTANGLE;
        triangleElements = (COORD_ELEMS + COLOR_ELEMS) * VERTEX_PER_TRIANGLE;
        verticesBuffer = new float[maxColoredRectangles*colRectElements + maxTexRectangles*texRectElements + triangleElements*maxTriangles];
        short[] indicesBuffer = new short[(maxColoredRectangles + maxTexRectangles) * INDICES_PER_RECTANGLE + maxTriangles * INDICES_PER_TRIANGLE];

        short j = 0;
        int i = 0;
        for(; i < (maxColoredRectangles + maxTexRectangles) * INDICES_PER_RECTANGLE; i+= INDICES_PER_RECTANGLE, j+= VERTEX_PER_RECTANGLE){
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
        elementsAdded = 0;
        currentIndex = 0;
    }

    public void draw(GL10 gl){
        if(elementsAdded == 0)
            return;

        vertices.clear();
        vertices.put(verticesBuffer, 0, currentIndex);
        vertices.flip();

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        vertices.position(0);
        gl.glVertexPointer(2, GL10.GL_FLOAT, elementSize, vertices);

        if(withTexture) {
            gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
            vertices.position(2);
            gl.glTexCoordPointer(2, GL10.GL_FLOAT, elementSize, vertices);
        }

        if(withColor){
            gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
            vertices.position(withTexture?4:2);
            gl.glColorPointer(4, GL10.GL_FLOAT, elementSize, vertices);
        }

        indices.position(0);
        gl.glDrawElements(GL10.GL_TRIANGLES, elementsAdded * INDICES_PER_ELEMENT, GL10.GL_UNSIGNED_SHORT, indices);

        if(withTexture)
            gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        if(withColor)
            gl.glDisableClientState(GL10.GL_COLOR_ARRAY);

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }
}
