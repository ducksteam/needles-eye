package com.ducksteam.needleseye.entity.collision;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;

/**
 * A group of collision objects
 * @author SkySourced
 */
public class ColliderGroup implements IHasCollision {
    public ArrayList<IHasCollision> colliders = new ArrayList<>();

    public void addCollider(IHasCollision collider){
        colliders.add(collider);
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
        Vector3 groupCentre = this.getCentre();
        for (IHasCollision collider : colliders) {
            Vector3 delta = groupCentre.cpy().sub(collider.getCentre());
            if (lockY) delta.y = 0;
            collider.setCentre(centre.cpy().add(delta), lockY);
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
    public String toString() {
        return "ColliderGroup{" +
                "colliders=" + colliders +
                '}';
    }
}
