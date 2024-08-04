package com.ducksteam.needleseye.entity.pickups;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.ducksteam.needleseye.Config;
import com.ducksteam.needleseye.Main;
import com.ducksteam.needleseye.entity.Entity;
import com.ducksteam.needleseye.player.Upgrade;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

/**
 * Entity for an upgrade pickup
 * @author SkySourced
 */
public class UpgradeEntity extends Entity {
    Upgrade upgrade;
    Matrix4 tmpMat = new Matrix4();
    Vector3 direction = new Vector3(0, 0.4f, 0.9f).nor();
    Vector3 tmp = new Vector3();

    public UpgradeEntity(Vector3 position, Upgrade upgrade) {
        super(position, new Quaternion(), 0f, new ModelInstance(((SceneAsset) Main.assMan.get(upgrade.getModelAddress())).scene.model), PICKUP_GROUP | btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT);
        this.upgrade = upgrade;

        collider.setContactCallbackFlag(PICKUP_GROUP);
        collider.setContactCallbackFilter(PLAYER_GROUP);

        //this.setModelAddress(upgrade.getModelAddress());ad
    }

    public Upgrade getUpgrade() {
        return upgrade;
    }

    public void update(float delta) {
        motionState.getWorldTransform(tmpMat);
        direction = rotateAroundSphere(direction, delta * 0.0001f);
        tmpMat.rotateTowardDirection(direction, Vector3.Y);
        tmpMat.getTranslation(tmp);
        tmp.x = Math.round(tmp.x);
        tmp.y = (float) Math.sin((double) Main.getTime()/300)/6 + Config.UPGRADE_HEIGHT;
        tmp.z = Math.round(tmp.z);
        tmpMat.setTranslation(tmp);
        motionState.setWorldTransform(tmpMat);
    }

    private Vector3 rotateAroundSphere(Vector3 vector, float angle){
        // Convert to spherical coordinates
        float inclination = (float) Math.acos(vector.z / vector.len());
        float azimuth = (float) Math.atan2(vector.y, vector.x);

        // Rotate
        azimuth += angle;

        // Convert to cartesian coordinates
        float x = (float) Math.sin(inclination) * (float) Math.cos(azimuth);
        float y = (float) Math.sin(inclination) * (float) Math.sin(azimuth);
        float z = (float) Math.cos(inclination);

        return new Vector3(x, y, z);
    }

    @Override
    public String getModelAddress() {
        return upgrade.getModelAddress();
    }
}