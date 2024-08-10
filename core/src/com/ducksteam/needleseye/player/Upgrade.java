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

    public Boolean isIconLoaded;
    String name;
    String description;
    Texture icon;
    String iconAddress;
    String modelAddress;
    public boolean isBaseUpgrade;

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

    public static void registerUpgrades() {
        for(BaseUpgrade upgrade : BaseUpgrade.values()) {
            if(upgrade == BaseUpgrade.NONE) continue;
            UpgradeRegistry.registerUpgrade(upgrade.name(), upgrade.upgradeClass);
            Gdx.app.debug("UpgradeRegistry", "Registered upgrade: " + upgrade.name()+ " with class: " + upgrade.upgradeClass);
        }
        UpgradeRegistry.registerUpgrade("Lead", LeadUpgrade.class);
        UpgradeRegistry.registerUpgrade("Mercury", MercuryUpgrade.class);
    }

    public void onPickup() {
        Gdx.app.debug("Upgrade", "Picked up upgrade: " + name);
    }

    public void onAttack() {
        Gdx.app.debug("Upgrade", "Attacked with upgrade: " + name);
    }

    public void onDamage() {
        Gdx.app.debug("Upgrade", "Damaged with upgrade: " + name);
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
        SOUL_THREAD(3, SoulThread.class,
                new Animation<>(
                        Config.ATTACK_ANIM_SPEED,
                        TextureRegion.split(new Texture(Gdx.files.internal("ui/ingame/soul_swing.png")), 640, 360)[0]),
                new Animation<>(
                        Config.ATTACK_ANIM_SPEED,
                        TextureRegion.split(new Texture(Gdx.files.internal("ui/ingame/soul_crack.png")), 640, 360)[0])),
        COAL_THREAD(5, CoalThread.class,
                new Animation<>(
                        Config.ATTACK_ANIM_SPEED,
                        TextureRegion.split(new Texture(Gdx.files.internal("ui/ingame/coal_swing.png")), 640, 360)[0]),
                new Animation<>(
                        Config.ATTACK_ANIM_SPEED,
                        TextureRegion.split(new Texture(Gdx.files.internal("ui/ingame/coal_crack.png")), 640, 360)[0])),
        JOLT_THREAD(4, JoltThread.class,
                new Animation<>(
                        Config.ATTACK_ANIM_SPEED,
                        TextureRegion.split(new Texture(Gdx.files.internal("ui/ingame/jolt_swing.png")), 640, 360)[0]),
                new Animation<>(
                        Config.ATTACK_ANIM_SPEED,
                        TextureRegion.split(new Texture(Gdx.files.internal("ui/ingame/jolt_crack.png")), 640, 360)[0])),
        THREADED_ROD(4, ThreadedRod.class,
                new Animation<>(
                        Config.ATTACK_ANIM_SPEED,
                        TextureRegion.split(new Texture(Gdx.files.internal("ui/ingame/trod_swing.png")), 640, 360)[0]),
                null),
        NONE(-1, null, null, null);

        public final Class<? extends Upgrade> upgradeClass;
        final int MAX_HEALTH;
        public final Animation<TextureRegion> swingAnim;
        public final Animation<TextureRegion> crackAnim;

        BaseUpgrade(int maxHealth, Class<? extends Upgrade> upgradeClass, Animation<TextureRegion> swingAnim, Animation<TextureRegion> crackAnim) {
            MAX_HEALTH = maxHealth;
            this.upgradeClass = upgradeClass;
            this.swingAnim = swingAnim;
            this.crackAnim = crackAnim;
        }
    }
}

