package com.chiefsource.unseenrealms.player;

import java.util.ArrayList;

public class Inventory {
    ArrayList<Upgrade> upgrades;
    int ropes;
    int rocks;

    public Inventory() {
        upgrades = new ArrayList<>();
        ropes = 3;
        rocks = 5;
    }

    public void addUpgrade(Upgrade upgrade) {
        upgrades.add(upgrade);
    }

    public ArrayList<Upgrade> getUpgrade(){
        return upgrades;
    }

    public void addRope() {
        ropes++;
    }

    public void addRock() {
        rocks++;
    }

    public void removeRope() {
        ropes--;
    }

    public void removeRock() {
        rocks--;
    }

    public int getRopes() {
        return ropes;
    }

    public int getRocks() {
        return rocks;
    }
}
