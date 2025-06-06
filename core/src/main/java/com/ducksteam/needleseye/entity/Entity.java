package com.ducksteam.needleseye.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController.AnimationListener;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.ducksteam.needleseye.Main;
import com.ducksteam.needleseye.entity.bullet.EntityMotionState;
import com.ducksteam.needleseye.entity.bullet.WorldTrigger;
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

	// Collision group flags
    /**
     * Custom collision flag for rooms
     */
	public static final short GROUND_GROUP = 1 << 11;
    /**
     * Custom collision flag for players
     */
	public static final short PLAYER_GROUP = 1 << 12;
    /**
     * Custom collision flag for enemies
     */
	public static final short ENEMY_GROUP = 1 << 13;
    /**
     * Custom collision flag for projectiles
     */
	public static final short PROJECTILE_GROUP = 1 << 14;
    /**
     * Custom collision flag for pickups
     */
	public static final int PICKUP_GROUP = 1 << 15;
    /** Collision flag for triggers */
    public static final int TRIGGER_GROUP = 1 << 16;
    /** Collision flag for decos */
    public static final int DECO_GROUP = 1 << 17;
    /** Collision flag for attack */
    public static final int ATTACK_GROUP = 1 << 18;
	//Rendering and collision data
    /**
     * Whether the entity is renderable
     */
	public Boolean isRenderable;
    /**
     * Whether the entity is static (i.e. mass 0 and cannot be moved by bullet)
     */
	public Boolean isStatic;
    /** Whether the entity is in view of the camera frustum. Only entities with this set to true will be rendered */
    public Boolean isInFrame;
    /**
     * The transformation matrix of the entity
     */
	public Matrix4 transform = new Matrix4();
    /**
     * The entity's collider
     */
	public btRigidBody collider;
    /**
     * The collision shape of the entity
     */
	public btCollisionShape collisionShape;
    /**
     * The motion state of the entity for synchronising position of model and collider
     */
	public EntityMotionState motionState;
    /**
     * The model instance of the entity
     * @deprecated in favour of scenes
     */
	@Deprecated private ModelInstance modelInstance;
    /**
     * The scene asset of the entity
     */
	protected Scene scene;

    /** The centre of the bounding sphere*/
    private final Vector3 boundingSphereCentre = new Vector3();

    /** The radius of the bounding sphere*/
    private final float boundingSphereRadius;

    /**
     * The mass of the entity
     */
	private float mass = 0f;
    /**
     * The collision flags of the entity
     */
	protected int flags;

    /**
     * A temporary matrix for entity positioning
     */
	protected static final Matrix4 tmpMat = new Matrix4();

    /**
     * The next entity ID to be assigned
     */
	public static int currentId = 1;
    /**
     * The ID of the entity
     */
	public int id;

	/**
	 * Creates a static entity with mass 0
	 *
	 * @param position      the initial position
	 * @param rotation      the initial rotation
	 * @param scene the model instance of the entity
	 */

	public Entity(Vector3 position, Quaternion rotation, Scene scene) {
		this(position, rotation, 0f, scene, btCollisionObject.CollisionFlags.CF_STATIC_OBJECT);
	}

    public Entity(Vector3 position, Quaternion rotation, float mass, Scene scene, int flags) {
        this(position, rotation, mass, scene, flags, true);
    }

	/**
	 * Creates a dynamic entity
	 *
	 * @param position      the initial position
	 * @param rotation      the initial rotation
	 * @param mass          the mass of the entity
	 * @param scene the model instance of the entity
     * @param flags the collision flags of the entity to be passed to bullet
	 */
	public Entity(Vector3 position, Quaternion rotation, float mass, Scene scene, int flags, boolean createRB) {
		transform.idt().translate(position).rotate(rotation);

		id = currentId++;
		Main.entities.put(id, this);

		isStatic = mass == 0;
		isRenderable = scene != null;

		this.mass = mass;
		this.flags = flags;

		setScene(scene, createRB);

        if(this.scene==null) {
            boundingSphereCentre.setZero();
            boundingSphereRadius = 0;
            return;
        }
        BoundingBox boundingBox = new BoundingBox();
        Vector3 dimensions = new Vector3();
        this.scene.modelInstance.calculateBoundingBox(boundingBox);
        boundingBox.getCenter(boundingSphereCentre);
        boundingBox.getDimensions(dimensions);
        boundingSphereRadius = dimensions.len() / 2f;
	}

    /**
     * Only used in {@link WorldTrigger}
     */
    protected Entity() {
        isRenderable = false;
        boundingSphereCentre.setZero();
        boundingSphereRadius = 0;
    }

    /**
	 * Basic update method for entities
	 * @param delta the time since the last frame
	 * */
	public void update(float delta){
		motionState.setWorldTransform(transform);
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
     * Create a convex hull shape (ideal for dynamic entities)
     * @param mesh the mesh to create the shape of
     * @param optimize whether to optimize the collision shape to more closely fit the object
     * @return the new collision shape
     */
    public static btConvexHullShape obtainConvexHullShape(final Mesh mesh, boolean optimize) {
        final btConvexHullShape shape = new btConvexHullShape(mesh.getVerticesBuffer(false), mesh.getNumVertices(),
            mesh.getVertexSize());
        if (!optimize) return shape;
        // now optimize the shape
        final btShapeHull hull = new btShapeHull(shape);
        hull.buildHull(shape.getMargin());
        final btConvexHullShape result = new btConvexHullShape(hull);
        // delete the temporary shape
        shape.dispose();
        hull.dispose();
        return result;
    }

	/**
	 * Returns the scene asset of the entity
	 * @return the scene asset
	 * */
	public Scene getScene() {
		if (scene == null) return null;
		try {
			motionState.getWorldTransform(scene.modelInstance.transform);
		} catch (Exception e) {
			Gdx.app.error("Entity", "Failed to get scene", e);
		}
		return scene;
	}

    /**
     * Sets the scene asset of the entity
     * @param scene the scene to set
     */
    public void setScene(Scene scene) {
        setScene(scene, true);
    }

	/**
	 * Sets the scene asset of the entity
	 * @param scene the scene asset to set
     * @param createRB whether to build the rigidbody
	 * */
	public void setScene(Scene scene, boolean createRB) {
		this.scene = scene;
		if (isRenderable && createRB) {
			collisionShape = Bullet.obtainStaticNodeShape(scene.modelInstance.nodes); // set collision shape to model
			motionState = new EntityMotionState(this, transform); // set motion state to entity

			// calculate inertia
			Vector3 inertia = new Vector3();
			collisionShape.calculateLocalInertia(mass, inertia);

			// Creates rigid body
			collider = new btRigidBody(mass, motionState, collisionShape, inertia);
			collider.obtain();
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
     * Gets the centre of the bounding sphere
     * @return the centre of the bounding sphere
     * */
    public Vector3 getBoundingSphereCentre() {
        return boundingSphereCentre;
    }

    /**
     * Gets the radius of the bounding sphere
     * @return the radius of the bounding sphere
     * */
    public float getBoundingSphereRadius() {
        return boundingSphereRadius;
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
	public synchronized void destroy() {
		dynamicsWorld.removeRigidBody(collider);
        collisionShape.dispose();
        motionState.dispose();
        collider.dispose();
		entities.remove(id);
		if (isRenderable && scene != null) sceneMan.removeScene(scene);
	}

    /**
     * Identifies if an object is in the camera frustum
     */
    public boolean isInFrustum() {
        Scene scene = getScene();
        if (scene == null) return false;
        /*BoundingBox bounds = new BoundingBox();
        scene.modelInstance.calculateBoundingBox(bounds);
        return camera.frustum.boundsInFrustum(bounds);*/

        if (this instanceof DecoInstance deco && deco.shattered) return true; // fixme

        Vector3 position = new Vector3();
        scene.modelInstance.transform.getTranslation(position);
        position.add(getBoundingSphereCentre());
        return Main.camera.frustum.sphereInFrustum(position, getBoundingSphereRadius());
    }

    /**
	 * A runnable interface that can be passed an entity to run code on
	 * */
	@FunctionalInterface
	public interface EntityRunnable {
        /**
         * Run code on an entity
         * @param entity the entity to run code on
         */
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
