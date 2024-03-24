package com.chiefsource.unseenrealms.map;

import com.badlogic.gdx.Gdx;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class MapManager {
    private ArrayList<RoomTemplate> templates;
    private ArrayList<Level> levels;
    private int levelIndex;

    // paths
    public final String ROOM_TEMPLATE_PATH = "data/rooms/";

    public MapManager() {
        templates = new ArrayList<RoomTemplate>();
        levels = new ArrayList<Level>();
        levelIndex = 0;

        // load room templates
        File roomDir = new File(ROOM_TEMPLATE_PATH);
        if (!roomDir.exists()) {
            Gdx.app.error("MapManager", "Room template directory not found: " + ROOM_TEMPLATE_PATH);
            return;
        }
        for (File file : Objects.requireNonNull(roomDir.listFiles())) {
            Gdx.app.debug("MapManager", "Loading room template: " + file.getName());
            if (file.getName().endsWith(".json")) {
                templates.add(RoomTemplate.loadRoomTemplate(file));
            }
            Gdx.app.debug("MapManager", "Loaded " + file.getName() + ": \n" + templates.getLast().toString());
        }
        Gdx.app.debug("MapManager", "Loaded " + templates.size() + " room templates");
    }
}
