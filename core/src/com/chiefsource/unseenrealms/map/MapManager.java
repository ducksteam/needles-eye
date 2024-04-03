package com.chiefsource.unseenrealms.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class MapManager {
    public static ArrayList<RoomTemplate> roomTemplates;
    public static ArrayList<DecoTemplate> decoTemplates;
    private final ArrayList<Level> levels;
    private int levelIndex; // number of levels generated

    // placeholder room for hallways
    public static final RoomTemplate HALLWAY_PLACEHOLDER = new RoomTemplate(RoomTemplate.RoomType.HALLWAY_PLACEHOLDER, 0, 0, false, null, null);

    // paths (these could be wrong, maybe change to just data/rooms/?)
    public final String ROOM_TEMPLATE_PATH = "assets/data/rooms/";
    public final String DECO_TEMPLATE_PATH = "assets/data/decos/";

    public MapManager() {
        roomTemplates = new ArrayList<>();
        decoTemplates = new ArrayList<>();
        levels = new ArrayList<>();
        levelIndex = 1;

        // load deco templates
        File decoDir = new File(DECO_TEMPLATE_PATH);
        if (!decoDir.exists()) { // check if the deco template directory exists
            Gdx.app.error("MapManager", "Deco template directory not found: " + DECO_TEMPLATE_PATH);
            return;
        }
        for (File file : Objects.requireNonNull(decoDir.listFiles())) { // load all deco templates
            if (file.getName().endsWith(".json")) { // only load json files
                decoTemplates.add(DecoTemplate.loadDecoTemplate(file)); // load the deco template
            }
            Gdx.app.debug("MapManager", "Loaded " + file.getName() + ": \n" + decoTemplates.getLast().toString());
        }
        Gdx.app.debug("MapManager", "Loaded " + decoTemplates.size() + " deco templates");

        // load room templates
        File roomDir = new File(ROOM_TEMPLATE_PATH);
        if (!roomDir.exists()) { // check if the room template directory exists
            Gdx.app.error("MapManager", "Room template directory not found: " + ROOM_TEMPLATE_PATH);
            return;
        }
        for (File file : Objects.requireNonNull(roomDir.listFiles())) { // load all room templates
            if (file.getName().endsWith(".json")) { // only load json files
                roomTemplates.add(RoomTemplate.loadRoomTemplate(file)); // load the room template
            }
            Gdx.app.debug("MapManager", "Loaded " + file.getName() + ": \n" + roomTemplates.getLast().toString());
        }
        Gdx.app.debug("MapManager", "Loaded " + roomTemplates.size() + " room templates");

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
            generateRoom(level, nextType); // generate a room of that type
        }

        // generate treasure rooms
        float treasureRand = (float) Math.random() * ((float) levelIndex + 2f / 3); // the chance of a treasure room increases with each level
        float treasureGuaranteed = (float) Math.floor(treasureRand); // the number of guaranteed treasure rooms, increases every 3 levels
        for (int i = 0; i < treasureGuaranteed; i++){
            generateRoom(level, RoomTemplate.RoomType.TREASURE);
        }
        if(Math.random() < treasureRand - treasureGuaranteed){ // chance of an extra treasure room
            generateRoom(level, RoomTemplate.RoomType.TREASURE);
        }

        generateRoom(level, RoomTemplate.RoomType.BOSS); // generate a boss room

        Gdx.app.debug("MapManager", "Generated level " + levelIndex + " with " + level.getRooms().size() + " rooms");
        Gdx.app.debug("Level "+levelIndex, level.toString());

        levels.add(level); // add the level to the list
        levelIndex++; // increment the level index
    }

    /**
     * Generate a room & add it to the level
     * @param level the level to generate the room in
     */
    private void generateRoom(Level level, RoomTemplate.RoomType type) {
        RoomTemplate template = getRandomRoomTemplate(type);
        Vector2 pos = generateRoomPos(template, level.getRooms());
        int rot = (int) Math.floor(Math.random() * 4) * 90;
        RoomInstance room = new RoomInstance(template, pos, rot);
        level.addRoom(room);
    }

    /**
     * Generates a valid position for a room.
     * @param template the room to generate a position for
     * @param rooms the rooms already placed in the level
     * @return a valid position for the room
     */
    private Vector2 generateRoomPos(RoomTemplate template, ArrayList<RoomInstance> rooms) {
        RoomInstance room = getRandomElement(rooms); // get a random room to attach to

        int doorCount = room.getRoom().getType() == RoomTemplate.RoomType.HALLWAY ? 7 : 4; // hallways have 7 doors

        int door = (int) Math.floor(Math.random() * doorCount);
        while ((doorCount == 7 && door == 3) || !room.getRoom().getDoors().get(door)) door = (int) Math.floor(Math.random() * doorCount); // but door 3 isn't used in hallways and doors can be disabled

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

        Vector2 rot = switch (room.getRot()) { // room rotation
            case 0 -> new Vector2(1, 1);
            case 90 -> new Vector2(1, -1);
            case 180 -> new Vector2(-1, -1);
            case 270 -> new Vector2(-1, 1);
            default -> throw new IllegalStateException("Unexpected value: " + room.getRot());
        };

        Vector2 pos = nonMutatingVectorAdd(room.getPos(), offset.scl(rot)); // add offset to room position
        //Gdx.app.debug("Door" + door, "Offset: " + offset + " Pos: " + pos);

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
        RoomTemplate template = getRandomElement(roomTemplates);
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

    public static Vector3 vector3FromArray(ArrayList<Double> array) {
        return new Vector3(array.get(0).floatValue(), array.get(1).floatValue(), array.get(2).floatValue());
    }

}
