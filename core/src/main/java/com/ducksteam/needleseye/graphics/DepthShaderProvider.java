package com.ducksteam.needleseye.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.DepthShader;
import net.mgsx.gltf.scene3d.shaders.PBRCommon;
import net.mgsx.gltf.scene3d.shaders.PBRDepthShader;
import net.mgsx.gltf.scene3d.shaders.PBRDepthShaderProvider;

public class DepthShaderProvider extends PBRDepthShaderProvider {
    static DepthShader.Config config;
    static String vertexShaderPath = "shaders/pbr_depth.vert";
    static String fragmentShaderPath = "shaders/pbr_depth.frag";

    static String versionPrefix = "#version 150\n";

    public DepthShaderProvider(DepthShader.Config config) {
        super(config);
    }

    public static DepthShaderProvider createDefault(){
        if (config == null) {
            config = createDefaultConfig();

            config.vertexShader = Gdx.files.internal(vertexShaderPath).readString();
            config.fragmentShader = Gdx.files.internal(fragmentShaderPath).readString();
        }
        return new DepthShaderProvider(config);
    }

    @Override
    protected Shader createShader(Renderable renderable) {
        // TODO only count used attributes, depth shader only require a few of them.
        PBRCommon.checkVertexAttributes(renderable);

        return new PBRDepthShader(renderable, config, versionPrefix + DepthShader.createPrefix(renderable, config) + morphTargetsPrefix(renderable));
    }
}
