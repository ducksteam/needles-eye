package com.ducksteam.needleseye.map;

import com.badlogic.gdx.Gdx;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;

/**
 * Represents a template for a decoration in the world
 * @author SkySourced
 */
@Deprecated
public class DecoTemplate {
    private String name;
    private String modelPath;
    private String texturePath;
    private boolean destructible;

    public static DecoTemplate loadDecoTemplate(File file) {
        Gson gson = new Gson();

        try {
            return gson.fromJson(new FileReader(file), DecoTemplate.class);
        } catch (Exception e) {
            Gdx.app.error("DecoTemplate", "Error loading deco template: " + file.getName(), e);
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

    @Override
    public String toString() {
        return "DecoTemplate{" +
                "name='" + name + '\'' +
                ", modelPath='" + modelPath + '\'' +
                ", texturePath='" + texturePath + '\'' +
                ", destructible=" + destructible +
                '}';
    }
}
