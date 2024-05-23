package com.ducksteam.unseenrealms.entity.collision;

import com.badlogic.gdx.graphics.g3d.ModelCache;
import com.badlogic.gdx.graphics.g3d.ModelInstance;

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
    public ModelCache render() {
        ModelCache cache = new ModelCache();
        for (IHasCollision collider : colliders) {
            cache.add(collider.render());
        }
        return cache;
    }
}
