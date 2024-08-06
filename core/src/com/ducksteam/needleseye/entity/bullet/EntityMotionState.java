package com.ducksteam.needleseye.entity.bullet;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import com.ducksteam.needleseye.entity.Entity;

public class EntityMotionState extends btMotionState {
	Entity entity;

	public EntityMotionState(Entity entity) {
		super();
		this.entity = entity;
	}

	public EntityMotionState(Entity entity, Matrix4 transform){
		this(entity);
		setWorldTransform(transform);
	}

	@Override
	public void getWorldTransform(Matrix4 worldTrans) {
		worldTrans.set(entity.transform);
	}

	@Override
	public void setWorldTransform(Matrix4 worldTrans) {
		entity.transform.set(worldTrans);
	}
}
