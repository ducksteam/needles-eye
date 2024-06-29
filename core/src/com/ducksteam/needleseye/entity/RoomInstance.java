package com.ducksteam.needleseye.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.ducksteam.needleseye.entity.collision.ColliderGroup;
import com.ducksteam.needleseye.entity.collision.IHasCollision;
import com.ducksteam.needleseye.map.MapManager;
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
        super(MapManager.getRoomPos(roomSpacePos).sub(new Vector3(5,0,0)), new Vector2(MathUtils.degRad*rot, 0), new Vector3(0.5F, 0.5F, 0.5F));
        this.room = room;
        this.roomSpacePos = roomSpacePos;
        this.rot = rot;

        if (room.getCollider() == null) return;
        ColliderGroup collider = room.getCollider();
        this.collider = collider;

        assert collider != null;
        Vector3 colliderCentreOffset = MapManager.getRoomPos(roomSpacePos.cpy().sub(new Vector2(1,1)));
        Gdx.app.debug("RoomInstance", "Moving " + room.getName() + "@" + getRoomSpacePos() + " by " + colliderCentreOffset);
        for (IHasCollision c : collider.colliders) {
            c.setCentre(c.getCentre().add(colliderCentreOffset), true);
            if (!room.getName().equals("brokenceiling")) continue;
            Gdx.app.debug(c.getClass().getSimpleName() + " moving " + room.getName() + "@" + getRoomSpacePos(), "From: " + c.getCentre() + " To: " + c.getCentre().cpy().add(colliderCentreOffset));
        }
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
    public void updatePosition() {
        if(getModelInstance() != null) {
            Vector3 euler = sphericalToEuler(getRotation());
            getModelInstance().transform.setFromEulerAnglesRad(euler.x, euler.y, euler.z).trn(getPosition().cpy().add(getModelOffset())).scale(getScale().x, getScale().y, getScale().z);
        }
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
