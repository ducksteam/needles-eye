#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

varying LOWP vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;

uniform vec2 u_screenSize; // screen size in pixels
uniform int u_kernelSize;


void main() {

    int kernelHalfExtent = (u_kernelSize - 1)/2;
    vec2 pixel = vec2(1)/u_screenSize;

    vec4 sum = vec4(0);
    for (int i = -kernelHalfExtent; i <= kernelHalfExtent; i++){
        for (int j = -kernelHalfExtent; j <= kernelHalfExtent; j++){
            sum += texture2D(u_texture, v_texCoords + vec2(i, j) * pixel);
        }
    }

    gl_FragColor = sum/float(u_kernelSize * u_kernelSize);
}
