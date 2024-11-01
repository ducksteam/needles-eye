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

import static com.ducksteam.needleseye.Main.dynamicsWorld;

public class OrbulonEnemy extends EnemyEntity {
	static final float MASS = 0f;
	static final float COLLIDER_SIZE = 0.5f;

	public static final String MODEL_ADDRESS = "models/enemies/gemorb.gltf";

	public OrbulonEnemy(Vector3 position, Quaternion rotation, RoomInstance room) {
		super(position, rotation, 4000, (EnemyRegistry.loaded) ? new Scene(((SceneAsset) Main.assMan.get(MODEL_ADDRESS)).scene):null, 5, room.getRoomSpacePos());
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
		return "Orbulon{" +
				"health=" + health +
				", position=" + getPosition() +
				", assignedRoom=" + assignedRoom +
				", chasing=" + ai.isChasing() +
				", windup=" + ai.isWindup() +
				", idling=" + ai.isIdling() +
				'}';
	}

	@Override
	public void onEnd(AnimationController.AnimationDesc animation) {
		if (animation.animation.id.equals("windup")) ai.attack();
		else if (animation.animation.id.equals("shoot")) ai.setWindup(false);
		else if (animation.animation.id.startsWith("idle")) ai.setIdling(true);
	}
}
