package com.ducksteam.unseenrealms.entity.enemies;

import com.badlogic.gdx.math.Vector3;

public class WormEnemy extends EnemyEntity{
    public WormEnemy(String modelAddress, Vector3 position) {
        super(position);
    }

    @Override
    public String getModelAddress() {
        return "models/worm.g3db";
    }
}
