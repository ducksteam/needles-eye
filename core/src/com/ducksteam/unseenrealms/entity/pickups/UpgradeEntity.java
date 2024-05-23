package com.ducksteam.unseenrealms.entity.pickups;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.ducksteam.unseenrealms.Main;
import com.ducksteam.unseenrealms.entity.collision.Collider;
import com.ducksteam.unseenrealms.entity.collision.ColliderBox;
import com.ducksteam.unseenrealms.entity.Entity;
import com.ducksteam.unseenrealms.player.Upgrade;

/**
 * Entity for an upgrade pickup
 * @author SkySourced
 */
public class UpgradeEntity extends Entity {
    Upgrade upgrade;

    public UpgradeEntity(Vector3 position, Upgrade upgrade) {
        super(position, new Vector2(0,0));
        this.upgrade= upgrade;
        this.collider = new ColliderBox(position, new Vector3(-0.5f, -0.5f,-0.5f), new Vector3(-0.5f, -0.5f,-0.5f));
        //this.setModelAddress(upgrade.getModelAddress());
    }

    public void update(float delta) {
        if (Collider.collidesWith(this.collider, Main.player.collider)) {
            Main.player.getInv().addUpgrade(this.upgrade);
            //TODO: Destroy entity
        }
    }

    @Override
    public String getModelAddress() {
        return upgrade.getModelAddress();
    }
}