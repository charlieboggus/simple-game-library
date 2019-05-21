package com.github.charlieboggus.sgl.input;

import com.github.charlieboggus.sgl.core.Input;
import org.lwjgl.glfw.GLFWCharCallback;
import org.lwjgl.glfw.GLFWKeyCallback;

import static org.lwjgl.glfw.GLFW.*;

public class KeyboardListener extends Input.InputDevice
{
    private GLFWKeyCallback keyCB;
    private GLFWCharCallback charCB;

    private final int[] states = new int[GLFW_KEY_LAST];
    private final boolean[] actions = new boolean[GLFW_KEY_LAST];

    private char last;

    public KeyboardListener(long context)
    {
        super(context);

        this.keyCB = GLFWKeyCallback.create((window, key, scancode, action, mods) -> {
            this.states[key] = action;
            this.actions[key] = (action != GLFW_RELEASE);
        });
        this.charCB = GLFWCharCallback.create((window, codepoint) -> last = (char) codepoint);

        glfwSetKeyCallback(context, this.keyCB);
        glfwSetCharCallback(context, this.charCB);
    }

    @Override
    protected void free()
    {
        this.keyCB.free();
        this.charCB.free();
    }

    @Override
    protected void refresh()
    {
        for(int i = 0; i < this.states.length; i++)
            this.states[i] = -1;
    }

    public boolean isKeyDown(Keys key)
    {
        return this.actions[key.getCode()];
    }

    public boolean isKeyPressed(Keys key)
    {
        return this.states[key.getCode()] == GLFW_PRESS;
    }

    public boolean isKeyReleased(Keys key)
    {
        return this.states[key.getCode()] == GLFW_RELEASE;
    }

    public char getLastChar()
    {
        return this.last;
    }
}
