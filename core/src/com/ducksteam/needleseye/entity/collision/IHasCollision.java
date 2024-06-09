package com.ducksteam.needleseye.entity.collision;

import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.math.Vector3;

/**
 * Interface for objects that can collide with other objects
 * @author SkySourced
 */
public interface IHasCollision {

    default boolean collidesWith(IHasCollision other) {
        return Collider.collidesWith(this, other);
    }

    RenderableProvider getRenderable();
    void updateColliderPosition(Vector3 centre);

    Vector3 getCentre();
    void setCentre(Vector3 centre);
}
