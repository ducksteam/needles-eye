#version 150

in vec4 a_position;
in vec4 a_color;
in vec2 a_texCoord0;

uniform mat4 u_projModelView;
out vec4 v_col;
out vec2 v_tex0;

void main() {
    gl_Position = u_projModelView * a_position;

    v_col = a_color;
    v_col.a *= 255.0/254.0;
    v_tex0 = a_texCoord0;

    gl_PointSize = 1.0;
}
