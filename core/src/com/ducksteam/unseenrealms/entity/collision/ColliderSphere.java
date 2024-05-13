package com.ducksteam.unseenrealms.entity.collision;

import com.badlogic.gdx.math.Vector3;

/**
 * Collider for a sphere
 * @author SkySourced
 */
public class ColliderSphere implements IHasCollision {
    public float radius;
    public Vector3 centre;

    public ColliderSphere(float radius, Vector3 centre) {
        this.radius = radius;
        this.centre = centre;
    }
}
