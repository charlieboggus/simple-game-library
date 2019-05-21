package com.github.charlieboggus.sgl.utility;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_NEAREST;

public enum TextureFilterMode
{
    Nearest(GL_NEAREST, GL_NEAREST),
    Bilinear(GL_LINEAR, GL_LINEAR),
    Trilinear(GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR),
    Anisotropic2x(2.0f),
    Anisotropic4x(4.0f),
    Anisotropic8x(8.0f),
    Anisotropic16x(16.0f);

    private final int minFilter;
    private final int magFilter;
    private final float samples;

    TextureFilterMode(int min, int mag)
    {
        this.minFilter = min;
        this.magFilter = mag;
        this.samples = 0.0f;
    }

    TextureFilterMode(float samples)
    {
        this.minFilter = GL_LINEAR_MIPMAP_LINEAR;
        this.magFilter = GL_LINEAR;
        this.samples = samples;
    }

    public int getMinFilter()
    {
        return this.minFilter;
    }

    public int getMagFilter()
    {
        return this.magFilter;
    }

    public float getSamples()
    {
        return this.samples;
    }

    public boolean isAnisotropic()
    {
        return this.equals(Anisotropic2x) || this.equals(Anisotropic4x) || this.equals(Anisotropic8x) || this.equals(Anisotropic16x);
    }

    @Override
    public String toString()
    {
        return this.name();
    }
}
