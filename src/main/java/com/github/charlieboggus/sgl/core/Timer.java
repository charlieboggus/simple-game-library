package com.github.charlieboggus.sgl.core;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

class Timer
{
    private float lastLoopTime;
    private float counter;
    private int fps;
    private int fpsCount;
    private int ups;
    private int upsCount;

    public void initialize()
    {
        this.lastLoopTime = this.getTime();
    }

    float getTime()
    {
        return (float) glfwGetTime();
    }

    float getDeltaTime()
    {
        float now = this.getTime();
        float last = this.getLastLoopTime();
        float delta = now - last;
        this.lastLoopTime = now;
        this.counter += delta;

        return delta;
    }

    float getLastLoopTime()
    {
        return this.lastLoopTime;
    }

    int getFPS()
    {
        return this.fps > 0 ? this.fps : this.fpsCount;
    }

    int getUPS()
    {
        return this.ups > 0 ? this.ups : this.upsCount;
    }

    void updateFPS()
    {
        this.fpsCount++;
    }

    void updateUPS()
    {
        this.upsCount++;
    }

    void update()
    {
        if(this.counter > 1.0f)
        {
            this.fps = this.fpsCount;
            this.fpsCount = 0;
            this.ups = this.upsCount;
            this.upsCount = 0;
            this.counter -= 1.0f;
        }
    }
}
