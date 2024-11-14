package com.ducksteam.needleseye.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;
import com.ducksteam.needleseye.Main;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a template for a room in the world
 * @author SkySourced
 */
public class RoomTemplate {

    public record EnemyTagPosition(String tag, Vector3 position) {}

    @Deprecated
    private Model model; // the model of the room

    // Represents the type of room
    public enum RoomType {
        SMALL(2, 0.8f), // 80% chance of being a small room
        HALLWAY(0, 0.85f), // 5% chance of being a hallway
        HALLWAY_PLACEHOLDER(0, 0), // represents the second tile of a hallway, purely for generation
        BATTLE(3, 1f), // 15% chance of being a battle room
        BOSS(0, 0), // boss rooms & treasure rooms are generated specially
        TREASURE(0, 0);

        final int difficulty; // the difficulty of the room, or how many enemies it should spawn
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

    public RoomTemplate(RoomType roomType, int width, int height, boolean spawn, String modelPath, Vector3 centreOffset, ArrayList<EnemyTagPosition> enemyTagPositions) {
        this.type = roomType;
        this.width = width;
        this.height = height;
        this.spawn = spawn;
        this.modelPath = modelPath;
        this.centreOffset = centreOffset;
        this.enemyTagPositions = enemyTagPositions;
    }

    public RoomTemplate() {}

    /** Load a room template from a file
     * @param file the file to load from
     * @return the room template
     */
    public static RoomTemplate loadRoomTemplate(File file) {
        Gson gson = new Gson(); // start json reader
        Map<?, ?> map; // create map to store values
        try {
            map = gson.fromJson(new FileReader(file), Map.class); // read the file to a map
        } catch (Exception e) { // file not found
            Gdx.app.error("RoomTemplate", "Error loading room template: " + file.getName(), e);
            return null;
        }
        RoomTemplate rt = new RoomTemplate(); // Create empty room template & read values from map
        rt.setType(RoomType.fromString((String) map.get("type")));
        rt.setWidth(((Double) map.get("width")).intValue());
        rt.setHeight(((Double) map.get("height")).intValue());
        rt.setModelPath((String) map.get("modelPath"));
        rt.setName((String) map.get("name"));

        try {
            //noinspection unchecked
            rt.setCentreOffset(MapManager.vector3FromArray((ArrayList<Double>) map.get("centre_offset")));
        } catch (Exception e) {
            rt.setCentreOffset(new Vector3(0, 0, 0));
        }

        // Read doors
        @SuppressWarnings("unchecked") LinkedTreeMap<String, Object> doors = (LinkedTreeMap<String, Object>) map.get("doors");
        rt.setDoors(new HashMap<>() {}); // Create empty doors map
        for (Map.Entry<String, Object> entry : doors.entrySet()) { // Read doors from GSON map
            rt.getDoors().put(Integer.parseInt(entry.getKey()), (boolean) entry.getValue()); // Add door to map
        }

        @SuppressWarnings("unchecked") ArrayList<LinkedTreeMap<String, Object>> enemyTagPositions = (ArrayList<LinkedTreeMap<String, Object>>) map.get("enemies");
        rt.setEnemyTagPositions(new ArrayList<>()); // Create empty enemy tag positions list
        for (LinkedTreeMap<String, Object> tagPosition : enemyTagPositions) {
            rt.getEnemyTagPositions().add(new EnemyTagPosition((String) tagPosition.get("tag"), MapManager.vector3FromArray((ArrayList<Double>) tagPosition.get("position"))));
        }

        return rt;
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
