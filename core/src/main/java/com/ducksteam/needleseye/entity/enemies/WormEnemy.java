package com.ducksteam.needleseye.entity.enemies;

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
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

import java.util.HashSet;
import java.util.Set;

import static com.ducksteam.needleseye.Main.dynamicsWorld;

/**
 * A worm enemy that chases the player
 * @author SkySourced
 */
public class WormEnemy extends EnemyEntity{
    static final float MASS = 40f;
    static final float IDLE_SPEED = 20f;
    static final float CHASE_SPEED = 300f;
    static final float CHASE_ANGLE_SPEED = 0.3f;
    static final Vector3 COLLIDER_SIZE = new Vector3(0.1f, 0.09f, 0.27f);

    /**
     * The model address for the worm enemy
     */
    public static final String MODEL_ADDRESS = "models/enemies/worm.gltf";

    /**
     * The {@link EnemyTag}s for the Worm enemy.
     * Used in map generation to determine which enemies to spawn
     */
    public static final Set<EnemyTag> tags = new HashSet<>(Set.of(EnemyTag.MELEE, EnemyTag.SMALL));

    /**
     * Create a new Worm enemy
     * @param position the position of the enemy
     * @param rotation the rotation of the enemy
     * @param room the room the enemy is in
     */
    public WormEnemy(Vector3 position, Quaternion rotation, RoomInstance room) {
        super(position, rotation, 4000, (EnemyRegistry.loaded) ? new Scene(((SceneAsset)Main.assMan.get(MODEL_ADDRESS)).scene):null, 5, room.getRoomSpacePos());
        // initialise ai
        setAi(new MeleeAI(this, IDLE_SPEED, CHASE_SPEED, CHASE_ANGLE_SPEED));

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
     * Worm does not have a special attack, only deals damage on contact
     * @return 0
     */
    @Override
    public int getDamage() {
        return 0;
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
        return id+"-Worm{" +
                "health=" + getHealth() +
                ", position=" + getPosition() +
                ", assignedRoom=" + getAssignedRoom() +
                ", chasing=" + getAi().isChasing() +
                '}';
    }
}
