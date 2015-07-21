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

    private GL10 gl;
    public static final int BYTES_ON_FLOAT = 4;
    public static final int VERTEX_PER_ELEMENT = 4;
    public static final int INDICES_PER_ELEMENT = 6;
    private final int vertexPerElement;
    private float[] posiciones;
    private int currentPosition;
    private int numElems;
    final FloatBuffer vertices;
    final ShortBuffer indices;
    final boolean withTexture;
    final boolean withColor;

    public Drawer(GL10 gl, int elems, boolean withTexture, boolean withColor){
        this.gl = gl;
        vertexPerElement = (2 + (withTexture?2:0) + (withColor?4:0)) * VERTEX_PER_ELEMENT;
        this.withTexture = withTexture;
        this.withColor = withColor;

        ByteBuffer buffer = ByteBuffer.allocateDirect(elems * vertexPerElement * BYTES_ON_FLOAT);
        buffer.order(ByteOrder.nativeOrder());
        vertices = buffer.asFloatBuffer();

        buffer = ByteBuffer.allocateDirect(elems * INDICES_PER_ELEMENT * Short.SIZE / 8);
        buffer.order(ByteOrder.nativeOrder());
        indices = buffer.asShortBuffer();

        short[] indices = new short[elems*6];
        int len = indices.length;
        short j = 0;
        for (int i = 0; i < len; i += 6, j += 4) {
            indices[i + 0] = (short)(j + 0);
            indices[i + 1] = (short)(j + 1);
            indices[i + 2] = (short)(j + 2);
            indices[i + 3] = (short)(j + 2);
            indices[i + 4] = (short)(j + 3);
            indices[i + 5] = (short)(j + 0);
        }

        this.indices.clear();
        this.indices.put(indices, 0, indices.length);
        this.indices.flip();

        posiciones = new float[elems * vertexPerElement];
        currentPosition = 0;
        numElems = elems;
    }


    public void reset(){
        currentPosition = 0;
    }

    public void addJavaVertex(float[] elemVertices){
        int i = 0;
        while(i < elemVertices.length){
            posiciones[currentPosition++] = elemVertices[i];
            i++;
        }
    }

    public void draw(){
        this.vertices.clear();
        this.vertices.put(posiciones, 0, currentPosition);
        this.vertices.flip();

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        vertices.position(0);
        gl.glVertexPointer(2, GL10.GL_FLOAT, vertexPerElement, vertices);

        if(withTexture){
            gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
            vertices.position(2);
            gl.glTexCoordPointer(2, GL10.GL_FLOAT, vertexPerElement, vertices);
        }

        if(withColor){
            gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
            vertices.position(withTexture?4:2);
            gl.glColorPointer(4, GL10.GL_FLOAT, vertexPerElement, vertices);
        }



        indices.position(0);
        gl.glDrawElements(GL10.GL_TRIANGLES, numElems * INDICES_PER_ELEMENT, GL10.GL_UNSIGNED_SHORT, indices);

        if(withTexture)
            gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        if(withColor)
            gl.glDisableClientState(GL10.GL_COLOR_ARRAY);

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }
}
