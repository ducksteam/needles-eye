package com.ducksteam.unseenrealms.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public abstract class WorldObject extends Entity{
    public WorldObject(Vector3 position, Vector2 rotation) {
        super(position, rotation);
    }
}
