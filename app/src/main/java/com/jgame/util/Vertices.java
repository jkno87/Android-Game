package com.jgame.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import javax.microedition.khronos.opengles.GL10;

public class Vertices {

    public static final int BYTES_ON_FLOAT = 4;
    public static final int TEXTURE_VERTEX = 4;
    public static final int VERTEX_PER_ELEMENT = 4;
    public static final int INDICES_PER_ELEMENT = 6;
    private GL10 gl;
    private final boolean hasColor;
    private final boolean hasTexCoords;
    private final int vertexSize;
    private float[] posiciones;
    private int currentPosition;
    private int numElems;
    final FloatBuffer vertices;
    final ShortBuffer indices;


    public Vertices(GL10 gl, int maxVertices, int maxIndices, boolean hasColor, boolean hasTexCoords){
        this.gl = gl;
        this.hasColor = hasColor;
        this.hasTexCoords = hasTexCoords;
        this.vertexSize = (3 + (hasColor?4:0) + (hasTexCoords?2:0)) * 4;

        ByteBuffer buffer = ByteBuffer.allocateDirect(maxVertices * vertexSize);
        buffer.order(ByteOrder.nativeOrder());
        vertices = buffer.asFloatBuffer();

        if(maxIndices > 0){
            buffer = ByteBuffer.allocateDirect(maxIndices * Short.SIZE / 8);
            buffer.order(ByteOrder.nativeOrder());
            indices = buffer.asShortBuffer();
        } else {
            indices = null;
        }
    }

    public Vertices(GL10 gl, int elems){
        this.gl = gl;
        this.hasColor = false;
        this.hasTexCoords = true;
        this.vertexSize = TEXTURE_VERTEX * BYTES_ON_FLOAT;

        ByteBuffer buffer = ByteBuffer.allocateDirect(elems * VERTEX_PER_ELEMENT * vertexSize);
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
        setIndices(indices, 0, indices.length);

        posiciones = new float[elems * VERTEX_PER_ELEMENT * TEXTURE_VERTEX];
        currentPosition = 0;
        numElems = elems;
    }

    public void addJavaVertex(float[] elemVertices){
        int i = 0;
        while(i < elemVertices.length){
            posiciones[currentPosition++] = elemVertices[i];
            i++;
        }
    }

    public void setVertices(float[] vertices, int offset, int length){
        this.vertices.clear();
        this.vertices.put(vertices, offset, length);
        this.vertices.flip();
    }

    public void setIndices(short[] indices, int offset, int length){
        this.indices.clear();
        this.indices.put(indices, offset, length);
        this.indices.flip();
    }

    public void drawMultiple(){
        setVertices(posiciones, 0, currentPosition);
        //draw(GL10.GL_TRIANGLES, 0, numElems * INDICES_PER_ELEMENT);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        vertices.position(0);
        gl.glVertexPointer(2, GL10.GL_FLOAT, vertexSize, vertices);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        vertices.position(2);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, vertexSize, vertices);
        indices.position(0);
        gl.glDrawElements(GL10.GL_TRIANGLES, numElems * INDICES_PER_ELEMENT, GL10.GL_UNSIGNED_SHORT, indices);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }


    public void draw(int primitiveType, int offset, int numVertices){
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        vertices.position(0);
        gl.glVertexPointer(2, GL10.GL_FLOAT, vertexSize, vertices);

        if(hasColor){
            gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
            vertices.position(2);
            gl.glColorPointer(4, GL10.GL_FLOAT, vertexSize, vertices);
        }

        if(hasTexCoords){
            gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
            vertices.position(hasColor?6:2);
            gl.glTexCoordPointer(2, GL10.GL_FLOAT, vertexSize, vertices);
        }

        if(indices != null){
            indices.position(offset);
            gl.glDrawElements(primitiveType, numVertices, GL10.GL_UNSIGNED_SHORT, indices);
        } else {
            gl.glDrawArrays(primitiveType, offset, numVertices);
        }

        if(hasTexCoords)
            gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        if(hasColor)
            gl.glDisableClientState(GL10.GL_COLOR_ARRAY);

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

    }
}