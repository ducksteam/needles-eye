package com.ducksteam.needleseye.entity;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

import static com.ducksteam.needleseye.Main.assMan;

/**
 * A wall entity in the world
 * @author thechiefpotatopeeler
 */
public class WallObject extends Entity {

    public boolean hasDoor;

    public static final String MODEL_ADDRESS = "models/rooms/wall.gltf";
    public static final String MODEL_ADDRESS_DOOR = "models/rooms/door.gltf";

    public WallObject(Vector3 position, Quaternion rotation, boolean hasDoor) {
        super(position, rotation, new Scene(((SceneAsset) assMan.get((hasDoor) ? MODEL_ADDRESS_DOOR : MODEL_ADDRESS)).scene));
        this.hasDoor = hasDoor;
    }

    @Override
    public String getModelAddress() {
        return MODEL_ADDRESS;
    }
}
