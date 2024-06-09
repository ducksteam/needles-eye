package com.ducksteam.needleseye.entity.collision;

import com.badlogic.gdx.graphics.g3d.ModelCache;
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
    public ModelCache getRenderable() {
        ModelCache cache = new ModelCache();
        for (IHasCollision collider : colliders) {
            cache.add(collider.getRenderable());
        }
        return cache;
    }

    @Override
    public void updateColliderPosition(Vector3 centre) {
        for (IHasCollision collider : colliders) {
            collider.updateColliderPosition(centre.cpy().add(collider.getCentre()));
        }
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
    public void setCentre(Vector3 centre) {
        Vector3 groupCentre = this.getCentre();
        for (IHasCollision collider : colliders) {
            Vector3 delta = groupCentre.cpy().sub(collider.getCentre());
            collider.setCentre(centre.cpy().add(delta));
        }
    }
}
