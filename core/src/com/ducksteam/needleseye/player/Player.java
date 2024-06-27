package com.ducksteam.needleseye.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.ducksteam.needleseye.Main;
import com.ducksteam.needleseye.entity.Entity;
import com.ducksteam.needleseye.entity.collision.ColliderBox;
import com.ducksteam.needleseye.player.Upgrade.BaseUpgrade;

import java.util.ArrayList;

/**
 * Represents the player in the game
 * @author SkySourced
 */
public class Player extends Entity {
    public BaseUpgrade baseUpgrade;

    ArrayList<Upgrade> upgrades;

    int health;
    int maxHealth;
    Vector3 pos;
    Vector3 vel;
    Vector3 rot; // rads

    public Player(Vector3 pos) {
        super(pos, new Vector2(0,0));
        baseUpgrade = BaseUpgrade.NONE;

        this.pos = pos;
        vel = new Vector3(0,0,0);
        rot = new Vector3(1,0,0);

        collider = new ColliderBox(pos, new Vector3(-0.5f, -1, -0.5f), new Vector3(0.5f, 1, 0.5f));
        health = -1;
        maxHealth = -1;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void damage(int damage) {
        health -= damage;
        if (health <= 0) Main.setGameState(Main.GameState.DEAD_MENU);
        if (health > maxHealth) setHealth(maxHealth);
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth, boolean heal) {
        this.maxHealth = maxHealth;
        if (heal) this.health = maxHealth;
    }

    public Vector3 getPos() {
        return pos;
    }

    public void setPos(Vector3 pos) {
        this.pos = pos.cpy();
        collider.setCentre(pos, false);
    }

    public Vector3 getVel() {
        return vel;
    }

    public void setVel(Vector3 vel) {
        this.vel = vel;
    }

    public Vector3 getRot() {
        return rot;
    }

    public void setRot(Vector3 rot) {
        this.rot = rot;
    }

    public void setBaseUpgrade(BaseUpgrade baseUpgrade) {
        this.baseUpgrade = baseUpgrade;
        this.setMaxHealth(baseUpgrade.MAX_HEALTH, true);
    }

    @Override
    public String getModelAddress() {
        return null;
    }
}
