package com.ducksteam.needleseye.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import net.mgsx.gltf.scene3d.shaders.PBRShaderConfig;
import net.mgsx.gltf.scene3d.shaders.PBRShaderProvider;

public class ColorShaderProvider extends PBRShaderProvider {
    static PBRShaderConfig config;
    static PBRShaderConfig pbrConfig;
    static String vertexShaderPath = "shaders/ne_color.vert";
    static String fragmentShaderPath = "shaders/ne_color.frag";
    static String pbrVertexShaderPath = "shaders/pbr.vert";
    static String pbrFragmentShaderPath = "shaders/pbr.frag";

    public ColorShaderProvider(PBRShaderConfig config) {
        super(config);
    }

    public static ColorShaderProvider createDefault(){
        if (config == null) {
            config = createDefaultConfig();
            config.numDirectionalLights = 1;
            config.numPointLights = 1;
            config.numSpotLights = 0;

            config.vertexShader = Gdx.files.internal(vertexShaderPath).readString();
            config.fragmentShader = Gdx.files.internal(fragmentShaderPath).readString();
        }
        return new ColorShaderProvider(config);
    }

    public static ColorShaderProvider createPBR(){
        if (pbrConfig == null) {
            pbrConfig = createDefaultConfig();
            pbrConfig.numDirectionalLights = 1;
            pbrConfig.numPointLights = 1;
            pbrConfig.numSpotLights = 0;

            pbrConfig.vertexShader = Gdx.files.internal(pbrVertexShaderPath).readString();
            pbrConfig.fragmentShader = Gdx.files.internal(pbrFragmentShaderPath).readString();
        }
        return new ColorShaderProvider(pbrConfig);
    }

    @Override
    public String createPrefixBase(Renderable renderable, PBRShaderConfig config) {
        return "#version 150\n" + DefaultShader.createPrefix(renderable, config);
    }
}
