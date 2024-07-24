package com.ducksteam.needleseye.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Animation;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
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

	private float mass = 0f;
	private int flags = btCollisionObject.CollisionFlags.CF_STATIC_OBJECT | GROUND_GROUP;
	private static final Matrix4 tmpMat = new Matrix4();

	public static int currentId = 0;
	public int id;

	/**
	 * Creates a static entity with mass 0
	 *
	 * @param position      the initial position
	 * @param rotation      the initial rotation
	 * @param modelInstance the model instance of the entity
	 */

	public Entity(Vector3 position, Quaternion rotation, ModelInstance modelInstance) {
		this(position, rotation, 0f, modelInstance, btCollisionObject.CollisionFlags.CF_STATIC_OBJECT | GROUND_GROUP);
	}

	/**
	 * Creates a dynamic entity
	 *
	 * @param position      the initial position
	 * @param rotation      the initial rotation
	 * @param mass          the mass of the entity
	 * @param modelInstance the model instance of the entity
	 */

	public Entity(Vector3 position, Quaternion rotation, float mass, ModelInstance modelInstance, int flags) {
		transform.idt().translate(position).rotate(rotation);

		id = currentId++;
		Main.entities.put(id, this);

		isStatic = mass == 0;
		isRenderable = modelInstance != null;

		this.modelInstance = modelInstance;

		this.mass = mass;
		this.flags = flags;

		if (isRenderable) {
			collisionShape = Bullet.obtainStaticNodeShape(modelInstance.nodes);
			motionState = new MotionState(this, transform);

			Vector3 inertia = new Vector3();
			collisionShape.calculateLocalInertia(mass, inertia);

			collider = new btRigidBody(mass, motionState, collisionShape, inertia);
			collider.setCollisionFlags(collider.getCollisionFlags() | flags);
			collider.setActivationState(Collision.DISABLE_DEACTIVATION);

			Main.dynamicsWorld.addRigidBody(collider);
		}
	}

	public static Vector3 quatToEuler(Quaternion quat) {
		return new Vector3(quat.getPitch(), quat.getYaw(), quat.getRoll());
	}

	public void setModelInstance(ModelInstance modelInstance) {
		this.modelInstance = modelInstance;
		if (isRenderable) {
			collisionShape = Bullet.obtainStaticNodeShape(modelInstance.nodes);
			motionState = new MotionState(this, transform);

			Vector3 inertia = new Vector3();
			collisionShape.calculateLocalInertia(mass, inertia);

			collider = new btRigidBody(mass, motionState, collisionShape, inertia);
			collider.setCollisionFlags(collider.getCollisionFlags() | flags);
			collider.setActivationState(Collision.DISABLE_DEACTIVATION);

			Main.dynamicsWorld.addRigidBody(collider);
		}
	}
	public abstract String getModelAddress();

	public ModelInstance getModelInstance() {
		motionState.getWorldTransform(modelInstance.transform); // i suppose this does save a little bit of memory
		return modelInstance;
	}

	public Vector3 getPosition() {
		motionState.getWorldTransform(tmpMat);
		return tmpMat.getTranslation(new Vector3());
	}

	public void setPosition(Vector3 position) {
		transform.setTranslation(position);
		motionState.setWorldTransform(transform);
	}

	public void translate(Vector3 translation) {
		transform.trn(translation);
		motionState.setWorldTransform(transform);
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

	public void setAnimation(String animationName) {
		if (isRenderable) {
			Animation animation = modelInstance.getAnimation(animationName);
			if (animation == null) Gdx.app.error("Entity", "Animation not found: " + animationName);
			else Gdx.app.debug("Entity", "Setting animation: " + animationName);
		}
	}

	public void destroy() {
		Main.dynamicsWorld.removeRigidBody(collider);
		collider.dispose();
		collisionShape.dispose();
		motionState.dispose();
	}
}
