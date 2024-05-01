package com.chiefsource.unseenrealms.entity.pickups;

import com.badlogic.gdx.math.Vector3;
import com.chiefsource.unseenrealms.Main;
import com.chiefsource.unseenrealms.entity.Entity;
import com.chiefsource.unseenrealms.entity.collision.Collider;
import com.chiefsource.unseenrealms.entity.collision.ColliderBox;
import com.chiefsource.unseenrealms.player.Upgrade;

public class UpgradeEntity extends Entity {
    Upgrade upgrade;

    public UpgradeEntity(Vector3 position, Upgrade upgrade) {
        super(position);
        this.collider = new ColliderBox(position, new Vector3(-0.5f, -0.5f,-0.5f), new Vector3(-0.5f, -0.5f,-0.5f));
        this.setModelAddress(upgrade.getModelAddress());
    }

    public void update(float delta) {
        if (Collider.collidesWith(this.collider, Main.player.collider)) {
            Main.player.getInv().addUpgrade(this.upgrade);
            //TODO: Destroy entity
        }
    }
}