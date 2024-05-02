package com.waddleworks.unseenrealms.entity.enemies;

import com.badlogic.gdx.math.Vector3;
import com.waddleworks.unseenrealms.entity.Entity;

public abstract class EnemyEntity extends Entity {
    public EnemyEntity( Vector3 position) {
        super(position);
    }
}
