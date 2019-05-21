package com.github.charlieboggus.sgl.core;

import com.github.charlieboggus.sgl.utility.*;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Display
{
    private static final Logger logger = Logger.getLogger(Display.class);

    private static Configuration config;

    private static long context;

    private static GLFWWindowSizeCallback sizeCB;
    private static GLFWFramebufferSizeCallback fbSizeCB;
    private static GLFWWindowPosCallback posCB;
    private static GLFWWindowFocusCallback focusCB;

    private static int width;
    private static int height;
    private static int viewportWidth;
    private static int viewportHeight;
    private static int positionX;
    private static int positionY;

    private static boolean isFocused;
    private static boolean isFullscreen;
    private static boolean isVsyncEnabled;
    private static boolean viewportChanged;
    private static boolean screenshotTaken;

    private static Color clearColor;
    private static AntialiasMode antialias;
    private static TextureFilterMode filter;

    private static int screenshotID = 0;

    /**
     * Method to create the application display
     *
     * @param cfg Application configuration settings
     * @return true upon successful creation, false otherwise
     */
    static boolean create(Configuration cfg)
    {
        config = cfg;

        // Create a temporary window to get OpenGL capabilities
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        long tmp = glfwCreateWindow(1, 1, "", NULL, NULL);
        glfwMakeContextCurrent(tmp);
        GL.createCapabilities();
        GLCapabilities caps = GL.getCapabilities();
        glfwDestroyWindow(tmp);
        GL.setCapabilities(null);

        if(!caps.OpenGL33)
        {
            logger.fatal("OpenGL 3.3 is not supported by the system!");
            return false;
        }

        // Create the actual window
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, cfg.isResizable() ? GLFW_TRUE : GLFW_FALSE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
        glfwWindowHint(GLFW_SAMPLES, cfg.getAntialiasMode().isMultisampled() ? cfg.getAntialiasMode().getSamples() : 0);
        context = glfwCreateWindow(cfg.getWidth(), cfg.getHeight(), cfg.getTitle(), NULL, NULL);
        if(context == NULL)
        {
            logger.fatal("Failed to create GLFW window!");
            return false;
        }

        // Set window size constraints if they exist
        if(cfg.getMinimumWidth() > 0 || cfg.getMinimumHeight() > 0)
            glfwSetWindowSizeLimits(context, cfg.getMinimumWidth(), cfg.getMinimumHeight(), -1, -1);

        // Center window on primary display if window isn't fullscreen
        GLFWVidMode vm = glfwGetVideoMode(glfwGetPrimaryMonitor());
        if(!cfg.isFullscreen())
            glfwSetWindowPos(context, (vm.width() - cfg.getWidth()) / 2, (vm.height() - cfg.getHeight()) / 2);

        // Make the context current & initialize OpenGL on main thread
        glfwMakeContextCurrent(context);
        glfwSwapInterval(cfg.isVsyncEnabled() ? 1 : 0);
        GL.createCapabilities();

        // Create and set the GLFW callbacks
        sizeCB = GLFWWindowSizeCallback.create((win, w, h) -> viewportChanged = true);
        glfwSetWindowSizeCallback(context, sizeCB);

        fbSizeCB = GLFWFramebufferSizeCallback.create((win, w, h) -> viewportChanged = true);
        glfwSetFramebufferSizeCallback(context, fbSizeCB);

        posCB = GLFWWindowPosCallback.create((win, x, y) -> { positionX = x; positionY = y; });
        glfwSetWindowPosCallback(context, posCB);

        focusCB = GLFWWindowFocusCallback.create((win, focus) -> isFocused = focus);
        glfwSetWindowFocusCallback(context, focusCB);

        // Set the window icon
        try
        {
            try(MemoryStack stack = MemoryStack.stackPush())
            {
                IntBuffer wb = stack.mallocInt(1);
                IntBuffer hb = stack.mallocInt(1);
                IntBuffer cb = stack.mallocInt(1);

                ByteBuffer buffer = FileIO.readByteBuffer(cfg.getIconPath());
                ByteBuffer image = STBImage.stbi_load_from_memory(buffer, wb, hb, cb, 0);
                GLFWImage.Buffer icons = GLFWImage.mallocStack(2, stack);
                icons.position(0).width(wb.get(0)).height(hb.get(0)).pixels(image);
                icons.position(1).width(wb.get(0)).height(hb.get(0)).pixels(image);
                icons.position(0);

                glfwSetWindowIcon(context, icons);
                STBImage.stbi_image_free(image);
            }
        }
        catch (Exception e)
        {
            logger.error("Failed to set window icon!");
        }

        // Initialize class variables
        try(MemoryStack stack = MemoryStack.stackPush())
        {
            IntBuffer a = stack.mallocInt(1);
            IntBuffer b = stack.mallocInt(1);

            // Get window size
            glfwGetWindowSize(context, a, b);
            width = a.get(0);
            height = b.get(0);
            a.clear();
            b.clear();

            // Get framebuffer size
            glfwGetFramebufferSize(context, a, b);
            viewportWidth = a.get(0);
            viewportHeight = b.get(0);
            a.clear();
            b.clear();

            // Get window position
            glfwGetWindowPos(context, a, b);
            positionX = a.get(0);
            positionY = b.get(0);
        }

        isFocused = true;
        isFullscreen = cfg.isFullscreen();
        isVsyncEnabled = cfg.isVsyncEnabled();
        viewportChanged = true;
        screenshotTaken = false;
        clearColor = cfg.getClearColor();
        antialias = cfg.getAntialiasMode();
        filter = cfg.getTextureFilterMode();

        // Finally, show the window after successful creation
        glfwShowWindow(context);

        return true;
    }

    /**
     * Method to destroy Display
     */
    static void destroy()
    {
        sizeCB.free();
        fbSizeCB.free();
        posCB.free();
        focusCB.free();
        glfwDestroyWindow(context);
        GL.setCapabilities(null);
    }

    /**
     * Method to clear the display before each rendering cycle
     */
    static void clear()
    {
        if(viewportChanged)
        {
            try(MemoryStack stack = MemoryStack.stackPush())
            {
                IntBuffer w = stack.mallocInt(1);
                IntBuffer h = stack.mallocInt(1);

                // Get window size
                glfwGetWindowSize(context, w, h);
                width = w.get(0);
                height = h.get(0);
                w.clear();
                h.clear();

                // Get framebuffer size
                glfwGetFramebufferSize(context, w, h);
                viewportWidth = w.get(0);
                viewportHeight = h.get(0);
            }

            glViewport(0, 0, viewportWidth, viewportHeight);
            viewportChanged = false;
        }

        glClearColor(clearColor.r(), clearColor.g(), clearColor.b(), clearColor.a());
        glClear(GL_COLOR_BUFFER_BIT);
    }

    /**
     * Method to refresh the display after each rendering cycle
     */
    static void refresh()
    {
        if(screenshotTaken)
        {
            // Read Pixel data from OpenGL front buffer
            ByteBuffer buffer = BufferUtils.createByteBuffer(viewportWidth * viewportHeight * 4);
            glReadBuffer(GL_FRONT);
            glReadPixels(0, 0, viewportWidth, viewportHeight, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

            // Create new buffered image from pixel data read from front buffer
            BufferedImage image = new BufferedImage(viewportWidth, viewportHeight, BufferedImage.TYPE_INT_ARGB);
            int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
            for(int i = 0; i < viewportHeight; i++)
            {
                for(int j = 0; j < viewportWidth; j++)
                {
                    int r = buffer.get() & 0xFF;
                    int g = buffer.get() & 0xFF;
                    int b = buffer.get() & 0xFF;
                    int a = buffer.get() & 0xFF;

                    pixels[(viewportHeight - i - 1) * viewportWidth + j] = (a << 24) | (r << 16) | (g << 8) | b;
                }
            }

            // Save the buffered image to disk
            try {
                FileIO.writeBufferedImage("screenshot" + screenshotID + ".png", image);
            }
            catch (IOException e) {
                logger.error("Failed to save screenshot: screenshot" + screenshotID + ".png");
            }

            if(config.isDebugging())
                logger.debug("Screenshot saved: screenshot" + screenshotID + ".png");

            screenshotID++;
            screenshotTaken = false;
        }

        glfwSwapBuffers(context);
    }

    /**
     * Method to check if the display should close
     *
     * @return true if display should close, false otherwise
     */
    static boolean shouldClose()
    {
        return glfwWindowShouldClose(context);
    }

    /**
     * Method to get the primary GLFW context
     *
     * @return the primary GLFW context
     */
    static long getContext()
    {
        return context;
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Method to take a screenshot of whatever is renderered to the display
     */
    public static void screenshot()
    {
        screenshotTaken = true;
    }

    /**
     * Method to set the size of the display
     *
     * @param width width to set the display width to
     * @param height height to set the display height to
     */
    public static void setSize(int width, int height)
    {
        GLFWVidMode vm = glfwGetVideoMode(glfwGetPrimaryMonitor());
        if(width <= 0)
            width = 1;
        if(height <= 0)
            height = 1;
        if(width > vm.width())
            width = vm.width();
        if(height > vm.height())
            height = vm.height();

        glfwSetWindowSize(context, width, height);

        viewportChanged = true;
    }

    /**
     * Method to set the position of the display
     *
     * @param x x position to move the display to
     * @param y y position to move the display to
     */
    public static void setPosition(int x, int y)
    {
        GLFWVidMode vm = glfwGetVideoMode(glfwGetPrimaryMonitor());
        if(x < 0)
            x = 0;
        if(y < 0)
            y = 0;
        if(x > vm.width())
            x = (vm.width() - width);
        if(y > vm.height())
            y = (vm.height() - height);

        glfwSetWindowPos(context, x, y);

        viewportChanged = true;
    }

    /**
     * Method to toggle the display between fullscreen and windowed mode
     */
    public static void toggleFullscreen()
    {
        setFullscreen(!isFullscreen);
    }

    /**
     * Method to set the display to either windowed or fullscreen mode
     *
     * @param fullscreen true to set the display fullscreen, false for windowed
     */
    public static void setFullscreen(boolean fullscreen)
    {
        if(isFullscreen == fullscreen)
            return;

        GLFWVidMode vm = glfwGetVideoMode(glfwGetPrimaryMonitor());
        if(width > vm.width())
            width = vm.width();
        if(height > vm.height())
            height = vm.height();

        if(fullscreen)
            glfwSetWindowMonitor(context, glfwGetPrimaryMonitor(), 0, 0, width, height, vm.refreshRate());
        else
        {
            int x = (vm.width() - width) / 2;
            int y = (vm.height() - height) / 2;
            glfwSetWindowMonitor(context, NULL, x, y, width, height, vm.refreshRate());
        }

        isFullscreen = fullscreen;
        viewportChanged = true;
    }

    /**
     * Method to toggle vsync on or off
     */
    public static void toggleVsyncEnabled()
    {
        setVsyncEnabled(!isVsyncEnabled);
    }

    /**
     * Method to turn vsync either on or off
     *
     * @param vsync true to turn vsync on, false to turn vsync off
     */
    public static void setVsyncEnabled(boolean vsync)
    {
        if(isVsyncEnabled == vsync)
            return;

        glfwSwapInterval(vsync ? 1 : 0);
        isVsyncEnabled = vsync;
    }

    /**
     * Method to get the current width of the display
     *
     * @return Current width of the display
     */
    public static int getWidth()
    {
        return width;
    }

    /**
     * Method to get the current height of the display
     *
     * @return Current height of the display
     */
    public static int getHeight()
    {
        return height;
    }

    /**
     * Method to get the current width of the viewport
     *
     * @return Current width of the viewport
     */
    public static int getViewportWidth()
    {
        return viewportWidth;
    }

    /**
     * Method to get the current height of the viewport
     *
     * @return Current height of the viewport
     */
    public static int getViewportHeight()
    {
        return viewportHeight;
    }

    /**
     * Method to get the current x position of the display
     *
     * @return current x position of the display
     */
    public static int getPositionX()
    {
        return positionX;
    }

    /**
     * Method to get the current y position of the display
     *
     * @return current y position of the display
     */
    public static int getPositionY()
    {
        return positionY;
    }

    /**
     * Method to get the current focus state of the display
     *
     * @return true if display is currently focused, false otherwise
     */
    public static boolean isFocused()
    {
        return isFocused;
    }

    /**
     * Method to get the current fullscreen state of the display
     *
     * @return true if display is currently fullscreen, false otherwise
     */
    public static boolean isFullscreen()
    {
        return isFullscreen;
    }

    /**
     * Method to get the current vsync state of the display
     *
     * @return true if vsync is currently enabled, false otherwise
     */
    public static boolean isVsyncEnabled()
    {
        return isVsyncEnabled;
    }

    /**
     * Method to get the Antialias mode
     *
     * @return the Antialias mode that is enabled
     */
    public static AntialiasMode getAntialiasMode()
    {
        return antialias;
    }

    /**
     * Method to get the Texture Filter mode
     *
     * @return the Texture Filter mode that is enabled
     */
    public static TextureFilterMode getFilterMode()
    {
        return filter;
    }
}
