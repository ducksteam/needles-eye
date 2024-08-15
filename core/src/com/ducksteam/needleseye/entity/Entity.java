package com.ducksteam.needleseye.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Animation;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.ducksteam.needleseye.Main;
import com.ducksteam.needleseye.entity.bullet.EntityMotionState;
import com.ducksteam.needleseye.entity.enemies.EnemyEntity;

import static com.ducksteam.needleseye.Main.*;

/**
 * This class represents an entity in the game world. It has a transform, a collider, and a model instance.
 * Static entities have mass 0 and are not affected by physics.
 * @author thechiefpotatopeeler
 * @author skysourced
 */
public abstract class Entity {

	// collision group flags
	public static final short GROUND_GROUP = 1 << 8;
	public static final short PLAYER_GROUP = 1 << 9;
	public static final short ENEMY_GROUP = 1 << 10;
	public static final short PROJECTILE_GROUP = 1 << 11;
	public static final short PICKUP_GROUP = 1 << 12;
	//Rendering and collision data
	public Boolean isRenderable;
	public Boolean isStatic;
	public Matrix4 transform = new Matrix4();
	public btRigidBody collider;
	public btCollisionShape collisionShape;
	public EntityMotionState motionState;
	private ModelInstance modelInstance;
	private float mass = 0f;
	private float freezeTime = 0f;
	private final int flags;
	protected static final Matrix4 tmpMat = new Matrix4();

	//Id data
	public static int currentId = 1;
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

		this.mass = mass;
		this.flags = flags;
		this.freezeTime = 0;

		setModelInstance(modelInstance);
	}

	/**
	 * Basic update method for entities
	 * @param delta the time since the last frame
	 * */
	public void update(float delta){
		if (freezeTime > 0) {
			freezeTime -= delta;
			if (freezeTime <= 0) {

			} else {

			}
		}
	}

	/**
	 * Sets the model instance of the entity
	 * @param modelInstance the model instance to set
	 * */
	public void setModelInstance(ModelInstance modelInstance) {
		this.modelInstance = modelInstance;
		if (isRenderable) {
			collisionShape = Bullet.obtainStaticNodeShape(modelInstance.nodes);
			motionState = new EntityMotionState(this, transform);

			Vector3 inertia = new Vector3();
			collisionShape.calculateLocalInertia(mass, inertia);

			//Modifies collider for the modelInstance
			collider = new btRigidBody(mass, motionState, collisionShape, inertia);
			collider.obtain();
			collider.setCollisionFlags(collider.getCollisionFlags() | flags);
			collider.setActivationState(Collision.DISABLE_DEACTIVATION);
			collider.setUserValue(this.id);
			if (this instanceof RoomInstance) collider.setFriction(0.2f);
			if (!(this instanceof EnemyEntity)) dynamicsWorld.addRigidBody(collider);
		}
	}
	/**
	 * Returns the model address of the entity
	 * @return the model address
	 * */
	public abstract String getModelAddress();

	/**
	 * Returns the model instance of the entity
	 * @return the model instance
	 * */
	public ModelInstance getModelInstance() {
		motionState.getWorldTransform(modelInstance.transform); // i suppose this does save a little bit of memory
		return modelInstance;
	}

	/**
	 * Gets the position of the entity
	 * @return the position of the entity
	 * */
	public Vector3 getPosition() {
		motionState.getWorldTransform(tmpMat);
		return tmpMat.getTranslation(new Vector3());
	}

	/**
	 * Sets the position of the entity
	 * @param position the position to set
	 * */
	public void setPosition(Vector3 position) {
		transform.setTranslation(position);
		motionState.setWorldTransform(transform);
//		if (isStatic) rebuildDynamicsWorld();
	}

	/**
	 * Translates the entity by a vector
	 * @param translation the vector to translate by
	 * */
	public void translate(Vector3 translation) {
		transform.trn(translation);
		motionState.setWorldTransform(transform);
	}

	/**
	 * Gets the velocity of the entity
	 * @return the velocity of the entity
	 * */
	public Vector3 getVelocity() {
		return collider.getLinearVelocity();
	}

	/**
	 * Sets the velocity of the entity
	 * @param velocity the velocity to set
	 * */
	public void setVelocity(Vector3 velocity) {
		collider.setLinearVelocity(velocity);
	}

	/**
	 * Gets the rotation of the entity
	 * @return the rotation of the entity
	 * */
	public Quaternion getRotation() {
		return transform.getRotation(new Quaternion());
	}

	/**
	 * Sets the rotation of the entity
	 * @param axis the rotation axis to set
	 * @param angle the rotation angle to set
	 * */
	public void setRotation(Vector3 axis, float angle) {
		transform.rotateRad(axis, angle);
	}

	/**
	 * Rotates the entity by a quaternion
	 * @param rotation the quaternion to rotate by
	 * */
	public void rotate(Quaternion rotation) {
		transform.rotate(rotation);
	}

	/**
	 * Sets the animation of the entity
	 * @param animationName the animation to set
	 * */
	public void setAnimation(String animationName) {
		if (isRenderable) {
			Animation animation = modelInstance.getAnimation(animationName);
			if (animation != null) {
				Gdx.app.debug("Entity", "Setting animation: " + animationName);
			}
		}
	}

	/**
	 * Disposes all relevant data from the entity
	 * */
	public void destroy() {
		dynamicsWorld.removeRigidBody(collider);
		entities.remove(id);
//		collisionShape.release();
//		motionState.release();
//		collider.release();
//		collider.dispose();
	}

	/**
	 * Freezes the entitu for a certain amount of time
	 * */
	public void freeze(int time) {
		freezeTime = time;
	}

	/**
	 * Runs a function on an entity
	 * */
	@FunctionalInterface
	public interface EntityRunnable {
		void run(Entity entity);
	}
}