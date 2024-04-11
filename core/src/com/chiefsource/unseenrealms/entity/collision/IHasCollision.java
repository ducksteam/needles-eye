package com.chiefsource.unseenrealms.entity.collision;

public interface IHasCollision {
    default boolean collidesWith(IHasCollision other) {
        return Collider.collidesWith(this, other);
    }
}
