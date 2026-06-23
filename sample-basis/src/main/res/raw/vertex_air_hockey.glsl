#version 300 es
layout (location = 0) in vec4 aPosition;
void main() {
    gl_PointSize = 8.0;
    gl_Position = aPosition;
}
