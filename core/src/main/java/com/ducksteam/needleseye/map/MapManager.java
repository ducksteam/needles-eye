package com.ducksteam.needleseye.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.ducksteam.needleseye.Config;
import com.ducksteam.needleseye.entity.EnemyRegistry;
import com.ducksteam.needleseye.entity.HallwayPlaceholderRoom;
import com.ducksteam.needleseye.entity.RoomInstance;
import com.ducksteam.needleseye.entity.WallObject;
import com.ducksteam.needleseye.entity.enemies.EnemyEntity;
import com.ducksteam.needleseye.entity.enemies.EnemyTag;

import java.util.*;
import java.util.stream.Collectors;


/**
 * A util class for managing the map generation process
 * @author SkySourced
 */
public class MapManager {

    public static ArrayList<RoomTemplate> roomTemplates; // all room templates
    public final ArrayList<Level> levels; // the levels in this run of the game

    public int levelIndex; // number of levels generated

    public MapGenerationVisualiser visualiser;
    public boolean visualise;

    // different translations for various rotations of hallway models
    private final static Vector3[] hallwayModelTranslations = new Vector3[]{
            new Vector3(-5, 0, 0), // 0 deg
            new Vector3(-10, 0, -5), // 90 deg
            new Vector3(-5, 0, -10), // 180 deg
            new Vector3(0, 0, -5) // 270 deg
    };

    private final static Vector2[] roomSpaceDoorTransformations = new Vector2[]{
            new Vector2(0f, -0.5f),
            new Vector2(0.5f, 0),
            new Vector2(-0.5f, 0),
            new Vector2(0, 0.5f),
            new Vector2(0.5f, 1f),
            new Vector2(-0.5f, 1f),
            new Vector2(0f, 1.5f)
    };

    private final static Vector2[] roomSpaceAdjacentRoomTransformations = new Vector2[]{
            new Vector2(0, -1),
            new Vector2(1, 0),
            new Vector2(-1, 0),
            new Vector2(0, 1),
            new Vector2(1, 1),
            new Vector2(-1, 1),
            new Vector2(0, 2)
    };

    public MapManager() {
        this(false);
    }

    public MapManager(boolean visualise) {
        roomTemplates = new ArrayList<>();
        levels = new ArrayList<>();
        levelIndex = 1;

        roomTemplates.addAll(RoomTemplate.loadRoomTemplates(Gdx.files.internal("data/rooms.json")));

        Gdx.app.debug("MapManager", "Loaded data for " + roomTemplates.size() + " room templates");
        if (visualise) {
            Gdx.app.log("MapManager", "Visualising map generation");
            visualiser = new MapGenerationVisualiser();
            this.visualise = true;
        }
    }

    /**
     * Add enemies to the level
     * @param level the level to be populated
     */
    public void populateLevel(Level level){
        level.getRooms().forEach((RoomInstance room)->{
            for (RoomTemplate.EnemyTagPosition tagPosition : room.getRoom().getEnemyTagPositions()){
                Class<? extends EnemyEntity> enemyClass = getSuitableEnemy(tagPosition.tag());
                if (enemyClass == null) {
                    Gdx.app.error("MapManager", "No suitable enemy found for tag: " + tagPosition.tag());
                    return;
                }
                Vector3 enemyPos = room.getPosition().cpy().add(tagPosition.position().cpy().rotate(Vector3.Y, room.getRot()));
                EnemyEntity enemy = EnemyRegistry.getNewEnemyInstance(enemyClass, enemyPos, new Quaternion(), room);
                assert enemy != null;
                enemy.setAssignedRoom(room);
                room.addEnemy(enemy);
            }
        });
    }

    /**
     * Get suitable enemy fulfilling tag requirements
     * @param tagString the tag string as in the json file
     * @return a suitable enemy class
     */
    public Class<? extends EnemyEntity> getSuitableEnemy(String tagString) {
        return EnemyRegistry.registeredEnemies.values().stream().filter(enemy -> {
            try {
                @SuppressWarnings("unchecked") Set<EnemyTag> tags = (Set<EnemyTag>) enemy.getDeclaredField("tags").get(null);
                ArrayList<String> possibleTagCombos = Arrays.stream(tagString.split(",")).collect(Collectors.toCollection(ArrayList::new));

                for(String possibleTagCombo : possibleTagCombos) { // tagString is split by commas into multiple possible tags/tag combos that are checked against
                    boolean allTagsPresent = true;
                    for(String possibleTag : possibleTagCombo.split("&")) {
                        if (tags.stream().noneMatch(tag -> tag.isChildOf(EnemyTag.fromString(possibleTag)))) allTagsPresent = false;
                    }
                    if (allTagsPresent) return true;
                }
                return false;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.collectingAndThen(
            Collectors.toList(),
            list -> list.get(new Random().nextInt(list.size()))
        ));
    }

    public void generateTestLevel() {
        Level level = new Level(levelIndex); // create an empty level object
        level.addRoom(new RoomInstance(getRoomWithName("pillars"), hallwayModelTranslations[0], new Vector2(0, 0), 0));
        level.addRoom(new RoomInstance(getRoomWithName("pillars"), hallwayModelTranslations[3].cpy().add(10, 0, 0), new Vector2(-1, 0), 90));
        level.addRoom(new RoomInstance(getRoomWithName("pillars"), hallwayModelTranslations[2].cpy().add(0, 0, -10), new Vector2(0, -1), 180));
        level.addRoom(new RoomInstance(getRoomWithName("pillars"), hallwayModelTranslations[1].cpy().add(-10, 0, 0), new Vector2(1, 0), 270));

        addWalls(level);
        populateLevel(level);

        levels.add(level); // add the level to the list
        levelIndex++; // increment the level index
    }

    /**
     * Generate a new level
     */

    public void generateLevel() {
        if (visualise) {
            visualiser.renderingComplete = false;
            visualiser.instructions.clear();
            visualiser.nextInstruction = 0;
        }

        Level level = new Level(levelIndex); // create an empty level object

        int rot = randomRotation();

        RoomInstance room = new RoomInstance(getRandomRoomTemplate(RoomTemplate.RoomType.HALLWAY), hallwayModelTranslations[rot/90], new Vector2(0,0), rot); // build a base hallway

        if (visualise) visualiser.addInstruction("add-room " + RoomTemplate.RoomType.HALLWAY + " " + room.getRoom().getName() + " 0 0 " + room.getRoom().getWidth() + " " + room.getRoom().getHeight() + " " + rot);

        level.addRoom(room);
        int battleRoomCount = 0;

        for (int i = 0; i < levelIndex * 5; i++){ // add a random number of rooms, increasing with each level
            RoomTemplate.RoomType nextType = RoomTemplate.RoomType.getRandomRoomType(); // get a random room type
            if (nextType == RoomTemplate.RoomType.BATTLE) { // limit the number of battle rooms
                if (battleRoomCount > levelIndex) generateRoom(level, nextType);
                else if (visualise) visualiser.addInstruction("msg Skipping battle room");
                battleRoomCount++;
            } else {
                generateRoom(level, nextType);
            }
        }

        // generate treasure rooms
        float treasureRand = (levelIndex + 2f) / 3; // the chance of a treasure room increases with each level
        float treasureGuaranteed = (float) Math.floor(treasureRand); // the number of guaranteed treasure rooms, increases every 3 levels
        if (visualise) visualiser.addInstruction("msg Generating " + treasureRand + " treasure rooms, " + treasureGuaranteed + " guaranteed");

        for (int i = 0; i < treasureGuaranteed; i++){
            generateRoom(level, RoomTemplate.RoomType.TREASURE);
        }

        if(Math.random() < treasureRand - treasureGuaranteed){ // chance of an extra treasure room
            generateRoom(level, RoomTemplate.RoomType.TREASURE);
            if (visualise) visualiser.addInstruction("msg Generating extra treasure room");
        } else if (visualise) visualiser.addInstruction("msg Not generating extra treasure room");

        Gdx.app.debug("MapManager", "Generated level " + levelIndex + " with " + level.getRooms().size() + " rooms");
        Gdx.app.debug("Level "+levelIndex, level.toString());

        // add walls and enemies
        addWalls(level);
        populateLevel(level);

        levels.add(level); // add the level to the list
        levelIndex++; // increment the level index
    }

    public void addWalls(Level level){
        level.walls = new HashMap<>();

        level.getRooms().forEach(roomInstance -> {
            RoomTemplate.RoomType type = roomInstance.getRoom().getType();
            int possibleDoors = type == RoomTemplate.RoomType.HALLWAY ? 7 : 4;
            if (type == RoomTemplate.RoomType.HALLWAY_PLACEHOLDER) return;

            for (int i = 0; i < possibleDoors; i++) {
                if (i == 3 && type == RoomTemplate.RoomType.HALLWAY) continue; // skip door 3 for hallways

                Vector2 doorTransformation = MapManager.roundVector2(roomSpaceDoorTransformations[i].cpy().rotateDeg(roomInstance.getRot()), 2).sub(0.5f, 0.5f);
                Vector2 currentRoomDoorPosition = roomInstance.getRoomSpacePos().cpy().add(doorTransformation);

                if (level.walls.get(currentRoomDoorPosition) != null) continue; // if there's already a wall there, skip it

                boolean adjacentRoomExists, adjacentRoomIsHallwayPlaceholder, currentRoomDoorEnabled, adjacentRoomDoorEnabled, wallHasDoor;
                int currentRoomDoor, adjacentRoomDoor, currentRoomRot, adjacentRoomRot;

                adjacentRoomDoor = -1;
                adjacentRoomDoorEnabled = false;

				currentRoomDoor = i;
                currentRoomDoorEnabled = roomInstance.getRoom().getDoors().get(currentRoomDoor);
                currentRoomRot = roomInstance.getRot();

                // check if the corresponding room exists
                Vector2 adjacentRoomOffset = MapManager.roundVector2(roomSpaceAdjacentRoomTransformations[i].cpy().rotateDeg(currentRoomRot));
                Vector2 adjacentRoomPosition = roomInstance.getRoomSpacePos().cpy().add(adjacentRoomOffset);
                adjacentRoomExists = level.getRooms().stream().anyMatch(room -> room.getRoomSpacePos().equals(adjacentRoomPosition));

                if (adjacentRoomExists) {
                    RoomInstance adjacentRoom = level.getRooms().stream().filter(room -> room.getRoomSpacePos().equals(adjacentRoomPosition)).findFirst().orElse(null);
                    if (adjacentRoom == null) {
						Gdx.app.error("MapManager", "Adjacent room found but not found - this should never happen");
                        return;
                    }
                    adjacentRoomIsHallwayPlaceholder = adjacentRoom.getRoom().getType() == RoomTemplate.RoomType.HALLWAY_PLACEHOLDER;
                    adjacentRoomRot = adjacentRoom.getRot();

                    // try to find correct door
                    if (adjacentRoomIsHallwayPlaceholder) {
                        for (int j = 4; j < 7; j++) {
                            if (adjacentRoomDoor != -1) break; // -1 means no door found yet
                            HallwayPlaceholderRoom placeholderRoom = (HallwayPlaceholderRoom) adjacentRoom;
                            RoomInstance associatedRoom = placeholderRoom.getAssociatedRoom();
                            if (associatedRoom == null) {
                                Gdx.app.error("MapManager", "Associated room not found");
                                return;
                            }
                            Vector2 adjacentRoomDoorPosition = associatedRoom.getCentreRoomSpacePos().add(roomSpaceDoorTransformations[j].cpy().rotateDeg(adjacentRoom.getRot()));
                            if (adjacentRoomDoorPosition.epsilonEquals(currentRoomDoorPosition, 0.25f)) {
                                adjacentRoomDoor = j;
                                adjacentRoomDoorEnabled = associatedRoom.getRoom().getDoors().get(adjacentRoomDoor);
                            }
                        }
                    } else {
                        for (int j = 0; j < 4; j++) {
                            if (adjacentRoomDoor != -1) break;
                            Vector2 adjacentRoomDoorPosition = adjacentRoom.getCentreRoomSpacePos().add(roomSpaceDoorTransformations[j].cpy().rotateDeg(adjacentRoomRot));
                            if (adjacentRoomDoorPosition.epsilonEquals(currentRoomDoorPosition, 0.25f)) {
                                adjacentRoomDoor = j;
                                adjacentRoomDoorEnabled = adjacentRoom.getRoom().getDoors().get(adjacentRoomDoor);
                            }
                        }
                    }
                }

                wallHasDoor = adjacentRoomExists && currentRoomDoorEnabled && adjacentRoomDoorEnabled;
                level.walls.put(currentRoomDoorPosition, new WallObject(getRoomPos(currentRoomDoorPosition), new Quaternion().setEulerAngles(((i % 3 == 0) ? 90 : 0)+roomInstance.getRot(), 0, 0), wallHasDoor));
            }
        });

        /*// parameters for the four different wall types
        Vector2[] translations = new Vector2[]{new Vector2(0, -0.5f), new Vector2(-0.5f, 0), new Vector2(-0.5f, -1), new Vector2(-1, -0.5f)};
        int[] rotations = new int[]{0, 90, 270, 180};
        Vector2[] roomOffsets = {new Vector2(1, 0), new Vector2(0, 1), new Vector2(0, -1), new Vector2(-1, 0)};

        level.getRooms().forEach(roomInstance -> {
            RoomTemplate.RoomType type = roomInstance.getRoom().getType(); // store type to save memories

            int possibleDoors = 4;

			for(int i = 0; i < possibleDoors; i++){
                if (i == 1 && type == RoomTemplate.RoomType.HALLWAY) continue; // skip door 3 for hallways
                if (i == 2 && type == RoomTemplate.RoomType.HALLWAY_PLACEHOLDER) continue; // skip door 0 for hallway placeholders
                if (level.walls.get(roomInstance.getRoomSpacePos().cpy().add(translations[i])) != null) continue; // if there's already a wall there, skip it

                boolean adjacentRoomExists;

                // select the correct translation, rotation, and position for the wall number
                Vector2 translation = translations[i];
                Vector3 position = getRoomPos(roomInstance.getRoomSpacePos()).cpy().add(new Vector3(translation.x,0,translation.y).scl(Config.ROOM_SCALE));

                Quaternion rotation = new Quaternion();
                rotation.set(Vector3.Y, rotations[i]);

                // check if the corresponding room exists
                Vector2 adjacentRoomOffset = roomOffsets[i];
                // if an adjacent room exists
                adjacentRoomExists = level.getRooms().stream().anyMatch(room -> room.getRoomSpacePos().equals(roomInstance.getRoomSpacePos().cpy().add(adjacentRoomOffset)));

                // create wall
                WallObject wall = new WallObject(position, rotation, adjacentRoomExists);
                level.walls.put(roomInstance.getRoomSpacePos().cpy().add(translation), wall);
            }
        });*/
    }

    /**
     * Get the current level
     * @return the last level
     */
    public Level getCurrentLevel(){
        return levels.getLast();
    }

    /**
     * Generate a room & add it to the level
     * @param level the level to generate the room in
     */
    private void generateRoom(Level level, RoomTemplate.RoomType type) {
        RoomTemplate template = getRandomRoomTemplate(type); // find the template with the correct type
        int rot = randomRotation(); // generate a random rotation
        Vector2 pos = generateRoomPos(template, level.getRooms(), rot); // generate a valid position for the room

        RoomInstance room;

        if (template.getType() == RoomTemplate.RoomType.HALLWAY) room = new RoomInstance(template, MapManager.getRoomPos(pos).add(hallwayModelTranslations[rot/90]), pos, rot); // create instance
        else room = new RoomInstance(template, pos, rot);

        if (visualise) visualiser.addInstruction("add-room " + type + " " + room.getRoom().getName() + " " + (int) pos.x + " " + (int) pos.y + " " + room.getRoom().getWidth() + " " + room.getRoom().getHeight() + " " + rot);

        level.addRoom(room); // add to level
    }

    /**
     * Generates a valid position for a room.
     * @param template the room to generate a position for
     * @param rooms the rooms already placed in the level
     * @return a valid position for the room
     */
    private Vector2 generateRoomPos(RoomTemplate template, ArrayList<RoomInstance> rooms, int rot) {
        RoomInstance room = getRandomElement(rooms); // get a random room to attach to
        if (visualise) visualiser.addInstruction("select-room " + (int) room.getRoomSpacePos().x + " " + (int) room.getRoomSpacePos().y);

        if (room.getRoom().getType() == RoomTemplate.RoomType.HALLWAY_PLACEHOLDER) {
            if (visualise) visualiser.addInstruction("msg Skipping hallway placeholder");
            return generateRoomPos(template, rooms, rot); // if the room is a placeholder, try again
        }

        int doorCount = room.getRoom().getType() == RoomTemplate.RoomType.HALLWAY ? 7 : 4; // hallways have 7 doors

        int door = (int) Math.floor(Math.random() * doorCount);
        while ((doorCount == 7 && door == 3) || !room.getRoom().getDoors().get(door)) door = (int) Math.floor(Math.random() * doorCount); // but door 3 isn't used in hallways and doors can be disabled
        if (visualise) visualiser.addInstruction("select-door " + door);

        // Door id reference
        /* this might be horizontally flipped? ask lila if you need to know
         *   6
         *   __
         * 4|3 |5
         *  |__|
         * 1|  |2
         *  |__|
         *   0
         */

        Vector2 offset = roomSpaceAdjacentRoomTransformations[door].cpy();

        Vector2 pos = MapManager.roundVector2(room.getRoomSpacePos().cpy().add(offset.rotateDeg(room.getRot()))); // add offset to room position

        if (visualise) {
            visualiser.addInstruction("try-position " + (int) pos.x + " " + (int) pos.y + " " + template.getWidth() + " " + template.getHeight() + " " + rot);
        }

        for (RoomInstance ri : rooms) { // check if the position is already taken
            for (int h = 0; h < template.getHeight(); h++) { // check all the tiles that the room would occupy
                for (int w = 0; w < template.getWidth(); w++){
                    if (ri.getRoomSpacePos().equals(pos.cpy().add(new Vector2(w, h).rotateDeg(rot)))) { // if the position is already taken
                        if (visualise) visualiser.addInstruction("position-test-fail " + (int) pos.x + " " + (int) pos.y);
                        return generateRoomPos(template, rooms, rot); // try again
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
        ArrayList<RoomTemplate> templates = roomTemplates.stream().filter(t -> t.getType() == type).collect(Collectors.toCollection(ArrayList::new));
        return getRandomElement(templates);
    }

    // Static utility methods follow

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
     * Get a vector3 from an array
     * @param array the array to get the vector from
     * @return the new vector3
     */
    public static Vector3 vector3FromArray(ArrayList<Double> array) {
        return new Vector3(array.get(0).floatValue(), array.get(1).floatValue(), array.get(2).floatValue());
    }

    public static Vector3 vector3FromArray(double[] array) {
        return new Vector3((float) array[0], (float) array[1], (float) array[2]);
    }

    /**
     * Generate random rotation
     * @return a random rotation
     */
    public static int randomRotation() {
        return (int) (Math.random() * 4) * 90;
    }

    /**
     * Rounds a Vector2 to nearest int values
     * @param vec the vector to round
     *   @return the rounded vector
     */
    public static Vector2 roundVector2(Vector2 vec) {
        return new Vector2(Math.round(vec.x), Math.round(vec.y));
    }

    /**
     * Rounds a Vector2 to nearest values divisible by a denominator
     * @param vec the vector to round
     * @param denominator the denominator to round to
     * @return the rounded vector
     */
    public static Vector2 roundVector2(Vector2 vec, int denominator) {
        vec.scl(denominator);
        return new Vector2((float) Math.round(vec.x) /denominator, (float) Math.round(vec.y) /denominator);
    }

    /**
     * Get a room template with a specific name
     * @param name the name of the room
     * @return the room template, or null if not found
     */
    public static RoomTemplate getRoomWithName(String name){
        return roomTemplates.stream().filter(room -> room.getName().equals(name)).findFirst().orElse(null);
    }

    /**
     * Get the room space position of a world space position
     * @param pos the world space position
     * @param round whether to round the position to nearest ints
     * @return the room space position
     */
    public static Vector2 getRoomSpacePos (Vector3 pos, boolean round) {
        if (round) return new Vector2((int) Math.ceil(pos.x / Config.ROOM_SCALE), (int) Math.ceil(pos.z / Config.ROOM_SCALE));
        else return new Vector2(pos.x / Config.ROOM_SCALE, pos.z / Config.ROOM_SCALE);
    }

    /**
     * Get the maximum world space position of a room space position
     * @param roomSpacePos the room space position
     * @return the maximum world space position
     */
    public static Vector3 getRoomPos (Vector2 roomSpacePos) {
        return new Vector3(roomSpacePos.x * Config.ROOM_SCALE, 0, roomSpacePos.y * Config.ROOM_SCALE);
    }
}
