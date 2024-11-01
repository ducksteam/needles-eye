package com.ducksteam.needleseye.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController.AnimationListener;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.ducksteam.needleseye.Main;
import com.ducksteam.needleseye.entity.bullet.EntityMotionState;
import com.ducksteam.needleseye.entity.enemies.EnemyEntity;
import net.mgsx.gltf.scene3d.scene.Scene;

import static com.ducksteam.needleseye.Main.*;

/**
 * This class represents an entity in the game world. It has a transform, a collider, and a model instance.
 * Static entities have mass 0 and are not affected by physics.
 * @author thechiefpotatopeeler
 * @author skysourced
 */
public abstract class Entity implements AnimationListener {

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
	private Scene scene;
	private float mass = 0f;
	private final int flags;

	// temp variables for calculations
	protected static final Matrix4 tmpMat = new Matrix4();

	//Id data
	public static int currentId = 1;
	public final int id;

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
	 * Creates a static entity with mass 0
	 *
	 * @param position      the initial position
	 * @param rotation      the initial rotation
	 * @param scene the model instance of the entity
	 */

	public Entity(Vector3 position, Quaternion rotation, Scene scene) {
		this(position, rotation, 0f, scene, btCollisionObject.CollisionFlags.CF_STATIC_OBJECT | GROUND_GROUP);
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

		setModelInstance(modelInstance);
	}

	/**
	 * Creates a dynamic entity
	 *
	 * @param position      the initial position
	 * @param rotation      the initial rotation
	 * @param mass          the mass of the entity
	 * @param scene the model instance of the entity
	 */

	public Entity(Vector3 position, Quaternion rotation, float mass, Scene scene, int flags) {
		transform.idt().translate(position).rotate(rotation);

		id = currentId++;
		Main.entities.put(id, this);

		isStatic = mass == 0;
		isRenderable = scene != null;

		this.mass = mass;
		this.flags = flags;

		setScene(scene);
	}

	/**
	 * Basic update method for entities
	 * @param delta the time since the last frame
	 * */
	public void update(float delta){

	}

	/**
	 * Sets the model instance of the entity
	 * @param modelInstance the model instance to set
	 * */
	@Deprecated
	public void setModelInstance(ModelInstance modelInstance) {
		this.modelInstance = modelInstance;
		if (isRenderable) {
			collisionShape = Bullet.obtainStaticNodeShape(modelInstance.nodes); // set collision shape to model
			motionState = new EntityMotionState(this, transform); // set motion state to entity

			// calculate inertia
			Vector3 inertia = new Vector3();
			collisionShape.calculateLocalInertia(mass, inertia);

			// Creates rigid body
			collider = new btRigidBody(mass, motionState, collisionShape, inertia);
			collider.obtain();
			collider.setCollisionFlags(collider.getCollisionFlags() | flags);
			collider.setActivationState(Collision.DISABLE_DEACTIVATION); // disable entity deactivation
			collider.setUserValue(this.id); // set user value to entity id
			if (this instanceof RoomInstance) collider.setFriction(0.2f); // increase friction on room instances
			if (!(this instanceof EnemyEntity)) dynamicsWorld.addRigidBody(collider); // enemy entities get modified more before being added to physics world
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
	@Deprecated
	public ModelInstance getModelInstance() {
		motionState.getWorldTransform(modelInstance.transform);
		return modelInstance;
	}

	/**
	 * Returns the scene asset of the entity
	 * @return the scene asset
	 * */
	public Scene getScene() {
		try {
			motionState.getWorldTransform(scene.modelInstance.transform);
		} catch (Exception e) {
//			Gdx.app.error("Entity", "Failed to get scene", e);
		}
		return scene;
	}

	/**
	 * Sets the scene asset of the entity
	 * @param scene the scene asset to set
	 * */
	public void setScene(Scene scene) {
		this.scene = scene;
		if (isRenderable) {
			collisionShape = Bullet.obtainStaticNodeShape(scene.modelInstance.nodes); // set collision shape to model
			motionState = new EntityMotionState(this, transform); // set motion state to entity

			// calculate inertia
			Vector3 inertia = new Vector3();
			collisionShape.calculateLocalInertia(mass, inertia);

			// Creates rigid body
			collider = new btRigidBody(mass, motionState, collisionShape, inertia);
			collider.obtain();
			collider.setCollisionFlags(collider.getCollisionFlags() | flags);
			collider.setActivationState(Collision.DISABLE_DEACTIVATION); // disable entity deactivation
			collider.setUserValue(this.id); // set user value to entity id
			if (this instanceof RoomInstance) collider.setFriction(0.2f); // increase friction on room instances
			if (!(this instanceof EnemyEntity)) dynamicsWorld.addRigidBody(collider); // enemy entities get modified more before being added to physics world
		}
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
	 * Gets the rotation of the entity
	 * @return the rotation of the entity
	 * */
	public Quaternion getRotation() {
		return transform.getRotation(new Quaternion());
	}

	/**
	 * Immediately changes the animation of the entity
	 * @param animationName the animation to set
	 * @param loopCount the number of times to play the animation, -1 for infinite
	 * */
	public void setAnimation(String animationName, int loopCount) {
		if (isRenderable) {
			try {
				scene.animationController.setAnimation(animationName, loopCount, this);
			} catch (Exception e) {
				Gdx.app.error("Entity", "Failed to set animation", e);
			}
		} else {
			Gdx.app.log("Entity", "Tried to set animation on non-renderable entity "+ id);
		}
	}

	/**
	 * Blends the animation of the entity to a new one
	 * @param animationName the animation to blend to
	 * @param loopCount the number of times to play the animation, -1 for infinite
	 * @param blendTime the time to blend the animation over
	 */
	public void blendAnimation(String animationName, int loopCount, float blendTime) {
		if (isRenderable) {
			try {
				scene.animationController.action(animationName, loopCount, 1f, this, blendTime);
			} catch (Exception e) {
				Gdx.app.error("Entity", "Failed to blend animation", e);
			}
		} else {
			Gdx.app.log("Entity", "Tried to blend animation on non-renderable entity "+ id);
		}
	}

	/**
	 * Disposes all relevant data from the entity
	 * */
	public void destroy() {
		dynamicsWorld.removeRigidBody(collider);
		entities.remove(id);
		if (isRenderable) sceneMan.removeScene(scene);
	}

	/**
	 * A runnable interface that can be passed an entity to run code on
	 * */
	@FunctionalInterface
	public interface EntityRunnable {
		void run(Entity entity);
	}

	@Override
	public void onEnd(AnimationController.AnimationDesc animation) {
		Gdx.app.debug("Entity", "Animation ended: " + animation.animation.id);
	}

	@Override
	public void onLoop(AnimationController.AnimationDesc animation) {
		Gdx.app.debug("Entity", "Animation looped: " + animation.animation.id);
	}
}