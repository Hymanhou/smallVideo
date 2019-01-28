#version 300 es
layout(location=0) in vec4 vPosition;
layout(location=1) in vec4 vCoord;
out vec2 textureCoordinate;
out vec2 blurCoord1s[14];

uniform float mWidth;
uniform float mHeight;

void main( )
{
    gl_Position = vPosition;
    textureCoordinate = vCoord.xy;

    highp float mul_x = 2.0 / mWidth;
    highp float mul_y = 2.0 / mHeight;

    // 14个采样点
    blurCoord1s[0] = vCoord.xy + vec2( 0.0 * mul_x, -10.0 * mul_y );
    blurCoord1s[1] = vCoord.xy + vec2( 8.0 * mul_x, -5.0 * mul_y );
    blurCoord1s[2] = vCoord.xy + vec2( 8.0 * mul_x, 5.0 * mul_y );
    blurCoord1s[3] = vCoord.xy + vec2( 0.0 * mul_x, 10.0 * mul_y );
    blurCoord1s[4] = vCoord.xy + vec2( -8.0 * mul_x, 5.0 * mul_y );
    blurCoord1s[5] = vCoord.xy + vec2( -8.0 * mul_x, -5.0 * mul_y );
    blurCoord1s[6] = vCoord.xy + vec2( 0.0 * mul_x, -6.0 * mul_y );
    blurCoord1s[7] = vCoord.xy + vec2( -4.0 * mul_x, -4.0 * mul_y );
    blurCoord1s[8] = vCoord.xy + vec2( -6.0 * mul_x, 0.0 * mul_y );
    blurCoord1s[9] = vCoord.xy + vec2( -4.0 * mul_x, 4.0 * mul_y );
    blurCoord1s[10] = vCoord.xy + vec2( 0.0 * mul_x, 6.0 * mul_y );
    blurCoord1s[11] = vCoord.xy + vec2( 4.0 * mul_x, 4.0 * mul_y );
    blurCoord1s[12] = vCoord.xy + vec2( 6.0 * mul_x, 0.0 * mul_y );
    blurCoord1s[13] = vCoord.xy + vec2( 4.0 * mul_x, -4.0 * mul_y );
}