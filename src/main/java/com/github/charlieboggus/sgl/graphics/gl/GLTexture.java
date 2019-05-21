package com.github.charlieboggus.sgl.graphics.gl;

import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT;
import static org.lwjgl.opengl.EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class GLTexture
{
    private int id;

    public GLTexture()
    {
        this.id = glGenTextures();
    }

    public void destroy()
    {
        if(this.id != -1)
        {
            glDeleteTextures(this.id);
            this.id = -1;
        }
    }

    public int getID()
    {
        return this.id;
    }

    public void bind()
    {
        this.bind(0);
    }

    public void bind(int unit)
    {
        glActiveTexture(GL_TEXTURE0 + unit);
        glBindTexture(GL_TEXTURE_2D, this.id);
    }

    public void unbind()
    {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void uploadTextureData(int internalFmt, int width, int height, int fmt, int type, ByteBuffer data)
    {
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glPixelStorei(GL_PACK_ALIGNMENT, 1);
        glTexImage2D(GL_TEXTURE_2D, 0, internalFmt, width, height, 0, fmt, type, data);
    }

    public void generateMipmap()
    {
        glGenerateMipmap(GL_TEXTURE_2D);
    }

    public void setWrap(int wrapS, int wrapT)
    {
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, wrapS);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, wrapT);
    }

    public void setFilter(int min, int mag)
    {
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, min);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, mag);
    }

    public void setAnisotropicFilter(float samples)
    {
        float max;
        try(MemoryStack stack = MemoryStack.stackPush())
        {
            FloatBuffer buffer = stack.mallocFloat(1);
            glGetFloatv(GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, buffer);
            max = buffer.get(0);
        }

        if(samples <= 0.0f)
            samples = 1.0f;
        if(samples > max)
            samples = max;

        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAX_ANISOTROPY_EXT, samples);
    }
}
