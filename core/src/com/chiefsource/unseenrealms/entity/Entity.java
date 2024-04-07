package com.chiefsource.unseenrealms.entity;

import com.badlogic.gdx.math.Vector3;

public class Entity {

    private String modelAddress;
    private Vector3 position;
    public Entity(String modelAddress,Vector3 position){
        this.modelAddress=modelAddress;
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
