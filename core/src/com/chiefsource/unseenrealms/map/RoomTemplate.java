package com.chiefsource.unseenrealms.map;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Map;

public class RoomTemplate {

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

    public enum RoomType {
        SMALL (2),
        HALLWAY (1),
        BATTLE (3),
        BOSS (0),
        TREASURE (0);

        final int difficulty;

        public int getDifficulty() {
            return difficulty;
        }

        RoomType(int difficulty) {
            this.difficulty = difficulty;
        }

        public static RoomType fromString(String s) {
            switch(s.toUpperCase()) {
                case "SMALL":
                    return SMALL;
                case "HALLWAY":
                    return HALLWAY;
                case "BATTLE":
                    return BATTLE;
                case "BOSS":
                    return BOSS;
                case "TREASURE":
                    return TREASURE;
                default:
                    return null;
            }
        }
    }

    private RoomType type;
    private int width;
    private int height;
    private boolean spawn;
    private String modelPath;
    private String texturePath;
    private String name;
    private ArrayList<DecoInstance> decos;

    public static RoomTemplate loadRoomTemplate(File file) {
        Gson gson = new Gson();

        Map<?, ?> map;

        try {
             map = gson.fromJson(new FileReader(file), Map.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        RoomTemplate rt = new RoomTemplate();
        rt.setType(RoomType.fromString((String) map.get("type")));
        rt.setWidth(((Double) map.get("width")).intValue());
        rt.setHeight(((Double) map.get("height")).intValue());
        rt.setSpawn((boolean) map.get("spawn"));
        rt.setModelPath((String) map.get("modelPath"));
        rt.setTexturePath((String) map.get("texturePath"));
        rt.setName((String) map.get("name"));
        // Decos will be weird i have a feeling
        return rt;
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

    public boolean isSpawn() {
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
}
