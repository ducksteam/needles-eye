package com.ducksteam.needleseye.entity;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import com.ducksteam.needleseye.Main;

/**
 * This class represents an entity in the game world. It has a transform, a collider, and a model instance.
 * Static entities have mass 0 and are not affected by physics.
 */
public abstract class Entity {

	// collision group flags
	public static final short GROUND_GROUP = 1 << 8;
	public static final short PLAYER_GROUP = 1 << 9;
	public static final short ENEMY_GROUP = 1 << 10;
	public static final short PROJECTILE_GROUP = 1 << 11;
	public static final short PICKUP_GROUP = 1 << 12;
	public static final short ALL = -1;

	public Boolean isRenderable;
	public Boolean isStatic;
	public Matrix4 transform = new Matrix4();
	public btRigidBody collider;
	public btCollisionShape collisionShape;
	public MotionState motionState;
	private ModelInstance modelInstance;
	private Vector3 modelOffset;

	/**
	 * Creates a static entity with mass 0
	 *
	 * @param position      the initial position
	 * @param rotation      the initial rotation
	 * @param modelInstance the model instance of the entity
	 */

	public Entity(Vector3 position, Quaternion rotation, ModelInstance modelInstance) {
		this(position, rotation, 0, modelInstance);
	}

	/**
	 * Creates a dynamic entity
	 *
	 * @param position      the initial position
	 * @param rotation      the initial rotation
	 * @param mass          the mass of the entity
	 * @param modelInstance the model instance of the entity
	 */

	public Entity(Vector3 position, Quaternion rotation, float mass, ModelInstance modelInstance) {
		transform.idt().translate(position).rotate(rotation);

		isStatic = mass == 0;
		isRenderable = modelInstance != null;

		this.modelInstance = modelInstance;

		setModelOffset(Vector3.Zero);

		if (isRenderable && modelInstance != null) {
			collisionShape = Bullet.obtainStaticNodeShape(modelInstance.nodes);
			motionState = new MotionState(this, transform);
			Vector3 inertia;
			if (isStatic) {
				inertia = Vector3.Zero;
			} else {
				inertia = new Vector3();
				collisionShape.calculateLocalInertia(mass, inertia);
			}
			collider = new btRigidBody(mass, motionState, collisionShape, inertia);
			Main.dynamicsWorld.addRigidBody(collider);
		}
	}

	public static Vector3 quatToEuler(Quaternion quat) {
		return new Vector3(quat.getPitch(), quat.getYaw(), quat.getRoll());
	}

	public abstract String getModelAddress();

	public ModelInstance getModelInstance() {
		motionState.getWorldTransform(modelInstance.transform); // i suppose this does save a little bit of memory
		return modelInstance;
	}

	public Vector3 getModelOffset() {
		return modelOffset;
	}

	public void setModelOffset(Vector3 offset) {
		this.modelOffset = offset;
	}

	public Vector3 getPosition() {
		return transform.getTranslation(new Vector3());
	}

	public void setPosition(Vector3 position) {
		transform.setTranslation(position);
	}

	public void translate(Vector3 translation) {
		transform.translate(translation);
	}

	public Vector3 getVelocity() {
		return collider.getLinearVelocity();
	}

	public void setVelocity(Vector3 velocity) {
		collider.setLinearVelocity(velocity);
	}

	public Quaternion getRotation() {
		return transform.getRotation(new Quaternion());
	}

	public void setRotation(Vector3 axis, float angle) {
		transform.rotateRad(axis, angle);
	}

	public void rotate(Quaternion rotation) {
		transform.rotate(rotation);
	}

	public void destroy() {
		collider.dispose();
		motionState.dispose();
	}
}
