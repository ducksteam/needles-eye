package com.ducksteam.needleseye.entity.enemies;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.ducksteam.needleseye.entity.Entity;

public abstract class EnemyEntity extends Entity {
    public EnemyEntity(Vector3 position, Quaternion rotation) {
        super(position, rotation);
    }
}
