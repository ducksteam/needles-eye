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
            UpgradeRegistry.registerUpgrade(upgrade.NAME, upgrade.UPGRADE_CLASS);
            Gdx.app.debug("UpgradeRegistry", "Registered upgrade: " + upgrade.name()+ " with class: " + upgrade.UPGRADE_CLASS);
        }
        UpgradeRegistry.registerUpgrade("Lead", LeadUpgrade.class);
        UpgradeRegistry.registerUpgrade("Mercury", MercuryUpgrade.class);
        UpgradeRegistry.registerUpgrade("Gold", GoldUpgrade.class);
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

        public final String NAME;
        public final Class<? extends Upgrade> UPGRADE_CLASS;
        final int MAX_HEALTH;
        public final Animation<TextureRegion> SWING_ANIM;
        public final Animation<TextureRegion> CRACK_ANIM;
        final int BASE_DAMAGE;

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

