package com.ducksteam.needleseye.entity;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.ducksteam.needleseye.entity.collision.IHasCollision;
import com.ducksteam.needleseye.map.RoomTemplate;

/**
 * Represents an instance of a room in the world
 * @author SkySourced
 */

public class RoomInstance extends WorldObject {
    RoomTemplate room;
    Vector2 roomSpacePos;
    int rot;//IN DEGREES

    public RoomInstance(RoomTemplate room, Vector2 roomSpacePos, int rot) {
        super(new Vector3(roomSpacePos.cpy().scl(20).x,0, roomSpacePos.cpy().scl(20).y).add(room.getCentreOffset()), new Vector2(MathUtils.degRad*rot, 0));
        this.room = room;
        this.roomSpacePos = roomSpacePos;
        this.rot = rot;

        if (room.getCollider() == null) return;
        collider.setCentre(this.getPosition()); // i have no idea if this works or any of the collision code that i just wrote
        this.collider = room.getCollider();
    }

    public RoomInstance(RoomTemplate room, Vector2 pos) {
        this(room, pos, 0);
    }

    public RoomTemplate getRoom() {
        return room;
    }

    public Vector2 getRoomSpacePos() {
        return roomSpacePos;
    }

    public int getRot() {
        return rot;
    }

    @Override
    public String toString() {
        return "RoomInstance{" +
                "room={type=" + room.getType() +
                ", name=" + room.getName() +
                "}, pos=" + roomSpacePos +
                ", rot=" + rot +
                '}';
    }

    @Override
    public String getModelAddress() {
        return room.getModelPath();
    }
}
