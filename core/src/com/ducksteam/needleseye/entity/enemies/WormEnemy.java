package com.ducksteam.needleseye.entity.enemies;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public class WormEnemy extends EnemyEntity{
    public WormEnemy(String modelAddress, Vector3 position, Quaternion rotation) {
        super(position, rotation);
    }

    @Override
    public String getModelAddress() {
        return "models/worm.g3db";
    }
}
