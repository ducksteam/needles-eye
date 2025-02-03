package com.ducksteam.needleseye.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.ducksteam.needleseye.map.RoomTemplate;

import java.util.ArrayList;

/**
 * Represents a placeholder room for a hallway.
 * This is used to prevent generation of rooms in the second position of a hallway
 * @author SkySourced
 */
public class HallwayPlaceholderRoom extends RoomInstance {
    /**
     * The room space coordinates of the room this placeholder is associated with
     */
    Vector2 associatedRoomCoords;
    /**
     * The room this placeholder is associated with
     */
    RoomInstance associatedRoom;

    /**
     * The template for a hallway placeholder room.
     * Very generic
     */
    public static final RoomTemplate HALLWAY_PLACEHOLDER_TEMPLATE = new RoomTemplate(RoomTemplate.RoomType.HALLWAY_PLACEHOLDER, 0, 0, false, null, new Vector3(0, 0, 0), new ArrayList<>(), new ArrayList<>());

    /**
     * Create a new hallway placeholder room
     * @param position the position of the placeholder
     * @param associatedRoom the room this placeholder is associated with
     */
    public HallwayPlaceholderRoom(Vector2 position, RoomInstance associatedRoom) {
        super(HALLWAY_PLACEHOLDER_TEMPLATE, position, associatedRoom.rot);
        this.associatedRoom = associatedRoom;
        this.associatedRoomCoords = associatedRoom.getRoomSpacePos();
    }

    /**
     * Get the room this placeholder is associated with
     * @return the associated room
     */
    public RoomInstance getAssociatedRoom() {
        return associatedRoom;
    }
}
