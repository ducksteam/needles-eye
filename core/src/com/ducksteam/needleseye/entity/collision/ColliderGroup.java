package com.ducksteam.needleseye.entity.collision;

import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * A group of collision objects
 * @author SkySourced
 */
public class ColliderGroup implements IHasCollision {
    public ArrayList<IHasCollision> colliders = new ArrayList<>();

    public void addCollider(IHasCollision collider){
        colliders.add(collider);
        colliders.sort(Comparator.comparing(Object::hashCode));
    }

    public ColliderGroup(IHasCollision... colliders) {
        for (IHasCollision collider : colliders) {
            addCollider(collider);
        }
    }

    @Override
    public RenderableProvider getRenderable() {
        throw new UnsupportedOperationException("Cannot render a group of colliders");
    }

    @Override
    public Vector3 getCentre() {
        Vector3 centre = new Vector3();
        for (IHasCollision collider : colliders) {
            centre.add(collider.getCentre());
        }
        return centre.scl(1f / colliders.size());
    }

    @Override
    public void setCentre(Vector3 centre, boolean lockY) {
        Vector3 centreDelta = centre.cpy().sub(getCentre());
        if (lockY) centreDelta.y = 0;
        for (IHasCollision collider : colliders) {
            collider.setCentre(collider.getCentre().cpy().add(centreDelta), lockY);
        }
    }

    @Override
    public IHasCollision copy() {
        ColliderGroup group = new ColliderGroup();
        for (IHasCollision collider : colliders) {
            group.addCollider(collider.copy());
        }
        return group;
    }

    @Override
    public void move(Vector3 delta, boolean lockY) {
        if (lockY) delta.y = 0;
        for (IHasCollision collider : colliders) {
            collider.move(delta, lockY);
        }
    }

    @Override
    public ArrayList<IHasCollision> getColliders() {
        return colliders;
    }


    @Override
    public String toString() {
        return "ColliderGroup{" +
                "colliders=" + colliders +
                '}';
    }
}
