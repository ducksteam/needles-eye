package com.chiefsource.unseenrealms.entity;

import com.badlogic.gdx.math.Vector3;

public class ColliderBox implements IHasCollision {
    Vector3 min;
    Vector3 max;
    Vector3 cOffset;

    ColliderBox(Vector3 min, Vector3 max) {
        this.min = min;
        this.max = max;
    }

    ColliderBox(Vector3 min, Vector3 max, Vector3 cOffset) {
        this.min = min;
        this.max = max;
        this.cOffset = cOffset;
    }

    ColliderBox(float width, float height, float depth) {
        this.min = new Vector3(-width / 2, -height / 2, -depth / 2);
        this.max = new Vector3(width / 2, height / 2, depth / 2);
    }

    ColliderBox(float width, float height, float depth, Vector3 cOffset) {
        this.min = new Vector3(-width / 2, -height / 2, -depth / 2);
        this.max = new Vector3(width / 2, height / 2, depth / 2);
        this.cOffset = cOffset;
    }
}
