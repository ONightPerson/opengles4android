#version 300 es
layout (location = 0) in vec4 aPosition;
layout (location = 1) in vec4 aColor;
uniform mat4 orthoMatrix;
out vec4 vColor;
void main() {
    gl_PointSize = 8.0;
    gl_Position = orthoMatrix * aPosition;
    vColor = aColor;
}
