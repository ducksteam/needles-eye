package com.ducksteam.unseenrealms.player;

import java.util.ArrayList;

/**
 * Represents the player's inventory
 * @author SkySourced
 */
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

    public ArrayList<Upgrade> getUpgrades(){
        return upgrades;
    }

    public void runDamageUpgrades(int damage){
        for (Upgrade upgrade : upgrades) {
            upgrade.onDamage(damage);
        }
    }

    public void runAttackUpgrades(){
        for (Upgrade upgrade : upgrades) {
            upgrade.onAttack();
        }
    }

    public void runThrowUpgrades(){
        for (Upgrade upgrade : upgrades) {
            upgrade.onThrow();
        }
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
