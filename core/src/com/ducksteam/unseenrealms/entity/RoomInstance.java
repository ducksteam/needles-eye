package com.ducksteam.unseenrealms.entity;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.ducksteam.unseenrealms.entity.WorldObject;
import com.ducksteam.unseenrealms.map.RoomTemplate;

/**
 * Represents an instance of a room in the world
 * @author SkySourced
 */

public class RoomInstance extends WorldObject {
    RoomTemplate room;
    Vector2 pos;
    int rot;//IN DEGREES

    public RoomInstance(RoomTemplate room, Vector2 pos, int rot) {
        super(new Vector3(pos.cpy().scl(100).x, pos.cpy().scl(100).y,0), new Vector2(MathUtils.degRad*rot, 0));
        this.room = room;
        this.pos = pos;
        this.rot = rot;
    }

    public RoomInstance(RoomTemplate room, Vector2 pos) {
        this(room, pos, 0);
    }

    public RoomTemplate getRoom() {
        return room;
    }

    public Vector2 getPos() {
        return pos;
    }

    public int getRot() {
        return rot;
    }

    @Override
    public String toString() {
        return "RoomInstance{" +
                "room={type=" + room.getType() +
                ", name=" + room.getName() +
                "}, pos=" + pos +
                ", rot=" + rot +
                '}';
    }

    @Override
    public String getModelAddress() {
        return room.getModelPath();
    }
}
