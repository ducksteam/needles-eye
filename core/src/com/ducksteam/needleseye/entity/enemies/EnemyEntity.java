package com.ducksteam.needleseye.entity.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.ducksteam.needleseye.Config;
import com.ducksteam.needleseye.Main;
import com.ducksteam.needleseye.entity.Entity;
import com.ducksteam.needleseye.entity.IHasHealth;
import com.ducksteam.needleseye.entity.RoomInstance;
import com.ducksteam.needleseye.entity.effect.DamageEffectManager;
import com.ducksteam.needleseye.entity.enemies.ai.IHasAi;

/**
 * Entity class to represent enemies in the game
 * @author skysourced
 * */
public abstract class EnemyEntity extends Entity implements IHasHealth {

    //Health and interaction data
    int health;
    int maxHealth;
    Vector2 assignedRoom;
    IHasAi ai;
    float damageTimeout = 0;
    static Vector3 tmp = new Vector3();

    /**
     * Constructor for Enemy
     * @param position Vector3 position of the enemy
     * @param rotation Quaternion rotation of the enemy
     * @param mass float mass of the enemy
     * @param modelInstance ModelInstance of the enemy
     * @param maxHealth int max health of the enemy
     * @param assignedRoom Vector2 room space position of the enemy
     * */
    public EnemyEntity(Vector3 position, Quaternion rotation, float mass, ModelInstance modelInstance, int maxHealth, Vector2 assignedRoom) {
        super(position, rotation, mass, modelInstance, ENEMY_GROUP | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);

        collider.setContactCallbackFlag(ENEMY_GROUP); // This is an enemy collider
        collider.setContactCallbackFilter(PLAYER_GROUP | GROUND_GROUP | PROJECTILE_GROUP); // Special logic should be applied when colliding with player

        setMaxHealth(maxHealth, true);
        this.assignedRoom = assignedRoom;
    }
    /**
     * Update method for the enemy
     * @param delta float delta time
     * */
    @Override
    public void update(float delta) {
        if (ai != null) ai.update(delta);
        if (getDamageTimeout() > 0) damageTimeout -= delta;
        if (health <= 0) this.destroy();
        if (getPosition().y < -10) this.destroy();
    }

    /**
     * Method to apply damage to the enemy
     * @param damage int damage to apply
     * */
    @Override
    public void damage(int damage) {
        if (damageTimeout > 0) return;
        DamageEffectManager.create(getPosition());
        tmp.set(Main.player.getPosition().sub(getPosition())).nor();
        tmp.y = 0;
        collider.applyCentralImpulse(tmp.scl(-Config.KNOCKBACK_FORCE));
        Gdx.app.debug("EnemyEntity", "Tmp " + tmp);
        health -= damage;
        setDamageTimeout(Config.DAMAGE_TIMEOUT);
        if (health > maxHealth) setHealth(maxHealth);
    }

    /**
     * Sets health of the enemy
     * */
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

    public int getContactDamage(){
        return 0;
    }

    public void setAssignedRoom(RoomInstance room){
        assignedRoom = room.getRoomSpacePos();
    }

    public Vector2 getAssignedRoom(){
        return assignedRoom;
    }

    @Override
    public void destroy() {
        super.destroy();
        if (assignedRoom != null) {
            Main.mapMan.getCurrentLevel().getRoom(assignedRoom).removeEnemy(this);
        }
    }
}
