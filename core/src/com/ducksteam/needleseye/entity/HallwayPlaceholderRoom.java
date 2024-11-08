package com.ducksteam.needleseye.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.ducksteam.needleseye.map.RoomTemplate;

public class HallwayPlaceholderRoom extends RoomInstance {
    Vector2 associatedRoomCoords;
    RoomInstance associatedRoom;

    public static final RoomTemplate HALLWAY_PLACEHOLDER_TEMPLATE = new RoomTemplate(RoomTemplate.RoomType.HALLWAY_PLACEHOLDER, 0, 0, false, null, null, new Vector3(0, 0, 0));

    public HallwayPlaceholderRoom(Vector2 position, RoomInstance associatedRoom) {
        super(HALLWAY_PLACEHOLDER_TEMPLATE, position, associatedRoom.rot);
        this.associatedRoom = associatedRoom;
        this.associatedRoomCoords = associatedRoom.getRoomSpacePos();
    }

    public RoomInstance getAssociatedRoom() {
        return associatedRoom;
    }
}
