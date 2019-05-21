#version 330 core

layout (location = 0) in vec2 position;
layout (location = 1) in vec4 color;
layout (location = 2) in vec2 texCoords;

out vec4 Color;
out vec2 TexCoords;

void main()
{
    Color = color;
    TexCoords = texCoords;

    gl_Position = vec4(position, 0.0, 1.0);
}