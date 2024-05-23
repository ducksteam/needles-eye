package com.ducksteam.unseenrealms.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.ducksteam.unseenrealms.entity.collision.IHasCollision;

public abstract class Entity {

    //TODO: Add numeric IDs for each object
    public static String id;
    public Boolean isRenderable;
    private Vector3 position;
    private Vector2 rotation; // azimuthal (xz plane) then polar (special plane)
    public IHasCollision collider;

    public Entity(Vector3 position, Vector2 rotation){
        this.position = position;
        this.rotation = rotation;
    }

    public abstract String getModelAddress();

    /*public void setModelAddress(String modelAddress) {
        this.modelAddress = modelAddress;
    }*/
    public Vector3 getPosition() {
        return position;
    }
    public void setPosition(Vector3 position) {
        this.position = position;
    }
    public Vector2 getRotation() {
        return rotation;
    }
    public void setRotation(Vector2 rotation) {
        this.rotation = rotation;
    }
}
