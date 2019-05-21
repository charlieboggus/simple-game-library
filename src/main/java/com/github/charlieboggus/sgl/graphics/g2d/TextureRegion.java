package com.github.charlieboggus.sgl.graphics.g2d;

public class TextureRegion extends Texture
{
    private int regionWidth;
    private int regionHeight;

    public TextureRegion(Texture texture)
    {
        super(texture);
        this.setRegion(0, 0, super.getWidth(), super.getHeight());
    }

    public TextureRegion(Texture texture, int regW, int regH)
    {
        super(texture);
        this.setRegion(0, 0, regW, regH);
    }

    public TextureRegion(Texture texture, int regX, int regY, int regW, int regH)
    {
        super(texture);
        this.setRegion(regX, regY, regW, regH);
    }

    public void setRegion(int regionX, int regionY, int regionW, int regionH)
    {
        float invWidth = 1.0f / super.getWidth();
        float invHeight = 1.0f / super.getHeight();

        this.setRegion(regionX * invWidth, regionY * invHeight, (regionX + regionW) * invWidth, (regionY + regionH) * invHeight);

        this.regionWidth = Math.abs(regionW);
        this.regionHeight = Math.abs(regionH);
    }

    private void setRegion(float s0, float t0, float s1, float t1)
    {
        int texW = super.getWidth();
        int texH = super.getHeight();

        this.regionWidth = Math.round(Math.abs(s1 - s0) * texW);
        this.regionHeight = Math.round(Math.abs(t1 - t0) * texH);

        if(this.regionWidth == 1 && this.regionHeight == 1)
        {
            float adjX = 0.25f / texW;
            float adjY = 0.25f / texH;

            s0 += adjX;
            s1 -= adjX;

            t0 += adjY;
            t1 -= adjY;
        }

        this.s0 = s0;
        this.t0 = t0;
        this.s1 = s1;
        this.t1 = t1;
    }

    @Override
    public int getWidth()
    {
        return this.regionWidth;
    }

    @Override
    public int getHeight()
    {
        return this.regionHeight;
    }
}
