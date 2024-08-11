package com.ducksteam.needleseye.entity.enemies;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.ducksteam.needleseye.Main;
import com.ducksteam.needleseye.entity.EnemyRegistry;
import com.ducksteam.needleseye.entity.RoomInstance;
import com.ducksteam.needleseye.entity.bullet.EntityMotionState;
import com.ducksteam.needleseye.entity.enemies.ai.MeleeAI;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

import static com.ducksteam.needleseye.Main.dynamicsWorld;

public class WormEnemy extends EnemyEntity{
    static final float moveSpeed = 1000f;

    public static String modelAddress = "models/enemies/worm.gltf";

    public WormEnemy(Vector3 position, Quaternion rotation, RoomInstance room) {
        super(position, rotation, 8, (EnemyRegistry.loaded) ? new ModelInstance(((SceneAsset)Main.assMan.get(modelAddress)).scene.model):null, 5, room.getRoomSpacePos());
        setAi(new MeleeAI(this, moveSpeed));

        collider.dispose();
        collisionShape.dispose();

        collisionShape = new btBoxShape(new Vector3(0.1f, 0.09f, 0.27f));
        motionState = new EntityMotionState(this, transform);

        Vector3 inertia = new Vector3();
        collisionShape.calculateLocalInertia(8, inertia);

        collider = new btRigidBody(8000, motionState, collisionShape, inertia);
        collider.obtain();
        collider.setCollisionFlags(collider.getCollisionFlags());
        collider.setActivationState(Collision.DISABLE_DEACTIVATION);
        collider.setUserValue(this.id);
        collider.setAngularFactor(Vector3.Y);
        dynamicsWorld.addRigidBody(collider);
    }

    @Override
    public int getContactDamage() {
        return 1;
    }

    @Override
    public String getModelAddress() {
        return modelAddress;
    }

}
