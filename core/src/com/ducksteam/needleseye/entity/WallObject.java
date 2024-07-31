package com.ducksteam.needleseye.entity;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

import static com.ducksteam.needleseye.Main.assMan;

public class WallObject extends Entity {

    public boolean hasDoor = false;

    public static String modelAddress = "models/rooms/wall.gltf";
    public static String modelAddressDoor = "models/rooms/door.gltf";


    public WallObject(Vector3 position, Quaternion rotation, boolean hasDoor) {
        super(position, rotation, new ModelInstance(((SceneAsset) assMan.get((hasDoor)?modelAddressDoor:modelAddress)).scene.model));
        this.hasDoor=hasDoor;
    }

    @Override
    public String getModelAddress() {
        return modelAddress;
    }
}
