package com.ducksteam.needleseye.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.ducksteam.needleseye.entity.collision.ColliderGroup;
import com.ducksteam.needleseye.entity.collision.IHasCollision;
import com.ducksteam.needleseye.map.RoomTemplate;

import static com.ducksteam.needleseye.Config.ROOM_SCALE;

/**
 * Represents an instance of a room in the world
 * @author SkySourced
 */

public class RoomInstance extends WorldObject {
    RoomTemplate room;
    Vector2 roomSpacePos;
    int rot;//IN DEGREES

    public RoomInstance(RoomTemplate room, Vector2 roomSpacePos, int rot) {
        super(new Vector3(roomSpacePos.cpy().scl(ROOM_SCALE).x-5,0, roomSpacePos.cpy().scl(ROOM_SCALE).y).add(room.getCentreOffset()), new Vector2(MathUtils.degRad*rot, 0), new Vector3(0.5F, 0.5F, 0.5F));
        this.room = room;
        this.roomSpacePos = roomSpacePos;
        this.rot = rot;

        if (room.getCollider() == null) return;
        ColliderGroup collider = room.getCollider();
        this.collider = collider;

        assert collider != null;
        Vector3 roomSpacePos3 = new Vector3(roomSpacePos.cpy().scl(ROOM_SCALE).x - 10, 0, roomSpacePos.cpy().scl(ROOM_SCALE).y - 10);
        for (IHasCollision c : collider.colliders) {
            Gdx.app.debug("Collider moving", "From: " + c.getCentre() + " To: " + c.getCentre().add(roomSpacePos3));
            c.setCentre(c.getCentre().add(roomSpacePos3), true);
        }

//        Vector3 colliderCentreOffset = new Vector3((float) (room.getWidth() * ROOM_SCALE) /2 +5, 0, (float) (room.getHeight() * ROOM_SCALE) /2);
//        collider.setCentre(this.getPosition().cpy().add(colliderCentreOffset), true); // i have no idea if this works or any of the collision code that i just wrote
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

    public boolean isAdjacent(RoomInstance other) {
        return roomSpacePos.dst(other.roomSpacePos) == 1;
    }

    /*public boolean hasCommonDoor(RoomInstance other) {
        return (isAdjacent(other) &&
    }*/

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
