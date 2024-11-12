package com.ducksteam.needleseye.entity.bullet;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import com.ducksteam.needleseye.entity.Entity;

/**
 * Wrapper for btMotionState that allows us to link a bullet physics object to an entity.
 * @author skysourced
 * */
public class EntityMotionState extends btMotionState {
	Entity entity;

	/**
	 * Create a new EntityMotionState for the given entity.
	 * @param entity The entity to link to the motion state.
	 * */
	public EntityMotionState(Entity entity) {
		super();
		this.entity = entity;
	}
	/**
	 * Create a new EntityMotionState for the given entity.
	 * @param entity The entity to link to the motion state.
	 * @param transform The initial transform of the entity.
	 * */
	public EntityMotionState(Entity entity, Matrix4 transform){
		this(entity);
		setWorldTransform(transform);
	}

	/**
	 * Get the transform of the entity.
	 * @param worldTrans The matrix to store the transform in.
	 * */
	@Override
	public void getWorldTransform(Matrix4 worldTrans) {
		worldTrans.set(entity.transform);
	}

	/**
	 * Set the transform of the entity.
	 * @param worldTrans The matrix to set the transform from.
	 * */
	@Override
	public void setWorldTransform(Matrix4 worldTrans) {
		entity.transform.set(worldTrans);
	}
}
