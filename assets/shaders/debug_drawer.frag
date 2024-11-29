#version 150

#ifdef GL_ES
precision mediump float;
#endif

in vec4 v_col;
in vec2 v_tex0;

uniform sampler2D u_sampler0;

out vec4 FragColor;

void main() {
    FragColor = v_col * texture(u_sampler0, v_tex0);
}
