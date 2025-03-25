package com.ducksteam.needleseye.map;

import com.ducksteam.needleseye.Main;

public class Playthrough {

    private String name;
    private int currentLevelId;
    private Seed seed;

    public Playthrough() {}

    public Playthrough(Seed seed, String name) {
        this.seed = seed;
        this.currentLevelId = 0;
        this.name = name;
    }

    public void update() {
        currentLevelId = Main.mapMan.levelIndex;
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

    public Seed getSeed() {
        return seed;
    }
}
