package com.ducksteam.unseenrealms.entity.collision;

import com.badlogic.gdx.graphics.g3d.ModelCache;

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
}
