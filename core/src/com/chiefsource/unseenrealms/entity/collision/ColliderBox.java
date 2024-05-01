package com.chiefsource.unseenrealms.entity.collision;

import com.badlogic.gdx.math.Vector3;

public class ColliderBox implements IHasCollision {
    public Vector3 min;
    public Vector3 max;

    public ColliderBox(Vector3 min, Vector3 max) {
        this.min = min;
        this.max = max;
    }

    public ColliderBox(Vector3 pos, Vector3 min, Vector3 max) {
        this.min = pos.add(min);
        this.max = pos.add(max);
    }

    public ColliderBox(float width, float height, float depth) {
        this.min = new Vector3(-width / 2, -height / 2, -depth / 2);
        this.max = new Vector3(width / 2, height / 2, depth / 2);
    }
}
