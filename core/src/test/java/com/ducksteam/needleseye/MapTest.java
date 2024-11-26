package com.ducksteam.needleseye;


import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.ducksteam.needleseye.map.MapManager;
import com.ducksteam.needleseye.map.RoomTemplate;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MapTest {
    ArrayList<RoomTemplate> roomTemplates = new ArrayList<>();
    String roomsJson = """
        [
          {
            "name": "battlerock",
            "type": "battle",
            "modelPath": "models/rooms/battlerock.gltf",
            "width": 1,
            "height": 1,
            "decos": [],
            "doors": {
              "0": true,
              "1": true,
              "2": true,
              "3": true
            },
            "enemies": [
              {
                "tag": "small",
                "position": [-2, 0.6, 0]
              },
              {
                "tag": "small",
                "position": [1, 0.6, 1.75]
              },
              {
                "tag": "small",
                "position": [1, 0.6, -1.75]
              }
            ]
          },
          {
            "name": "brokenceiling",
            "type": "small",
            "modelPath": "models/rooms/brokenceiling.gltf",
            "width": 1,
            "height": 1,
            "decos": [],
            "doors": {
              "0": true,
              "1": true,
              "2": true,
              "3": true
            },
            "enemies": [
              {
                "tag": "all",
                "position": [-2, 0.6, 0]
              },
              {
                "tag": "all",
                "position": [2, 0.6, 0]
              }
            ]
          },
          {
            "name": "pedestal",
            "type": "treasure",
            "modelPath": "models/rooms/pedestal.gltf",
            "width": 1,
            "height": 1,
            "decos": [],
            "doors": {
              "0": true,
              "1": false,
              "2": false,
              "3": false
            },
            "enemies": [

            ]
          },
          {
            "name": "pillars",
            "type": "hallway",
            "modelPath": "models/rooms/pillars.gltf",
            "width": 1,
            "height": 2,
            "decos": [
              {
                "name": "pot",
                "position": [0, 0, 0]
              }
            ],
            "doors": {
              "0": true,
              "1": true,
              "2": true,
              "3": false,
              "4": true,
              "5": true,
              "6": true
            },
            "enemies": [

            ]
          },
          {
            "name": "prison",
            "type": "hallway",
            "modelPath": "models/rooms/prison.gltf",
            "width": 1,
            "height": 2,
            "decos": [],
            "doors": {
              "0": true,
              "1": true,
              "2": true,
              "3": false,
              "4": true,
              "5": true,
              "6": true
            },
            "enemies": [

            ]
          },
          {
            "name": "rockroom",
            "type": "small",
            "modelPath": "models/rooms/rockroom.gltf",
            "width": 1,
            "height": 1,
            "decos": [
              {
                "name": "pot",
                "position": [0, 0, 0]
              }
            ],
            "doors": {
              "0": true,
              "1": true,
              "2": true,
              "3": true
            },
            "enemies": [
              {
                "tag": "ranged",
                "position": [-3.5, 0.6, -3.5]
              },
              {
                "tag": "melee",
                "position": [0, 0.6, 2]
              },
              {
                "tag": "melee",
                "position": [2, 0.6, 0]
              }
            ]
          },
          {
            "name": "slantedcorridor",
            "type": "small",
            "modelPath": "models/rooms/slantedcorridor.gltf",
            "width": 1,
            "height": 1,
            "decos": [
              {
                "name": "pot",
                "position": [0, 0, 0]
              },
              {
                "name": "pot",
                "position": [0.5, 0, 0]
              },
              {
                "name": "pot",
                "position": [0, 0.5, 0]
              }
            ],
            "doors": {
              "0": true,
              "1": true,
              "2": true,
              "3": true
            },
            "enemies": [
              {
                "tag": "melee",
                "position": [-2.5, 0.6, 1.2]
              },
              {
                "tag": "melee",
                "position": [2.5, 0.6, -1.2]
              }
            ]
          }
        ]
        """;

    private void loadTemplates() {
        Json json = new Json();
        roomTemplates = RoomTemplate.loadRoomTemplates(json.fromJson(null, roomsJson));
    }

    private RoomTemplate getRoomTemplateWithName(String name) {
        if (roomTemplates.isEmpty()) loadTemplates();
        for (RoomTemplate roomTemplate : roomTemplates) {
            if (roomTemplate.getName().equals(name)) {
                return roomTemplate;
            }
        }
        throw new IllegalArgumentException("Room template not found");
    }

    @Test
    public void testGetConnectingDoor() {
//        RoomInstance rockroom = new RoomInstance(getRoomTemplateWithName("rockroom"), new Vector2(1, 1), 0);

        // normal connections
        assertEquals(5, MapManager.getConnectingDoor(getRoomTemplateWithName("prison"), 0, new Vector2(0, 0), new Vector2(1,1), 1, 0));
        assertEquals(4, MapManager.getConnectingDoor(getRoomTemplateWithName("pillars"), 0, new Vector2(2, 0), new Vector2(1,1), 2, 0));
        assertEquals(3, MapManager.getConnectingDoor(getRoomTemplateWithName("rockroom"), 0, new Vector2(1, 0), new Vector2(1,1), 0, 0));
        assertEquals(0, MapManager.getConnectingDoor(getRoomTemplateWithName("pedestal"), 0, new Vector2(5, 7), new Vector2(5, 6), 3, 0));

        // door disabled
        assertEquals(-1, MapManager.getConnectingDoor(getRoomTemplateWithName("pedestal"), 0, new Vector2(5, 7), new Vector2(5, 6), 0, 0));
        assertEquals(-1, MapManager.getConnectingDoor(getRoomTemplateWithName("pedestal"), 0, new Vector2(5, 7), new Vector2(5, 6), 1, 90));
        assertEquals(-1, MapManager.getConnectingDoor(getRoomTemplateWithName("pedestal"), 90, new Vector2(5, 7), new Vector2(6, 7), 3, 180));

        // no doors touching at all
        assertEquals(-1, MapManager.getConnectingDoor(getRoomTemplateWithName("pillars"), 0, new Vector2(5, 7), new Vector2(1, 6), 2, 0));
        assertEquals(-1, MapManager.getConnectingDoor(getRoomTemplateWithName("brokenceiling"), 0, new Vector2(5, 7), new Vector2(9, 6), 4, 180));
        assertEquals(-1, MapManager.getConnectingDoor(getRoomTemplateWithName("slantedcorridor"), 90, new Vector2(5, 7), new Vector2(9, 6), 6, 270));

        // observed cases
        assertEquals(3, MapManager.getConnectingDoor(getRoomTemplateWithName("rockroom"), 90, new Vector2(1, 0), new Vector2(0, 0), 1, 180));

    }

    @Test
    public void testGetDoorRoomSpacePos() {
        assertEquals(new Vector2(0.5f, 0), MapManager.getDoorRoomSpacePos(new Vector2(0, 0), 0, 0));
        assertEquals(new Vector2(0, 0.5f), MapManager.getDoorRoomSpacePos(new Vector2(0, 0), 1, 0));
        assertEquals(new Vector2(1, 0.5f), MapManager.getDoorRoomSpacePos(new Vector2(0, 0), 2, 0));
        assertEquals(new Vector2(0.5f, 1), MapManager.getDoorRoomSpacePos(new Vector2(0, 0), 3, 0));
        assertEquals(new Vector2(0, 1.5f), MapManager.getDoorRoomSpacePos(new Vector2(0, 0), 4, 0));
        assertEquals(new Vector2(1, 1.5f), MapManager.getDoorRoomSpacePos(new Vector2(0, 0), 5, 0));
        assertEquals(new Vector2(0.5f, 2), MapManager.getDoorRoomSpacePos(new Vector2(0, 0), 6, 0));

        assertEquals(new Vector2(1, 0.5f), MapManager.getDoorRoomSpacePos(new Vector2(0, 0), 0, 90));
        assertEquals(new Vector2(0.5f, 0), MapManager.getDoorRoomSpacePos(new Vector2(0, 0), 1, 90));
        assertEquals(new Vector2(0.5f, 1), MapManager.getDoorRoomSpacePos(new Vector2(0, 0), 2, 90));
        assertEquals(new Vector2(0, 0.5f), MapManager.getDoorRoomSpacePos(new Vector2(0, 0), 3, 90));
        assertEquals(new Vector2(-0.5f, 0), MapManager.getDoorRoomSpacePos(new Vector2(0, 0), 4, 90));
        assertEquals(new Vector2(-0.5f, 1), MapManager.getDoorRoomSpacePos(new Vector2(0, 0), 5, 90));
        assertEquals(new Vector2(-1, 0.5f), MapManager.getDoorRoomSpacePos(new Vector2(0, 0), 6, 90));

        assertEquals(new Vector2(0.5f, 1), MapManager.getDoorRoomSpacePos(new Vector2(0, 0), 0, 180));
        assertEquals(new Vector2(1, 0.5f), MapManager.getDoorRoomSpacePos(new Vector2(0, 0), 1, 180));
        assertEquals(new Vector2(0, 0.5f), MapManager.getDoorRoomSpacePos(new Vector2(0, 0), 2, 180));
        assertEquals(new Vector2(0.5f, 0), MapManager.getDoorRoomSpacePos(new Vector2(0, 0), 3, 180));
        assertEquals(new Vector2(1, -0.5f), MapManager.getDoorRoomSpacePos(new Vector2(0, 0), 4, 180));
        assertEquals(new Vector2(0, -0.5f), MapManager.getDoorRoomSpacePos(new Vector2(0, 0), 5, 180));
        assertEquals(new Vector2(0.5f, -1), MapManager.getDoorRoomSpacePos(new Vector2(0, 0), 6, 180));

        assertEquals(new Vector2(0, 0.5f), MapManager.getDoorRoomSpacePos(new Vector2(0, 0), 0, 270));
        assertEquals(new Vector2(0.5f, 1), MapManager.getDoorRoomSpacePos(new Vector2(0, 0), 1, 270));
        assertEquals(new Vector2(0.5f, 0), MapManager.getDoorRoomSpacePos(new Vector2(0, 0), 2, 270));
        assertEquals(new Vector2(1, 0.5f), MapManager.getDoorRoomSpacePos(new Vector2(0, 0), 3, 270));
        assertEquals(new Vector2(1.5f, 1), MapManager.getDoorRoomSpacePos(new Vector2(0, 0), 4, 270));
        assertEquals(new Vector2(1.5f, 0), MapManager.getDoorRoomSpacePos(new Vector2(0, 0), 5, 270));
        assertEquals(new Vector2(2, 0.5f), MapManager.getDoorRoomSpacePos(new Vector2(0, 0), 6, 270));

        assertEquals(new Vector2(19.5f, 39), MapManager.getDoorRoomSpacePos(new Vector2(19, 39), 0, 0));
        assertEquals(new Vector2(19, 39.5f), MapManager.getDoorRoomSpacePos(new Vector2(19, 39), 1, 0));
        assertEquals(new Vector2(20, 39.5f), MapManager.getDoorRoomSpacePos(new Vector2(19, 39), 2, 0));
        assertEquals(new Vector2(19.5f, 40), MapManager.getDoorRoomSpacePos(new Vector2(19, 39), 3, 0));
        assertEquals(new Vector2(19, 40.5f), MapManager.getDoorRoomSpacePos(new Vector2(19, 39), 4, 0));
        assertEquals(new Vector2(20, 40.5f), MapManager.getDoorRoomSpacePos(new Vector2(19, 39), 5, 0));
        assertEquals(new Vector2(19.5f, 41), MapManager.getDoorRoomSpacePos(new Vector2(19, 39), 6, 0));

        assertEquals(new Vector2(20, 39.5f), MapManager.getDoorRoomSpacePos(new Vector2(19, 39), 0, 90));
        assertEquals(new Vector2(19.5f, 39), MapManager.getDoorRoomSpacePos(new Vector2(19, 39), 1, 90));
        assertEquals(new Vector2(19.5f, 40), MapManager.getDoorRoomSpacePos(new Vector2(19, 39), 2, 90));
        assertEquals(new Vector2(19, 39.5f), MapManager.getDoorRoomSpacePos(new Vector2(19, 39), 3, 90));
        assertEquals(new Vector2(18.5f, 39), MapManager.getDoorRoomSpacePos(new Vector2(19, 39), 4, 90));
        assertEquals(new Vector2(18.5f, 40), MapManager.getDoorRoomSpacePos(new Vector2(19, 39), 5, 90));
        assertEquals(new Vector2(18, 39.5f), MapManager.getDoorRoomSpacePos(new Vector2(19, 39), 6, 90));

        assertEquals(new Vector2(19.5f, 40), MapManager.getDoorRoomSpacePos(new Vector2(19, 39), 0, 180));
        assertEquals(new Vector2(20, 39.5f), MapManager.getDoorRoomSpacePos(new Vector2(19, 39), 1, 180));
        assertEquals(new Vector2(19, 39.5f), MapManager.getDoorRoomSpacePos(new Vector2(19, 39), 2, 180));
        assertEquals(new Vector2(19.5f, 39), MapManager.getDoorRoomSpacePos(new Vector2(19, 39), 3, 180));
        assertEquals(new Vector2(20, 38.5f), MapManager.getDoorRoomSpacePos(new Vector2(19, 39), 4, 180));
        assertEquals(new Vector2(19, 38.5f), MapManager.getDoorRoomSpacePos(new Vector2(19, 39), 5, 180));
        assertEquals(new Vector2(19.5f, 38), MapManager.getDoorRoomSpacePos(new Vector2(19, 39), 6, 180));

        assertEquals(new Vector2(19, 39.5f), MapManager.getDoorRoomSpacePos(new Vector2(19, 39), 0, 270));
        assertEquals(new Vector2(19.5f, 40), MapManager.getDoorRoomSpacePos(new Vector2(19, 39), 1, 270));
        assertEquals(new Vector2(19.5f, 39), MapManager.getDoorRoomSpacePos(new Vector2(19, 39), 2, 270));
        assertEquals(new Vector2(20, 39.5f), MapManager.getDoorRoomSpacePos(new Vector2(19, 39), 3, 270));
        assertEquals(new Vector2(20.5f, 40), MapManager.getDoorRoomSpacePos(new Vector2(19, 39), 4, 270));
        assertEquals(new Vector2(20.5f, 39), MapManager.getDoorRoomSpacePos(new Vector2(19, 39), 5, 270));
        assertEquals(new Vector2(21, 39.5f), MapManager.getDoorRoomSpacePos(new Vector2(19, 39), 6, 270));

        assertEquals(new Vector2(-9.5f, -7), MapManager.getDoorRoomSpacePos(new Vector2(-10, -7), 0, 0));
        assertEquals(new Vector2(-10,  -6.5f), MapManager.getDoorRoomSpacePos(new Vector2(-10, -7), 1, 0));
        assertEquals(new Vector2(-9, -6.5f), MapManager.getDoorRoomSpacePos(new Vector2(-10, -7), 2, 0));

        assertEquals(new Vector2(-9, -6.5f), MapManager.getDoorRoomSpacePos(new Vector2(-10, -7), 0, 90));
        assertEquals(new Vector2(-9.5f, -7), MapManager.getDoorRoomSpacePos(new Vector2(-10, -7), 1, 90));
        assertEquals(new Vector2(-9.5f, -6), MapManager.getDoorRoomSpacePos(new Vector2(-10, -7), 2, 90));

        assertEquals(new Vector2(-9.5f, -6), MapManager.getDoorRoomSpacePos(new Vector2(-10, -7), 0, 180));
        assertEquals(new Vector2(-9, -6.5f), MapManager.getDoorRoomSpacePos(new Vector2(-10, -7), 1, 180));
        assertEquals(new Vector2(-10, -6.5f), MapManager.getDoorRoomSpacePos(new Vector2(-10, -7), 2, 180));

        assertEquals(new Vector2(-10, -6.5f), MapManager.getDoorRoomSpacePos(new Vector2(-10, -7), 0, 270));
        assertEquals(new Vector2(-9.5f, -6), MapManager.getDoorRoomSpacePos(new Vector2(-10, -7), 1, 270));
        assertEquals(new Vector2(-9.5f, -7), MapManager.getDoorRoomSpacePos(new Vector2(-10, -7), 2, 270));
    }
}
