package com.chiefsource.unseenrealms.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class MapManager {
    private ArrayList<RoomTemplate> templates;
    private ArrayList<DecoInstance> decos;
    private ArrayList<Level> levels;
    private int levelIndex; // number of levels generated

    // placeholder room for hallways
    public static final RoomTemplate HALLWAY_PLACEHOLDER = new RoomTemplate(RoomTemplate.RoomType.HALLWAY_PLACEHOLDER, 0, 0, false, null, null);

    // paths
    public final String ROOM_TEMPLATE_PATH = "data/rooms/";

    public MapManager() {
        templates = new ArrayList<>();
        levels = new ArrayList<>();
        levelIndex = 1;

        // load room templates
        File roomDir = new File(ROOM_TEMPLATE_PATH);
        if (!roomDir.exists()) { // check if the room template directory exists
            Gdx.app.error("MapManager", "Room template directory not found: " + ROOM_TEMPLATE_PATH);
            return;
        }
        for (File file : Objects.requireNonNull(roomDir.listFiles())) { // load all room templates
            Gdx.app.debug("MapManager", "Loading room template: " + file.getName());
            if (file.getName().endsWith(".json")) { // only load json files
                templates.add(RoomTemplate.loadRoomTemplate(file)); // load the room template
            }
            Gdx.app.debug("MapManager", "Loaded " + file.getName() + ": \n" + templates.getLast().toString());
        }
        Gdx.app.debug("MapManager", "Loaded " + templates.size() + " room templates");

        generateLevel(); // generate the first level
    }

    /**
     * Generate a new level
     */
    public void generateLevel() {
        Level level = new Level(levelIndex); // create an empty level object

        RoomInstance room = new RoomInstance(getRandomRoomTemplate(RoomTemplate.RoomType.HALLWAY), new Vector2(0, 0)); // build a base hallway
        level.addRoom(room);

        for (int i = 0; i < levelIndex * 5; i++){ // add a random number of rooms, increasing with each level
            RoomTemplate.RoomType nextType = RoomTemplate.RoomType.getRandomRoomType(); // get a random room type
            RoomTemplate nextTemplate = getRandomRoomTemplate(nextType); // get a random room template of that type

            Vector2 nextPos = generateRoomPos(nextTemplate, level.getRooms()); // generate a valid position for the room
            int rot = (int) Math.floor(Math.random() * 4) * 90; // random rotation

            RoomInstance nextRoom = new RoomInstance(nextTemplate, nextPos, rot); // create the room
            level.addRoom(nextRoom);
        }

        Gdx.app.debug("MapManager", "Generated level " + levelIndex + " with " + level.getRooms().size() + " rooms");
        Gdx.app.debug("Level "+levelIndex, level.toString());

        levels.add(level); // add the level to the list
        levelIndex++; // increment the level index
    }

    /**
     * Generates a valid position for a room.
     * Does not consider any alternative rotations, or if the selected border has a door disabled
     * @param template the room to generate a position for
     * @param rooms the rooms already placed in the level
     * @return a valid position for the room
     */
    private Vector2 generateRoomPos(RoomTemplate template, ArrayList<RoomInstance> rooms) {
        RoomInstance room = getRandomElement(rooms); // get a random room to attach to

        int doorCount = room.getRoom().getType() == RoomTemplate.RoomType.HALLWAY ? 7 : 4; // hallways have 7 doors

        int door = (int) Math.floor(Math.random() * doorCount);
        while (doorCount == 7 && door == 3) door = (int) Math.floor(Math.random() * doorCount); // but door 3 isn't used in hallways

        /*   6
         *   _
         * 4|3|5
         *  |_|
         * 1| |2
         *  |_|
         *   0
         */

        Vector2 offset = switch (door) { // door offset
            case 0 -> new Vector2(0, -1);
            case 1 -> new Vector2(-1, 0);
            case 2 -> new Vector2(1, 0);
            case 3 -> new Vector2(0, 1);
            case 4 -> new Vector2(-1, 1);
            case 5 -> new Vector2(1, 1);
            case 6 -> new Vector2(0, 2);
            default -> throw new IllegalStateException("Unexpected value: " + door);
        };

        offset.rotateDeg(room.getRot()); // rotate offset to match room rotation
        Vector2 pos = nonMutatingVectorAdd(room.getPos(), offset); // add offset to room position
        Gdx.app.debug("Door" + door, "Offset: " + offset + " Pos: " + pos);

        for (RoomInstance ri : rooms) { // check if the position is already taken
            for (int h = 0; h < template.getHeight(); h++) { // check all the tiles that the room would occupy
                for (int w = 0; w < template.getWidth(); w++){
                    if (ri.getPos().equals(nonMutatingVectorAdd(pos, new Vector2(w, h)))) { // if the position is already taken
                        return generateRoomPos(template, rooms); // try again
                    }
                }
            }
        }

        return pos; // the position is valid
    }

    /**
     * Get a random room template of a specific type
     * @param type the type of room to get
     * @return a random room template
     */
    private RoomTemplate getRandomRoomTemplate(RoomTemplate.RoomType type){
        RoomTemplate template = getRandomElement(templates);
        if (template.getType() == type || type == null) { // if the template is the correct type
            return template;
        } else { // if the template is the wrong type, try again
            return getRandomRoomTemplate(type);
        }
    }

    /**
     * Get a random room template, regardless of type
     * @return a random room template
     */

    private RoomTemplate getRandomRoomTemplate() {
        return getRandomRoomTemplate(null);
    }

    /**
     * Get a random element from a list
     * @param list the list to get an element from
     * @param <E> the type of the list
     * @return a random element from the list
     */
    public static <E> E getRandomElement(ArrayList<E> list) {
        return list.get((int) (Math.random() * list.size()));
    }

    /**
     * Add two vectors together without mutating the original vectors
     * Because Vector2.add() mutates the target
     * Why??
     * @param a the first vector
     * @param b the second vector
     * @return the sum of the two vectors
     */
    public static Vector2 nonMutatingVectorAdd(Vector2 a, Vector2 b) {
        return new Vector2(a.x + b.x, a.y + b.y);
    }


}
