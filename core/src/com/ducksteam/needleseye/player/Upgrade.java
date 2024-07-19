package com.ducksteam.needleseye.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.ducksteam.needleseye.UpgradeRegistry;

import java.util.HashMap;

/**
 * Represents an upgrade that the player can pick up
 * @author SkySourced
 */
public class Upgrade {

    public Boolean isIconLoaded;
    String name;
    String description;
    Texture icon;
    String iconAddress;
    String modelAddress;

    public Upgrade() {
        this.name = "Upgrade";
        this.description = "This is an upgrade";
        this.modelAddress = null;
    }
    public Upgrade (String name, String description, String iconAddress, String modelAddress) {
        this.name = name;
        this.description = description;
        this.iconAddress = iconAddress;
        this.modelAddress = modelAddress;
    }

    public static void registerUpgrades() {
        for(BaseUpgrade upgrade : BaseUpgrade.values()) {
            if(upgrade == BaseUpgrade.NONE) continue;
            UpgradeRegistry.registerUpgrade(upgrade.name(), upgrade.upgradeClass);
            Gdx.app.debug("UpgradeRegistry", "Registered upgrade: " + upgrade.name()+ " with class: " + upgrade.upgradeClass);
        }
    }

    /*
    * Accessor/mutator methods ______.
    *                                |
    *                                V
    * */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconAddress() {
        return iconAddress;
    }

    public void setIconAddress(String iconAddress) {
        this.iconAddress = iconAddress;
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

    public void setIconFromMap(HashMap<String,Texture> map){
        if(map.containsKey(iconAddress)) {
            icon = map.get(iconAddress);
        } else {
            Gdx.app.error("Upgrade", "Icon not found: " + iconAddress);
        }
    }

    public String getModelAddress() {
        return modelAddress;
    }

    public void setModelAddress(String modelAddress) {
        this.modelAddress = modelAddress;
    }

    public enum BaseUpgrade {
        SOUL_THREAD (3,SoulThread.class),
        COAL_THREAD (5,CoalThread.class),
        JOLT_THREAD (4,JoltThread.class),
        THREADED_ROD (4,ThreadedRod.class),
        NONE (-1,null);

        public Class<? extends Upgrade> upgradeClass;
        final int MAX_HEALTH;

        BaseUpgrade(int maxHealth, Class<? extends Upgrade> upgradeClass) {
            MAX_HEALTH = maxHealth;
            this.upgradeClass = upgradeClass;
        }
    }

    public class SoulThread extends Upgrade {
        public SoulThread() {
            super("Soul Thread", "Increases maximum health by 3", "ui/icons/soul_thread.png", null);
        }
    }

    public class CoalThread extends Upgrade {
        public CoalThread() {
            super("Coal Thread", "Increases maximum health by 5", null, null);
        }
    }

    public class JoltThread extends Upgrade {
        public JoltThread() {
            super("Jolt Thread", "Increases maximum health by 4", null, null);
        }
    }

    public class ThreadedRod extends Upgrade {
        public ThreadedRod() {
            super("Threaded Rod", "Increases maximum health by 4", null, null);
        }
    }
}

