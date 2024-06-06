package com.ducksteam.needleseye.entity.enemies;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.ducksteam.needleseye.entity.Entity;

public abstract class EnemyEntity extends Entity {
    public EnemyEntity( Vector3 position, Vector2 rot) {
        super(position, rot);
    }
}
