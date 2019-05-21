package com.github.charlieboggus.sgl.utility;

public enum AntialiasMode
{
    Disabled(0),
    MSAA2x(2),
    MSAA4x(4),
    MSAA8x(8),
    FXAA(0);

    private final int samples;

    AntialiasMode(int samples)
    {
        this.samples = samples;
    }

    public int getSamples()
    {
        return this.samples;
    }

    public boolean isEnabled()
    {
        return !this.equals(Disabled);
    }

    public boolean isMultisampled()
    {
        return !this.equals(Disabled) && !this.equals(FXAA);
    }

    public boolean isFXAA()
    {
        return this.equals(FXAA);
    }

    @Override
    public String toString()
    {
        return this.name().toLowerCase();
    }

    public static AntialiasMode fromString(String str)
    {
        if(MSAA2x.name().equalsIgnoreCase(str))
            return MSAA2x;
        else if(MSAA4x.name().equalsIgnoreCase(str))
            return MSAA4x;
        else if(MSAA8x.name().equalsIgnoreCase(str))
            return MSAA8x;
        else if(FXAA.name().equalsIgnoreCase(str))
            return FXAA;
        else
            return Disabled;
    }
}
