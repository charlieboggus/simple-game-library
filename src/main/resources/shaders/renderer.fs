#version 330 core

in vec4 Color;
in vec2 TexCoords;

out vec4 FragColor;

uniform sampler2D image0;

void main()
{
    FragColor = texture(image0, TexCoords) * Color;
}