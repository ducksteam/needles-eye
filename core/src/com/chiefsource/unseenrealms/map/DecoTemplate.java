package com.chiefsource.unseenrealms.map;

import com.google.gson.Gson;

public class DecoTemplate {
    private String name;
    private String modelPath;
    private String texturePath;
    private boolean destructible;

    public static DecoTemplate loadDecoTemplate(String path) {
        Gson gson = new Gson();

        try {
            return gson.fromJson(path, DecoTemplate.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public boolean isDestructible() {
        return destructible;
    }

    public void setDestructible(boolean destructible) {
        this.destructible = destructible;
    }
}
