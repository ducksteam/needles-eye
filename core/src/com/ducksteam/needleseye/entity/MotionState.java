package com.ducksteam.needleseye.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;

public class MotionState extends btMotionState {
	Entity entity;

	public MotionState(Entity entity) {
		super();
		this.entity = entity;
	}

	public MotionState(Entity entity, Matrix4 transform){
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
