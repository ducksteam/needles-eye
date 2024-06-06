package com.ducksteam.unseenrealms.entity;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.ducksteam.unseenrealms.entity.collision.IHasCollision;

public abstract class Entity {

    //TODO: Add numeric IDs for each object
    public static String id;
    public Boolean isRenderable;
    private ModelInstance modelInstance;
    private Vector3 modelOffset;
    private Vector3 position;
    private Vector2 rotation; // azimuthal (xz plane) then polar (special plane)
    public IHasCollision collider;

    public Entity(Vector3 position, Vector2 rotation){
        this.position = position;
        this.rotation = rotation;
        setModelOffset(Vector3.Zero);
    }

    public abstract String getModelAddress();

    /*public void setModelAddress(String modelAddress) {
        this.modelAddress = modelAddress;
    }*/

    public ModelInstance getModelInstance() {
        return modelInstance;
    }

    public void setModelInstance(ModelInstance modelInstance) {
        this.modelInstance = modelInstance;
    }
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

    public void updatePosition(){
        if(modelInstance != null) {
            modelInstance.transform.setTranslation(position.cpy().add(modelOffset));
        }
        if(collider != null){
            collider.updateColliderPosition(position.cpy());
        }
        //modelInstance.transform.setToRotation(Vector3.Y, rotation.x);
    }

    public void setModelOffset(Vector3 offset){
        this.modelOffset = offset;
    }
}
