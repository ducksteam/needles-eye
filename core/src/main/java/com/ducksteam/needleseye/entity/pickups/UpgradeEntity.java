package com.ducksteam.needleseye.entity.pickups;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.ducksteam.needleseye.Config;
import com.ducksteam.needleseye.Main;
import com.ducksteam.needleseye.entity.Entity;
import com.ducksteam.needleseye.entity.bullet.WorldTrigger;
import com.ducksteam.needleseye.player.Player;
import com.ducksteam.needleseye.player.Upgrade;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

/**
 * Entity for an upgrade pickup
 * @author SkySourced
 */
public class UpgradeEntity extends Entity {
    Upgrade upgrade; // contained upgrade

    WorldTrigger trigger;

    // temp variables for calculations
    Vector3 tmp = new Vector3();
    Matrix4 tmpMat = new Matrix4();

    /**
     * Create a new upgrade entity
     * @param position the position of the entity
     * @param upgrade the upgrade contained within the entity
     */
    public UpgradeEntity(Vector3 position, Upgrade upgrade) {
        super(position, new Quaternion(), 0f, new Scene(((SceneAsset) Main.assMan.get(upgrade.getModelAddress())).scene), PICKUP_GROUP | btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT);
        this.upgrade = upgrade;

        trigger = new WorldTrigger(new btBoxShape(new Vector3(1, 1, 1)), new Matrix4().setToTranslation(position), (Entity e) -> {
            Upgrade pickup = getUpgrade();
            ((Player) e).upgrades.add(pickup);
            Player.timeSincePickup = 0;
            getUpgrade().onPickup();
            destroy();
        }, Entity.PLAYER_GROUP, WorldTrigger.TriggerType.ONCE_ON_ENTER);

        // set initial rotation
        motionState.getWorldTransform(tmpMat);
        tmpMat.rotateRad(Vector3.X, 0.4f);
        motionState.setWorldTransform(tmpMat);
    }

    /**
     * Get the upgrade contained within the entity
     * @return the entity's upgrade
     */
    public Upgrade getUpgrade() {
        return upgrade;
    }

    /**
     * update position and rotation
     * @param delta the time since the last frame
     */
    public void update(float delta) {
        motionState.getWorldTransform(tmpMat);
        tmpMat.getTranslation(tmp);
        tmpMat.rotateRad(Vector3.Y, delta);
        tmp.x = Math.round(tmp.x);
        tmp.y = (float) Math.sin((double) Main.getTime()/300)/6 + Config.UPGRADE_HEIGHT;
        tmp.z = Math.round(tmp.z);
        tmpMat.setTranslation(tmp);
        motionState.setWorldTransform(tmpMat);
    }

    /**
     * get the model address of the upgrade
     * @return the model address
     */
    @Override
    public String getModelAddress() {
        return upgrade.getModelAddress();
    }

    @Override
    public String toString() {
        return id + "-UpgradeEntity{" +
            "upgrade=" + upgrade +
            "position=" + getPosition() +
            '}';
    }

    @Override
    public void destroy() {
        trigger.destroy();
        super.destroy();
    }
}
