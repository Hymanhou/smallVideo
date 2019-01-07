#version 300 es
precision mediump float;
in vec2 v_texCoord;
uniform sampler2D s_texture;
out vec4 fragColor;
void main(){
  fragColor = texture(s_texture, v_texCoord);
  //fragColor = vec4(1.0, 0.0, 0.0, 1.0);
}

