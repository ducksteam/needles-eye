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


//////// compat.frag

// Extensions required for WebGL and some Android versions

#ifdef GLSL3
#define textureCubeLodEXT textureLod
#define texture2DLodEXT textureLod
#else
#ifdef USE_TEXTURE_LOD_EXT
#extension GL_EXT_shader_texture_lod: enable
#else
// Note : "textureCubeLod" is used for compatibility but should be "textureLod" for GLSL #version 130 (OpenGL 3.0+)
#define textureCubeLodEXT textureCubeLod
#define texture2DLodEXT texture2DLod
#endif
#endif

// required to have same precision in both shader for light structure
#ifdef GL_ES
#define LOWP lowp
#define MED mediump
#define HIGH highp
precision highp float;
#else
#define MED
#define LOWP
#define HIGH
#endif

// translate GLSL 120 to 130
#ifdef GLSL3
#define varying in
out vec4 out_FragColor;
#define textureCube texture
#define texture2D texture
#else
#define out_FragColor gl_FragColor
#endif

// force unlitFlag when there is no lighting
#ifndef lightingFlag
#ifndef unlitFlag
#define unlitFlag
#endif
#endif

//////// functions.glsl
// Constants
const float M_PI = 3.141592653589793;
const float c_MinRoughness = 0.04;

// Utilities
#define saturate(_v) clamp((_v), 0.0, 1.0)

// sRGB conversions
vec4 SRGBtoLINEAR(vec4 srgbIn)
{
    #ifdef MANUAL_SRGB
    #ifdef SRGB_FAST_APPROXIMATION
    vec3 linOut = pow(srgbIn.xyz,vec3(2.2));
    #else //SRGB_FAST_APPROXIMATION
    vec3 bLess = step(vec3(0.04045),srgbIn.xyz);
    vec3 linOut = mix( srgbIn.xyz/vec3(12.92), pow((srgbIn.xyz+vec3(0.055))/vec3(1.055),vec3(2.4)), bLess );
    #endif //SRGB_FAST_APPROXIMATION
    return vec4(linOut,srgbIn.w);;
    #else //MANUAL_SRGB
    return srgbIn;
    #endif //MANUAL_SRGB
}

// sRGB conversions for transmission source
vec4 tsSRGBtoLINEAR(vec4 srgbIn)
{
    #ifdef TS_MANUAL_SRGB
    #ifdef TS_SRGB_FAST_APPROXIMATION
    vec3 linOut = pow(srgbIn.xyz,vec3(2.2));
    #else
    vec3 bLess = step(vec3(0.04045),srgbIn.xyz);
    vec3 linOut = mix( srgbIn.xyz/vec3(12.92), pow((srgbIn.xyz+vec3(0.055))/vec3(1.055),vec3(2.4)), bLess );
    #endif
    return vec4(linOut,srgbIn.w);;
    #else
    return srgbIn;
    #endif
}

// sRGB conversions for mirror source
vec4 msSRGBtoLINEAR(vec4 srgbIn)
{
    #ifdef MS_MANUAL_SRGB
    #ifdef MS_SRGB_FAST_APPROXIMATION
    vec3 linOut = pow(srgbIn.xyz,vec3(2.2));
    #else
    vec3 bLess = step(vec3(0.04045),srgbIn.xyz);
    vec3 linOut = mix( srgbIn.xyz/vec3(12.92), pow((srgbIn.xyz+vec3(0.055))/vec3(1.055),vec3(2.4)), bLess );
    #endif
    return vec4(linOut,srgbIn.w);;
    #else
    return srgbIn;
    #endif
}
#ifdef iridescenceFlag

//////// iridescence.glsl
// FROM functions.glsl

float sq(float t)
{
    return t * t;
}

vec2 sq(vec2 t)
{
    return t * t;
}

vec3 sq(vec3 t)
{
    return t * t;
}

vec4 sq(vec4 t)
{
    return t * t;
}

// FROM brdf.glsl

vec3 F_Schlick(vec3 f0, vec3 f90, float VdotH)
{
    return f0 + (f90 - f0) * pow(clamp(1.0 - VdotH, 0.0, 1.0), 5.0);
}

float F_Schlick(float f0, float f90, float VdotH)
{
    float x = clamp(1.0 - VdotH, 0.0, 1.0);
    float x2 = x * x;
    float x5 = x * x2 * x2;
    return f0 + (f90 - f0) * x5;
}

float F_Schlick(float f0, float VdotH)
{
    float f90 = 1.0; //clamp(50.0 * f0, 0.0, 1.0);
    return F_Schlick(f0, f90, VdotH);
}

vec3 F_Schlick(vec3 f0, float f90, float VdotH)
{
    float x = clamp(1.0 - VdotH, 0.0, 1.0);
    float x2 = x * x;
    float x5 = x * x2 * x2;
    return f0 + (f90 - f0) * x5;
}

vec3 F_Schlick(vec3 f0, float VdotH)
{
    float f90 = 1.0; //clamp(dot(f0, vec3(50.0 * 0.33)), 0.0, 1.0);
    return F_Schlick(f0, f90, VdotH);
}

vec3 Schlick_to_F0(vec3 f, vec3 f90, float VdotH) {
    float x = clamp(1.0 - VdotH, 0.0, 1.0);
    float x2 = x * x;
    float x5 = clamp(x * x2 * x2, 0.0, 0.9999);

    return (f - f90 * x5) / (1.0 - x5);
}

float Schlick_to_F0(float f, float f90, float VdotH) {
    float x = clamp(1.0 - VdotH, 0.0, 1.0);
    float x2 = x * x;
    float x5 = clamp(x * x2 * x2, 0.0, 0.9999);

    return (f - f90 * x5) / (1.0 - x5);
}

vec3 Schlick_to_F0(vec3 f, float VdotH) {
    return Schlick_to_F0(f, vec3(1.0), VdotH);
}

float Schlick_to_F0(float f, float VdotH) {
    return Schlick_to_F0(f, 1.0, VdotH);
}

// FROM iridescence.glsl

// XYZ to sRGB color space
const mat3 XYZ_TO_REC709 = mat3(
3.2404542, -0.9692660,  0.0556434,
-1.5371385,  1.8760108, -0.2040259,
-0.4985314,  0.0415560,  1.0572252
);

// Assume air interface for top
// Note: We don't handle the case fresnel0 == 1
vec3 Fresnel0ToIor(vec3 fresnel0) {
    vec3 sqrtF0 = sqrt(fresnel0);
    return (vec3(1.0) + sqrtF0) / (vec3(1.0) - sqrtF0);
}

// Conversion FO/IOR
vec3 IorToFresnel0(vec3 transmittedIor, float incidentIor) {
    return sq((transmittedIor - vec3(incidentIor)) / (transmittedIor + vec3(incidentIor)));
}

// ior is a value between 1.0 and 3.0. 1.0 is air interface
float IorToFresnel0(float transmittedIor, float incidentIor) {
    return sq((transmittedIor - incidentIor) / (transmittedIor + incidentIor));
}

// Fresnel equations for dielectric/dielectric interfaces.
// Ref: https://belcour.github.io/blog/research/2017/05/01/brdf-thin-film.html
// Evaluation XYZ sensitivity curves in Fourier space
vec3 evalSensitivity(float OPD, vec3 shift) {
    float phase = 2.0 * M_PI * OPD * 1.0e-9;
    vec3 val = vec3(5.4856e-13, 4.4201e-13, 5.2481e-13);
    vec3 pos = vec3(1.6810e+06, 1.7953e+06, 2.2084e+06);
    vec3 var = vec3(4.3278e+09, 9.3046e+09, 6.6121e+09);

    vec3 xyz = val * sqrt(2.0 * M_PI * var) * cos(pos * phase + shift) * exp(-sq(phase) * var);
    xyz.x += 9.7470e-14 * sqrt(2.0 * M_PI * 4.5282e+09) * cos(2.2399e+06 * phase + shift[0]) * exp(-4.5282e+09 * sq(phase));
    xyz /= 1.0685e-7;

    vec3 srgb = XYZ_TO_REC709 * xyz;
    return srgb;
}

vec3 evalIridescence(float outsideIOR, float eta2, float cosTheta1, float thinFilmThickness, vec3 baseF0) {
    vec3 I;

    // Force iridescenceIor -> outsideIOR when thinFilmThickness -> 0.0
    float iridescenceIor = mix(outsideIOR, eta2, smoothstep(0.0, 0.03, thinFilmThickness));
    // Evaluate the cosTheta on the base layer (Snell law)
    float sinTheta2Sq = sq(outsideIOR / iridescenceIor) * (1.0 - sq(cosTheta1));

    // Handle TIR:
    float cosTheta2Sq = 1.0 - sinTheta2Sq;
    if (cosTheta2Sq < 0.0) {
        return vec3(1.0);
    }

    float cosTheta2 = sqrt(cosTheta2Sq);

    // First interface
    float R0 = IorToFresnel0(iridescenceIor, outsideIOR);
    float R12 = F_Schlick(R0, cosTheta1);
    float R21 = R12;
    float T121 = 1.0 - R12;
    float phi12 = 0.0;
    if (iridescenceIor < outsideIOR) phi12 = M_PI;
    float phi21 = M_PI - phi12;

    // Second interface
    vec3 baseIOR = Fresnel0ToIor(clamp(baseF0, 0.0, 0.9999)); // guard against 1.0
    vec3 R1 = IorToFresnel0(baseIOR, iridescenceIor);
    vec3 R23 = F_Schlick(R1, cosTheta2);
    vec3 phi23 = vec3(0.0);
    if (baseIOR[0] < iridescenceIor) phi23[0] = M_PI;
    if (baseIOR[1] < iridescenceIor) phi23[1] = M_PI;
    if (baseIOR[2] < iridescenceIor) phi23[2] = M_PI;

    // Phase shift
    float OPD = 2.0 * iridescenceIor * thinFilmThickness * cosTheta2;
    vec3 phi = vec3(phi21) + phi23;

    // Compound terms
    vec3 R123 = clamp(R12 * R23, 1e-5, 0.9999);
    vec3 r123 = sqrt(R123);
    vec3 Rs = sq(T121) * R23 / (vec3(1.0) - R123);

    // Reflectance term for m = 0 (DC term amplitude)
    vec3 C0 = R12 + Rs;
    I = C0;

    // Reflectance term for m > 0 (pairs of diracs)
    vec3 Cm = Rs - T121;
    for (int m = 1; m <= 2; ++m)
    {
        Cm *= r123;
        vec3 Sm = 2.0 * evalSensitivity(float(m) * OPD, float(m) * phi);
        I += Cm * Sm;
    }

    // Since out of gamut colors might be produced, negative color values are clamped to 0.
    return max(I, vec3(0.0));
}
#endif

//////// material.glsl
#ifdef normalFlag
#ifdef tangentFlag
varying mat3 v_TBN;
#else
varying vec3 v_normal;
#endif

#endif //normalFlag

#if defined(colorFlag)
varying vec4 v_color;
#endif

#ifdef blendedFlag
uniform float u_opacity;
#ifdef alphaTestFlag
uniform float u_alphaTest;
#endif //alphaTestFlag
#endif //blendedFlag

#ifdef textureFlag
varying MED vec2 v_texCoord0;
#endif // textureFlag

#ifdef textureCoord1Flag
varying MED vec2 v_texCoord1;
#endif // textureCoord1Flag

// texCoord unit mapping

#ifndef v_diffuseUV
#define v_diffuseUV v_texCoord0
#endif

#ifndef v_emissiveUV
#define v_emissiveUV v_texCoord0
#endif

#ifndef v_normalUV
#define v_normalUV v_texCoord0
#endif

#ifndef v_occlusionUV
#define v_occlusionUV v_texCoord0
#endif

#ifndef v_metallicRoughnessUV
#define v_metallicRoughnessUV v_texCoord0
#endif

#ifndef v_transmissionUV
#define v_transmissionUV v_texCoord0
#endif

#ifndef v_thicknessUV
#define v_thicknessUV v_texCoord0
#endif

#ifndef v_specularFactorUV
#define v_specularFactorUV v_texCoord0
#endif

#ifndef v_specularColorUV
#define v_specularColorUV v_texCoord0
#endif

#ifndef v_iridescenceUV
#define v_iridescenceUV v_texCoord0
#endif

#ifndef v_iridescenceThicknessUV
#define v_iridescenceThicknessUV v_texCoord0
#endif

#ifdef diffuseColorFlag
uniform vec4 u_diffuseColor;
#endif

#ifdef baseColorFactorFlag
uniform vec4 u_BaseColorFactor;
#endif

#ifdef diffuseTextureFlag
uniform sampler2D u_diffuseTexture;
#endif

#ifdef normalTextureFlag
uniform sampler2D u_normalTexture;
uniform float u_NormalScale;
#endif

#ifdef emissiveColorFlag
uniform vec4 u_emissiveColor;
#endif

#ifdef emissiveTextureFlag
uniform sampler2D u_emissiveTexture;
#endif

#ifdef occlusionTextureFlag
uniform sampler2D u_OcclusionSampler;
uniform float u_OcclusionStrength;
#endif

#ifdef metallicRoughnessTextureFlag
uniform sampler2D u_MetallicRoughnessSampler;
#endif

#ifdef transmissionTextureFlag
uniform sampler2D u_transmissionSampler;
#endif

#ifdef transmissionFlag
uniform float u_transmissionFactor;
#endif

#ifdef volumeFlag
uniform float u_thicknessFactor;
uniform float u_attenuationDistance;
uniform vec3 u_attenuationColor;
#endif

#ifdef thicknessTextureFlag
uniform sampler2D u_thicknessSampler;
#endif

#ifdef iorFlag
uniform float u_ior;
#else
#define u_ior 1.5
#endif

#ifdef specularFactorFlag
uniform float u_specularFactor;
#else
#define u_specularFactor 1.0
#endif

#ifdef specularColorFlag
uniform vec3 u_specularColorFactor;
#endif

#ifdef specularFactorTextureFlag
uniform sampler2D u_specularFactorSampler;
#endif

#ifdef specularColorTextureFlag
uniform sampler2D u_specularColorSampler;
#endif

#ifdef iridescenceFlag
uniform float u_iridescenceFactor;
uniform float u_iridescenceIOR;
uniform float u_iridescenceThicknessMin;
uniform float u_iridescenceThicknessMax;
#endif

#ifdef iridescenceTextureFlag
uniform sampler2D u_iridescenceSampler;
#endif

#ifdef iridescenceThicknessTextureFlag
uniform sampler2D u_iridescenceThicknessSampler;
#endif

uniform vec2 u_MetallicRoughnessValues;

// Encapsulate the various inputs used by the various functions in the shading equation
// We store values in structs to simplify the integration of alternative implementations
// PBRSurfaceInfo contains light independant information (surface/material only)
// PBRLightInfo contains light information (incident rays)
struct PBRSurfaceInfo
{
    vec3 n;						  // Normal vector at surface point
    vec3 v;						  // Vector from surface point to camera
    float NdotV;                  // cos angle between normal and view direction
    float perceptualRoughness;    // roughness value, as authored by the model creator (input to shader)
    float metalness;              // metallic value at the surface
    vec3 reflectance0;            // full reflectance color (normal incidence angle)
    vec3 reflectance90;           // reflectance color at grazing angle
    float alphaRoughness;         // roughness mapped to a more linear change in the roughness (proposed by [2])
    vec3 diffuseColor;            // color contribution from diffuse lighting
    vec3 specularColor;           // color contribution from specular lighting

    float thickness;           	  // volume thickness at surface point (used for refraction)

    float specularWeight;		  // Amount of specular for the material (default is 1.0)

#ifdef iridescenceFlag
    float iridescenceFactor;
    float iridescenceIOR;
    float iridescenceThickness;
    vec3 iridescenceFresnel;
    vec3 iridescenceF0;
#endif
};

vec4 getBaseColor()
{
    // The albedo may be defined from a base texture or a flat color
    #ifdef baseColorFactorFlag
    vec4 baseColorFactor = u_BaseColorFactor;
    #else
    vec4 baseColorFactor = vec4(1.0, 1.0, 1.0, 1.0);
    #endif

    #ifdef diffuseTextureFlag
    vec4 baseColor = SRGBtoLINEAR(texture2D(u_diffuseTexture, v_diffuseUV)) * baseColorFactor;
    #else
    vec4 baseColor = baseColorFactor;
    #endif

    #ifdef colorFlag
    baseColor *= v_color;
    #endif
    return baseColor;
}

#ifndef unlitFlag
// Find the normal for this fragment, pulling either from a predefined normal map
// or from the interpolated mesh normal and tangent attributes.
vec3 getNormal()
{
    #ifdef tangentFlag
    #ifdef normalTextureFlag
    vec3 n = texture2D(u_normalTexture, v_normalUV).rgb;
    n = normalize(v_TBN * ((2.0 * n - 1.0) * vec3(u_NormalScale, u_NormalScale, 1.0)));
    #else
    vec3 n = normalize(v_TBN[2].xyz);
    #endif
    #else
    vec3 n = normalize(v_normal);
    #endif

    return n;
}
#endif

float getTransmissionFactor()
{
    #ifdef transmissionFlag
    float transmissionFactor = u_transmissionFactor;
    #ifdef transmissionTextureFlag
    transmissionFactor *= texture2D(u_transmissionSampler, v_transmissionUV).r;
    #endif
    return transmissionFactor;
    #else
    return 0.0;
    #endif
}

float getThickness()
{
    #ifdef volumeFlag
    float thickness = u_thicknessFactor;
    #ifdef thicknessTextureFlag
    thickness *= texture2D(u_thicknessSampler, v_thicknessUV).g;
    #endif
    return thickness;
    #else
    return 0.0;
    #endif
}

#ifdef iridescenceFlag
PBRSurfaceInfo getIridescenceInfo(PBRSurfaceInfo info){
    info.iridescenceFactor = u_iridescenceFactor;
    info.iridescenceIOR = u_iridescenceIOR;
    info.iridescenceThickness = u_iridescenceThicknessMax;

    #ifdef iridescenceTextureFlag
    info.iridescenceFactor *= texture2D(u_iridescenceSampler, v_iridescenceUV).r;
    #endif

    #ifdef iridescenceThicknessTextureFlag
    float thicknessFactor = texture2D(u_iridescenceThicknessSampler, v_iridescenceThicknessUV).g;
    info.iridescenceThickness = mix(u_iridescenceThicknessMin, u_iridescenceThicknessMax, thicknessFactor);
    #endif

    info.iridescenceFresnel = info.specularColor;
    info.iridescenceF0 = info.specularColor;

    if (info.iridescenceThickness == 0.0) {
        info.iridescenceFactor = 0.0;
    }

    if (info.iridescenceFactor > 0.0) {
        info.iridescenceFresnel = evalIridescence(1.0, info.iridescenceIOR, info.NdotV, info.iridescenceThickness, info.specularColor);
        info.iridescenceF0 = Schlick_to_F0(info.iridescenceFresnel, info.NdotV);
    }

    return info;
}
#endif

//////// env.glsl

#ifdef fogFlag
uniform vec4 u_fogColor;

#ifdef fogEquationFlag
uniform vec3 u_fogEquation;
#endif

#endif // fogFlag


#ifdef ambientLightFlag
uniform vec3 u_ambientLight;
#endif // ambientLightFlag


uniform vec4 u_cameraPosition;

uniform mat4 u_worldTrans;

varying vec3 v_position;


#ifdef transmissionSourceFlag
uniform sampler2D u_transmissionSourceSampler;
uniform float u_transmissionSourceMipmapScale;
#endif

uniform mat4 u_projViewTrans;

uniform vec4 u_clippingPlane;

void applyClippingPlane(){
    #ifdef clippingPlaneFlag
    if(dot(v_position - u_clippingPlane.xyz * u_clippingPlane.w, u_clippingPlane.xyz) < 0.0){
        discard;
    }
    #endif
}
#ifndef unlitFlag

//////// lights.glsl

#if numDirectionalLights > 0
struct DirectionalLight
{
    vec3 color;
    vec3 direction;
};
uniform DirectionalLight u_dirLights[numDirectionalLights];
#endif // numDirectionalLights


#if numPointLights > 0
struct PointLight
{
    vec3 color;
    vec3 position;
};
uniform PointLight u_pointLights[numPointLights];
#endif // numPointLights

#if numSpotLights > 0
struct SpotLight
{
    vec3 color;
    vec3 position;
    vec3 direction;
    float cutoffAngle;
    float exponent;
};
uniform SpotLight u_spotLights[numSpotLights];
#endif // numSpotLights


struct PBRLightInfo
{
    float NdotL;                  // cos angle between normal and light direction
    float NdotH;                  // cos angle between normal and half vector
    float LdotH;                  // cos angle between light direction and half vector
    float VdotH;                  // cos angle between view direction and half vector
};

struct PBRLightContribs
{
    vec3 diffuse;
    vec3 specular;
    vec3 transmission;
};


// Basic Lambertian diffuse
// Implementation from Lambert's Photometria https://archive.org/details/lambertsphotome00lambgoog
// See also [1], Equation 1
vec3 diffuse(PBRSurfaceInfo pbrSurface)
{
    return pbrSurface.diffuseColor / M_PI;
}

// The following equation models the Fresnel reflectance term of the spec equation (aka F())
// Implementation of fresnel from [4], Equation 15
vec3 specularReflection(PBRSurfaceInfo pbrSurface, PBRLightInfo pbrLight)
{
    return pbrSurface.reflectance0 + (pbrSurface.reflectance90 - pbrSurface.reflectance0) * pow(clamp(1.0 - pbrLight.VdotH, 0.0, 1.0), 5.0);
}

// This calculates the specular geometric attenuation (aka G()),
// where rougher material will reflect less light back to the viewer.
// This implementation is based on [1] Equation 4, and we adopt their modifications to
// alphaRoughness as input as originally proposed in [2].
float geometricOcclusion(PBRSurfaceInfo pbrSurface, PBRLightInfo pbrLight, float alphaRoughness)
{
    float NdotL = pbrLight.NdotL;
    float NdotV = pbrSurface.NdotV;
    float r = alphaRoughness;

    float attenuationL = 2.0 * NdotL / (NdotL + sqrt(r * r + (1.0 - r * r) * (NdotL * NdotL)));
    float attenuationV = 2.0 * NdotV / (NdotV + sqrt(r * r + (1.0 - r * r) * (NdotV * NdotV)));
    return attenuationL * attenuationV;
}

// The following equation(s) model the distribution of microfacet normals across the area being drawn (aka D())
// Implementation from "Average Irregularity Representation of a Roughened Surface for Ray Reflection" by T. S. Trowbridge, and K. P. Reitz
// Follows the distribution function recommended in the SIGGRAPH 2013 course notes from EPIC Games [1], Equation 3.
float microfacetDistribution(PBRSurfaceInfo pbrSurface, PBRLightInfo pbrLight, float alphaRoughness)
{
    float roughnessSq = alphaRoughness * alphaRoughness;
    float f = (pbrLight.NdotH * roughnessSq - pbrLight.NdotH) * pbrLight.NdotH + 1.0;
    return roughnessSq / (M_PI * f * f);
}

#ifdef volumeFlag

// Compute attenuated light as it travels through a volume.
vec3 applyVolumeAttenuation(vec3 radiance, float transmissionDistance, PBRSurfaceInfo pbrSurface)
{
    if (u_attenuationDistance == 0.0)
    {
        // Attenuation distance is +â (which we indicate by zero), i.e. the transmitted color is not attenuated at all.
        return radiance;
    }
    else
    {
        // Compute light attenuation using Beer's law.
        vec3 attenuationCoefficient = -log(u_attenuationColor) / u_attenuationDistance;
        vec3 transmittance = exp(-attenuationCoefficient * transmissionDistance); // Beer's law
        return transmittance * radiance;
    }
}


vec3 getVolumeTransmissionRay(vec3 n, vec3 v, PBRSurfaceInfo pbrSurface)
{
    // Direction of refracted light.
    vec3 refractionVector = refract(-v, n, 1.0 / u_ior);

    // Compute rotation-independant scaling of the model matrix.
    vec3 modelScale;
    modelScale.x = length(vec3(u_worldTrans[0].xyz));
    modelScale.y = length(vec3(u_worldTrans[1].xyz));
    modelScale.z = length(vec3(u_worldTrans[2].xyz));

    // The thickness is specified in local space.
    return normalize(refractionVector) * pbrSurface.thickness * modelScale;
}

#endif

float applyIorToRoughness(float roughness)
{
    // Scale roughness with IOR so that an IOR of 1.0 results in no microfacet refraction and
    // an IOR of 1.5 results in the default amount of microfacet refraction.
    return roughness * clamp(u_ior * 2.0 - 2.0, 0.0, 1.0);
}

vec3 getLightTransmission(PBRSurfaceInfo pbrSurface, vec3 l)
{
    vec3 n = pbrSurface.n;
    vec3 v = pbrSurface.v;

    vec3 l_mirror = normalize(l + 2.0*n*dot(-l, n));     // Mirror light reflection vector on surface
    vec3 h = normalize(l_mirror+v);               // Half vector between both l_mirror and v

    float NdotV = pbrSurface.NdotV;
    float NdotL = clamp(dot(n, l_mirror), 0.001, 1.0);
    float NdotH = clamp(dot(n, h), 0.0, 1.0);
    float LdotH = clamp(dot(l_mirror, h), 0.0, 1.0);
    float VdotH = clamp(dot(v, h), 0.0, 1.0);

    PBRLightInfo pbrLight = PBRLightInfo(
    NdotL,
    NdotH,
    LdotH,
    VdotH
    );

    #ifdef iorFlag
    float alphaRoughness = applyIorToRoughness(pbrSurface.alphaRoughness);
    #else
    float alphaRoughness = pbrSurface.alphaRoughness;
    #endif

    // Calculate the shading terms for the microfacet specular shading model
    vec3 F = specularReflection(pbrSurface, pbrLight);
    float G = geometricOcclusion(pbrSurface, pbrLight, alphaRoughness);
    float D = microfacetDistribution(pbrSurface, pbrLight, alphaRoughness);

    // Calculation of analytical lighting contribution
    return (1.0 - F) * diffuse(pbrSurface) * D * G  / (4.0 * NdotL * NdotV);
}

// Light contribution calculation independent of light type
// l is a unit vector from surface point to light
PBRLightContribs getLightContribution(PBRSurfaceInfo pbrSurface, vec3 l, vec3 color)
{
    vec3 n = pbrSurface.n;
    vec3 v = pbrSurface.v;
    vec3 h = normalize(l+v);               // Half vector between both l and v

    float NdotV = pbrSurface.NdotV;
    float NdotL = clamp(dot(n, l), 0.001, 1.0);
    float NdotH = clamp(dot(n, h), 0.0, 1.0);
    float LdotH = clamp(dot(l, h), 0.0, 1.0);
    float VdotH = clamp(dot(v, h), 0.0, 1.0);

    PBRLightInfo pbrLight = PBRLightInfo(
    NdotL,
    NdotH,
    LdotH,
    VdotH
    );

    // Calculate the shading terms for the microfacet specular shading model
    vec3 F = specularReflection(pbrSurface, pbrLight) * pbrSurface.specularWeight;
    float G = geometricOcclusion(pbrSurface, pbrLight, pbrSurface.alphaRoughness);
    float D = microfacetDistribution(pbrSurface, pbrLight, pbrSurface.alphaRoughness);

    // Calculation of analytical lighting contribution
    #ifdef iridescenceFlag
    vec3 iridescenceFresnelMax = vec3(max(max(pbrSurface.iridescenceFresnel.r, pbrSurface.iridescenceFresnel.g), pbrSurface.iridescenceFresnel.b));
    vec3 lam_F = mix(F, iridescenceFresnelMax * pbrSurface.specularWeight, pbrSurface.iridescenceFactor);
    vec3 diffuseContrib = (1.0 - lam_F) * diffuse(pbrSurface);

    vec3 ggx_F = mix(F, pbrSurface.iridescenceFresnel, pbrSurface.iridescenceFactor);
    vec3 specContrib = ggx_F * G * D / (4.0 * NdotL * NdotV);

    #else
    vec3 diffuseContrib = (1.0 - F) * diffuse(pbrSurface);
    vec3 specContrib = F * G * D / (4.0 * NdotL * NdotV);
    #endif

    // Obtain final intensity as reflectance (BRDF) scaled by the energy of the light (cosine law)
    vec3 factor = color * NdotL;

    // transmission
    #ifdef transmissionFlag
    vec3 transmittedLight = getLightTransmission(pbrSurface, l);

    #ifdef volumeFlag
    vec3 transmissionRay = getVolumeTransmissionRay(n, v, pbrSurface);
    transmittedLight = applyVolumeAttenuation(transmittedLight, length(transmissionRay), pbrSurface);
    #endif


    #else
    vec3 transmittedLight = vec3(0.0);
    #endif


    return PBRLightContribs(diffuseContrib * factor, specContrib * factor, transmittedLight * factor);
}

#if numDirectionalLights > 0
PBRLightContribs getDirectionalLightContribution(PBRSurfaceInfo pbrSurface, DirectionalLight light)
{
    vec3 l = normalize(-light.direction);  // Vector from surface point to light
    return getLightContribution(pbrSurface, l, light.color);
}
#endif

#if numPointLights > 0
PBRLightContribs getPointLightContribution(PBRSurfaceInfo pbrSurface, PointLight light)
{
    // light direction and distance
    vec3 d = light.position - v_position.xyz;
    float dist2 = dot(d, d);
    d *= inversesqrt(dist2);

    return getLightContribution(pbrSurface, d, light.color / (1.0 + dist2));
}
#endif

#if numSpotLights > 0
PBRLightContribs getSpotLightContribution(PBRSurfaceInfo pbrSurface, SpotLight light)
{
    // light distance
    vec3 d = light.position - v_position.xyz;
    float dist2 = dot(d, d);
    d *= inversesqrt(dist2);

    // light direction
    vec3 l = normalize(-light.direction);  // Vector from surface point to light

    // from https://github.com/KhronosGroup/glTF/blob/master/extensions/2.0/Khronos/KHR_lights_punctual/README.md#inner-and-outer-cone-angles
    float lightAngleOffset = light.cutoffAngle;
    float lightAngleScale = light.exponent;

    float cd = dot(l, d);
    float angularAttenuation = saturate(cd * lightAngleScale + lightAngleOffset);
    angularAttenuation *= angularAttenuation;

    return getLightContribution(pbrSurface, d, light.color * (angularAttenuation / (1.0 + dist2)));
}
#endif

//////// shadows.glsl
#ifdef shadowMapFlag
uniform float u_shadowBias;
uniform sampler2D u_shadowTexture;
uniform float u_shadowPCFOffset;
varying vec3 v_shadowMapUv;

#ifdef numCSM

// arrays of samplers don't seem to work well with ANGLE/DXD11 on Windows so use invididual uniforms instead
uniform sampler2D u_csmSamplers0;
uniform sampler2D u_csmSamplers1;
uniform sampler2D u_csmSamplers2;
uniform sampler2D u_csmSamplers3;
uniform sampler2D u_csmSamplers4;
uniform sampler2D u_csmSamplers5;
uniform sampler2D u_csmSamplers6;
uniform sampler2D u_csmSamplers7;
uniform vec2 u_csmPCFClip[numCSM];
varying vec3 v_csmUVs[numCSM];

float getCSMShadowness(sampler2D sampler, vec3 uv, vec2 offset){
    const vec4 bitShifts = vec4(1.0, 1.0 / 255.0, 1.0 / 65025.0, 1.0 / 16581375.0);
    return step(uv.z, dot(texture2D(sampler, uv.xy + offset), bitShifts) + u_shadowBias); // (1.0/255.0)
}

float getCSMShadow(sampler2D sampler, vec3 uv, float pcf){
    return (
    getCSMShadowness(sampler, uv, vec2(pcf,pcf)) +
    getCSMShadowness(sampler, uv, vec2(-pcf,pcf)) +
    getCSMShadowness(sampler, uv, vec2(pcf,-pcf)) +
    getCSMShadowness(sampler, uv, vec2(-pcf,-pcf)) ) * 0.25;
}
float getShadow()
{
    for(int i=0 ; i<numCSM ; i++){
        vec2 pcfClip = u_csmPCFClip[i];
        float pcf = pcfClip.x;
        float clip = pcfClip.y;
        vec3 uv = v_csmUVs[i];
        if(uv.x >= clip && uv.x <= 1.0 - clip &&
        uv.y >= clip && uv.y <= 1.0 - clip &&
        uv.z >= 0.0 && uv.z <= 1.0){

            #if numCSM > 0
            if(i == 0) return getCSMShadow(u_csmSamplers0, uv, pcf);
            #endif
            #if numCSM > 1
            if(i == 1) return getCSMShadow(u_csmSamplers1, uv, pcf);
            #endif
            #if numCSM > 2
            if(i == 2) return getCSMShadow(u_csmSamplers2, uv, pcf);
            #endif
            #if numCSM > 3
            if(i == 3) return getCSMShadow(u_csmSamplers3, uv, pcf);
            #endif
            #if numCSM > 4
            if(i == 4) return getCSMShadow(u_csmSamplers4, uv, pcf);
            #endif
            #if numCSM > 5
            if(i == 5) return getCSMShadow(u_csmSamplers5, uv, pcf);
            #endif
            #if numCSM > 6
            if(i == 6) return getCSMShadow(u_csmSamplers6, uv, pcf);
            #endif
            #if numCSM > 7
            if(i == 7) return getCSMShadow(u_csmSamplers7, uv, pcf);
            #endif
        }
    }
    // default map
    return getCSMShadow(u_shadowTexture, v_shadowMapUv, u_shadowPCFOffset);
}

#else

float getShadowness(vec2 offset)
{
    const vec4 bitShifts = vec4(1.0, 1.0 / 255.0, 1.0 / 65025.0, 1.0 / 16581375.0);
    return step(v_shadowMapUv.z, dot(texture2D(u_shadowTexture, v_shadowMapUv.xy + offset), bitShifts) + u_shadowBias); // (1.0/255.0)
}

float getShadow()
{
    return (//getShadowness(vec2(0,0)) +
    getShadowness(vec2(u_shadowPCFOffset, u_shadowPCFOffset)) +
    getShadowness(vec2(-u_shadowPCFOffset, u_shadowPCFOffset)) +
    getShadowness(vec2(u_shadowPCFOffset, -u_shadowPCFOffset)) +
    getShadowness(vec2(-u_shadowPCFOffset, -u_shadowPCFOffset))) * 0.25;
}

#endif

#endif //shadowMapFlag
#endif
#ifdef USE_IBL

//////// ibl.glsl
#ifdef ENV_ROTATION
uniform mat3 u_envRotation;
#endif


uniform samplerCube u_DiffuseEnvSampler;

#ifdef diffuseSpecularEnvSeparateFlag
uniform samplerCube u_SpecularEnvSampler;
#else
#define u_SpecularEnvSampler u_DiffuseEnvSampler
#endif

#ifdef brdfLUTTexture
uniform sampler2D u_brdfLUT;
#endif

#ifdef USE_TEX_LOD
uniform float u_mipmapScale; // = 9.0 for resolution of 512x512
#endif

#ifdef mirrorSpecularFlag
uniform sampler2D u_mirrorSpecularSampler;
uniform float u_mirrorMipmapScale;
uniform vec3 u_mirrorNormal;
#endif

uniform vec2 u_viewportInv;

// Calculation of the lighting contribution from an optional Image Based Light source.
// Precomputed Environment Maps are required uniform inputs and are computed as outlined in [1].
// See our README.md on Environment Maps [3] for additional discussion.

vec2 sampleBRDF(PBRSurfaceInfo pbrSurface)
{
    #ifdef brdfLUTTexture
    vec2 brdfSamplePoint = clamp(vec2(pbrSurface.NdotV, 1.0 - pbrSurface.perceptualRoughness), vec2(0.0, 0.0), vec2(1.0, 1.0));
    return texture2D(u_brdfLUT, brdfSamplePoint).xy;
    #else // TODO not sure about how to compute it ...
    return vec2(pbrSurface.NdotV, pbrSurface.perceptualRoughness);
    #endif
}

#ifdef transmissionSourceFlag


vec3 getTransmissionSample(vec2 fragCoord, float roughness)
{
    #ifdef USE_TEX_LOD
    float framebufferLod = u_transmissionSourceMipmapScale * applyIorToRoughness(roughness);
    vec3 transmittedLight = tsSRGBtoLINEAR(texture2DLodEXT(u_transmissionSourceSampler, fragCoord.xy, framebufferLod)).rgb;
    #else
    vec3 transmittedLight = tsSRGBtoLINEAR(texture2D(u_transmissionSourceSampler, fragCoord.xy)).rgb;
    #endif
    return transmittedLight;
}


vec3 getIBLTransmissionContribution(PBRSurfaceInfo pbrSurface, vec3 n, vec3 v, vec2 brdf)
{
    #ifdef volumeFlag
    // Compute transmission ray in order to change view angle with IBL
    vec3 transmissionRay = getVolumeTransmissionRay(n, -v, pbrSurface);
    vec3 refractedRayExit = v_position + transmissionRay;
    #else
    vec3 refractedRayExit = v_position;
    #endif

    // Project refracted vector on the framebuffer, while mapping to normalized device coordinates.
    vec4 ndcPos = u_projViewTrans * vec4(refractedRayExit, 1.0);
    vec2 refractionCoords = ndcPos.xy / ndcPos.w;
    refractionCoords += 1.0;
    refractionCoords /= 2.0;

    // Sample framebuffer to get pixel the refracted ray hits.
    vec3 transmittedLight = getTransmissionSample(refractionCoords, pbrSurface.perceptualRoughness);

    #ifdef volumeFlag
    transmittedLight = applyVolumeAttenuation(transmittedLight, length(transmissionRay), pbrSurface);
    #endif

    vec3 specularColor = pbrSurface.reflectance0 * brdf.x + pbrSurface.reflectance90 * brdf.y;

    return (1.0 - specularColor) * transmittedLight * pbrSurface.diffuseColor;
}

#else

vec3 getIBLTransmissionContribution(PBRSurfaceInfo pbrSurface, vec3 n, vec3 v, vec2 brdf)
{
    #ifdef volumeFlag
    // Compute transmission ray in order to change view angle with IBL
    vec3 transmissionRay = getVolumeTransmissionRay(n, -v, pbrSurface);
    vec3 refractedRayExit = v_position + transmissionRay;
    v = normalize(refractedRayExit - u_cameraPosition.xyz);
    #endif

    #ifdef ENV_ROTATION
    vec3 specularDirection = u_envRotation * v;
    #else
    vec3 specularDirection = v;
    #endif

    #ifdef USE_TEX_LOD
    // IOR has impact on roughness
    #ifdef iorFlag
    float lod = applyIorToRoughness(pbrSurface.perceptualRoughness) * u_mipmapScale;
    #else
    float lod = pbrSurface.perceptualRoughness * u_mipmapScale;
    #endif


    vec3 specularLight = SRGBtoLINEAR(textureCubeLodEXT(u_SpecularEnvSampler, specularDirection, lod)).rgb;
    #else
    vec3 specularLight = SRGBtoLINEAR(textureCube(u_SpecularEnvSampler, specularDirection)).rgb;
    #endif


    vec3 specularColor = pbrSurface.reflectance0 * brdf.x + pbrSurface.reflectance90 * brdf.y;

    vec3 attenuatedColor = specularLight;

    #ifdef volumeFlag
    attenuatedColor = applyVolumeAttenuation(attenuatedColor, length(transmissionRay), pbrSurface);
    #endif

    return (1.0 - specularColor) * attenuatedColor * pbrSurface.diffuseColor;
}

#endif


PBRLightContribs getIBLContribution(PBRSurfaceInfo pbrSurface, vec3 n, vec3 reflection)
{
    vec2 brdf = sampleBRDF(pbrSurface);

    #ifdef ENV_ROTATION
    vec3 diffuseDirection = u_envRotation * n;
    #else
    vec3 diffuseDirection = n;
    #endif
    vec3 diffuseLight = SRGBtoLINEAR(textureCube(u_DiffuseEnvSampler, diffuseDirection)).rgb;

    #ifdef mirrorSpecularFlag
    float lod = (pbrSurface.perceptualRoughness * u_mirrorMipmapScale);
    vec2 mirrorCoord = gl_FragCoord.xy * u_viewportInv;

    // normal perturbation
    vec3 i1 = reflect(reflection, n);
    vec3 i2 = reflect(reflection, u_mirrorNormal);
    vec2 p = (u_projViewTrans * vec4(i2 - i1, 0.0)).xy;
    mirrorCoord += p / 2.0;
    mirrorCoord.x = 1.0 - mirrorCoord.x;

    vec3 specularLight = msSRGBtoLINEAR(texture2DLodEXT(u_mirrorSpecularSampler, mirrorCoord, lod)).rgb;

    #else

    #ifdef ENV_ROTATION
    vec3 specularDirection = u_envRotation * reflection;
    #else
    vec3 specularDirection = reflection;
    #endif

    #ifdef USE_TEX_LOD
    float lod = (pbrSurface.perceptualRoughness * u_mipmapScale);
    vec3 specularLight = SRGBtoLINEAR(textureCubeLodEXT(u_SpecularEnvSampler, specularDirection, lod)).rgb;
    #else
    vec3 specularLight = SRGBtoLINEAR(textureCube(u_SpecularEnvSampler, specularDirection)).rgb;
    #endif

    #endif


    #ifdef iridescenceFlag

    // GGX
    vec3 ggx_Fr = max(vec3(1.0 - pbrSurface.perceptualRoughness), pbrSurface.specularColor) - pbrSurface.specularColor;
    vec3 ggx_k_S = mix(pbrSurface.specularColor + ggx_Fr * pow(1.0 - pbrSurface.NdotV, 5.0), pbrSurface.iridescenceFresnel, pbrSurface.iridescenceFactor);
    vec3 ggx_FssEss = ggx_k_S * brdf.x + brdf.y;

    vec3 specular = specularLight * ggx_FssEss * pbrSurface.specularWeight;

    // Lambertian
    vec3 iridescenceF0Max = vec3(max(max(pbrSurface.iridescenceF0.r, pbrSurface.iridescenceF0.g), pbrSurface.iridescenceF0.b));
    vec3 mixedF0 = mix(pbrSurface.specularColor, iridescenceF0Max, pbrSurface.iridescenceFactor);

    vec3 lam_Fr = max(vec3(1.0 - pbrSurface.perceptualRoughness), mixedF0) - mixedF0;
    vec3 lam_k_S = mixedF0 + lam_Fr * pow(1.0 - pbrSurface.NdotV, 5.0);
    vec3 lam_FssEss = pbrSurface.specularWeight * lam_k_S * brdf.x + brdf.y;

    float Ems = (1.0 - (brdf.x + brdf.y));
    vec3 F_avg = pbrSurface.specularWeight * (mixedF0 + (1.0 - mixedF0) / 21.0);
    vec3 FmsEms = Ems * lam_FssEss * F_avg / (1.0 - F_avg * Ems);
    vec3 k_D = pbrSurface.diffuseColor * (1.0 - lam_FssEss + FmsEms);

    vec3 diffuse = (FmsEms + k_D) * diffuseLight;

    #else
    vec3 diffuse = diffuseLight * pbrSurface.diffuseColor;
    vec3 specular = specularLight * (pbrSurface.specularColor * brdf.x + brdf.y) * pbrSurface.specularWeight;
    #endif

    #ifdef transmissionFlag
    vec3 transmission = getIBLTransmissionContribution(pbrSurface, n, -pbrSurface.v, brdf);
    #else
    vec3 transmission = vec3(0.0);
    #endif

    return PBRLightContribs(diffuse, specular, transmission);
}
#endif

#ifdef unlitFlag

void main() {
    vec4 baseColor = getBaseColor();

    vec3 color = baseColor.rgb;

    // final frag color
    #ifdef GAMMA_CORRECTION
    out_FragColor = vec4(pow(color,vec3(1.0/GAMMA_CORRECTION)), baseColor.a);
    #else
    out_FragColor = vec4(color, baseColor.a);
    #endif

    // Blending and Alpha Test
    #ifdef blendedFlag
    out_FragColor.a = baseColor.a * u_opacity;
    #ifdef alphaTestFlag
    if (out_FragColor.a <= u_alphaTest)
    discard;
    #endif
    #else
    out_FragColor.a = 1.0;
    #endif
    applyClippingPlane();
}

#else

void main() {

    // Metallic and Roughness material properties are packed together
    // In glTF, these factors can be specified by fixed scalar values
    // or from a metallic-roughness map
    float perceptualRoughness = u_MetallicRoughnessValues.y;
    float metallic = u_MetallicRoughnessValues.x;
    #ifdef metallicRoughnessTextureFlag
    // Roughness is stored in the 'g' channel, metallic is stored in the 'b' channel.
    // This layout intentionally reserves the 'r' channel for (optional) occlusion map data
    vec4 mrSample = texture2D(u_MetallicRoughnessSampler, v_metallicRoughnessUV);
    perceptualRoughness = mrSample.g * perceptualRoughness;
    metallic = mrSample.b * metallic;
    #endif
    perceptualRoughness = clamp(perceptualRoughness, c_MinRoughness, 1.0);
    metallic = clamp(metallic, 0.0, 1.0);
    // Roughness is authored as perceptual roughness; as is convention,
    // convert to material roughness by squaring the perceptual roughness [2].
    float alphaRoughness = perceptualRoughness * perceptualRoughness;

    vec4 baseColor = getBaseColor();

    #ifdef iorFlag
    vec3 f0 = vec3(pow(( u_ior - 1.0) /  (u_ior + 1.0), 2.0));
    #else
    vec3 f0 = vec3(0.04); // from ior 1.5 value
    #endif

    // Specular
    #ifdef specularFlag
    float specularFactor = u_specularFactor;
    #ifdef specularFactorTextureFlag
    specularFactor *= texture2D(u_specularFactorSampler, v_specularFactorUV).a;
    #endif
    #ifdef specularColorFlag
    vec3 specularColorFactor = u_specularColorFactor;
    #else
    vec3 specularColorFactor = vec3(1.0);
    #endif
    #ifdef specularTextureFlag
    specularColorFactor *= SRGBtoLINEAR(texture2D(u_specularColorSampler, v_specularColorUV)).rgb;
    #endif
    // Compute specular
    vec3 dielectricSpecularF0 = min(f0 * specularColorFactor, vec3(1.0));
    f0 = mix(dielectricSpecularF0, baseColor.rgb, metallic);
    vec3 specularColor = f0;
    float specularWeight = specularFactor;
    vec3 diffuseColor = mix(baseColor.rgb, vec3(0), metallic);
    #else
    float specularWeight = 1.0;
    vec3 diffuseColor = baseColor.rgb * (vec3(1.0) - f0);
    diffuseColor *= 1.0 - metallic;
    vec3 specularColor = mix(f0, baseColor.rgb, metallic);
    #endif


    // Compute reflectance.
    float reflectance = max(max(specularColor.r, specularColor.g), specularColor.b);

    // For typical incident reflectance range (between 4% to 100%) set the grazing reflectance to 100% for typical fresnel effect.
    // For very low reflectance range on highly diffuse objects (below 4%), incrementally reduce grazing reflecance to 0%.
    float reflectance90 = clamp(reflectance * 25.0, 0.0, 1.0);
    vec3 specularEnvironmentR0 = specularColor.rgb;
    vec3 specularEnvironmentR90 = vec3(1.0, 1.0, 1.0) * reflectance90;

    vec3 surfaceToCamera = u_cameraPosition.xyz - v_position;
    float eyeDistance = length(surfaceToCamera);

    vec3 n = getNormal();                             // normal at surface point
    vec3 v = surfaceToCamera / eyeDistance;        // Vector from surface point to camera
    vec3 reflection = -normalize(reflect(v, n));

    float NdotV = clamp(abs(dot(n, v)), 0.001, 1.0);

    PBRSurfaceInfo pbrSurface = PBRSurfaceInfo(
    n,
    v,
    NdotV,
    perceptualRoughness,
    metallic,
    specularEnvironmentR0,
    specularEnvironmentR90,
    alphaRoughness,
    diffuseColor,
    specularColor,
    getThickness(),
    specularWeight
    #ifdef iridescenceFlag
    , 0.0, 0.0, 0.0, vec3(0.0), vec3(0.0)
    #endif
    );

    #ifdef iridescenceFlag
    pbrSurface = getIridescenceInfo(pbrSurface);
    #endif

    vec3 f_diffuse = vec3(0.0);
    vec3 f_specular = vec3(0.0);
    vec3 f_transmission = vec3(0.0);

    // Calculate lighting contribution from image based lighting source (IBL)

    #if defined(USE_IBL) && defined(ambientLightFlag)
    PBRLightContribs contribIBL = getIBLContribution(pbrSurface, n, reflection);
    f_diffuse += contribIBL.diffuse * u_ambientLight;
    f_specular += contribIBL.specular * u_ambientLight;
    f_transmission += contribIBL.transmission * u_ambientLight;
    vec3 ambientColor = vec3(0.0, 0.0, 0.0);
    #elif defined(USE_IBL)
    PBRLightContribs contribIBL = getIBLContribution(pbrSurface, n, reflection);
    f_diffuse += contribIBL.diffuse;
    f_specular += contribIBL.specular;
    f_transmission += contribIBL.transmission;
    vec3 ambientColor = vec3(0.0, 0.0, 0.0);
    #elif defined(ambientLightFlag)
    vec3 ambientColor = u_ambientLight;
    #else
    vec3 ambientColor = vec3(0.0, 0.0, 0.0);
    #endif

    // Apply ambient occlusion only to ambient light
    #ifdef occlusionTextureFlag
    float ao = texture2D(u_OcclusionSampler, v_occlusionUV).r;
    f_diffuse = mix(f_diffuse, f_diffuse * ao, u_OcclusionStrength);
    f_specular = mix(f_specular, f_specular * ao, u_OcclusionStrength);
    #endif


    #if (numDirectionalLights > 0)
    // Directional lights calculation
    PBRLightContribs contrib0 = getDirectionalLightContribution(pbrSurface, u_dirLights[0]);
    #ifdef shadowMapFlag
    float shadows = getShadow();
    f_diffuse += contrib0.diffuse * shadows;
    f_specular += contrib0.specular * shadows;
    f_transmission += contrib0.transmission * shadows; // TODO does transmission affected by shadows ?
    #else
    f_diffuse += contrib0.diffuse;
    f_specular += contrib0.specular;
    f_transmission += contrib0.transmission;
    #endif

    for(int i=1 ; i<numDirectionalLights ; i++){
        PBRLightContribs contrib = getDirectionalLightContribution(pbrSurface, u_dirLights[i]);
        f_diffuse += contrib.diffuse;
        f_specular += contrib.specular;
        f_transmission += contrib.transmission;
    }
    #endif

    #if (numPointLights > 0)
    // Point lights calculation
    for(int i=0 ; i<numPointLights ; i++){
        PBRLightContribs contrib = getPointLightContribution(pbrSurface, u_pointLights[i]);
        f_diffuse += contrib.diffuse;
        f_specular += contrib.specular;
        f_transmission += contrib.transmission;
    }
    #endif // numPointLights

    #if (numSpotLights > 0)
    // Spot lights calculation
    for(int i=0 ; i<numSpotLights ; i++){
        PBRLightContribs contrib = getSpotLightContribution(pbrSurface, u_spotLights[i]);
        f_diffuse += contrib.diffuse;
        f_specular += contrib.specular;
        f_transmission += contrib.transmission;
    }
    #endif // numSpotLights

    // mix diffuse with transmission
    #ifdef transmissionFlag
    f_diffuse = mix(f_diffuse, f_transmission, getTransmissionFactor());
    #endif

    vec3 color = ambientColor + f_diffuse + f_specular;

    // Add emissive
    #if defined(emissiveTextureFlag) && defined(emissiveColorFlag)
    vec3 emissive = SRGBtoLINEAR(texture2D(u_emissiveTexture, v_emissiveUV)).rgb * u_emissiveColor.rgb;
    #elif defined(emissiveTextureFlag)
    vec3 emissive = SRGBtoLINEAR(texture2D(u_emissiveTexture, v_emissiveUV)).rgb;
    #elif defined(emissiveColorFlag)
    vec3 emissive = u_emissiveColor.rgb;
    #endif

    #if defined(emissiveTextureFlag) || defined(emissiveColorFlag)
    color += emissive;
    #endif


    // final frag color
    #ifdef GAMMA_CORRECTION
    out_FragColor = vec4(pow(color,vec3(1.0/GAMMA_CORRECTION)), baseColor.a);
    #else
    out_FragColor = vec4(color, baseColor.a);
    #endif

    #ifdef fogFlag
    #ifdef fogEquationFlag
    float fog = (eyeDistance - u_fogEquation.x) / (u_fogEquation.y - u_fogEquation.x);
    fog = clamp(fog, 0.0, 1.0);
    fog = pow(fog, u_fogEquation.z);
    #else
    float fog = min(1.0, eyeDistance * eyeDistance * u_cameraPosition.w);
    #endif
    out_FragColor.rgb = mix(out_FragColor.rgb, u_fogColor.rgb, fog * u_fogColor.a);
    #endif

    // Blending and Alpha Test
    #ifdef blendedFlag
    out_FragColor.a = baseColor.a * u_opacity;
    #ifdef alphaTestFlag
    if (out_FragColor.a <= u_alphaTest)
    discard;
    #endif
    #else
    out_FragColor.a = 1.0;
    #endif

    applyClippingPlane();

}

#endif
