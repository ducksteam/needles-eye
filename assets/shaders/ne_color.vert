//#version 150
//#define positionFlag
//#define tangentFlag
//#define normalFlag
//#define lightingFlag
//#define ambientCubemapFlag
//#define numDirectionalLights 2
//#define numPointLights 5
//#define numSpotLights 0
//#define texCoord0Flag
//#define diffuseTextureFlag
//#define diffuseTextureCoord texCoord0
//#define normalTextureFlag
//#define normalTextureCoord texCoord0
//#define baseColorFactorFlag
//#define metallicRoughnessTextureFlag
//#define ambientLightFlag
//#define MANUAL_SRGB
//#define GAMMA_CORRECTION 2.2
//#define TS_MANUAL_SRGB
//#define MS_MANUAL_SRGB
//#define v_diffuseUV v_texCoord0
//#define v_normalUV v_texCoord0
//#define v_metallicRoughnessUV v_texCoord0
//#define textureFlag
#line 1

out vec3 v_position;

in vec3 a_position;
uniform mat4 u_projViewTrans;

#if defined(colorFlag)
out vec4 v_color;
in vec4 a_color;
#endif // colorFlag

#ifdef normalFlag
in vec3 a_normal;
uniform mat3 u_normalMatrix;
#ifdef tangentFlag
out mat3 v_TBN;
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

#ifdef textureCoord1Flag
in vec2 a_texCoord1;
out vec2 v_texCoord1;
uniform mat3 u_texCoord1Transform;
#endif // textureCoord1Flag

uniform mat4 u_worldTrans;

void main() {

    #ifdef textureFlag
    v_texCoord0 = (u_texCoord0Transform * vec3(a_texCoord0, 1.0)).xy;
    #endif

    #ifdef textureCoord1Flag
    v_texCoord1 = (u_texCoord1Transform * vec3(a_texCoord1, 1.0)).xy;
    #endif

    #if defined(colorFlag)
    v_color = a_color;
    #endif // colorFlag

    vec4 pos = u_worldTrans * vec4(a_position, 1.0);

    v_position = vec3(pos.xyz) / pos.w;
    gl_Position = u_projViewTrans * pos;


    #if defined(normalFlag)

    vec3 normal = a_normal.xyz;

    // normal new
    #ifdef tangentFlag

    vec3 tangent = a_tangent.xyz;

    vec3 normalW = normalize(vec3(u_normalMatrix * normal.xyz));
    vec3 tangentW = normalize(vec3(u_worldTrans * vec4(tangent, 0.0)));
    vec3 bitangentW = cross(normalW, tangentW) * a_tangent.w;
    v_TBN = mat3(tangentW, bitangentW, normalW);
    #else // tangentFlag != 1
    v_normal = normalize(vec3(u_normalMatrix * normal.xyz));
    #endif
    #endif // normalFlag

}
