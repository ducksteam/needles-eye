package com.ducksteam.needleseye.entity.enemies;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.ducksteam.needleseye.Config;
import com.ducksteam.needleseye.Main;
import com.ducksteam.needleseye.entity.Entity;
import com.ducksteam.needleseye.entity.IHasHealth;
import com.ducksteam.needleseye.entity.enemies.ai.IHasAi;

public abstract class EnemyEntity extends Entity implements IHasHealth {

    int health;
    int maxHealth;
    Vector2 assignedRoom;
    IHasAi ai;
    float damageTimeout = 0;

    public EnemyEntity(Vector3 position, Quaternion rotation, float mass, ModelInstance modelInstance, int maxHealth, Vector2 assignedRoom) {
        super(position, rotation, mass, modelInstance, ENEMY_GROUP);
        setMaxHealth(maxHealth, true);
        this.assignedRoom = assignedRoom;
    }

    @Override
    public void update(float delta) {
        if (ai != null) ai.update(delta);
        if (getDamageTimeout() > 0) damageTimeout -= delta;
    }

    @Override
    public void damage(int damage) {
        if (damageTimeout > 0) return;
        health -= damage;
        setDamageTimeout(Config.DAMAGE_TIMEOUT);
        if (health > maxHealth) setHealth(maxHealth);
        if (health <= 0) this.destroy();
    }

    @Override
    public void setHealth(int health) {
        this.health = health;
    }

    @Override
    public int getHealth() {
        return health;
    }

    @Override
    public void setMaxHealth(int maxHealth, boolean heal) {
        this.maxHealth = maxHealth;
        if (heal) setHealth(maxHealth);
    }

    @Override
    public int getMaxHealth() {
        return maxHealth;
    }

    @Override
    public void setDamageTimeout(float timeout) {
        this.damageTimeout = timeout;
    }

    @Override
    public float getDamageTimeout() {
        return damageTimeout;
    }

    public IHasAi getAi() {
        return ai;
    }

    public void setAi(IHasAi ai) {
        this.ai = ai;
    }

    @Override
    public void destroy() {
        super.destroy();
        if (assignedRoom != null) {
            Main.mapMan.getCurrentLevel().getRoom(assignedRoom).removeEnemy(this);
        }
    }
}
