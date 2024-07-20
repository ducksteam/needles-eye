package com.ducksteam.needleseye.entity.enemies;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.ducksteam.needleseye.Main;

public class WormEnemy extends EnemyEntity{
    public WormEnemy(String modelAddress, Vector3 position, Quaternion rotation) {
        super(position, rotation, 8, new ModelInstance((Model) Main.assMan.get(modelAddress)));
    }

    @Override
    public String getModelAddress() {
        return "models/worm.g3db";
    }
}
