package com.ducksteam.unseenrealms.entity.collision;

/**
 * Interface for objects that can collide with other objects
 * @author SkySourced
 */
public interface IHasCollision {
    default boolean collidesWith(IHasCollision other) {
        return Collider.collidesWith(this, other);
    }
}
