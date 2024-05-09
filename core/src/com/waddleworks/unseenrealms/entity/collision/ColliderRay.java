package com.waddleworks.unseenrealms.entity.collision;

import com.badlogic.gdx.math.Vector3;

public class ColliderRay implements IHasCollision {
    public Vector3 origin;
    public Vector3 direction;
    public boolean infinite;

    public ColliderRay(Vector3 origin, Vector3 direction) {
        this.origin = origin;
        this.direction = direction;
        if (origin == null) {
            throw new IllegalArgumentException("Origin cannot be null");
        }
        if (direction == null) {
            throw new IllegalArgumentException("Direction cannot be null");
        }
        this.infinite = false;
    }

    public ColliderRay(Vector3 origin, Vector3 direction, boolean infinite) {
        this(origin, direction);
        this.infinite = infinite;
    }

    public Vector3 getPoint(float distance) {
        return origin.cpy().add(direction.cpy().scl(distance));
    }
}
