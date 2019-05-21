package com.github.charlieboggus.sgl.graphics.g2d;

import com.github.charlieboggus.sgl.core.Display;
import com.github.charlieboggus.sgl.graphics.gl.GLShaderProgram;
import com.github.charlieboggus.sgl.graphics.gl.GLVertexArray;
import com.github.charlieboggus.sgl.graphics.gl.GLVertexBuffer;
import com.github.charlieboggus.sgl.utility.Color;
import com.github.charlieboggus.sgl.utility.Logger;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;

public class SpriteBatch
{
    private static final Logger logger = Logger.getLogger(SpriteBatch.class);

    private final Matrix4f projection = new Matrix4f();
    private final Matrix4f view = new Matrix4f();

    private final GLShaderProgram program;
    private final GLVertexArray vao;
    private final GLVertexBuffer vbo;

    private FloatBuffer vertices;
    private int verticesCount;

    private Texture image0;
    private Camera camera;
    private TrueTypeFont defaultFont;

    private boolean isDrawing;
    private int calls;
    private int totalCalls;

    public SpriteBatch()
    {
        // Initialize Renderer state
        this.vertices = BufferUtils.createFloatBuffer(65536);
        this.verticesCount = 0;

        this.image0 = null;
        this.camera = null;
        this.defaultFont = new TrueTypeFont();

        this.isDrawing = false;
        this.calls = 0;
        this.totalCalls = 0;

        // Create the Shader Program
        this.program = new GLShaderProgram();
        this.program.loadShaders("shaders/renderer.vs", "shaders/renderer.fs");
        this.program.link();
        this.program.validate();

        // Create the Vertex Array & Buffers
        this.vao = new GLVertexArray();
        this.vao.bind();
            this.vbo = new GLVertexBuffer();
            this.vbo.bind();
            this.vbo.uploadBufferData(this.vertices.capacity() * Float.BYTES, GL_DYNAMIC_DRAW);

            // Position Attribute
            this.vao.setVertexAttributePointer(0, 2, GL_FLOAT, 8 * Float.BYTES, 0);
            this.vao.enableVertexAttribute(0);

            // Color Attribute
            this.vao.setVertexAttributePointer(1, 4, GL_FLOAT, 8 * Float.BYTES, 2 * Float.BYTES);
            this.vao.enableVertexAttribute(1);

            // Texture Coordinates Attribute
            this.vao.setVertexAttributePointer(2, 2, GL_FLOAT, 8 * Float.BYTES, 6 * Float.BYTES);
            this.vao.enableVertexAttribute(2);
        this.vao.unbind();
        this.vbo.unbind();
    }

    public void dispose()
    {
        this.vbo.destroy();
        this.vao.destroy();
        this.program.destroy();
        this.defaultFont.dispose();
    }

    public void setCamera(Camera camera)
    {
        this.camera = camera;
    }

    public int getRenderCalls()
    {
        return this.calls;
    }

    public int getTotalRenderCalls()
    {
        return this.totalCalls;
    }

    public void begin()
    {
        if(this.isDrawing)
            logger.error("SpriteBatch is already drawing!");
        else
        {
            // Reset the projection and view matrices at the beginning of each render cycle
            this.projection.identity();
            this.projection.ortho2D(0.0f, Display.getViewportWidth(), Display.getViewportHeight(), 0.0f);
            this.view.identity();
            if(this.camera != null)
                this.view.set(this.camera.getViewMatrix());

            // Bind the shader program & set global uniforms
            this.program.bind();
            this.program.setUniform1i("image0", 0);
            // TODO: global uniforms (like FXAA and stuff)

            // Set the OpenGL state
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            if(Display.getAntialiasMode().isMultisampled())
                glEnable(GL_MULTISAMPLE);

            // Set the renderer state
            this.vertices.clear();
            this.verticesCount = 0;
            this.image0 = null;
            this.isDrawing = true;
            this.calls = 0;
        }
    }

    public void end()
    {
        if(!this.isDrawing)
            logger.error("SpriteBatch is not currently drawing!");
        else
        {
            // Flush the renderer if  there are still vertices remaining
            if(this.verticesCount > 0)
                this.flush();

            // Reset the OpenGL state
            glDisable(GL_BLEND);
            if(Display.getAntialiasMode().isMultisampled())
                glDisable(GL_MULTISAMPLE);

            // Unbind Shader Program
            this.program.unbind();

            // Reset the Renderer state
            this.vertices.clear();
            this.verticesCount = 0;
            this.image0 = null;
            this.isDrawing = false;
            this.calls = 0;
        }
    }

    private void flush()
    {
        // Flip the vertices buffer if there are vertices stored in it to allow reading from it
        if(this.verticesCount > 0)
            this.vertices.flip();

        // Draw
        // TODO: figure out multiple texture units
        this.image0.bind();
        this.vao.bind();
        this.vbo.bind();
        this.vbo.uploadBufferSubData(this.vertices, 0);

        glDrawArrays(GL_TRIANGLES, 0, this.verticesCount);

        this.vao.unbind();
        this.vbo.unbind();
        // TODO: Figure out multiple texture units
        this.image0.unbind();

        // Increment render call counters
        this.calls++;
        this.totalCalls++;

        // Reset the drawing state
        this.vertices.clear();
        this.verticesCount = 0;
        this.image0 = null;
    }

    // Single Texture Draw Methods -------------------------------------------------------------------------------------

    public void drawTexture(Texture texture, float x, float y)
    {
        this.drawTexture(texture, x, y, 0.0f, 1.0f, Color.White, true);
    }

    public void drawTexture(Texture texture, float x, float y, boolean useCamera)
    {
        this.drawTexture(texture, x, y, 0.0f, 1.0f, Color.White, useCamera);
    }

    public void drawTexture(Texture texture, float x, float y, Color c)
    {
        this.drawTexture(texture, x, y, 0.0f, 1.0f, c, true);
    }

    public void drawTexture(Texture texture, float x, float y, Color c, boolean useCamera)
    {
        this.drawTexture(texture, x, y, 0.0f, 1.0f, c, useCamera);
    }

    public void drawTexture(Texture texture, float x, float y, float rotation)
    {
        this.drawTexture(texture, x, y, rotation, 1.0f, Color.White, true);
    }

    public void drawTexture(Texture texture, float x, float y, float rotation, boolean useCamera)
    {
        this.drawTexture(texture, x, y, rotation, 1.0f, Color.White, useCamera);
    }

    public void drawTexture(Texture texture, float x, float y, float rotation, Color c)
    {
        this.drawTexture(texture, x, y, rotation, 1.0f, c, true);
    }

    public void drawTexture(Texture texture, float x, float y, float rotation, Color c, boolean useCamera)
    {
        this.drawTexture(texture, x, y, rotation, 1.0f, c, useCamera);
    }

    public void drawTexture(Texture texture, float x, float y, float rotation, float scale)
    {
        this.drawTexture(texture, x, y, rotation, scale, Color.White, true);
    }

    public void drawTexture(Texture texture, float x, float y, float rotation, float scale, boolean useCamera)
    {
        this.drawTexture(texture, x, y, rotation, scale, Color.White, useCamera);
    }

    public void drawTexture(Texture texture, float x, float y, float rotation, float scale, Color col)
    {
        this.drawTexture(texture, x, y, rotation, scale, col, true);
    }

    /**
     * Method for drawing a given texture to the screen
     *
     * @param texture The texture to draw to the screen
     * @param x x-position to draw the texture at (xpos of upper left corner)
     * @param y y-position to draw the texture at (ypos of upper left corner)
     * @param rotation the angle of rotation to rotate the texture (in degrees)
     * @param scale the scale of the texture
     * @param col the color to tint the texture
     * @param useCamera true to make the drawn texture affected by the camera, false otherwise
     */
    public void drawTexture(Texture texture, float x, float y, float rotation, float scale, Color col, boolean useCamera)
    {
        this.updateCurrentTexture(texture);

        // Create model transformation matrix
        Matrix4f model = new Matrix4f();
        model.translate(new Vector3f(x, y, 0.0f));
        model.translate(new Vector3f(0.5f * texture.getWidth(), 0.5f * texture.getHeight(), 0.0f));
        model.rotateZ((float) Math.toRadians(rotation));
        model.translate(new Vector3f(-0.5f * texture.getWidth(), -0.5f * texture.getHeight(), 0.0f));
        model.scale(new Vector3f(scale * texture.getWidth(), scale * texture.getHeight(), 1.0f));

        // Get MVP transformation matrix
        Matrix4f mvp = new Matrix4f(this.projection).mul(useCamera ? this.view : new Matrix4f()).mul(model);

        // Multiply each vertex by the MVP matrix
        Vector4f v1 = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f).mul(mvp);    // Bottom Left
        Vector4f v2 = new Vector4f(1.0f, 0.0f, 0.0f, 1.0f).mul(mvp);    // Bottom Right
        Vector4f v3 = new Vector4f(1.0f, 1.0f, 0.0f, 1.0f).mul(mvp);    // Top Right
        Vector4f v4 = new Vector4f(0.0f, 1.0f, 0.0f, 1.0f).mul(mvp);    // Top Left

        // Upload transformed vertices to vertex buffer
        this.vertices.put(v4.x).put(v4.y).put(col.r()).put(col.g()).put(col.b()).put(col.a()).put(texture.getS0()).put(texture.getT1());
        this.vertices.put(v2.x).put(v2.y).put(col.r()).put(col.g()).put(col.b()).put(col.a()).put(texture.getS1()).put(texture.getT0());
        this.vertices.put(v1.x).put(v1.y).put(col.r()).put(col.g()).put(col.b()).put(col.a()).put(texture.getS0()).put(texture.getT0());
        this.vertices.put(v4.x).put(v4.y).put(col.r()).put(col.g()).put(col.b()).put(col.a()).put(texture.getS0()).put(texture.getT1());
        this.vertices.put(v3.x).put(v3.y).put(col.r()).put(col.g()).put(col.b()).put(col.a()).put(texture.getS1()).put(texture.getT1());
        this.vertices.put(v2.x).put(v2.y).put(col.r()).put(col.g()).put(col.b()).put(col.a()).put(texture.getS1()).put(texture.getT0());
        this.verticesCount += 6;
    }

    // Text rendering methods ------------------------------------------------------------------------------------------

    public void drawText(String text, float x, float y)
    {
        this.drawText(this.defaultFont, text, x, y, 0.0f, 1.0f, Color.White, true);
    }

    public void drawText(String text, float x, float y, boolean useCamera)
    {
        this.drawText(this.defaultFont, text, x, y, 0.0f, 1.0f, Color.White, useCamera);
    }

    public void drawText(String text, float x, float y, Color c)
    {
        this.drawText(this.defaultFont, text, x, y, 0.0f, 1.0f, c, true);
    }

    public void drawText(String text, float x, float y, Color c, boolean useCamera)
    {
        this.drawText(this.defaultFont, text, x, y, 0.0f, 1.0f, c, useCamera);
    }

    public void drawText(String text, float x, float y, float rotation)
    {
        this.drawText(this.defaultFont, text, x, y, rotation, 1.0f, Color.White, true);
    }

    public void drawText(String text, float x, float y, float rotation, boolean useCamera)
    {
        this.drawText(this.defaultFont, text, x, y, rotation, 1.0f, Color.White, useCamera);
    }

    public void drawText(String text, float x, float y, float rotation, Color c)
    {
        this.drawText(this.defaultFont, text, x, y, rotation, 1.0f, c, true);
    }

    public void drawText(String text, float x, float y, float rotation, Color c, boolean useCamera)
    {
        this.drawText(this.defaultFont, text, x, y, rotation, 1.0f, c, useCamera);
    }

    public void drawText(String text, float x, float y, float rotation, float scale)
    {
        this.drawText(this.defaultFont, text, x, y, rotation, scale, Color.White, true);
    }

    public void drawText(String text, float x, float y, float rotation, float scale, boolean useCamera)
    {
        this.drawText(this.defaultFont, text, x, y, rotation, scale, Color.White, useCamera);
    }

    public void drawText(String text, float x, float y, float rotation, float scale, Color c)
    {
        this.drawText(this.defaultFont, text, x, y, rotation, scale, c, true);
    }

    public void drawText(String text, float x, float y, float rotation, float scale, Color c, boolean useCamera)
    {
        this.drawText(this.defaultFont, text, x, y, rotation, scale, c, useCamera);
    }

    public void drawText(TrueTypeFont font, String text, float x, float y)
    {
        this.drawText(font, text, x, y, 0.0f, 1.0f, Color.White, true);
    }

    public void drawText(TrueTypeFont font, String text, float x, float y, boolean useCamera)
    {
        this.drawText(font, text, x, y, 0.0f, 1.0f, Color.White, useCamera);
    }

    public void drawText(TrueTypeFont font, String text, float x, float y, Color c)
    {
        this.drawText(font, text, x, y, 0.0f, 1.0f, c, true);
    }

    public void drawText(TrueTypeFont font, String text, float x, float y, Color c, boolean useCamera)
    {
        this.drawText(font, text, x, y, 0.0f, 1.0f, c, useCamera);
    }

    public void drawText(TrueTypeFont font, String text, float x, float y, float rotation)
    {
        this.drawText(font, text, x, y, rotation, 1.0f, Color.White, true);
    }

    public void drawText(TrueTypeFont font, String text, float x, float y, float rotation, boolean useCamera)
    {
        this.drawText(font, text, x, y, rotation, 1.0f, Color.White, useCamera);
    }

    public void drawText(TrueTypeFont font, String text, float x, float y, float rotation, Color c)
    {
        this.drawText(font, text, x, y, rotation, 1.0f, c, true);
    }

    public void drawText(TrueTypeFont font, String text, float x, float y, float rotation, Color c, boolean useCamera)
    {
        this.drawText(font, text, x, y, rotation, 1.0f, c, useCamera);
    }

    public void drawText(TrueTypeFont font, String text, float x, float y, float rotation, float scale)
    {
        this.drawText(font, text, x, y, rotation, scale, Color.White, true);
    }

    public void drawText(TrueTypeFont font, String text, float x, float y, float rotation, float scale, boolean useCamera)
    {
        this.drawText(font, text, x, y, rotation, scale, Color.White, useCamera);
    }

    public void drawText(TrueTypeFont font, String text, float x, float y, float rotation, float scale, Color c)
    {
        this.drawText(font, text, x, y, rotation, scale, c, true);
    }

    /**
     * Method for rendering text to the screen (Note: any text rendered using this method will be affected by the SpriteBatch camera if one exists)
     *
     * @param font font to use for rendering text
     * @param text the text to render
     * @param x x position where the text should start
     * @param y y position where the text should start
     * @param rotation the rotation of the text (in degrees)
     * @param scale the scale of the text
     * @param c the color to render the text in
     * @param useCamera set to true for the text to move with the camera, false to lock it in place
     */
    public void drawText(TrueTypeFont font, String text, float x, float y, float rotation, float scale, Color c, boolean useCamera)
    {
        // Check if we need to update the current texture
        this.updateCurrentTexture(font.getFontTexture());

        int textHeight = font.getTextHeight(text);
        float drawX = x;
        float drawY = y;
        if(textHeight > font.getFontHeight())
            drawY += textHeight - font.getFontHeight();

        // Iterate over each character in the string
        for(int i = 0; i < text.length(); i++)
        {
            // Get the current character
            char ch = text.charAt(i);
            if(ch == '\n')
            {
                drawY -= font.getFontHeight();
                drawX = x;
                continue;
            }
            if(ch == '\r')
                continue;

            TrueTypeFont.Glyph g = font.getGlyph(ch);

            // Create model transformation matrix for current character
            Matrix4f model = new Matrix4f();
            model.translate(new Vector3f(drawX, drawY, 0.0f));
            model.translate(new Vector3f(0.5f * g.w, 0.5f * g.h, 0.0f));
            model.rotateZ((float) Math.toRadians(rotation));
            model.translate(new Vector3f(-0.5f * g.w, -0.5f * g.h, 0.0f));
            model.scale(new Vector3f(scale * g.w, scale * g.h, 1.0f));

            // Get MVP transformation matrix
            Matrix4f mvp = new Matrix4f(this.projection).mul(useCamera ? this.view : new Matrix4f()).mul(model);

            // Multiply each quad vertex by the MVP transformation matrix
            Vector4f v1 = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f).mul(mvp);    // Bottom Left
            Vector4f v2 = new Vector4f(1.0f, 0.0f, 0.0f, 1.0f).mul(mvp);    // Bottom Right
            Vector4f v3 = new Vector4f(1.0f, 1.0f, 0.0f, 1.0f).mul(mvp);    // Top Right
            Vector4f v4 = new Vector4f(0.0f, 1.0f, 0.0f, 1.0f).mul(mvp);    // Top Left

            // Get texture coordinates for current glyph
            float s0 = font.getGlyphS0(ch);
            float t0 = font.getGlyphT0(ch);
            float s1 = font.getGlyphS1(ch);
            float t1 = font.getGlyphT1(ch);

            // Upload transformed vertices to vertex buffer
            this.vertices.put(v1.x).put(v1.y).put(c.r()).put(c.g()).put(c.b()).put(c.a()).put(s0).put(t0);  // Bottom Left
            this.vertices.put(v2.x).put(v2.y).put(c.r()).put(c.g()).put(c.b()).put(c.a()).put(s1).put(t0);  // Bottom Right
            this.vertices.put(v3.x).put(v3.y).put(c.r()).put(c.g()).put(c.b()).put(c.a()).put(s1).put(t1);  // Top Right
            this.vertices.put(v1.x).put(v1.y).put(c.r()).put(c.g()).put(c.b()).put(c.a()).put(s0).put(t0);  // Bottom Left
            this.vertices.put(v3.x).put(v3.y).put(c.r()).put(c.g()).put(c.b()).put(c.a()).put(s1).put(t1);  // Top Right
            this.vertices.put(v4.x).put(v4.y).put(c.r()).put(c.g()).put(c.b()).put(c.a()).put(s0).put(t1);  // Top Left
            this.verticesCount += 6;

            // Update the x position we're drawing characters at
            drawX += g.w;
        }
    }

    // Blended texture draw methods ------------------------------------------------------------------------------------

    // TODO: figure this out

    // Utility Methods -------------------------------------------------------------------------------------------------

    /**
     * Method for updating the currently bound texture. If a draw method has been called on a new texture we need to
     * flush the SpriteBatch and unbind the old texture before uploading vertices for and binding the new texture.
     *
     * @param texture the texture to check against the currently bound texture
     */
    private void updateCurrentTexture(Texture texture)
    {
        if(this.image0 == null)
            this.image0 = texture;
        else
        {
            if(this.image0 != texture || this.vertices.remaining() < 32)
            {
                this.flush();
                this.image0 = texture;
            }
        }
    }
}
