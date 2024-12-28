package com.ducksteam.needleseye.map;

import com.ducksteam.needleseye.entity.Entity;
import com.ducksteam.needleseye.player.Player;

import java.util.concurrent.ConcurrentHashMap;

public class Playthrough {

    int currentLevelId;
    String seed;
    Player player;
    ConcurrentHashMap<Integer, Entity> entities;

    public Playthrough(String seed, Player player) {
        this.seed = seed;
        this.player = player;
        this.entities = new ConcurrentHashMap<>();
        this.currentLevelId = 0;
    }
}
