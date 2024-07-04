package com.ducksteam.needleseye.entity.collision;

import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;

/**
 * Interface for objects that can collide with other objects
 * @author SkySourced
 */
public interface IHasCollision {
    default boolean collidesWith(IHasCollision other) {
        return Collider.collidesWith(this, other);
    }
    default Vector3 contactNormal(IHasCollision other) { return Collider.contactNormal(this, other);}
    RenderableProvider getRenderable();

    Vector3 getCentre();
    void setCentre(Vector3 centre, boolean lockY);
    void move(Vector3 delta, boolean lockY);
    IHasCollision copy();
    ArrayList<IHasCollision> getColliders();
}
