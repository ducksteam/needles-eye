#version 150

#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

in LOWP vec4 v_color;
in vec2 v_texCoords;

uniform sampler2D u_texture;

uniform vec2 u_screenSize; // screen size in pixels
uniform int u_kernelSize;

out vec4 FragColor;

void main() {

    int kernelHalfExtent = (u_kernelSize - 1)/2;
    vec2 pixel = vec2(1)/u_screenSize;

    vec4 sum = vec4(0);
    for (int i = -kernelHalfExtent; i <= kernelHalfExtent; i++){
        for (int j = -kernelHalfExtent; j <= kernelHalfExtent; j++){
            sum += texture(u_texture, v_texCoords + vec2(i, j) * pixel);
        }
    }

    FragColor = sum/float(u_kernelSize * u_kernelSize);
}
