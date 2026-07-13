#version 300 es
precision mediump float;
uniform sampler2D textureUnit;
in vec2 vTextureCoordinates;
out vec4 fragColor;
void main()                    		
{
    fragColor = texture(textureUnit, vTextureCoordinates);
}