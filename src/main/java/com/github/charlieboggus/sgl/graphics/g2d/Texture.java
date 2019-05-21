package com.github.charlieboggus.sgl.graphics.g2d;

import com.github.charlieboggus.sgl.core.Display;
import com.github.charlieboggus.sgl.graphics.gl.GLTexture;
import com.github.charlieboggus.sgl.utility.Color;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;

public class Texture
{
    private GLTexture tex;

    private int width;
    private int height;

    float s0;
    float t0;
    float s1;
    float t1;

    public Texture(Texture texture)
    {
        this.width = texture.width;
        this.height = texture.height;

        this.s0 = 0.0f;
        this.t0 = 0.0f;
        this.s1 = 1.0f;
        this.t1 = 1.0f;

        this.tex = texture.tex;
    }

    public Texture(String file)
    {
        int width;
        int height;
        int components;
        ByteBuffer data;

        try (MemoryStack stack = MemoryStack.stackPush())
        {
            IntBuffer wb = stack.mallocInt(1);
            IntBuffer hb = stack.mallocInt(1);
            IntBuffer cb = stack.mallocInt(1);
            STBImage.stbi_set_flip_vertically_on_load(false);

            data = STBImage.stbi_load(file, wb, hb, cb, 0);
            width = wb.get(0);
            height = hb.get(0);
            components = cb.get(0);
        }

        this.generateTexture(width, height, components, data);
    }

    public Texture(int width, int height)
    {
        ByteBuffer data = BufferUtils.createByteBuffer(width * height * 4);
        int i = 0;
        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                Color c = (x / 16) % 2 == 0 ? ((y / 16) % 2 == 0 ? Color.White : Color.Coral) : ((y / 16) % 2 == 0 ? Color.Coral : Color.White);
                data.put(i, (byte) c.redByte());
                data.put(i + 1, (byte) c.greenByte());
                data.put(i + 2, (byte) c.blueByte());
                data.put(i + 3, (byte) c.alphaByte());
                i += 4;
            }
        }

        this.generateTexture(width, height, 4, data);
    }

    public Texture(int width, int height, int components, ByteBuffer data)
    {
        this.generateTexture(width, height, components, data);
    }

    public void dispose()
    {
        this.tex.destroy();
    }

    public int getWidth()
    {
        return this.width;
    }

    public int getHeight()
    {
        return this.height;
    }

    void bind()
    {
        this.tex.bind();
    }

    void bind(int unit)
    {
        this.tex.bind(unit);
    }

    void unbind()
    {
        this.tex.unbind();
    }

    float getS0()
    {
        return this.s0;
    }

    float getT0()
    {
        return this.t0;
    }

    float getS1()
    {
        return this.s1;
    }

    float getT1()
    {
        return this.t1;
    }

    private void generateTexture(int width, int height, int components, ByteBuffer data)
    {
        this.width = width;
        this.height = height;

        this.s0 = 0.0f;
        this.t0 = 0.0f;
        this.s1 = 1.0f;
        this.t1 = 1.0f;

        this.tex = new GLTexture();
        this.tex.bind();
        this.tex.uploadTextureData((components == 4) ? GL_RGBA8 : GL_RGB, width, height, (components == 4) ? GL_RGBA : GL_RGB, GL_UNSIGNED_BYTE, data);
        this.tex.generateMipmap();
        this.tex.setWrap(GL_CLAMP_TO_EDGE, GL_CLAMP_TO_EDGE);
        this.tex.setFilter(Display.getFilterMode().getMinFilter(), Display.getFilterMode().getMagFilter());
        if (Display.getFilterMode().isAnisotropic())
            this.tex.setAnisotropicFilter(Display.getFilterMode().getSamples());
        this.tex.unbind();
    }
}
