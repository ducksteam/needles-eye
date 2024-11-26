package com.ducksteam.needleseye.map;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.ducksteam.needleseye.Main;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents a template for a room in the world
 * @author SkySourced
 */
public class RoomTemplate {

    /**
     * Represents the position of a placed enemy in the room
     * @param tag the string combination of tags that can possibly be placed there
     * @param position the position of the enemy in the room
     */
    public record EnemyTagPosition(String tag, Vector3 position) {}

    @Deprecated
    private Model model; // the model of the room

    /**
     * Represents the type a room is.
     * This is used in map generation to specify the contents of a level
     */
    public enum RoomType {
        /**
         * Represents a small room. These typically have some enemies.
         * These have an 80% chance of generation.
         */
        SMALL(2, 0.8f), // 80% chance of being a small room
        /**
         * Represents a hallway. These do not typically have enemies.
         * These have a 5% chance of generation.
         */
        HALLWAY(0, 0.85f), // 5% chance of being a hallway
        /**
         * Represents a placeholder for the second tile of a hallway. This is purely for generation and should not be used in-game.
         */
        HALLWAY_PLACEHOLDER(0, 0),
        /**
         * Represents a battle room. These generally have more enemies.
         * These have a 15% chance of generation.
         */
        BATTLE(3, 1f),
        /**
         * Represents a boss room. Currently unused, and as such has a 0 chance of generation.
         */
        BOSS(0, 0),
        /**
         * Represents a room with an upgrade in it. This has a 0 chance of generation as it is generated specially.
         */
        TREASURE(0, 0);

        /**
         * The difficulty of the room, or how many enemies it should spawn.
         * This was deprecated in favour of {@link com.ducksteam.needleseye.entity.enemies.EnemyTag}s.
         */
        @Deprecated final int difficulty;
        /**
         * The chance of this room type being generated, described as a normalised weight table between 0 and 1.
         */
        final float normalChance; // the chance of this room type being generated

        RoomType(int difficulty, float normalChance) {
            this.difficulty = difficulty;
            this.normalChance = normalChance;
        }

        /**
         * Get a room type from a string
         * @param s the string to convert
         * @return the room type
         */
        public static RoomType fromString(String s) {
            return switch (s.toUpperCase()) {
                case "SMALL" -> SMALL;
                case "HALLWAY" -> HALLWAY;
                case "BATTLE" -> BATTLE;
                case "BOSS" -> BOSS;
                case "TREASURE" -> TREASURE;
                default -> null;
            };
        }

        /**
         * Get a random room type
         * @return the room type
         */
        public static RoomType getRandomRoomType() {
            double rand = Math.random();
            for (RoomType type : RoomType.values()) {
                if (rand < type.normalChance) {
                    return type;
                }
            }
            return null;
        }
    }

    private RoomType type; // the type of room
    private int width; // the width of the room
    private int height; // the height of the room
    private boolean spawn; // whether enemies can spawn in this room
    private String modelPath; // the path to the model of the room
    private String name; // the name of the room (only used internally)
    private HashMap<Integer, Boolean> doors; // the doors of the room and whether they are compatible with the model
    private Vector3 centreOffset; // the offset of the centre of the room
    private ArrayList<EnemyTagPosition> enemyTagPositions; // the positions of enemy tags in the room

    /**
     * Create a pre-populated room template
     * @param roomType the type of room
     * @param width the width of the room in room space
     * @param height the height of the room in room space
     * @param spawn whether enemies can spawn in the room
     * @param modelPath the path to the model of the room
     * @param centreOffset the offset of the centre of the room
     * @param enemyTagPositions the positions of enemy tags in the room
     */
    public RoomTemplate(RoomType roomType, int width, int height, boolean spawn, String modelPath, Vector3 centreOffset, ArrayList<EnemyTagPosition> enemyTagPositions) {
        this.type = roomType;
        this.width = width;
        this.height = height;
        this.spawn = spawn;
        this.modelPath = modelPath;
        this.centreOffset = centreOffset;
        this.enemyTagPositions = enemyTagPositions;
    }

    /**
     * Create an empty room template to be populated with setters
     */
    public RoomTemplate() {}

    /** Load room templates from JSON
     * @param map the JSON array to read from
     * @return the room template
     */
    public static ArrayList<RoomTemplate> loadRoomTemplates(Array<JsonValue> map) {
        ArrayList<RoomTemplate> rtArray = new ArrayList<>();

        for (JsonValue room : map) {
            RoomTemplate rt = new RoomTemplate();

            // Create empty room template & read values from map
            rt.setType(RoomType.fromString(room.get("type").asString()));
            rt.setWidth(room.get("width").asInt());
            rt.setHeight(room.get("height").asInt());
            rt.setModelPath(room.get("modelPath").asString());
            rt.setName(room.get("name").asString());

            try {
                rt.setCentreOffset(MapManager.vector3FromArray(room.get("centreOffset").asDoubleArray()));
            } catch (Exception e) {
                rt.setCentreOffset(new Vector3(0, 0, 0));
            }

            // Read doors
            JsonValue doors = room.get("doors");
            rt.setDoors(new HashMap<>() {
                {
                    for (JsonValue door : doors) {
                        put(Integer.parseInt(door.name), door.asBoolean());
                    }
                }
            });

            // Read enemy tag positions
            JsonValue enemyTagPositions = room.get("enemies");
            rt.setEnemyTagPositions(new ArrayList<>() {
                {
                    for (JsonValue tagPosition : enemyTagPositions) {
                        add(new EnemyTagPosition(tagPosition.getString("tag"), MapManager.vector3FromArray(tagPosition.get("position").asDoubleArray())));
                    }
                }
            });

            rtArray.add(rt);
        }

        return rtArray;
    }

    /**
     * Get the model of the room
     * @return the model
     */
    @Deprecated
    public Model getModel() {
        return this.model;
    }

    /**
     * Set the model of the room
     * @param model the model
     */
    @Deprecated
    public void setModel(Model model) {
        this.model = model;
    }

    /**
     * Get the scene of the room
     * @return a new copy of the scene
     */
    public Scene getScene() {
        if (getModelPath() != null) return new Scene(((SceneAsset) Main.assMan.get(getModelPath())).scene);
        else return null; // should only be null for HallwayPlaceholderRoom
    }

    /**
     * Get the type of the room
     * @return the type
     */
    public RoomType getType() {
        return type;
    }

    /**
     * Set the type of the room
     * @param type the type
     */
    public void setType(RoomType type) {
        this.type = type;
    }

    /**
     * Get the width of the room
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Set the width of the room
     * @param width the width
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Get the height of the room
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Set the height of the room
     * @param height the height
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Get the path to the model of the room
     * @return the path
     */
    public String getModelPath() {
        return modelPath;
    }

    /**
     * Set the path to the model of the room
     * @param modelPath the path
     */
    public void setModelPath(String modelPath) {
        this.modelPath = modelPath;
    }

    /**
     * Get the name of the room
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the room
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the state of the doors enabled in the room
     * @return the doors map
     */
    public HashMap<Integer, Boolean> getDoors() {
        return doors;
    }

    /**
     * Set the state of the doors enabled in the room
     * @param doors the doors map
     */
    public void setDoors(HashMap<Integer, Boolean> doors) {
        this.doors = doors;
    }

    /**
     * Get the offset of the centre of the room
     * @return the offset
     */
    public Vector3 getCentreOffset() {
        return centreOffset;
    }

    /**
     * Set the offset of the centre of the room
     * @param centreOffset the offset
     */
    public void setCentreOffset(Vector3 centreOffset) {
        this.centreOffset = centreOffset;
    }

    /**
     * Get the enemy tags and positions in the room
     * @return the enemy tags and positions
     */
    public ArrayList<EnemyTagPosition> getEnemyTagPositions() {
        return enemyTagPositions;
    }

    /**
     * Set the enemy tags and positions in the room
     * @param enemyTagPositions the enemy tags and positions
     */
    public void setEnemyTagPositions(ArrayList<EnemyTagPosition> enemyTagPositions) {
        this.enemyTagPositions = enemyTagPositions;
    }

    /**
     * Get the string representation of the room template for debugging
     * @return the string
     */
    @Override
    public String toString() {
        return "RoomTemplate{" +
                "type=" + type +
                ", width=" + width +
                ", height=" + height +
                ", spawn=" + spawn +
                ", modelPath='" + modelPath + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
