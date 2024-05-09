package com.ducksteam.unseenrealms.map;

import com.badlogic.gdx.math.Vector2;

/**
 * A room in a level
 */

public class RoomInstance extends RoomTemplate {
    RoomTemplate room;
    Vector2 pos;
    int rot;

    public RoomInstance(RoomTemplate room, Vector2 pos, int rot) {
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
}
