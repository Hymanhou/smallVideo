#version 300 es
layout(location=0) in vec4 a_position;
layout(location=1) in vec4 a_texCoord;
uniform mat4 u_texMatrix;
out vec2 v_texCoord;
void main() {
    gl_Position = a_position;
    v_texCoord = (u_texMatrix * a_texCoord).xy;
}
