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

in vec3 a_position;
in vec2 a_texCoord0;
in vec4 a_sizeAndRotation;
in vec4 a_color;

out MED vec2 v_texCoords0;
out vec4 v_color;

uniform mat4 u_projViewTrans;

uniform vec3 u_cameraInvDirection;
uniform vec3 u_cameraRight;
uniform vec3 u_cameraUp;

void main() {
    vec3 right = u_cameraRight;
    vec3 up = u_cameraUp;
    vec3 look = u_cameraInvDirection;

    //Rotate around look
    vec3 axis = look;
    float c = a_sizeAndRotation.z;
    float s = a_sizeAndRotation.w;
    float oc = 1.0 - c;

    mat3 rot = mat3(oc * axis.x * axis.x + c, oc * axis.x * axis.y - axis.z * s,  oc * axis.z * axis.x + axis.y * s,
    oc * axis.x * axis.y + axis.z * s,  oc * axis.y * axis.y + c,           oc * axis.y * axis.z - axis.x * s,
    oc * axis.z * axis.x - axis.y * s,  oc * axis.y * axis.z + axis.x * s,  oc * axis.z * axis.z + c);
    vec3 vec = right * a_sizeAndRotation.x + up * a_sizeAndRotation.y;
    vec *= rot;

    gl_Position = u_projViewTrans * vec4(a_position + vec, 1.0);
    v_texCoords0 = a_texCoord0;
    v_color = a_color;
}
