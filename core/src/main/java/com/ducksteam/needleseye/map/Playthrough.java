package com.ducksteam.needleseye.map;

import com.badlogic.gdx.Gdx;
import com.ducksteam.needleseye.Main;
import com.ducksteam.needleseye.player.Player;
import com.ducksteam.needleseye.player.Upgrade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Playthrough {

    String name;
    int currentLevelId;
    Seed seed;
    public PlaythroughPlayerData playerData;
    public boolean fromSave = false;

    public Playthrough() {}

    public Playthrough(Seed seed, String name) {
        this.seed = seed;
        this.currentLevelId = 0;
        this.name = name;
    }

    public void update() {
        setCurrentLevelId(Main.mapMan.levelIndex);
        updatePlayerData(Main.player);
    }

    /**
     * Verifies the input of the playthrough
     * @param playthrough the playthrough to check
     * @return true if there are errors
     */
    public static boolean checkPlaythrough(Playthrough playthrough) {
        boolean errors = false;
        if (playthrough.name.isEmpty()) {
            errors = true;
            Gdx.app.error("Playthrough check", "Playthrough name is empty");
        }
        if (playthrough.currentLevelId <= 0) {
            errors = true;
            Gdx.app.error("Playthrough check", "currentLevelId is invalid");
        }
        if (playthrough.seed == null) {
            errors = true;
            Gdx.app.error("Playthrough check", "Seed is invalid");
        } else {
            if (playthrough.seed.type == Seed.SeedType.STRING && playthrough.seed.seedString.isEmpty()){
                errors = true;
                Gdx.app.error("Playthrough check", "Seed string is empty");
            }
            if (playthrough.seed.type == Seed.SeedType.STRING && playthrough.seed.seedString.hashCode() != playthrough.seed.getSeed()) {
                errors = true;
                Gdx.app.error("Playthrough check", "Seed string does not hash to long seed");
            }
        }
        if (playthrough.playerData.upgrades.stream().filter(s -> Arrays.stream(Upgrade.BaseUpgrade.values()).map(u -> u.DISPLAY_NAME).anyMatch(bu -> bu.equals(s))).count() != 1) {
            errors = true;
            Gdx.app.error("Playthrough check", "Player does not have only one base upgrade");
        }
        if (playthrough.playerData.upgrades.stream().map(UpgradeRegistry::getUpgradeInstance).anyMatch(upgrade -> upgrade.getName().equals(Upgrade.FAKE_NAME))) {
            errors = true;
            Gdx.app.error("Playthrough check", "Player has invalid upgrade");
        }
        if (playthrough.playerData.health <= 0) {
            errors = true;
            Gdx.app.error("Playthrough check", "Player has no health");
        }
        if (playthrough.playerData.maxHealth <= 0) {
            errors = true;
            Gdx.app.error("Playthrough check", "Player has no max health");
        }
        if (playthrough.playerData.maxHealth < playthrough.playerData.health) {
            errors = true;
            Gdx.app.error("Playthrough check", "Player has more health than max health");
        }
        if (playthrough.playerData.baseUpgrade == Upgrade.BaseUpgrade.NONE || playthrough.playerData.baseUpgrade == null) {
            errors = true;
            Gdx.app.error("Playthrough check", "Player has no base upgrade");
        }
        return errors;
    }

    public String getName() {
        return name;
    }

    public int getCurrentLevelId() {
        return currentLevelId;
    }

    public void setCurrentLevelId(int currentLevelId) {
        this.currentLevelId = currentLevelId;
    }

    public void updatePlayerData(Player player) {
        playerData = new PlaythroughPlayerData(player);
    }

    public Seed getSeed() {
        return seed;
    }

    public static class PlaythroughPlayerData {
        public ArrayList<String> upgrades;
        public int health;
        public int maxHealth;
        public Upgrade.BaseUpgrade baseUpgrade;

        PlaythroughPlayerData() {}

        PlaythroughPlayerData(Player player) {
            upgrades = player.upgrades.stream().map(Upgrade::getName).collect(Collectors.toCollection(ArrayList::new));
            health = player.getHealth();
            maxHealth = player.getMaxHealth();
            baseUpgrade = player.baseUpgrade;
        }
    }
}
