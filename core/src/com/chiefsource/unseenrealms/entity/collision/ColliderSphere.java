package com.chiefsource.unseenrealms.entity.collision;

import com.badlogic.gdx.math.Vector3;

public class ColliderSphere implements IHasCollision {
    public float radius;
    public Vector3 centre;

    public ColliderSphere(float radius, Vector3 centre) {
        this.radius = radius;
        this.centre = centre;
    }
}
