out vec4 color;

void main() {
    color = vec4(gl_FragCoord.z);
    color.a = 1.0;
}
