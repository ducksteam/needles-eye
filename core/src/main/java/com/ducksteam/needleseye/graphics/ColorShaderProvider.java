package com.ducksteam.needleseye.graphics;

import net.mgsx.gltf.scene3d.shaders.PBRShaderConfig;
import net.mgsx.gltf.scene3d.shaders.PBRShaderProvider;

public class ShaderProvider extends PBRShaderProvider {
    PBRShaderConfig config;
    DepthShader.Con

    public ShaderProvider(PBRShaderConfig config) {
        super(config);
    }

    public static ShaderProvider createDefaultColor(){

    }

    public static ShaderProvider createDefaultDepth(){

    }
}
