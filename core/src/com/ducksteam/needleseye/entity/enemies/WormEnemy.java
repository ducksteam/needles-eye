package com.ducksteam.needleseye.entity.enemies;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class WormEnemy extends EnemyEntity{
    public WormEnemy(String modelAddress, Vector3 position, Vector2 rotation) {
        super(position, rotation);
    }

    @Override
    public String getModelAddress() {
        return "models/worm.g3db";
    }
}
