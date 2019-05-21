package com.github.charlieboggus.sgl.input;

import com.github.charlieboggus.sgl.core.Input;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

import static org.lwjgl.glfw.GLFW.*;

public class MouseListener extends Input.InputDevice
{
    private GLFWMouseButtonCallback mbCB;
    private GLFWCursorPosCallback cursorCB;
    private GLFWScrollCallback scrollCB;

    private final int[] states = new int[8];
    private final boolean[] actions = new boolean[8];
    private final Vector2f cursorPos = new Vector2f();
    private final Vector2f cursorDisp = new Vector2f();
    private final Vector2f scrollOffset = new Vector2f();

    public MouseListener(long context)
    {
        super(context);

        this.mbCB = GLFWMouseButtonCallback.create((window, button, action, mods) -> {
            this.states[button] = action;
            this.actions[button] = (action != GLFW_RELEASE);
        });

        this.cursorCB = GLFWCursorPosCallback.create((window, xpos, ypos) -> {
            this.cursorDisp.x = (float) xpos - this.cursorPos.x;
            this.cursorDisp.y = (float) ypos - this.cursorPos.y;
            this.cursorPos.x = (float) xpos;
            this.cursorPos.y = (float) ypos;
        });

        this.scrollCB = GLFWScrollCallback.create((window, xoff, yoff) -> {
            this.scrollOffset.x = (float) xoff;
            this.scrollOffset.y = (float) yoff;
        });

        glfwSetMouseButtonCallback(context, this.mbCB);
        glfwSetCursorPosCallback(context, this.cursorCB);
        glfwSetScrollCallback(context, this.scrollCB);
    }

    @Override
    protected void free()
    {
        this.mbCB.free();
        this.cursorCB.free();
        this.scrollCB.free();
    }

    @Override
    protected void refresh()
    {
        for(int i = 0; i < this.states.length; i++)
            this.states[i] = -1;
        this.scrollOffset.set(0, 0);
    }

    public boolean isMouseButtonDown(Buttons b)
    {
        return this.actions[b.getCode()];
    }

    public boolean isMouseButtonPressed(Buttons b)
    {
        return this.states[b.getCode()] == GLFW_PRESS;
    }

    public boolean isMouseButtonReleased(Buttons b)
    {
        return this.states[b.getCode()] == GLFW_RELEASE;
    }

    public boolean scrollWheelUp()
    {
        return this.scrollOffset.y > 0.0f;
    }

    public boolean scrollWheelDown()
    {
        return this.scrollOffset.y < 0.0f;
    }

    public boolean scrollWheelLeft()
    {
        return this.scrollOffset.x < 0.0f;
    }

    public boolean scrollWheelRight()
    {
        return this.scrollOffset.x > 0.0f;
    }

    public float getCursorPositionX()
    {
        return this.cursorPos.x;
    }

    public float getCursorPositionY()
    {
        return this.cursorPos.y;
    }

    public Vector2f getCursorPosition()
    {
        return this.cursorPos;
    }

    public float getCursorDisplacementX()
    {
        return this.cursorDisp.x;
    }

    public float getCursorDisplacementY()
    {
        return this.cursorDisp.y;
    }

    public Vector2f getCursorDisplacement()
    {
        return this.cursorDisp;
    }
}
