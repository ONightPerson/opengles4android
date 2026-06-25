#version 300 es
precision mediump float;
uniform sampler2D textureUnit;
in vec2 vTextureCoor;
out vec4 fragColor;
void main() {
    fragColor = texture(textureUnit, vTextureCoor);
}