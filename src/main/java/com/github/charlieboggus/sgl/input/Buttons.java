package com.github.charlieboggus.sgl.input;

import static org.lwjgl.glfw.GLFW.*;

public enum Buttons
{
    Left(GLFW_MOUSE_BUTTON_LEFT, "Left Mouse"),
    Right(GLFW_MOUSE_BUTTON_RIGHT, "Right Mouse"),
    Middle(GLFW_MOUSE_BUTTON_MIDDLE, "Middle Mouse"),
    Button4(GLFW_MOUSE_BUTTON_4, "Mouse 4"),
    Button5(GLFW_MOUSE_BUTTON_5, "Mouse 5"),
    Button6(GLFW_MOUSE_BUTTON_6, "Mouse 6"),
    Button7(GLFW_MOUSE_BUTTON_7, "Mouse 7"),
    Button8(GLFW_MOUSE_BUTTON_8, "Mouse 8");

    private final int code;
    private final String name;

    Buttons(int code, String name)
    {
        this.code = code;
        this.name = name;
    }

    int getCode()
    {
        return this.code;
    }

    public String getName()
    {
        return this.name;
    }
}
