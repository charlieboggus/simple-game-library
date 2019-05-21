package com.github.charlieboggus.sgl.graphics.g2d;

import com.github.charlieboggus.sgl.core.Display;
import com.github.charlieboggus.sgl.graphics.gl.GLShaderProgram;
import com.github.charlieboggus.sgl.graphics.gl.GLVertexArray;
import com.github.charlieboggus.sgl.graphics.gl.GLVertexBuffer;
import com.github.charlieboggus.sgl.utility.Color;
import com.github.charlieboggus.sgl.utility.Logger;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;

public class ShapeBatch
{
    private static final Logger logger = Logger.getLogger(ShapeBatch.class);

    public static final int DRAW_MODE_POINTS = GL_POINTS;
    public static final int DRAW_MODE_LINES = GL_LINES;
    public static final int DRAW_MODE_FILLED = GL_TRIANGLES;

    public static float DEFAULT_LINE_WIDTH = 1.0f;

    private static final String vertexShader =
            "#version 330 core\n" +
            "layout (location = 0) in vec2 pos;\n" +
            "layout (location = 1) in vec4 col;\n" +
            "out vec4 Color;\n" +
            "void main()\n" +
            "{\n" +
            "Color = col;\n" +
            "gl_Position = vec4(pos, 0.0, 1.0);\n" +
            "}";

    private static final String fragmentShader =
            "#version 330 core\n" +
            "in vec4 Color;\n" +
            "out vec4 FragColor;\n" +
            "void main()\n" +
            "{\n" +
            "FragColor = Color;\n" +
            "}";

    private final Matrix4f projection = new Matrix4f();
    private final Matrix4f view = new Matrix4f();

    private final GLShaderProgram program;
    private final GLVertexArray vao;
    private final GLVertexBuffer vbo;

    private FloatBuffer vertices;
    private int verticesCount;

    private Camera camera;
    private int drawMode;
    private boolean isDrawing;

    public ShapeBatch()
    {
        this.vertices = BufferUtils.createFloatBuffer(65536);
        this.verticesCount = 0;
        this.drawMode = DRAW_MODE_FILLED;
        this.isDrawing = false;
        this.camera = null;

        this.program = new GLShaderProgram();
        this.program.attachShaders(vertexShader, fragmentShader);
        this.program.link();
        this.program.validate();

        this.vao = new GLVertexArray();
        this.vao.bind();
            this.vbo = new GLVertexBuffer();
            this.vbo.bind();
            this.vbo.uploadBufferData(this.vertices.capacity() * Float.BYTES, GL_DYNAMIC_DRAW);

            // Position attribute
            this.vao.setVertexAttributePointer(0, 2, GL_FLOAT, 6 * Float.BYTES, 0);
            this.vao.enableVertexAttribute(0);

            // Color attribute
            this.vao.setVertexAttributePointer(1, 4, GL_FLOAT, 6 * Float.BYTES, 2 * Float.BYTES);
            this.vao.enableVertexAttribute(1);
        this.vao.unbind();
        this.vbo.unbind();
    }

    public void dispose()
    {
        this.vbo.destroy();
        this.vao.destroy();
        this.program.destroy();
    }

    public void setCamera(Camera camera)
    {
        this.camera = camera;
    }

    public void begin()
    {
        this.begin(DRAW_MODE_FILLED);
    }

    public void begin(int mode)
    {
        if(this.isDrawing)
            logger.error("Renderer is currently drawing!");
        else if(mode != DRAW_MODE_FILLED && mode != DRAW_MODE_LINES && mode != DRAW_MODE_POINTS)
            logger.error("Invalid drawing mode!");
        else
        {
            // Reset matrices
            this.projection.identity();
            this.projection.ortho2D(0.0f, Display.getViewportWidth(), Display.getViewportHeight(), 0.0f);
            this.view.identity();
            if(this.camera != null)
                this.view.set(this.camera.getViewMatrix());

            // Bind Shader Program & set global uniforms
            this.program.bind();

            // Set the OpenGL state
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            if(Display.getAntialiasMode().isMultisampled())
                glEnable(GL_MULTISAMPLE);

            this.isDrawing = true;
            this.drawMode = mode;
            this.vertices.clear();
            this.verticesCount = 0;
        }
    }

    public void end()
    {
        if(!this.isDrawing)
            logger.error("Renderer is not currently drawing!");
        else
        {
            // Flush any remaining vertices
            if(this.verticesCount > 0)
                this.flush();

            // Reset OpenGL state
            glDisable(GL_BLEND);
            if(Display.getAntialiasMode().isMultisampled())
                glDisable(GL_MULTISAMPLE);

            // Unbind program
            this.program.unbind();

            // Reset renderer state
            this.isDrawing = false;
            this.vertices.clear();
            this.verticesCount = 0;
        }
    }

    private void flush()
    {
        // Flip the buffer if there are vertices in it
        if(this.verticesCount > 0)
            this.vertices.flip();

        // Draw
        this.vao.bind();
        this.vbo.bind();
        this.vbo.uploadBufferSubData(this.vertices, 0);
        glDrawArrays(this.drawMode, 0, this.verticesCount);
        this.vao.unbind();
        this.vbo.unbind();

        // Reset vertices
        this.vertices.clear();
        this.verticesCount = 0;
    }

    // Point Methods ---------------------------------------------------------------------------------------------------

    public void drawPoint(float x, float y, Color c)
    {
        if(this.vertices.remaining() < 32)
            this.flush();
    }

    // Line Methods ----------------------------------------------------------------------------------------------------

    public void drawLine(float x1, float y1, float x2, float y2, Color c)
    {
        this.drawLine(x1, y1, x2, y2, 0.0f, c, c);
    }

    public void drawLine(float x1, float y1, float x2, float y2, float rotation, Color c)
    {
        this.drawLine(x1, y1, x2, y2, rotation, DEFAULT_LINE_WIDTH, c, c);
    }

    public void drawLine(float x1, float y1, float x2, float y2, float rotation, float width, Color c)
    {
        this.drawLine(x1, y1, x2, y2, rotation, width, c, c);
    }

    public void drawLine(float x1, float y1, float x2, float y2, Color c1, Color c2)
    {
        this.drawLine(x1, y1, x2, y2, 0.0f, DEFAULT_LINE_WIDTH, c1, c2);
    }

    public void drawLine(float x1, float y1, float x2, float y2, float rotation, Color c1, Color c2)
    {
        this.drawLine(x1, y1, x2, y2, rotation, DEFAULT_LINE_WIDTH, c1, c2);
    }

    public void drawLine(float x1, float y1, float x2, float y2, float rotation, float width, Color c1, Color c2)
    {
        if(this.vertices.remaining() < 16)
            this.flush();

        if(this.drawMode == DRAW_MODE_FILLED)
        {
            Vector2f v = new Vector2f(y2 - y1, x1 - x2).normalize();
            width *= 0.5f;

            float tx = v.x * width;
            float ty = v.y * width;
            float midX = ((x1 + tx) + (x2 - tx)) / 2.0f;
            float midY = ((y1 + ty) + (y2 - ty)) / 2.0f;

            Matrix4f model = new Matrix4f();


            Matrix4f mvp = new Matrix4f(this.projection).mul(this.view).mul(model);
        }
        else
        {
            Matrix4f model = new Matrix4f();
            model.translate(new Vector3f(x1, y1, 0.0f));
            model.translate(new Vector3f((x1 + x2) / 2.0f, (y1 + y2) / 2.0f, 0.0f));
            model.rotateZ((float) Math.toRadians(rotation));
            model.translate(new Vector3f(-((x1 + x2) / 2.0f), -((y1 + y2) / 2.0f), 0.0f));

            Matrix4f mvp = new Matrix4f(this.projection).mul(this.view).mul(model);

            Vector4f v1 = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f).mul(mvp);
            Vector4f v2 = new Vector4f(1.0f, 0.0f, 0.0f, 1.0f).mul(mvp);
        }
    }

    // Rectangle Methods -----------------------------------------------------------------------------------------------

    public void drawRectangle(float x, float y, float w, float h, float rot, float scale, Color c)
    {
        this.drawRectangle(x, y, w, h, rot, scale, c, c, c, c);
    }

    public void drawRectangle(float x, float y, float w, float h, float rot, float scale, Color c1, Color c2)
    {
        this.drawRectangle(x, y, w, h, rot, scale, c1, c2, c1, c2);
    }

    public void drawRectangle(float x, float y, float w, float h, float rot, float scale, Color c1, Color c2, Color c3, Color c4)
    {
        if(this.vertices.remaining() < 32)
            this.flush();

        Matrix4f model = new Matrix4f();
        model.translate(new Vector3f(x, y, 0.0f));
        model.translate(new Vector3f(0.5f * w, 0.5f * h, 0.0f));
        model.rotateZ((float) Math.toRadians(rot));
        model.translate(new Vector3f(-0.5f * w, -0.5f * h, 0.0f));
        model.scale(new Vector3f(scale * w, scale * h, 1.0f));

        Matrix4f mvp = new Matrix4f(this.projection).mul(this.view).mul(model);

        Vector4f v1 = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f).mul(mvp);
        Vector4f v2 = new Vector4f(1.0f, 0.0f, 0.0f, 1.0f).mul(mvp);
        Vector4f v3 = new Vector4f(1.0f, 1.0f, 0.0f, 1.0f).mul(mvp);
        Vector4f v4 = new Vector4f(0.0f, 1.0f, 0.0f, 1.0f).mul(mvp);

        if(this.drawMode == DRAW_MODE_LINES)
        {
            this.vertices.put(v1.x).put(v1.y).put(c1.r()).put(c1.g()).put(c1.b()).put(c1.a());
            this.vertices.put(v2.x).put(v2.y).put(c2.r()).put(c2.g()).put(c2.b()).put(c2.a());
            this.vertices.put(v2.x).put(v2.y).put(c2.r()).put(c2.g()).put(c2.b()).put(c2.a());
            this.vertices.put(v3.x).put(v3.y).put(c3.r()).put(c3.g()).put(c3.b()).put(c3.a());
            this.vertices.put(v3.x).put(v3.y).put(c3.r()).put(c3.g()).put(c3.b()).put(c3.a());
            this.vertices.put(v4.x).put(v4.y).put(c4.r()).put(c4.g()).put(c4.b()).put(c4.a());
            this.vertices.put(v4.x).put(v4.y).put(c4.r()).put(c4.g()).put(c4.b()).put(c4.a());
            this.vertices.put(v1.x).put(v1.y).put(c1.r()).put(c1.g()).put(c1.b()).put(c1.a());
            this.verticesCount += 8;
        }
        else
        {
            this.vertices.put(v1.x).put(v1.y).put(c1.r()).put(c1.g()).put(c1.b()).put(c1.a());
            this.vertices.put(v2.x).put(v2.y).put(c2.r()).put(c2.g()).put(c2.b()).put(c2.a());
            this.vertices.put(v3.x).put(v3.y).put(c3.r()).put(c3.g()).put(c3.b()).put(c3.a());
            this.vertices.put(v3.x).put(v3.y).put(c3.r()).put(c3.g()).put(c3.b()).put(c3.a());
            this.vertices.put(v4.x).put(v4.y).put(c4.r()).put(c4.g()).put(c4.b()).put(c4.a());
            this.vertices.put(v1.x).put(v1.y).put(c1.r()).put(c1.g()).put(c1.b()).put(c1.a());
            this.verticesCount += 6;
        }
    }
}
