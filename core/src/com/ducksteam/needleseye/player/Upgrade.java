package com.ducksteam.needleseye.player;

import com.badlogic.gdx.graphics.Texture;

/**
 * Represents an upgrade that the player can pick up
 * @author SkySourced
 */
public abstract class Upgrade {
    String name;
    String description;
    Texture icon;
    String modelAddress;

    public Upgrade (String name, String description, Texture icon, String modelAddress) {
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.modelAddress = modelAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Texture getIcon() {
        return icon;
    }

    public void setIcon(Texture icon) {
        this.icon = icon;
    }

    public String getModelAddress() {
        return modelAddress;
    }

    public void setModelAddress(String modelAddress) {
        this.modelAddress = modelAddress;
    }

    public enum BaseUpgrade {
        SOUL_THREAD,
        COAL_THREAD,
        JOLT_THREAD,
        THREADED_ROD,
        NONE
    }

}

