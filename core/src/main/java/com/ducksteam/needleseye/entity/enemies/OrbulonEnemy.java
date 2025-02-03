package com.ducksteam.needleseye.entity.enemies;

import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.ducksteam.needleseye.Main;
import com.ducksteam.needleseye.entity.EnemyRegistry;
import com.ducksteam.needleseye.entity.RoomInstance;
import com.ducksteam.needleseye.entity.bullet.EntityMotionState;
import com.ducksteam.needleseye.entity.enemies.ai.OrbulonAI;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

import java.util.HashSet;
import java.util.Set;

import static com.ducksteam.needleseye.Main.dynamicsWorld;

/**
 * A ranged enemy that does not move and shoots at the player
 * @author SkySourced
 */
public class OrbulonEnemy extends EnemyEntity {
	static final float MASS = 0f;
	static final float COLLIDER_SIZE = 0.5f;

    /**
     * The model address for the Orbulon enemy
     */
	public static final String MODEL_ADDRESS = "models/enemies/gemorb.gltf";

    /**
     * The {@link EnemyTag}s for the Orbulon enemy.
     * Used in map generation to determine which enemies to spawn
     */
    public static final Set<EnemyTag> tags = new HashSet<>(Set.of(EnemyTag.RANGED, EnemyTag.MEDIUM));

    /**
     * Create a new Orbulon enemy
     * @param position the position of the enemy
     * @param rotation the rotation of the enemy
     * @param room the room the enemy is in
     */
	public OrbulonEnemy(Vector3 position, Quaternion rotation, RoomInstance room) {
		super(position, rotation, MASS, (EnemyRegistry.loaded) ? new Scene(((SceneAsset) Main.assMan.get(MODEL_ADDRESS)).scene):null, 15, room.getRoomSpacePos());
		// initialise ai
		setAi(new OrbulonAI(this));

		// destroy old bullet objects
		collider.dispose();
		collisionShape.dispose();

		// create new shape and motionstate
		collisionShape = new btSphereShape(COLLIDER_SIZE);
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

	@Override
	public int getContactDamage() {
		return 0;
	}

	@Override
	public int getDamage() {
		return 2;
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
		return id+"-Orbulon{" +
				"health=" + getHealth() +
				", position=" + getPosition() +
				", assignedRoom=" + getAssignedRoom() +
				", chasing=" + getAi().isChasing() +
				", windup=" + getAi().isWindup() +
				", idling=" + getAi().isIdling() +
				'}';
	}

	@Override
	public void onEnd(AnimationController.AnimationDesc animation) {
		if (animation.animation.id.equals("windup")) getAi().attack();
		else if (animation.animation.id.equals("shoot")) getAi().setWindup(false);
		else if (animation.animation.id.startsWith("idle")) getAi().setIdling(false);
	}
}
