package com.ducksteam.unseenrealms.entity.collision;

import com.badlogic.gdx.math.Vector3;

/**
 * Collider for a sphere
 * @author SkySourced
 */
public class ColliderSphere implements IHasCollision {
    public float radius;
    public Vector3 centre;

    public ColliderSphere(Vector3 centre, float radius) {
        this.radius = radius;
        this.centre = centre;
    }
}
