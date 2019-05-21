package com.github.charlieboggus.sgl.utility;

import org.joml.Vector3f;
import org.joml.Vector4f;

public class Color
{
    public static final Color Clear         = new Color(0.0f, 0.0f, 0.0f, 0.0f);
    public static final Color Black         = new Color(0.0f, 0.0f, 0.0f, 1.0f);
    public static final Color White         = new Color(1.0f, 1.0f, 1.0f, 1.0f);
    public static final Color Gray          = new Color(0.5f, 0.5f, 0.5f, 1.0f);
    public static final Color Gray10        = new Color(0.1f, 0.1f, 0.1f, 1.0f);
    public static final Color Gray20        = new Color(0.2f, 0.2f, 0.2f, 1.0f);
    public static final Color Gray30        = new Color(0.3f, 0.3f, 0.3f, 1.0f);
    public static final Color Gray40        = new Color(0.4f, 0.4f, 0.4f, 1.0f);
    public static final Color Gray50        = new Color(0.5f, 0.5f, 0.5f, 1.0f);
    public static final Color Gray60        = new Color(0.6f, 0.6f, 0.6f, 1.0f);
    public static final Color Gray70        = new Color(0.7f, 0.7f, 0.7f, 1.0f);
    public static final Color Gray80        = new Color(0.8f, 0.8f, 0.8f, 1.0f);
    public static final Color Gray90        = new Color(0.9f, 0.9f, 0.9f, 1.0f);
    public static final Color LightGray     = new Color(0.75f, 0.75f, 0.75f, 1.0f);
    public static final Color DarkGray      = new Color(0.25f, 0.25f, 0.25f, 1.0f);

    public static final Color Red           = new Color(1.0f, 0.0f, 0.0f, 1.0f);
    public static final Color Green         = new Color(0.0f, 1.0f, 0.0f, 1.0f);
    public static final Color Blue          = new Color(0.0f, 0.0f, 1.0f, 1.0f);
    public static final Color Cyan          = new Color(0.0f, 1.0f, 1.0f, 1.0f);
    public static final Color Magenta       = new Color(1.0f, 0.0f, 1.0f, 1.0f);
    public static final Color Yellow        = new Color(1.0f, 1.0f, 0.0f, 1.0f);
    public static final Color Orange        = new Color(1.0f, 0.6f, 0.0f, 1.0f);

    public static final Color RoyalBlue     = fromRGBA8888(0x4169E1FF);
    public static final Color SlateBlue     = fromRGBA8888(0x7B68EEFF);
    public static final Color SkyBlue       = fromRGBA8888(0x87CEEBFF);
    public static final Color Chartreuse    = fromRGBA8888(0x7FFF00FF);
    public static final Color Lime          = fromRGBA8888(0x32CD32FF);
    public static final Color SeaGreen      = fromRGBA8888(0x20B2AAFF);
    public static final Color Forest        = fromRGBA8888(0x228B22FF);
    public static final Color Olive         = fromRGBA8888(0x6B8E23FF);
    public static final Color Goldenrod     = fromRGBA8888(0xDAA520FF);
    public static final Color Brown         = fromRGBA8888(0xA52A2AFF);
    public static final Color Tan           = fromRGBA8888(0xD2B48CFF);
    public static final Color Firebrick     = fromRGBA8888(0xB22222FF);
    public static final Color Scarlet       = fromRGBA8888(0x560319FF);
    public static final Color Coral         = fromRGBA8888(0xFF7F50FF);
    public static final Color Salmon        = fromRGBA8888(0xFA8072FF);
    public static final Color Tomato        = fromRGBA8888(0xFF6347FF);
    public static final Color Pink          = fromRGBA8888(0xFFC0CBFF);
    public static final Color Purple        = fromRGBA8888(0xA020F0FF);
    public static final Color Violet        = fromRGBA8888(0x9400D3FF);
    public static final Color Maroon        = fromRGBA8888(0xB03060FF);
    public static final Color Plum          = fromRGBA8888(0xDDA0DDFF);


    public static Color fromRGB565(int rgb565)
    {
        float r = ((rgb565 & 0x0000F800) >>> 11) / 31.0f;
        float g = ((rgb565 & 0x000007E0) >>> 5) / 63.0f;
        float b = (rgb565 & 0x0000001F) / 31.0f;

        return new Color(r, g, b, 1.0f);
    }

    public static Color fromRGBA4444(int rgba4444)
    {
        float r = ((rgba4444 & 0x0000F000) >>> 12) / 15.0f;
        float g = ((rgba4444 & 0x00000F00) >>> 8) / 15.0f;
        float b = ((rgba4444 & 0x000000F0) >>> 4) / 15.0f;
        float a = (rgba4444 & 0x0000000F) / 15.0f;

        return new Color(r, g, b, a);
    }

    public static Color fromRGB888(int rgb888)
    {
        float r = ((rgb888 & 0x00FF0000) >>> 16) / 255.0f;
        float g = ((rgb888 & 0x0000FF00) >>> 8) / 255.0f;
        float b = (rgb888 & 0x000000FF) / 255.0f;

        return new Color(r, g, b, 1.0f);
    }

    public static Color fromRGBA8888(int rgba8888)
    {
        float r = ((rgba8888 & 0xFF000000) >>> 24) / 255.0f;
        float g = ((rgba8888 & 0x00FF0000) >>> 16) / 255.0f;
        float b = ((rgba8888 & 0x0000FF00) >>> 8) / 255.0f;
        float a = (rgba8888 & 0x000000FF) / 255.0f;

        return new Color(r, g, b, a);
    }

    public static Color fromARGB8888(int argb8888)
    {
        float a = ((argb8888 & 0xFF000000) >>> 24) / 255.0f;
        float r = ((argb8888 & 0x00FF0000) >>> 16) / 255.0f;
        float g = ((argb8888 & 0x0000FF00) >>> 8) / 255.0f;
        float b = (argb8888 & 0x000000FF) / 255.0f;

        return new Color(r, g, b, a);
    }

    public static Color fromABGR8888(int abgr8888)
    {
        float a = ((abgr8888 & 0xFF000000) >>> 24) / 255.0f;
        float b = ((abgr8888 & 0x00FF0000) >>> 16) / 255.0f;
        float g = ((abgr8888 & 0x0000FF00) >>> 8) / 255.0f;
        float r = (abgr8888 & 0x000000FF) / 255.0f;

        return new Color(r, g, b, a);
    }

    public static Color fromString(String str)
    {
        try {
            return fromRGBA8888((int) Long.parseLong(str.substring(2), 16));
        }
        catch (Exception e) {
            return Color.Clear;
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    private float r;
    private float g;
    private float b;
    private float a;

    public Color(float r, float g, float b, float a)
    {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
        this.clamp();
    }

    private void clamp()
    {
        if(this.r < 0.0f)
            this.r = 0.0f;
        else if(this.r > 1.0f)
            this.r = 1.0f;

        if(this.g < 0.0f)
            this.g = 0.0f;
        else if(this.g > 1.0f)
            this.g = 1.0f;

        if(this.b < 0.0f)
            this.b = 0.0f;
        else if(this.b > 1.0f)
            this.b = 1.0f;

        if(this.a < 0.0f)
            this.a = 0.0f;
        else if(this.a > 1.0f)
            this.a = 1.0f;
    }

    public float r()
    {
        return this.r;
    }

    public float g()
    {
        return this.g;
    }

    public float b()
    {
        return this.b;
    }

    public float a()
    {
        return this.a;
    }

    public int redByte()
    {
        return (int)((this.toRGBA8888() >>> 24) & 0xFF);
    }

    public int greenByte()
    {
        return (int)((this.toRGBA8888() >>> 16) & 0xFF);
    }

    public int blueByte()
    {
        return (int)((this.toRGBA8888() >>> 8) & 0xFF);
    }

    public int alphaByte()
    {
        return (int)(this.toRGBA8888() & 0xFF);
    }

    public Color mul(Color c)
    {
        this.r *= c.r();
        this.g *= c.g();
        this.b *= c.b();
        this.a *= c.a();
        this.clamp();

        return this;
    }

    public Color mul(float scalar)
    {
        this.r *= scalar;
        this.g *= scalar;
        this.b *= scalar;
        this.a *= scalar;
        this.clamp();

        return this;
    }

    public Color add(Color c)
    {
        this.r += c.r();
        this.g += c.g();
        this.b += c.b();
        this.a += c.a();
        this.clamp();

        return this;
    }

    public Color sub(Color c)
    {
        this.r -= c.r();
        this.g -= c.g();
        this.b -= c.b();
        this.a -= c.a();
        this.clamp();

        return this;
    }

    public Color lerp(Color target, float t)
    {
        this.r += t * (target.r() - this.r());
        this.g += t * (target.g() - this.g());
        this.b += t * (target.b() - this.b());
        this.a += t * (target.a() - this.a());
        this.clamp();

        return this;
    }

    public Color lerp(float r, float g, float b, float a, float t)
    {
        this.r += t * (r - this.r());
        this.g += t * (g - this.g());
        this.b += t * (b - this.b());
        this.a += t * (a - this.a());
        this.clamp();

        return this;
    }

    public int toRGB565()
    {
        return ((int)(this.r() * 31) << 11) | ((int)(this.g() * 63) << 5) | ((int)(this.b() * 31));
    }

    public int toRGBA4444()
    {
        return ((int)(this.r() * 15) << 12) | ((int)(this.g() * 15) << 8) | ((int)(this.b() * 15) << 4) | ((int)(this.a() * 15));
    }

    public int toRGB888()
    {
        return ((int)(this.r() * 255) << 16) | ((int)(this.g() * 255) << 8) | ((int)(this.b() * 255));
    }

    public int toRGBA8888()
    {
        return ((int)(this.r() * 255) << 24) | ((int)(this.g() * 255) << 16) | ((int)(this.b() * 255) << 8) | ((int)(this.a() * 255));
    }

    public float toRGBA8888_float()
    {
        return Float.intBitsToFloat(this.toRGBA8888());
    }

    public int toARGB8888()
    {
        return ((int)(this.a() * 255) << 24) | ((int)(this.r() * 255) << 16) | ((int)(this.g() * 255) << 8) | ((int)(this.b() * 255));
    }

    public int toABGR8888()
    {
        return ((int)(this.a() * 255) << 24) | ((int)(this.b() * 255) << 16) | ((int)(this.g() * 255) << 8) | ((int)(this.r() * 255));
    }

    public Vector3f toVector3()
    {
        return new Vector3f(this.r(), this.g(), this.b());
    }

    public Vector4f toVector4()
    {
        return new Vector4f(this.r(), this.g(), this.b(), this.a());
    }

    @Override
    public String toString()
    {
        return "0x" + Integer.toHexString(this.toRGBA8888()).toUpperCase();
    }
}
