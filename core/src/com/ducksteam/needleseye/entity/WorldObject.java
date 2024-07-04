package com.ducksteam.needleseye.entity;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public abstract class WorldObject extends Entity{
    public WorldObject(Vector3 position, Quaternion rotation, Vector3 scale) {
        super(position, rotation, scale);
    }
}
