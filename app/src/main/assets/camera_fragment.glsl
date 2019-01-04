#version 300 es
#extension GL_OES_EGL_image_external_essl3 : require
precision mediump float;
in vec2 v_texCoord;
uniform samplerExternalOES s_texture;
out vec4 fragColor;
void main(){
  fragColor = texture(s_texture, v_texCoord);
  //fragColor = vec4(1.0, 0.0, 0.0, 1.0);
}

