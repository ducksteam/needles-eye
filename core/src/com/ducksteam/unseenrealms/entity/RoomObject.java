package com.ducksteam.unseenrealms.entity;

import com.badlogic.gdx.math.Vector3;

@Deprecated
public class RoomObject extends WorldObject{
    RoomInstance roomData;
    String modelAddress;
    public RoomObject(RoomInstance room) {
        super(new Vector3(room.getPos().cpy().scl(100).x,room.getPos().cpy().scl(100).y,0), room.getRotation());
        this.roomData=room;
        this.modelAddress=room.getRoom().getModelPath();
    }

    @Override
    public String getModelAddress() {
        return modelAddress;
    }
}
