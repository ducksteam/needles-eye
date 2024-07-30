package com.ducksteam.needleseye.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;
import com.ducksteam.needleseye.entity.DecoInstance;
import com.ducksteam.needleseye.entity.collision.ColliderBox;
import com.ducksteam.needleseye.entity.collision.ColliderGroup;
import com.ducksteam.needleseye.entity.collision.ColliderRay;
import com.ducksteam.needleseye.entity.collision.ColliderSphere;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

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

    private Model model;

    public enum RoomType {
        SMALL(2, 0.9f), // 90% chance of being a small room
        HALLWAY(1, 0.95f), // 5% chance of being a hallway
        HALLWAY_PLACEHOLDER(0, 0), // represents the second tile of a hallway, purely for generation
        BATTLE(3, 1f), // 5% chance of being a battle room
        BOSS(0, 0), // boss rooms & treasure rooms are generated specially
        TREASURE(0, 0);

        final int difficulty;
        final float normalChance;

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

        public int getDifficulty() {
            return difficulty;
        }
    }

    private RoomType type;
    private int width;
    private int height;
    private boolean spawn;
    private ColliderGroup collider;
    private String modelPath;
    private String texturePath;
    private String name;
    private ArrayList<DecoInstance> decos;
    private HashMap<Integer, Boolean> doors;
    private Vector3 centreOffset;

    public RoomTemplate(RoomType roomType, int width, int height, boolean spawn, String modelPath, String texturePath, Vector3 centreOffset) {
        this.type = roomType;
        this.width = width;
        this.height = height;
        this.spawn = spawn;
        this.modelPath = modelPath;
        this.texturePath = texturePath;
        this.centreOffset = centreOffset;
    }



    public RoomTemplate() {}

    /** Load a room template from a file
     * @param file the file to load from
     * @return the room template
     */
    public static RoomTemplate loadRoomTemplate(File file) {
        Gson gson = new Gson();
        Map<?, ?> map;
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
        rt.setSpawn((boolean) map.get("spawn"));
        rt.setModelPath((String) map.get("modelPath"));
        rt.setTexturePath((String) map.get("texturePath"));
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

        /*// Read decos
        @SuppressWarnings("unchecked") ArrayList<LinkedTreeMap<String, Object>> decos = (ArrayList<LinkedTreeMap<String, Object>>) map.get("decos");
        rt.setDecos(new ArrayList<>());

        for(LinkedTreeMap<String, Object> entry : decos) { // Read decos from GSON map
            DecoTemplate template = null;
            Vector3 position = null;
            for(Map.Entry<String, Object> e : entry.entrySet()) { // For each deco in the JSON array
                switch (e.getKey()) { // Read values from GSON map
                    case "name" -> template = MapManager.decoTemplates.stream().filter(d -> d.getName().equals(e.getValue())).findFirst().orElse(null);
                    case "position" -> //noinspection unchecked
							position = MapManager.vector3FromArray((ArrayList<Double>) e.getValue());

                }
                if (template == null || position == null) {
                    Gdx.app.error(rt.getName(),"Deco template not found: " + e.getValue());
                } else {
                    DecoInstance deco = new DecoInstance(template, position);
                    rt.getDecos().add(deco);
                }
            }
        }*/

        // Read mesh
        rt.collider = new ColliderGroup();
        @SuppressWarnings("unchecked") ArrayList<LinkedTreeMap<String, Object>> mesh = (ArrayList<LinkedTreeMap<String, Object>>) map.get("collision");
//        if (mesh.isEmpty()) Gdx.app.error(rt.getName(),"no collision data found in room template");
        for (LinkedTreeMap<String, Object> o : mesh) {
            switch ((String) o.get("type")) {
                case "box" -> {
                    @SuppressWarnings("unchecked") ArrayList<Double> pos1 = (ArrayList<Double>) o.get("position1");
                    @SuppressWarnings("unchecked") ArrayList<Double> pos2 = (ArrayList<Double>) o.get("position2");
                    rt.collider.addCollider(new ColliderBox(
                            MapManager.vector3FromArray(pos1),
                            MapManager.vector3FromArray(pos2)
                    ));
                }
                case "sphere" -> {
                    @SuppressWarnings("unchecked") ArrayList<Double> pos = (ArrayList<Double>) o.get("position1");
                    Double radius = (Double) o.get("radius");
                    rt.collider.addCollider(new ColliderSphere(
                            MapManager.vector3FromArray(pos),
                            radius.floatValue()
                    ));
                }
                case "ray" -> {
                    @SuppressWarnings("unchecked") ArrayList<Double> pos = (ArrayList<Double>) o.get("position1");
                    Double polar = (Double) o.get("polar");
                    Double azimuth = (Double) o.get("azimuth");
                    rt.collider.addCollider(new ColliderRay(
                            MapManager.vector3FromArray(pos),
                            polar.floatValue(),
                            azimuth.floatValue()
                    ));
                }
            }
        }

        return rt;
    }

    public Model getModel() {
        return this.model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public RoomType getType() {
        return type;
    }

    public void setType(RoomType type) {
        this.type = type;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean canSpawn() {
        return spawn;
    }

    public void setSpawn(boolean spawn) {
        this.spawn = spawn;
    }

    public String getModelPath() {
        return modelPath;
    }

    public void setModelPath(String modelPath) {
        this.modelPath = modelPath;
    }

    public String getTexturePath() {
        return texturePath;
    }

    public void setTexturePath(String texturePath) {
        this.texturePath = texturePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<DecoInstance> getDecos() {
        return decos;
    }

    public void setDecos(ArrayList<DecoInstance> decos) {
        this.decos = decos;
    }

    public HashMap<Integer, Boolean> getDoors() {
        return doors;
    }

    public void setDoors(HashMap<Integer, Boolean> doors) {
        this.doors = doors;
    }

    public Vector3 getCentreOffset() {
        return centreOffset;
    }

    public void setCentreOffset(Vector3 centreOffset) {
        this.centreOffset = centreOffset;
    }

    public ColliderGroup getCollider() {
        return collider;
    }

    public void setCollider(ColliderGroup collider) {
        this.collider = collider;
    }

    @Override
    public String toString() {
        return "RoomTemplate{" +
                "type=" + type +
                ", width=" + width +
                ", height=" + height +
                ", spawn=" + spawn +
                ", modelPath='" + modelPath + '\'' +
                ", texturePath='" + texturePath + '\'' +
                ", name='" + name + '\'' +
                ", decos=" + decos +
                '}';
    }
}