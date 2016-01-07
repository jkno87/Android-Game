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

    private final static int VERTEX_PER_ELEMENT = 4;
    private final static int INDICES_PER_ELEMENT = 6;
    private final int elementSize;
    private final boolean withColor;
    private final boolean withTexture;
    private final FloatBuffer vertices;
    private final ShortBuffer indices;
    float[] verticesBuffer;
    int elementsAdded;
    int currentIndex;

    public Drawer(boolean withTexture, boolean withColor, int maxTextures){
        this.withColor = withColor;
        this.withTexture = withTexture;
        elementSize = (2 + (withTexture?2:0) + (withColor?4:0)) * VERTEX_PER_ELEMENT;
        verticesBuffer = new float[elementSize * maxTextures];

        short[] indicesBuffer = new short[maxTextures * INDICES_PER_ELEMENT];
        int len = indicesBuffer.length;
        short j = 0;
        for(int i = 0; i < len; i+= 6, j+= 4){
            indicesBuffer[i + 0] = (short)(j + 0);
            indicesBuffer[i + 1] = (short)(j + 1);
            indicesBuffer[i + 2] = (short)(j + 2);
            indicesBuffer[i + 3] = (short)(j + 2);
            indicesBuffer[i + 4] = (short)(j + 3);
            indicesBuffer[i + 5] = (short)(j + 0);
        }

        ByteBuffer buffer = ByteBuffer.allocateDirect(maxTextures * elementSize * Float.SIZE);
        buffer.order(ByteOrder.nativeOrder());
        vertices = buffer.asFloatBuffer();

        buffer = ByteBuffer.allocateDirect(maxTextures * INDICES_PER_ELEMENT * Short.SIZE / 8);
        buffer.order(ByteOrder.nativeOrder());
        indices = buffer.asShortBuffer();

        indices.clear();
        indices.put(indicesBuffer, 0, maxTextures * INDICES_PER_ELEMENT);
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
