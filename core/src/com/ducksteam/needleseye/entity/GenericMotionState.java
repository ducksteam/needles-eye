package com.ducksteam.needleseye.entity;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;

public class GenericMotionState extends btMotionState {
    Matrix4 transform;

    public GenericMotionState(Matrix4 transform) {
        super();
        this.transform = transform;
    }

    @Override
    public void setWorldTransform(Matrix4 worldTrans) {
        transform.set(worldTrans);
    }

    @Override
    public void getWorldTransform(Matrix4 worldTrans) {
        worldTrans.set(transform);
    }
}
