package com.ducksteam.needleseye.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ducksteam.needleseye.Config;
import com.ducksteam.needleseye.Main;
import com.ducksteam.needleseye.entity.*;
import com.ducksteam.needleseye.entity.enemies.EnemyEntity;
import com.ducksteam.needleseye.entity.enemies.EnemyTag;
import com.ducksteam.needleseye.player.Player;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.ducksteam.needleseye.Main.entities;
import static com.ducksteam.needleseye.Main.player;


/**
 * A util class for managing the map generation process
 * @author SkySourced
 */
public class MapManager {

    /** All room templates */
    public static ArrayList<RoomTemplate> roomTemplates;
    /** All deco templates */
    public static ArrayList<DecoTemplate> decoTemplates;
    /** The levels from the current run */
    public final ArrayList<Level> levels;

    /** The number of levels generated/the number to use for the next level to be generated*/
    public int levelIndex;

    /** The visualiser for map generation*/
    public MapGenerationVisualiser visualiser;
    /**Whether to visualise the map generation*/
    public boolean visualise;
    /** The seeded random instance. <b>Only to be used for level generation to preserve seed uniformity</b> */
    protected static Random random;
    /** The seed */
    public static Seed seed;
    /**
     * The translations for hallway models at different rotations
     */
    private final static Vector3[] hallwayModelTranslations = new Vector3[]{
            new Vector3(-5, 0, 0), // 0 deg
            new Vector3(-10, 0, -5), // 90 deg
            new Vector3(-5, 0, -10), // 180 deg
            new Vector3(0, 0, -5) // 270 deg
    };

    /**
     * The transformations for doors in room space. These are from the centre of the room, so they may need to be offset by 0.5f on both axis to convert from the lower left corner typically obtained by flooring the position
     */
    private final static Vector2[] roomSpaceDoorTransformations = new Vector2[]{
            new Vector2(0f, -0.5f),
            new Vector2(-0.5f, 0),
            new Vector2(0.5f, 0),
            new Vector2(0, 0.5f),
            new Vector2(-0.5f, 1f),
            new Vector2(0.5f, 1f),
            new Vector2(0f, 1.5f)
    };

    /**
     * The transformations for adjacent rooms in room space
     */
    private final static Vector2[] roomSpaceAdjacentRoomTransformations = new Vector2[]{
            new Vector2(0, -1),
            new Vector2(-1, 0),
            new Vector2(1, 0),
            new Vector2(0, 1),
            new Vector2(-1, 1),
            new Vector2(1, 1),
            new Vector2(0, 2)
    };

    /**
     * This very large position is used to indicate that a room position is invalid, can be changed if needed
     */
    private final static Vector2 INVALID_ROOM_POS = new Vector2(-10000, -10000);
    /**
     * This speeds up the map generation process by making some rooms check all possible rotations, instead of iterating through and finding a position for one random rotation
     */
    private int rotationOverride = -1;

    /**
     * Create a new map manager
     */
    public MapManager() {
        this(false);
    }

    /**
     * Create a new map manager
     * @param visualise whether to visualise the map generation
     */
    public MapManager(boolean visualise) {
        roomTemplates = new ArrayList<>();
        decoTemplates = new ArrayList<>();
        levels = new ArrayList<>();
        levelIndex = 1;

        Json json = new Json();
        Array<JsonValue> map;

        try {
            map = json.fromJson(null, Gdx.files.internal("data/decos/decos.json"));
            decoTemplates.addAll(DecoTemplate.loadDecoTemplates(map));
        } catch (Exception e) {
            Gdx.app.error("DecoTemplate", "Error loading deco templates", e);
        }
        Gdx.app.debug("MapManager", "Loaded data for " + decoTemplates.size() + " deco templates");

        try {
            map = json.fromJson(null, Gdx.files.internal("data/rooms/rooms.json"));
            roomTemplates.addAll(RoomTemplate.loadRoomTemplates(map));
        } catch (Exception e) {
            Gdx.app.error("RoomTemplate", "Error loading room templates", e);
        }
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
    public void addEnemies(Level level, boolean backgroundLoad){
        level.getRooms().forEach((RoomInstance room)->{
            for (RoomTemplate.EnemyTagPosition tagPosition : room.getRoom().getEnemyTagPositions()){
                Class<? extends EnemyEntity> enemyClass = getSuitableEnemy(tagPosition.tag());
                if (enemyClass == null) {
                    Gdx.app.error("MapManager", "No suitable enemy found for tag: " + tagPosition.tag());
                    return;
                }
                if (!backgroundLoad) {
                    Vector3 enemyPos = room.getPosition().cpy().add(tagPosition.position().cpy().rotate(Vector3.Y, room.getRot()));
                    EnemyEntity enemy = EnemyRegistry.getNewEnemyInstance(enemyClass, enemyPos, new Quaternion(), room);
                    assert enemy != null;
                    enemy.setAssignedRoom(room);
                    room.addEnemy(enemy);
                }
            }
        });
    }

    /** Add decos to a level
     * @param level the level to add decos to
     */
    public void addDecos(Level level, boolean backgroundLoad){
        level.getRooms().forEach(room -> {
            for (RoomTemplate.DecoTagPosition tagPosition : room.getRoom().getDecoTagPositions()){
                if (tagPosition.chance() < random.nextFloat()) continue;
                DecoTemplate template;
                if (tagPosition.tagName()) template = getDecoWithName(tagPosition.tag());
                else template = getSuitableDeco(tagPosition.tag());

                if (template == null) { Gdx.app.error("MapManager", "No suitable deco found for tag: " + tagPosition.tag()); return; }

                if (!backgroundLoad) new DecoInstance(template, room, tagPosition.position(), new Quaternion());
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
            list -> list.get(random.nextInt(list.size()))
        ));
    }

    /** Get suitable deco fulfilling tag requirements
     * @param tagString the tag string as in the json file
     * @return a suitable deco template
     */
    public DecoTemplate getSuitableDeco(String tagString) {
        return decoTemplates.stream().filter(decoTemplate -> {
            ArrayList<String> possibleTagCombos = Arrays.stream(tagString.split(",")).collect(Collectors.toCollection(ArrayList::new));

            for(String possibleTagCombo : possibleTagCombos) {
                boolean allTagsPresent = true;
                for(String possibleTag : possibleTagCombo.split("&")) {
                    if (decoTemplate.tags.stream().noneMatch(tag -> tag.isChildOf(DecoTag.fromString(possibleTag)))) allTagsPresent = false;
                }
                if (allTagsPresent) return true;
            }
            return false;
        }).collect(Collectors.collectingAndThen(
            Collectors.toList(),
            list -> list.get(random.nextInt(list.size()))
        ));
    }

    /**
     * Generate a test level, with known parameters
     */
    public void generateTestLevel() {
        Level level = new Level(levelIndex); // create an empty level object
        level.addRoom(new RoomInstance(getRoomWithName("brokenceiling"), new Vector2(0, 0), 0));

        addWalls(level);
        addEnemies(level, false);
        addDecos(level, false);

        levels.add(level); // add the level to the list
        levelIndex++; // increment the level index
    }

    /**
     * Create a new level with a random layout, the size of which is determined by the level index
     * @param backgroundLoad if this level will not be played.
     *                 This is used in updating the map state to the required state in savegame loading
     */
    public void generateLevel(boolean backgroundLoad) {
        if (random == null) {
            setSeed(new Seed());
        }

        if (!backgroundLoad) Main.currentSave.update();

        if (visualise) {
            visualiser.renderingComplete = false;
            visualiser.instructions.clear();
            visualiser.nextInstruction = 0;
            visualiser.informSeed(seed);
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
                if (battleRoomCount < levelIndex) generateRoom(level, nextType, backgroundLoad);
                else if (visualise) visualiser.addInstruction("msg Skipping battle room");
                battleRoomCount++;
            } else {
                generateRoom(level, nextType, backgroundLoad);
            }
        }

        // generate treasure rooms
        float treasureRand = (levelIndex + 2f) / 3; // the chance of a treasure room increases with each level
        float treasureGuaranteed = (float) Math.floor(treasureRand); // the number of guaranteed treasure rooms, increases every 3 levels
        if (visualise) visualiser.addInstruction("msg Generating " + treasureRand + " treasure rooms, " + (int) treasureGuaranteed + " guaranteed");

        for (int i = 0; i < treasureGuaranteed; i++){
            generateRoom(level, RoomTemplate.RoomType.TREASURE, true);
        }

        if(random.nextFloat() < treasureRand - treasureGuaranteed){ // chance of an extra treasure room
            generateRoom(level, RoomTemplate.RoomType.TREASURE, true);
            if (visualise) visualiser.addInstruction("msg Generating extra treasure room");
        } else if (visualise) visualiser.addInstruction("msg Not generating extra treasure room");

        Gdx.app.debug("MapManager", "Generated level " + levelIndex + " with " + level.getRooms().size() + " rooms");
        Gdx.app.debug("Level "+levelIndex, level.toString());

        // generate extra entities
        if(!backgroundLoad) addWalls(level);
        addEnemies(level, backgroundLoad);
        addDecos(level, backgroundLoad);

        levels.add(level); // add the level to the list
        levelIndex++; // increment the level index
    }

    /**
     * Synchronise the RNG to the state from a playthrough.
     * This is achieved by generating the previous levels.
     * @param playthrough playthrough to sync to
     */
    public void updateToPlaythroughState(Playthrough playthrough) {
        setSeed(playthrough.getSeed());

        if (playthrough.getCurrentLevelId() == 1) {
            entities.clear();
            player = new Player(Config.PLAYER_START_POSITION, playthrough.playerData);
        }

        while (levelIndex <= playthrough.getCurrentLevelId()) {
            generateLevel(levelIndex + 1 <= playthrough.getCurrentLevelId()); // backgroundLoad should only be false on the last level to load
            if (levelIndex == playthrough.getCurrentLevelId()) { // before final level is generated
                Main.entities.clear();
                player = new Player(Config.PLAYER_START_POSITION, playthrough.playerData);
            }
        }
    }

    /**
     * Add walls to a level, based on template door enabling and physical proximity
     * @param level the level to add walls to
     */
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
    }

    /**
     * Return the door on the first room that connects to the specified door on the second room.
     * Does not check if the door is enabled on the target room
     * @param template the room template of the first room
     * @param rot the rotation of the first room
     * @param roomSpacePos the room space position of the first room
     * @param targetRoomSpacePos the room space position of the second room
     * @param targetDoor the door on the second room
     * @param targetRot the rotation of the second room
     * @return the door that connects the two rooms, or -1 if no connection / the door is disabled on the first room
     */
    public static int getConnectingDoor(RoomTemplate template, int rot, Vector2 roomSpacePos, Vector2 targetRoomSpacePos, int targetDoor, int targetRot) {
        Vector2 targetDoorPos = getDoorRoomSpacePos(targetRoomSpacePos, targetDoor, targetRot); // get the position of the target door
        for (Map.Entry<Integer, Boolean> entry : template.getDoors().entrySet()) { // check all the doors on the room
            if (entry.getValue() && targetDoorPos.epsilonEquals(getDoorRoomSpacePos(roomSpacePos, entry.getKey(), rot), 0.25f)) {
                return entry.getKey();
            } // if the door is enabled and in the correct position, return the door
        }
        return -1; // no connection
    }

    /**
     * Get the room space position of a door
     * @param roomSpacePos the room space position in the lower left corner of the room
     * @param door the door id
     * @param rot the rotation of the room
     * @return the room space position of the door
     */
    public static Vector2 getDoorRoomSpacePos(Vector2 roomSpacePos, int door, int rot) {
        return MapManager.roundVector2(roomSpacePos.cpy().add(roomSpaceDoorTransformations[door].cpy().rotateDeg(rot)).add(0.5f, 0.5f), 2);
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
     * @param type the type of room to generate
     * @param forceRotation whether to force a position on rotation (passed through to generateRoomPos)
     */
    private void generateRoom(Level level, RoomTemplate.RoomType type, boolean forceRotation) {
        RoomTemplate template = getRandomRoomTemplate(type); // find the template with the correct type
        int rot = randomRotation(); // generate a random rotation
        Vector2 pos = generateRoomPos(template, level.getRooms(), rot, forceRotation); // generate a valid position for the room
        while (pos.equals(INVALID_ROOM_POS)){
            rot = randomRotation();
            pos = generateRoomPos(template, level.getRooms(), rot, forceRotation);
        }
        if (rotationOverride != -1) {
            rot = rotationOverride;
            rotationOverride = -1;
            Gdx.app.log("MapManager", "Overriding rotation to " + rot);
        }
        RoomInstance room;

        if (template.getType() == RoomTemplate.RoomType.HALLWAY) room = new RoomInstance(template, MapManager.getRoomPos(pos).add(hallwayModelTranslations[rot/90]), pos, rot); // create instance
        else room = new RoomInstance(template, pos, rot);

        if (visualise) visualiser.addInstruction("add-room " + type + " " + room.getRoom().getName() + " " + (int) pos.x + " " + (int) pos.y + " " + room.getRoom().getWidth() + " " + room.getRoom().getHeight() + " " + rot);

        level.addRoom(room); // add to level
    }

    /**
     * Generate a room in a level
     * @param level the level to generate the room in
     * @param type the type of room to generate
     */
    public void generateRoom(Level level, RoomTemplate.RoomType type) {
        generateRoom(level, type, false);
    }

    /**
     * Generates a valid position for a room.
     * @param template the room to generate a position for
     * @param rooms the rooms already placed in the level
     * @param rot the rotation of the room
     * @param forceRotation whether to force a position on rotation
     * @return a valid position for the room
     */
    private Vector2 generateRoomPos(RoomTemplate template, ArrayList<RoomInstance> rooms, int rot, boolean forceRotation) {
        RoomInstance room = getRandomElement(rooms, (RoomInstance i) -> i.getRoom().getType() != RoomTemplate.RoomType.HALLWAY_PLACEHOLDER); // get a random room to attach to
        if (visualise) visualiser.addInstruction("select-room " + (int) room.getRoomSpacePos().x + " " + (int) room.getRoomSpacePos().y);

        // this should never trigger thanks to the predicate above but i will leave it as i am unsure and unconfident
        if (room.getRoom().getType() == RoomTemplate.RoomType.HALLWAY_PLACEHOLDER) {
            if (visualise) visualiser.addInstruction("msg Skipping hallway placeholder");
            return INVALID_ROOM_POS.cpy(); // if the room is a placeholder, try again
        }

        int doorCount = room.getRoom().getType() == RoomTemplate.RoomType.HALLWAY ? 7 : 4; // hallways have 7 doors

        int door = random.nextInt(doorCount);
        while ((doorCount == 7 && door == 3) || !room.getRoom().getDoors().get(door)) door = random.nextInt(doorCount); // but door 3 isn't used in hallways and doors can be disabled
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

        // add offset to room position
        Vector2 pos = MapManager.roundVector2(room.getRoomSpacePos().cpy().add(offset.rotateDeg(room.getRot())));

        if (visualise) {
            visualiser.addInstruction("try-position " + template.getName() + " " + (int) pos.x + " " + (int) pos.y + " " + template.getWidth() + " " + template.getHeight() + " " + rot);
        }

        // check if the position is already taken
        for (RoomInstance ri : rooms) {
            for (int h = 0; h < template.getHeight(); h++) { // check all the tiles that the room would occupy
                for (int w = 0; w < template.getWidth(); w++){
                    if (ri.getRoomSpacePos().equals(pos.cpy().add(new Vector2(w, h).rotateDeg(rot)))) { // if the position is already taken
                        if (visualise) visualiser.addInstruction("msg Position (" + pos.x + ", " + pos.y + ") occupied");
                        return INVALID_ROOM_POS.cpy(); // try again
                    }
                }
            }
        }

        // check if the doors connect
        if (getConnectingDoor(template, rot, pos, room.getRoomSpacePos(), door, room.getRot()) == -1) {
            if (forceRotation){
                if (visualise) visualiser.addInstruction("msg Forcing rotation");
                for (int i = 0; i < 4; i++){
                    if (getConnectingDoor(template, i * 90, pos, room.getRoomSpacePos(), door, room.getRot()) != -1) {
                        rotationOverride = i * 90;
                        break;
                    }
                }
                if (rotationOverride == -1) {
                    if (visualise) visualiser.addInstruction("msg Doors do not connect");
                    return INVALID_ROOM_POS.cpy();
                }
            } else {
                if (visualise) visualiser.addInstruction("msg Doors do not connect");
                return INVALID_ROOM_POS.cpy();
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

    /**
     * Sets the seed.
     * @param s the seed object to set
     */
    public static void setSeed(Seed s) {
        if (s == null) return;
        seed = s;
        random = new Random(seed.getSeed());
        Main.currentSave = new Playthrough(s, "name");
    }

    public void resetSeed() {
        random = null;
        seed = null;
        Gdx.app.log("Seeding", "Reset seed");
    }

    // Static utility methods follow

    /**
     * Get a random element from a list
     * @param list the list to get an element from
     * @param <E> the type of the list
     * @return a random element from the list
     */
    protected static <E> E getRandomElement(ArrayList<E> list) {
        return list.get(random.nextInt(list.size()));
    }

    /**
     * Get a random element from a list that matches a filter
     * @param list the list to get an element from
     * @param filter the filter to apply
     * @return a random element from the list
     * @param <E> the type of the list
     */
    protected static <E> E getRandomElement(ArrayList<E> list, Predicate<E> filter) {
        ArrayList<E> filtered = list.stream().filter(filter).collect(Collectors.toCollection(ArrayList::new));
        return getRandomElement(filtered);
    }

    /**
     * Get a vector3 from an array
     * @param array the array to get the vector from
     * @return the new vector3
     */
    public static Vector3 vector3FromArray(double[] array) {
        return new Vector3((float) array[0], (float) array[1], (float) array[2]);
    }

    /**
     * Generate random rotation
     * @return a random rotation
     */
    public static int randomRotation() {
        return random.nextInt(0, 3) * 90;
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

    /** Get a deco template with a specific name
     * @param name the name of the deco
     * @return the deco template or null if not found
     */
    public static DecoTemplate getDecoWithName(String name){
        return decoTemplates.stream().filter(deco -> deco.getName().equals(name)).findFirst().orElse(null);
    }

    /**
     * Get the room space position of a world space position
     * @param pos the world space position
     * @param round whether to round the position to nearest int
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
