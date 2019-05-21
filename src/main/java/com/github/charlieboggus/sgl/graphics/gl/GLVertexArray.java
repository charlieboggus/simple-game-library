package com.github.charlieboggus.sgl.graphics.gl;

import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class GLVertexArray
{
    private final int id;

    public GLVertexArray()
    {
        this.id = glGenVertexArrays();
    }

    public void destroy()
    {
        glDeleteVertexArrays(this.id);
    }

    public int getID()
    {
        return this.id;
    }

    public void bind()
    {
        glBindVertexArray(this.id);
    }

    public void unbind()
    {
        glBindVertexArray(0);
    }

    public void enableVertexAttribute(int location)
    {
        glEnableVertexAttribArray(location);
    }

    public void disableVertexAttribute(int location)
    {
        glDisableVertexAttribArray(location);
    }

    public void setVertexAttributePointer(int location, int size, int type, int stride, int offset)
    {
        glVertexAttribPointer(location, size, type, false, stride, offset);
    }
}
