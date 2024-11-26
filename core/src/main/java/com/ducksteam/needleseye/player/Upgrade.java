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
    /**
     * Whether the upgrade is a base upgrade
     */
    public boolean isBaseUpgrade;

    /**
     * Create a new upgrade with dummy values
     * Only used for when searching fails
     */
    public Upgrade() {
        this.name = "Upgrade";
        this.description = "This is an upgrade";
        this.modelAddress = null;
    }

    /**
     * Create a new upgrade
     * @param name the name of the upgrade
     * @param description the description of the upgrade
     * @param iconAddress the address of the icon
     * @param modelAddress the address of the model
     */
    public Upgrade (String name, String description, String iconAddress, String modelAddress) {
        this(name, description, iconAddress, modelAddress, false);
    }

    /**
     * Create a new upgrade
     * @param name the name of the upgrade
     * @param description the description of the upgrade
     * @param iconAddress the address of the icon
     * @param modelAddress the address of the model
     * @param isBaseUpgrade whether the upgrade is a base upgrade
     */
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
        /**
         * The soul thread upgrade
         */
        SOUL_THREAD("Soul Thread", 3, SoulThread.class,"ui/ingame/soul_swing.png", "ui/ingame/soul_crack.png", 3, 2),
        /**
         * The coal thread upgrade
         */
        COAL_THREAD("Coal Thread", 5, CoalThread.class, "ui/ingame/coal_swing.png", "ui/ingame/coal_crack.png", 1.5f, 2),
        /**
         * The jolt thread upgrade
         */
        JOLT_THREAD("Jolt Thread", 4, JoltThread.class, "ui/ingame/jolt_swing.png", "ui/ingame/jolt_crack.png", 1, 3),
        /**
         * The threaded rod upgrade
         */
        THREADED_ROD("Threaded Rod", 4, ThreadedRod.class, "ui/ingame/trod_swing.png", null, 1, 3),
        /**
         * The player has not selected a base upgrade. The game will not progress past loading if this is 'selected'
         */
        NONE(null, -1, null, null, null, 1, -1);

        /**
         * The display name of the upgrade.
         * Not actually shown to the player.
         */
        public final String NAME;
        /**
         * The class of the upgrade.
         * Technically makes this entire enum redundant, but oh well
         */
        public final Class<? extends Upgrade> UPGRADE_CLASS;
        /**
         * The max health of the upgrade
         */
        final int MAX_HEALTH; // max health of the upgrade
        /**
         * The swing animation of the upgrade
         */
        public final Animation<TextureRegion> SWING_ANIM;
        /**
         * The crack animation of the upgrade
         */
        public final Animation<TextureRegion> CRACK_ANIM;
        /**
         * The base damage for the upgrade
         */
        final int BASE_DAMAGE;

        BaseUpgrade(String name, int maxHealth, Class<? extends Upgrade> upgradeClass, String swingAnimPath, String crackAnimPath, float crackAnimMult, int baseDamage) {
            this.NAME = name;
            this.MAX_HEALTH = maxHealth;
            this.UPGRADE_CLASS = upgradeClass;
            if(swingAnimPath != null) this.SWING_ANIM = new Animation<>(
                    Config.ATTACK_ANIM_SPEED,
                    TextureRegion.split(new Texture(Gdx.files.internal(swingAnimPath)), 640, 360)[0]);
            else this.SWING_ANIM = null;
            if(crackAnimPath != null) this.CRACK_ANIM = new Animation<>(
                    Config.ATTACK_ANIM_SPEED*crackAnimMult,
                    TextureRegion.split(new Texture(Gdx.files.internal(crackAnimPath)), 640, 360)[0]);
            else this.CRACK_ANIM = null;
            this.BASE_DAMAGE = baseDamage;
        }
    }
}

