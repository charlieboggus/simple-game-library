package com.github.charlieboggus.sgl.core;

import com.github.charlieboggus.sgl.input.KeyboardListener;
import com.github.charlieboggus.sgl.input.MouseListener;

import static org.lwjgl.glfw.GLFW.glfwGetClipboardString;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetClipboardString;

public class Input
{
    public static abstract class InputDevice
    {
        public InputDevice(long context) { }
        protected abstract void free();
        protected abstract void refresh();
    }

    private static InputDevice keyboard;
    private static InputDevice mouse;

    static void create()
    {
        keyboard = new KeyboardListener(Display.getContext());
        mouse = new MouseListener(Display.getContext());
    }

    static void destroy()
    {
        keyboard.free();
        mouse.free();
    }

    static void poll()
    {
        glfwPollEvents();
    }

    static void refresh()
    {
        keyboard.refresh();
        mouse.refresh();
    }

    public static KeyboardListener Keyboard()
    {
        return (KeyboardListener) keyboard;
    }

    public static MouseListener Mouse()
    {
        return (MouseListener) mouse;
    }

    public static void setClipboardString(String str)
    {
        glfwSetClipboardString(Display.getContext(), str);
    }

    public static String getClipboardString()
    {
        return glfwGetClipboardString(Display.getContext());
    }
}
