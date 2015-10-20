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

    private class VertexInfo {
        final ArrayList<Float> position;
        final ArrayList<Float> color;
        final ArrayList<Float> texture;

        private VertexInfo(){
            position = new ArrayList<>();
            color = null;
            texture = null;
        }
    }


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

    public void addSquare(float x, float y, float len){
        addVertex(x - len);
        addVertex(y - len);
        addVertex(x + len);
        addVertex(y - len);
        addVertex(x + len);
        addVertex(y + len);
        addVertex(x - len);
        addVertex(y + len);
    }

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
