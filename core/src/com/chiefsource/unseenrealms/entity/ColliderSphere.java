package com.chiefsource.unseenrealms.entity;

public class ColliderSphere implements IHasCollision {
    private float radius;

    public ColliderSphere(float radius) {
        this.radius = radius;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
}
