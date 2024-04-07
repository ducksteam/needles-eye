package com.chiefsource.unseenrealms.entity;

import com.badlogic.gdx.math.Vector3;

public class Entity {

    //TODO: Add numeric IDs for each object
    public static String id;
    public static String modelAddress;
    private Vector3 position;
    public Entity(Vector3 position){
        this.position=position;
    }

    public String getModelAddress() {
        return modelAddress;
    }
    public void setModelAddress(String modelAddress) {
        this.modelAddress = modelAddress;
    }
    public Vector3 getPosition() {
        return position;
    }
    public void setPosition(Vector3 position) {
        this.position = position;
    }
}
