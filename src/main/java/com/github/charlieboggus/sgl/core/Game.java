package com.github.charlieboggus.sgl.core;

public abstract class Game
{
    Application app;

    public int getFPS()
    {
        return this.app.getFPS();
    }

    public int getUPS()
    {
        return this.app.getUPS();
    }

    public abstract void initialize();
    public abstract void shutdown();
    public abstract void processInput();
    public abstract void update(float delta);
    public abstract void render();
}
