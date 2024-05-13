package com.ducksteam.unseenrealms.entity.collision;

import java.util.ArrayList;

public class ColliderGroup implements IHasCollision {
    public ArrayList<IHasCollision> colliders = new ArrayList<>();

    public void add(IHasCollision collider){
        colliders.add(collider);
    }
}
