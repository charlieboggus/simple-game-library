package com.github.charlieboggus.sgl.graphics.gl;

import com.github.charlieboggus.sgl.core.Display;
import com.github.charlieboggus.sgl.utility.Logger;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class GLFramebuffer
{
    private static final Logger logger = Logger.getLogger(GLFramebuffer.class);

    private int id;
    private boolean bound;

    public GLFramebuffer()
    {
        this.id = glGenFramebuffers();
        this.bound = false;
    }

    public void destroy()
    {
        glDeleteFramebuffers(this.id);
    }

    public void bind()
    {
        this.bind(GL_FRAMEBUFFER);
    }

    public void bind(int target)
    {
        glBindFramebuffer(target, this.id);
        this.bound = true;
    }

    public void unbind()
    {
        this.unbind(GL_FRAMEBUFFER);
    }

    public void unbind(int target)
    {
        glBindFramebuffer(target, 0);
        this.bound = false;
    }

    public void clear()
    {
        if(this.bound)
        {
            glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);
        }
    }

    public void createColorAttachment(GLTexture texture)
    {
        this.bind();
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture.getID(), 0);
        if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
            logger.error("Failed to create GLFramebuffer Color Attachment!");
        this.unbind();
    }
}
