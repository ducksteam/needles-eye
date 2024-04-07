package com.chiefsource.unseenrealms.entity;

import com.badlogic.gdx.math.Vector3;

public abstract class EnemyEntity extends Entity{
    public EnemyEntity(String modelAddress, Vector3 position) {
        super(modelAddress, position);
    }
}
