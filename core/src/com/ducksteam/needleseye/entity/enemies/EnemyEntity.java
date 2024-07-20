package com.ducksteam.needleseye.entity.enemies;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.ducksteam.needleseye.entity.Entity;

public abstract class EnemyEntity extends Entity {
    public EnemyEntity(Vector3 position, Quaternion rotation, float mass, ModelInstance modelInstance) {
        super(position, rotation, mass, modelInstance, ENEMY_GROUP);
    }
}
