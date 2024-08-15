package com.ducksteam.needleseye.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.ducksteam.needleseye.Config;
import com.ducksteam.needleseye.UpgradeRegistry;
import com.ducksteam.needleseye.player.upgrades.*;

import java.util.HashMap;

/**
 * Represents an upgrade that the player can pick up
 * @author SkySourced
 */
public class Upgrade {

    String name; // name of the upgrade
    String description; // flavour text for upgrade
    Texture icon; // icon for the upgrade
    String iconAddress; // file path to icon
    String modelAddress; // file path to model
    public boolean isBaseUpgrade; // whether the upgrade is a base upgrade

    public Upgrade() {
        this.name = "Upgrade";
        this.description = "This is an upgrade";
        this.modelAddress = null;
    }

    public Upgrade (String name, String description, String iconAddress, String modelAddress) {
        this(name, description, iconAddress, modelAddress, false);
    }

    public Upgrade (String name, String description, String iconAddress, String modelAddress, boolean isBaseUpgrade) {
        this.name = name;
        this.description = description;
        this.iconAddress = iconAddress;
        this.modelAddress = modelAddress;
        this.isBaseUpgrade = isBaseUpgrade;
    }

    /**
     * Add all upgrades to the UpgradeRegistry
     */
    public static void registerUpgrades() {
        // Register all base upgrades
        for(BaseUpgrade upgrade : BaseUpgrade.values()) {
            if(upgrade == BaseUpgrade.NONE) continue;
            UpgradeRegistry.registerUpgrade(upgrade.NAME, upgrade.UPGRADE_CLASS);
            Gdx.app.debug("UpgradeRegistry", "Registered upgrade: " + upgrade.name()+ " with class: " + upgrade.UPGRADE_CLASS);
        }
        // Register other upgrades
        UpgradeRegistry.registerUpgrade("Lead", LeadUpgrade.class);
        UpgradeRegistry.registerUpgrade("Mercury", MercuryUpgrade.class);
        UpgradeRegistry.registerUpgrade("Gold", GoldUpgrade.class);
    }

    /**
     * Method to be overridden for each upgrade, triggered on pickup
     */
    public void onPickup() {
        Gdx.app.debug("Upgrade", "Picked up upgrade: " + name);
    }

    /**
     * Method to be overridden for each upgrade, triggered on damage
     */
    public void onDamage() {
        Gdx.app.debug("Upgrade", "Damaged with upgrade: " + name);
    }

    /**
     * Get the name of the upgrade
     * @return name of the upgrade
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the upgrade
     * @param name the name of the upgrade
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the address of the icon
     * @return the address of the icon
     */
    public String getIconAddress() {
        return iconAddress;
    }

    /**
     * Get the description of the upgrade
     * @return the description of the upgrade
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the icon of the upgrade
     * @return the icon of the upgrade
     */
    public Texture getIcon() {
        return icon;
    }

    /**
     * Set the icon of the upgrade
     * @param map a map with upgrade name keys and upgrade icon values
     */
    public void setIconFromMap(HashMap<String,Texture> map){
        if(map.containsKey(iconAddress)) {
            icon = map.get(iconAddress);
        } else {
            Gdx.app.error("Upgrade", "Icon not found: " + iconAddress);
        }
    }

    /**
     * Get the address of the model
     * @return the address of the model
     */
    public String getModelAddress() {
        return modelAddress;
    }

    /**
     * Base upgrade enum
     */
    public enum BaseUpgrade {
        SOUL_THREAD("Soul Thread", 3, SoulThread.class,
                new Animation<>(
                        Config.ATTACK_ANIM_SPEED,
                        TextureRegion.split(new Texture(Gdx.files.internal("ui/ingame/soul_swing.png")), 640, 360)[0]),
                new Animation<>(
                        Config.ATTACK_ANIM_SPEED*3,
                        TextureRegion.split(new Texture(Gdx.files.internal("ui/ingame/soul_crack.png")), 640, 360)[0]),
                2),
        COAL_THREAD("Coal Thread", 5, CoalThread.class,
                new Animation<>(
                        Config.ATTACK_ANIM_SPEED,
                        TextureRegion.split(new Texture(Gdx.files.internal("ui/ingame/coal_swing.png")), 640, 360)[0]),
                new Animation<>(
                        Config.ATTACK_ANIM_SPEED*1.5f,
                        TextureRegion.split(new Texture(Gdx.files.internal("ui/ingame/coal_crack.png")), 640, 360)[0]),
                2),
        JOLT_THREAD("Jolt Thread", 4, JoltThread.class,
                new Animation<>(
                        Config.ATTACK_ANIM_SPEED,
                        TextureRegion.split(new Texture(Gdx.files.internal("ui/ingame/jolt_swing.png")), 640, 360)[0]),
                new Animation<>(
                        Config.ATTACK_ANIM_SPEED,
                        TextureRegion.split(new Texture(Gdx.files.internal("ui/ingame/jolt_crack.png")), 640, 360)[0]),
                3),
        THREADED_ROD("Threaded Rod", 4, ThreadedRod.class,
                new Animation<>(
                        Config.ATTACK_ANIM_SPEED,
                        TextureRegion.split(new Texture(Gdx.files.internal("ui/ingame/trod_swing.png")), 640, 360)[0]),
                null,
                3),
        NONE(null, -1, null, null, null, -1);

        public final String NAME; // name of the upgrade
        public final Class<? extends Upgrade> UPGRADE_CLASS; // class of the upgrade
        final int MAX_HEALTH; // max health of the upgrade
        public final Animation<TextureRegion> SWING_ANIM; // swing animation of the upgrade
        public final Animation<TextureRegion> CRACK_ANIM; // crack animation of the upgrade
        final int BASE_DAMAGE; // base damage for the upgrade

        BaseUpgrade(String name, int maxHealth, Class<? extends Upgrade> upgradeClass, Animation<TextureRegion> swingAnim, Animation<TextureRegion> crackAnim, int baseDamage) {
            this.NAME = name;
            this.MAX_HEALTH = maxHealth;
            this.UPGRADE_CLASS = upgradeClass;
            this.SWING_ANIM = swingAnim;
            this.CRACK_ANIM = crackAnim;
            this.BASE_DAMAGE = baseDamage;
        }
    }
}

