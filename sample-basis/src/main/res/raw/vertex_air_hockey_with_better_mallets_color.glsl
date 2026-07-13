#version 300 es
uniform mat4 matrix;
layout (location = 0) in vec4 aPosition;
void main() {
    gl_Position = matrix * aPosition;
}