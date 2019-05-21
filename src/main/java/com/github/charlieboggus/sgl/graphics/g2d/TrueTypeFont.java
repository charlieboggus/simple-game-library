package com.github.charlieboggus.sgl.graphics.g2d;

import com.github.charlieboggus.sgl.core.Display;
import com.github.charlieboggus.sgl.utility.FileIO;

import org.lwjgl.system.MemoryUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class TrueTypeFont
{
    static class Glyph
    {
        final float x;
        final float y;
        final float w;
        final float h;
        final float advance;

        private Glyph(float w, float h, float x, float y, float advance)
        {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            this.advance = advance;
        }
    }

    private final Map< Character, Glyph > glyphs;
    private Texture fontTexture;
    private int fontTextureWidth;
    private int fontTextureHeight;
    private int fontHeight;

    public TrueTypeFont()
    {
        this.glyphs = new HashMap<>();
        this.fontTexture = this.createFontTexture(new Font(Font.MONOSPACED, Font.PLAIN, 16));
    }

    public TrueTypeFont(int size)
    {
        this.glyphs = new HashMap<>();
        this.fontTexture = this.createFontTexture(new Font(Font.MONOSPACED, Font.PLAIN, size));
    }

    public TrueTypeFont(String path)
    {
        this.glyphs = new HashMap<>();
        try {
            this.fontTexture = this.createFontTexture(Font.createFont(Font.TRUETYPE_FONT, FileIO.getInputStream(path)).deriveFont(Font.PLAIN, 16));
        }
        catch (Exception e) {
            this.fontTexture = this.createFontTexture(new Font(Font.MONOSPACED, Font.PLAIN, 16));
        }
    }

    public TrueTypeFont(String path, int size)
    {
        this.glyphs = new HashMap<>();
        try {
            this.fontTexture = this.createFontTexture(Font.createFont(Font.TRUETYPE_FONT, FileIO.getInputStream(path)).deriveFont(Font.PLAIN, size));
        }
        catch (Exception e) {
            this.fontTexture = this.createFontTexture(new Font(Font.MONOSPACED, Font.PLAIN, size));
        }
    }

    public void dispose()
    {
        this.fontTexture.dispose();
    }

    public int getTextWidth(String str)
    {
        int width = 0;
        int lineWidth = 0;
        for(int i = 0; i < str.length(); i++)
        {
            char c = str.charAt(i);
            if(c == '\n')
            {
                width =  Math.max(width, lineWidth);
                lineWidth = 0;
                continue;
            }

            if(c == '\r')
                continue;

            Glyph g = this.glyphs.get(c);
            lineWidth += g.w;
        }

        width = Math.max(width, lineWidth);

        return width;
    }

    public int getTextHeight(String str)
    {
        int height = 0;
        int lineHeight = 0;
        for(int i = 0; i < str.length(); i++)
        {
            char c = str.charAt(i);
            if(c == '\n')
            {
                height += lineHeight;
                lineHeight = 0;
                continue;
            }

            if(c == '\r')
                continue;

            Glyph g = this.glyphs.get(c);
            lineHeight = Math.max(lineHeight, (int) g.h);
        }

        height += lineHeight;

        return height;
    }

    Texture getFontTexture()
    {
        return this.fontTexture;
    }

    int getFontHeight()
    {
        return this.fontHeight;
    }

    float getGlyphS0(char c)
    {
        Glyph g = this.glyphs.get(c);

        return g.x / this.fontTextureWidth;
    }

    float getGlyphT0(char c)
    {
        Glyph g = this.glyphs.get(c);

        return g.y / this.fontTextureHeight;
    }

    float getGlyphS1(char c)
    {
        Glyph g = this.glyphs.get(c);

        return (g.x + g.w) / this.fontTextureWidth;
    }

    float getGlyphT1(char c)
    {
        Glyph g = this.glyphs.get(c);

        return (g.y + g.h) / this.fontTextureHeight;
    }

    Glyph getGlyph(char c)
    {
        return this.glyphs.get(c);
    }

    private Texture createFontTexture(Font font)
    {
        int imageWidth = 0;
        int imageHeight = 0;

        for(int i = 32; i < 256; i++)
        {
            if(i == 127)
                continue;

            char c = (char) i;
            BufferedImage ch = this.createCharImage(font, c);
            if(ch == null)
                continue;

            imageWidth += ch.getWidth();
            imageHeight = Math.max(imageHeight, ch.getHeight());
        }

        this.fontHeight = imageHeight;

        BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        int x = 0;
        for(int i = 32; i < 256; i++)
        {
            if(i == 127)
                continue;

            char c = (char) i;
            BufferedImage ch = this.createCharImage(font, c);
            if(ch == null)
                continue;

            int charW = ch.getWidth();
            int charH = ch.getHeight();
            Glyph gl = new Glyph(charW, charH, x, image.getHeight() - charH, 0.0f);
            g.drawImage(ch, x, 0, null);
            x += gl.w;
            this.glyphs.put(c, gl);
        }

        int width = image.getWidth();
        int height = image.getHeight();
        int[] pixels = new int[width * height];
        image.getRGB(0, 0, width, height, pixels, 0, width);
        ByteBuffer buffer = MemoryUtil.memAlloc(width * height * 4);
        for(int i = 0; i < height; i++)
        {
            for(int j = 0; j < width; j++)
            {
                int pixel = pixels[i * width + j];

                buffer.put((byte)((pixel >> 16) & 0xFF));
                buffer.put((byte)((pixel >> 8)  & 0xFF));
                buffer.put((byte) (pixel        & 0xFF));
                buffer.put((byte)((pixel >> 24) & 0xFF));
            }
        }
        buffer.flip();
        this.fontTextureWidth = width;
        this.fontTextureHeight = height;

        return new Texture(width, height, 4, buffer);
    }

    private BufferedImage createCharImage(Font font, char c)
    {
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        if(Display.getAntialiasMode().isEnabled())
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setFont(font);
        FontMetrics metrics = g.getFontMetrics();
        g.dispose();

        int charWidth = metrics.charWidth(c);
        int charHeight = metrics.getHeight();

        if(charWidth == 0)
            return null;

        image = new BufferedImage(charWidth, charHeight, BufferedImage.TYPE_INT_ARGB);
        g = image.createGraphics();
        if(Display.getAntialiasMode().isEnabled())
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setFont(font);
        g.setPaint(Color.WHITE);
        g.drawString(String.valueOf(c), 0, metrics.getAscent());
        g.dispose();

        return image;
    }
}
