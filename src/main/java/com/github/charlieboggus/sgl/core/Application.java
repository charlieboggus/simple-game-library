package com.github.charlieboggus.sgl.core;

import com.github.charlieboggus.sgl.utility.Logger;

import org.lwjgl.glfw.GLFWErrorCallback;

import static org.lwjgl.glfw.GLFW.*;

public class Application
{
    private static final Logger logger = Logger.getLogger(Application.class);

    private GLFWErrorCallback errorCB;
    private Configuration cfg;
    private Timer timer;

    public Application(Configuration cfg)
    {
        // Set the GLFW error callback
        this.errorCB = GLFWErrorCallback.create((error, desc) -> {
            String msg = "[GLFW Error] " + GLFWErrorCallback.getDescription(desc) + " (Error Code: " + Integer.toHexString(error).toUpperCase() + ")";
            logger.error(msg);
        });
        glfwSetErrorCallback(this.errorCB);

        // Initialize GLFW
        if(!glfwInit())
        {
            logger.fatal("Failed to initialize GLFW!");
            throw new RuntimeException();
        }

        // Set the Configuration reference
        this.cfg = cfg;
    }

    public void start(Game game)
    {
        initialize(game);
        loop(game);
        shutdown(game);
    }

    private void initialize(Game game)
    {
        // Initialize application timer
        this.timer = new Timer();
        this.timer.initialize();

        // Create the display
        if(!Display.create(this.cfg))
        {
            logger.fatal("Failed to create application display!");
            glfwTerminate();
            this.errorCB.free();
            throw new RuntimeException();
        }

        // Initialize other Engine components
        // TODO: initialize other engine stuff
        Input.create();
        Audio.create(this.cfg);

        // Initialize the game
        game.app = this;
        game.initialize();
    }

    private void loop(Game game)
    {
        float accumulator = 0.0f;
        float interval = 1.0f / this.cfg.getTargetUPS();
        while(!Display.shouldClose())
        {
            float delta = this.timer.getDeltaTime();
            accumulator += delta;

            // Events
            Input.poll();
            game.processInput();

            // Update game logic
            while(accumulator >= interval)
            {
                game.update(delta);
                this.timer.updateUPS();
                accumulator -= interval;
            }

            // Render
            Display.clear();
            game.render();
            this.timer.updateFPS();

            // Refresh
            Input.refresh();
            Display.refresh();
            this.timer.update();

            // Synchronize FPS if necessary
            if(!Display.isVsyncEnabled() || !Display.isFocused())
                this.synchronize(Display.isFocused() ? this.cfg.getTargetFPS() : this.cfg.getIdleFPS());
        }
    }

    private void shutdown(Game game)
    {
        // Shutdown the game
        game.shutdown();

        // Shutdown engine
        Audio.destroy();
        Input.destroy();
        Display.destroy();

        // Shutdown GLFW
        glfwTerminate();
        this.errorCB.free();
    }

    private void synchronize(int targetFPS)
    {
        float last = this.timer.getLastLoopTime();
        float now = this.timer.getTime();
        float target = 1.0f / targetFPS;
        while(now - last < target)
        {
            try { Thread.sleep(1); } catch (InterruptedException e) { /* Ignore */ }
            now = this.timer.getTime();
        }
    }

    int getFPS()
    {
        return this.timer.getFPS();
    }

    int getUPS()
    {
        return this.timer.getUPS();
    }
}
