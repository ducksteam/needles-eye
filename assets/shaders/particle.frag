#version 150

#ifdef GL_ES
#define LOWP lowp
#define MED mediump
#define HIGH highp
precision mediump float;
#else
#define MED
#define LOWP
#define HIGH
#endif

in vec4 v_color;
in MED vec2 v_texCoords0;
uniform sampler2D u_diffuseTexture;

out vec4 FragColor;

void main() {
    FragColor = texture(u_diffuseTexture, v_texCoords0) * v_color;
}
