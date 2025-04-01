package com.ducksteam.needleseye.map;

import com.ducksteam.needleseye.Main;
import com.ducksteam.needleseye.player.Player;
import com.ducksteam.needleseye.player.Upgrade;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Playthrough {

    private String name;
    private int currentLevelId;
    private Seed seed;
    private PlaythroughPlayerData playerData;

    public Playthrough() {}

    public Playthrough(Seed seed, String name) {
        this.seed = seed;
        this.currentLevelId = 0;
        this.name = name;
    }

    public void update() {
        setCurrentLevelId(Main.mapMan.levelIndex);
        setPlayer(Main.player);
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

    public void setPlayer(Player player) {
        playerData = new PlaythroughPlayerData(player);
    }

    public Seed getSeed() {
        return seed;
    }

    static class PlaythroughPlayerData {
        ArrayList<String> upgrades;
        int health;
        int maxHealth;
        Upgrade.BaseUpgrade baseUpgrade;

        PlaythroughPlayerData(Player player) {
            upgrades = player.upgrades.stream().map(Upgrade::getName).collect(Collectors.toCollection(ArrayList::new));
            health = player.getHealth();
            maxHealth = player.getMaxHealth();
            baseUpgrade = player.baseUpgrade;
        }
    }
}
