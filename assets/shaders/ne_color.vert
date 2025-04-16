//#version 150

out vec3 v_position;

in vec3 a_position;
uniform mat4 u_projViewTrans;

uniform mat4 u_worldTrans;

#ifdef normalFlag
    in vec3 a_normal;
    uniform mat3 u_normalMatrix;
    #ifdef tangentFlag
        out mat3 v_TBN; // tangent, binormal, normal all combined into a matrix
    #else
        out vec3 v_normal;
    #endif
#endif // normalFlag

#ifdef tangentFlag
    in vec4 a_tangent;
#endif

#ifdef textureFlag
    in vec2 a_texCoord0;
    out vec2 v_texCoord0;
    uniform mat3 u_texCoord0Transform;
#endif // textureFlag


void main() {
    #ifdef textureFlag
		v_texCoord0 = (u_texCoord0Transform * vec3(a_texCoord0, 1.0)).xy;
    #endif

    vec3 morph_pos = a_position;
    vec4 morph = vec4(morph_pos, 1.0);

    vec4 pos = u_worldTrans * morph;

    v_position = vec3(pos.xyz) / pos.w;
    gl_Position = u_projViewTrans * pos;

    #ifdef normalFlag
    vec3 normal = a_normal;

    #ifdef tangentFlag
    vec3 tangent = a_tangent.xyz;

    vec3 normalW = normalize(vec3(u_normalMatrix * normal.xyz));
    vec3 tangentW = normalize(vec3(u_worldTrans * vec4(tangent, 0.0)));
    vec3 bitangentW = cross(normalW, tangentW) * a_tangent.w;
    v_TBN = mat3(tangentW, bitangentW, normalW);

    #else // no tange
    v_normal = normalize(vec3(u_normalMatrix * normal.xyz));
    #endif

    #endif
}
