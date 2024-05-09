package com.waddleworks.unseenrealms.entity;

import com.badlogic.gdx.math.Vector3;
import com.waddleworks.unseenrealms.entity.collision.IHasCollision;

public abstract class Entity {

    //TODO: Add numeric IDs for each object
    public static String id;
    public Boolean isRenderable;
    private Vector3 position;
    public IHasCollision collider;

    public Entity(Vector3 position){
        this.position=position;
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
}
