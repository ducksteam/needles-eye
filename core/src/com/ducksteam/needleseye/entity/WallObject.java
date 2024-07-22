package com.ducksteam.needleseye.entity;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

import static com.ducksteam.needleseye.Main.assMan;

public class WallObject extends Entity {

    public static String modelAddress = "models/rooms/door.gltf";


    public WallObject(Vector3 position, Quaternion rotation) {
        super(position, rotation, new ModelInstance(((SceneAsset) assMan.get("models/rooms/door.gltf")).scene.model));
    }

    @Override
    public String getModelAddress() {
        return modelAddress;
    }
}
