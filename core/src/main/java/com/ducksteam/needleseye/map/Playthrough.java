package com.ducksteam.needleseye.map;

import com.ducksteam.needleseye.entity.Entity;
import com.ducksteam.needleseye.player.Player;

import java.util.concurrent.ConcurrentHashMap;

public class Playthrough {

    private final String name;
    private int currentLevelId;
    private final String seed;
    public ConcurrentHashMap<Integer, Entity> entities;

    public Playthrough(String seed, String name) {
        this.seed = seed;
        this.entities = new ConcurrentHashMap<>();
        this.currentLevelId = 0;
        this.name = name;
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

    public String getSeed() {
        return seed;
    }
}
