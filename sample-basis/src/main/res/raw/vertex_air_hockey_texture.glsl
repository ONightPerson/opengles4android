#version 300 es
layout (location = 0) in vec4 position;
layout (location = 1) in vec2 textureCoor;
uniform mat4 matrix;
out vec2 vTextureCoor;
void main() {
    gl_PointSize = 8.0;
    gl_Position = matrix * position;
    vTextureCoor = textureCoor;
}
