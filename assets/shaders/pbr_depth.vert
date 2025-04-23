in vec3 a_position;
uniform mat4 u_projViewWorldTrans;

void main() {
    vec4 pos = u_projViewWorldTrans * vec4(a_position, 1.0);

	gl_Position = pos;
}
