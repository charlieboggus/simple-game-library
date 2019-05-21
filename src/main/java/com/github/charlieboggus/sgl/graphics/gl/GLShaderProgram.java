package com.github.charlieboggus.sgl.graphics.gl;

import com.github.charlieboggus.sgl.utility.FileIO;
import com.github.charlieboggus.sgl.utility.Logger;

import org.joml.*;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;

public class GLShaderProgram
{
    private static final Logger logger = Logger.getLogger(GLShaderProgram.class);

    private final int id;
    private int vertexId;
    private int fragmentId;

    public GLShaderProgram()
    {
        this.id = glCreateProgram();
    }

    public void destroy()
    {
        this.unbind();

        if(this.vertexId > 0)
        {
            glDetachShader(this.id, this.vertexId);
            glDeleteShader(this.vertexId);
            this.vertexId = 0;
        }

        if(this.fragmentId > 0)
        {
            glDetachShader(this.id, this.fragmentId);
            glDeleteShader(this.fragmentId);
            this.fragmentId = 0;
        }

        glDeleteProgram(this.id);
    }

    public void bind()
    {
        glUseProgram(this.id);
    }

    public void unbind()
    {
        glUseProgram(0);
    }

    public void loadShaders(String vertexFile, String fragmentFile)
    {
        try
        {
            String vertexSrc = FileIO.readString(vertexFile);
            String fragmentSrc = FileIO.readString(fragmentFile);
            this.attachShaders(vertexSrc, fragmentSrc);
        }
        catch (Exception e)
        {
            logger.error("Failed to load shader files!");
        }
    }

    public void attachShaders(String vertexSrc, String fragmentSrc)
    {
        this.vertexId = this.compileShader(GL_VERTEX_SHADER, vertexSrc);
        if(this.vertexId == -1)
        {
            logger.error("Failed to attach vertex shader!");
        }
        else
            glAttachShader(this.id, this.vertexId);

        this.fragmentId = this.compileShader(GL_FRAGMENT_SHADER, fragmentSrc);
        if(this.fragmentId == -1)
        {
            logger.error("Failed to attach fragment shader!");
        }
        else
            glAttachShader(this.id, this.fragmentId);
    }

    public void link()
    {
        glLinkProgram(this.id);
        if(glGetProgrami(this.id, GL_LINK_STATUS) == 0)
        {
            logger.error("Failed to link shader program!");
            logger.error("Shader Info Log: " + glGetShaderInfoLog(this.id));
        }
    }

    public void validate()
    {
        glValidateProgram(this.id);
        glValidateProgram(this.id);
        if(glGetProgrami(this.id, GL_VALIDATE_STATUS) == 0)
        {
            logger.warning("Failed to validate Shader Program!");
            logger.warning("Shader Info Log: " + glGetShaderInfoLog(this.id));
        }
    }

    private int compileShader(int type, String src)
    {
        if(type != GL_VERTEX_SHADER && type != GL_FRAGMENT_SHADER)
        {
            logger.error("Invalid shader type passed to compileShader!");
            return -1;
        }

        int shader = glCreateShader(type);
        if(shader == 0)
        {
            logger.error("Failed to create " + (type == GL_VERTEX_SHADER ? "vertex" : "fragment") + " shader!");
            return -1;
        }

        glShaderSource(shader, src);
        glCompileShader(shader);
        if(glGetShaderi(shader, GL_COMPILE_STATUS) == 0)
        {
            logger.error("Failed to compile " + (type == GL_VERTEX_SHADER ? "vertex" : "fragment") + " shader!");
            logger.error("Shader info log: " + glGetShaderInfoLog(shader));
            glDeleteShader(shader);
            return -1;
        }

        return shader;
    }

    // Uniform Methods -------------------------------------------------------------------------------------------------

    public int getUniformLocation(String name)
    {
        return glGetUniformLocation(this.id, name);
    }

    public void setUniformBoolean(String name, boolean b)
    {
        this.setUniformBoolean(this.getUniformLocation(name), b);
    }

    public void setUniformBoolean(int location, boolean b)
    {
        glUniform1i(location, b ? 1 : 0);
    }

    public void setUniform1i(String name, int v)
    {
        this.setUniform1i(this.getUniformLocation(name), v);
    }

    public void setUniform1i(int location, int v)
    {
        glUniform1i(location, v);
    }

    public void setUniform1iv(String name, int[] v)
    {
        this.setUniform1iv(this.getUniformLocation(name), v);
    }

    public void setUniform1iv(String name, IntBuffer v)
    {
        this.setUniform1iv(this.getUniformLocation(name), v);
    }

    public void setUniform1iv(int location, int[] v)
    {
        glUniform1iv(location, v);
    }

    public void setUniform1iv(int location, IntBuffer v)
    {
        glUniform1iv(location, v);
    }

    public void setUniform2iv(String name, int[][] v)
    {
        this.setUniform2iv(this.getUniformLocation(name), v);
    }

    public void setUniform2iv(String name, Vector2i v)
    {
        this.setUniform2iv(this.getUniformLocation(name), v);
    }

    public void setUniform2iv(int location, int[][] v)
    {
        int[] glArr = new int[v.length * v[0].length];
        for(int i = 0; i < v.length; i++)
        {
            for(int j = 0; j < v[i].length; j++)
                glArr[i * v[i].length + j] = v[i][j];
        }

        glUniform2iv(location, glArr);
    }

    public void setUniform2iv(int location, Vector2i v)
    {
        try(MemoryStack stack = MemoryStack.stackPush())
        {
            IntBuffer b = stack.mallocInt(2);
            v.get(b);

            glUniform2iv(location, b);
        }
    }

    public void setUniform3iv(String name, Vector3i v)
    {
        this.setUniform3iv(this.getUniformLocation(name), v);
    }

    public void setUniform3iv(int location, Vector3i v)
    {
        try(MemoryStack stack = MemoryStack.stackPush())
        {
            IntBuffer b = stack.mallocInt(3);
            v.get(b);

            glUniform3iv(location, b);
        }
    }

    public void setUniform4iv(String name, Vector4i v)
    {
        this.setUniform4iv(this.getUniformLocation(name), v);
    }

    public void setUniform4iv(int location, Vector4i v)
    {
        try(MemoryStack stack = MemoryStack.stackPush())
        {
            IntBuffer b = stack.mallocInt(4);
            v.get(b);

            glUniform4iv(location, b);
        }
    }

    public void setUniform1f(String name, float v)
    {
        this.setUniform1f(this.getUniformLocation(name), v);
    }

    public void setUniform1f(int location, float v)
    {
        glUniform1f(location, v);
    }

    public void setUniform1fv(String name, float[] v)
    {
        this.setUniform1fv(this.getUniformLocation(name), v);
    }

    public void setUniform1fv(String name, FloatBuffer v)
    {
        this.setUniform1fv(this.getUniformLocation(name), v);
    }

    public void setUniform1fv(int location, float[] v)
    {
        glUniform1fv(location, v);
    }

    public void setUniform1fv(int location, FloatBuffer v)
    {
        glUniform1fv(location, v);
    }

    public void setUniform2fv(String name, float[][] v)
    {
        this.setUniform2fv(this.getUniformLocation(name), v);
    }

    public void setUniform2fv(String name, Vector2f v)
    {
        this.setUniform2fv(this.getUniformLocation(name), v);
    }

    public void setUniform2fv(int location, float[][] v)
    {
        float[] glArr = new float[v.length * v[0].length];
        for(int i = 0; i < v.length; i++)
        {
            for(int j = 0; j < v[i].length; j++)
                glArr[i * v[i].length + j] = v[i][j];
        }

        glUniform2fv(location, glArr);
    }

    public void setUniform2fv(int location, Vector2f v)
    {
        try(MemoryStack stack = MemoryStack.stackPush())
        {
            FloatBuffer b = stack.mallocFloat(2);
            v.get(b);

            glUniform2fv(location, b);
        }
    }

    public void setUniform3fv(String name, Vector3f v)
    {
        this.setUniform3fv(this.getUniformLocation(name), v);
    }

    public void setUniform3fv(int location, Vector3f v)
    {
        try(MemoryStack stack = MemoryStack.stackPush())
        {
            FloatBuffer b = stack.mallocFloat(3);
            v.get(b);

            glUniform3fv(location, b);
        }
    }

    public void setUniform4fv(String name, Vector4f v)
    {
        this.setUniform4fv(this.getUniformLocation(name), v);
    }

    public void setUniform4fv(int location, Vector4f v)
    {
        try(MemoryStack stack = MemoryStack.stackPush())
        {
            FloatBuffer b = stack.mallocFloat(4);
            v.get(b);

            glUniform4fv(location, b);
        }
    }

    public void setUniformMatrix3fv(String name, Matrix3f mat)
    {
        this.setUniformMatrix3fv(this.getUniformLocation(name), mat);
    }

    public void setUniformMatrix3fv(String name, Matrix3f mat, boolean transpose)
    {
        this.setUniformMatrix3fv(this.getUniformLocation(name), mat, transpose);
    }

    public void setUniformMatrix3fv(int location, Matrix3f mat)
    {
        try(MemoryStack stack = MemoryStack.stackPush())
        {
            FloatBuffer b = stack.mallocFloat(9);
            mat.get(b);

            glUniformMatrix3fv(location, false, b);
        }
    }

    public void setUniformMatrix3fv(int location, Matrix3f mat, boolean transpose)
    {
        try(MemoryStack stack = MemoryStack.stackPush())
        {
            FloatBuffer b = stack.mallocFloat(9);
            mat.get(b);

            glUniformMatrix3fv(location, transpose, b);
        }
    }

    public void setUniformMatrix4fv(String name, Matrix4f mat)
    {
        this.setUniformMatrix4fv(this.getUniformLocation(name), mat);
    }

    public void setUniformMatrix4fv(String name, Matrix4f mat, boolean transpose)
    {
        this.setUniformMatrix4fv(this.getUniformLocation(name), mat, transpose);
    }

    public void setUniformMatrix4fv(int location, Matrix4f mat)
    {
        try(MemoryStack stack = MemoryStack.stackPush())
        {
            FloatBuffer b = stack.mallocFloat(16);
            mat.get(b);

            glUniformMatrix4fv(location, false, b);
        }
    }

    public void setUniformMatrix4fv(int location, Matrix4f mat, boolean transpose)
    {
        try(MemoryStack stack = MemoryStack.stackPush())
        {
            FloatBuffer b = stack.mallocFloat(16);
            mat.get(b);

            glUniformMatrix4fv(location, transpose, b);
        }
    }
}
