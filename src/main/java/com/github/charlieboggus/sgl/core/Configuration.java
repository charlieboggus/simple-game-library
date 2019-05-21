package com.github.charlieboggus.sgl.core;

import com.github.charlieboggus.sgl.utility.AntialiasMode;
import com.github.charlieboggus.sgl.utility.Color;
import com.github.charlieboggus.sgl.utility.TextureFilterMode;

public class Configuration
{
    public static String CONFIG_FILE_NAME = "sgl.cfg";

    public static Configuration load()
    {
        return new Configuration();
    }

    public static void save(Configuration config)
    {
    }

    // -----------------------------------------------------------------------------------------------------------------

    private boolean debugging = true;

    private String title = "SGL Application";
    private String icon = "icon.png";
    private int targetFPS = 144;
    private int targetUPS = 30;
    private int idleFPS = 30;

    private int width = 1920;
    private int height = 1080;
    private int minWidth = -1;
    private int minHeight = -1;
    private boolean resizable = true;
    private boolean fullscreen = false;
    private boolean vsync = false;

    private Color clearColor = Color.Magenta;
    private TextureFilterMode textureFilterMode = TextureFilterMode.Anisotropic16x;
    private AntialiasMode antialiasMode = AntialiasMode.MSAA8x;

    public boolean isDebugging()
    {
        return this.debugging;
    }

    public void setDebugging(boolean b)
    {
        this.debugging = b;
    }

    public String getTitle()
    {
        return this.title;
    }

    public void setTitle(String str)
    {
        this.title = str;
    }

    public String getIconPath()
    {
        return this.icon;
    }

    public void setIconPath(String str)
    {
        this.icon = str;
    }

    public int getTargetFPS()
    {
        return this.targetFPS;
    }

    public void setTargetFPS(int t)
    {
        this.targetFPS = t;
    }

    public int getTargetUPS()
    {
        return this.targetUPS;
    }

    public void setTargetUPS(int t)
    {
        this.targetUPS = t;
    }

    public int getIdleFPS()
    {
        return this.idleFPS;
    }

    public void setIdleFPS(int t)
    {
        this.idleFPS = t;
    }

    public int getWidth()
    {
        return this.width;
    }

    public void setWidth(int w)
    {
        this.width = w;
    }

    public int getHeight()
    {
        return this.height;
    }

    public void setHeight(int h)
    {
        this.height = h;
    }

    public void setResolution(int w, int h)
    {
        this.width = w;
        this.height = h;
    }

    public int getMinimumWidth()
    {
        return this.minWidth;
    }

    public void setMinimumWidth(int w)
    {
        this.minWidth = w;
    }

    public int getMinimumHeight()
    {
        return this.minHeight;
    }

    public void setMinimumHeight(int h)
    {
        this.minHeight = h;
    }

    public void setMinimumResolution(int w, int h)
    {
        this.minWidth = w;
        this.minHeight = h;
    }

    public boolean isResizable()
    {
        return this.resizable;
    }

    public void setResizable(boolean b)
    {
        this.resizable = b;
    }

    public boolean isFullscreen()
    {
        return this.fullscreen;
    }

    public void setFullscreen(boolean b)
    {
        this.fullscreen = b;
    }

    public boolean isVsyncEnabled()
    {
        return this.vsync;
    }

    public void setVsyncEnabled(boolean b)
    {
        this.vsync = b;
    }

    public Color getClearColor()
    {
        return this.clearColor;
    }

    public void setClearColor(Color c)
    {
        this.clearColor = c;
    }

    public TextureFilterMode getTextureFilterMode()
    {
        return this.textureFilterMode;
    }

    public void setTextureFilterMode(TextureFilterMode mode)
    {
        this.textureFilterMode = mode;
    }

    public AntialiasMode getAntialiasMode()
    {
        return this.antialiasMode;
    }

    public void setAntialiasMode(AntialiasMode mode)
    {
        this.antialiasMode = mode;
    }
}
