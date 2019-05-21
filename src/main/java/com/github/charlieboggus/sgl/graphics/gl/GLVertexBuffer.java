package com.github.charlieboggus.sgl.graphics.gl;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL15.*;

public class GLVertexBuffer
{
    private final int id;
    private final int target;

    public GLVertexBuffer()
    {
        this(GL_ARRAY_BUFFER);
    }

    public GLVertexBuffer(int target)
    {
        this.id = glGenBuffers();
        this.target = target;
    }

    public void destroy()
    {
        glDeleteBuffers(this.id);
    }

    public int getID()
    {
        return this.id;
    }

    public void bind()
    {
        glBindBuffer(this.target, this.id);
    }

    public void unbind()
    {
        glBindBuffer(this.target, 0);
    }

    public void uploadBufferData(int size, int usage)
    {
        glBufferData(this.target, size, usage);
    }

    public void uploadBufferData(FloatBuffer data, int usage)
    {
        glBufferData(this.target, data, usage);
    }

    public void uploadBufferData(IntBuffer data, int usage)
    {
        glBufferData(this.target, data, usage);
    }

    public void uploadBufferData(float[] data, int usage)
    {
        glBufferData(this.target, data, usage);
    }

    public void uploadBufferData(int[] data, int usage)
    {
        glBufferData(this.target, data, usage);
    }

    public void uploadBufferSubData(FloatBuffer data, int offset)
    {
        glBufferSubData(this.target, offset, data);
    }

    public void uploadBufferSubData(IntBuffer data, int offset)
    {
        glBufferSubData(this.target, offset, data);
    }

    public void uploadBufferSubData(float[] data, int offset)
    {
        glBufferSubData(this.target, offset, data);
    }

    public void uploadBufferSubData(int[] data, int offset)
    {
        glBufferSubData(this.target, offset, data);
    }
}
