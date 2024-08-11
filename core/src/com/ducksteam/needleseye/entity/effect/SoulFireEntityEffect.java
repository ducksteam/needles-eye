package com.ducksteam.needleseye.entity.effect;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.ducksteam.needleseye.entity.Entity;

public class SoulFireEntityEffect extends Entity {

    public static String staticModelAddress = "models/effects/soulfire.gltf";
    public SoulFireEntityEffect(Vector3 position, Quaternion rotation, ModelInstance modelInstance) {
        super(position, rotation, modelInstance);
    }

    @Override
    public String getModelAddress() {
        return staticModelAddress;
    }

}
