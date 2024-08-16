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

/**
 * A worm enemy that chases the player
 * @author SkySourced
 */
public class WormEnemy extends EnemyEntity{
    static final float MASS = 4000f;
    static final float IDLE_SPEED = 2000f;
    static final float CHASE_SPEED = 30000f;
    static final Vector3 COLLIDER_SIZE = new Vector3(0.1f, 0.09f, 0.27f);

    public static final String MODEL_ADDRESS = "models/enemies/worm.gltf";

    public WormEnemy(Vector3 position, Quaternion rotation, RoomInstance room) {
        super(position, rotation, 4000, (EnemyRegistry.loaded) ? new ModelInstance(((SceneAsset)Main.assMan.get(MODEL_ADDRESS)).scene.model):null, 5, room.getRoomSpacePos());
        // initialise ai
        setAi(new MeleeAI(this, IDLE_SPEED, CHASE_SPEED));

        // destroy old bullet objects
        collider.dispose();
        collisionShape.dispose();

        // create new shape and motionstate
        collisionShape = new btBoxShape(COLLIDER_SIZE);
        motionState = new EntityMotionState(this, transform);

        // calculate inertia
        Vector3 inertia = new Vector3();
        collisionShape.calculateLocalInertia(MASS, inertia);

        // create rigid body
        collider = new btRigidBody(MASS, motionState, collisionShape, inertia);
        collider.obtain();
        collider.setCollisionFlags(collider.getCollisionFlags());
        collider.setActivationState(Collision.DISABLE_DEACTIVATION); // entity should not be deactivated
        collider.setUserValue(this.id); // set entity id
        collider.setAngularFactor(Vector3.Y); // lock x/z rotation
        collider.setDamping(0.8f, 0.8f); // set damping factor
        dynamicsWorld.addRigidBody(collider); // add to world
    }

    /**
     * Defines damage
     * @return contact damage
     */
    @Override
    public int getContactDamage() {
        return 1;
    }

    /**
     * Get the model address
     * @return the model address
     */
    @Override
    public String getModelAddress() {
        return MODEL_ADDRESS;
    }

    /**
     * Returns a string representation for debugging
     * @return string version of the object
     */
    @Override
    public String toString() {
        return "Worm{" +
                "health=" + health +
                ", position=" + getPosition() +
                ", assignedRoom=" + assignedRoom +
                ", chasing=" + ai.isChasing() +
                '}';
    }
}
