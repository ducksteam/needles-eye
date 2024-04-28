package com.chiefsource.unseenrealms.player;

import com.badlogic.gdx.math.Vector3;

public class Player {
    int health;
    int maxHealth;
    Vector3 pos;
    Vector3 vel;
    Vector3 rot; // rads
    Inventory inv;

    public Player(Vector3 pos) {
        this.pos = pos;
        vel = new Vector3(0,0,0);
        rot = new Vector3(0,0,0);
        inv = new Inventory();
        health = 6;
        maxHealth = 6;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public Vector3 getPos() {
        return pos;
    }

    public void setPos(Vector3 pos) {
        this.pos = pos;
    }

    public Vector3 getVel() {
        return vel;
    }

    public void setVel(Vector3 vel) {
        this.vel = vel;
    }

    public Inventory getInv() {
        return inv;
    }

    public void setInv(Inventory inv) {
        this.inv = inv;
    }

    public Vector3 getRot() {
        return rot;
    }

    public void setRot(Vector3 rot) {
        this.rot = rot;
    }
}
