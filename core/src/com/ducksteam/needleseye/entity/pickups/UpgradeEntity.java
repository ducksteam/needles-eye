package com.ducksteam.needleseye.entity.pickups;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.ducksteam.needleseye.Main;
import com.ducksteam.needleseye.entity.Entity;
import com.ducksteam.needleseye.player.Upgrade;

/**
 * Entity for an upgrade pickup
 * @author SkySourced
 */
public class UpgradeEntity extends Entity {
    Upgrade upgrade;

    public UpgradeEntity(Vector3 position, Upgrade upgrade) {
        super(position, new Quaternion(), 0f, new ModelInstance((Model) Main.assMan.get(upgrade.getModelAddress())), PICKUP_GROUP | btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT);
        this.upgrade = upgrade;

        //this.setModelAddress(upgrade.getModelAddress());
    }

    public void update(float delta) {

    }

    @Override
    public String getModelAddress() {
        return upgrade.getModelAddress();
    }
}