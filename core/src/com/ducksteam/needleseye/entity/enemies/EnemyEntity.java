package com.ducksteam.needleseye.entity.enemies;

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
import com.ducksteam.needleseye.entity.effect.ParalysisEffectManager;
import com.ducksteam.needleseye.entity.enemies.ai.IHasAi;

/**
 * Entity class to represent enemies in the game
 * @author skysourced
 * */
public abstract class EnemyEntity extends Entity implements IHasHealth {

    int health;
    int maxHealth;
    Vector2 assignedRoom; // the room the enemy spawned in
    IHasAi ai; // the AI algorithm for the enemy
    float damageTimeout = 0;
    float paralyseTime = 0;

    // Temporary vector for calculations
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

        setMaxHealth(maxHealth, true); // Set the max health and heal the enemy
        this.assignedRoom = assignedRoom;
    }
    /**
     * Update method for the enemy
     * @param delta float delta time
     * */
    @Override
    public void update(float delta) {
        if (paralyseTime <= 0) {
            if (ai != null) ai.update(delta);
        }
        if (getDamageTimeout() > 0) damageTimeout -= delta;

        // delete the enemy if health is 0 or below, or if position is <-10
        if (health <= 0) this.destroy();
        if (getPosition().y < -10) this.destroy();

        // if the enemy's y pos is above 5, set it to 2
        if (getPosition().y > 5) this.setPosition(new Vector3(getPosition().x, 2, getPosition().z));

        // if the enemy is paralysed, reduce the time it will be paralysed for
        if (paralyseTime > 0) paralyseTime -= (float) (0.43 * Math.pow(Math.E, paralyseTime/2) * delta);
        if (paralyseTime <= 0) paralyseTime = 0;
    }

    /**
     * Method to apply damage to the enemy
     * @param damage int damage to apply
     * */
    @Override
    public void damage(int damage) {
        if (damageTimeout > 0) return;
        DamageEffectManager.create(getPosition()); // create effect

        tmp.set(Main.player.getPosition().sub(getPosition())).nor();
        tmp.y = 0;
        collider.applyCentralImpulse(tmp.scl(-Config.KNOCKBACK_FORCE));

        health -= damage;
        setDamageTimeout(Config.DAMAGE_TIMEOUT);
        if (health > maxHealth) setHealth(maxHealth);
    }
    /**
     * Method
     * @param duration float duration of paralysis
     */
    @Override
    public void setParalyseTime(float duration){
        paralyseTime = duration;
        if (duration > 0) ParalysisEffectManager.create(this);
    }

    /**
     * Method
     * @return float
     */
    @Override
    public float getParalyseTime(){
        return paralyseTime;
    }

    /**
     * Sets health of the enemy
     */
    @Override
    public void setHealth(int health) {
        this.health = health;
    }

    /**
     * Gets health of the enemy
     * @return int health of the enemy
     */
    @Override
    public int getHealth() {
        return health;
    }

    /**
     * Sets the max health of the enemy
     * @param maxHealth int max health of the enemy
     * @param heal boolean whether to heal the enemy
     */
    @Override
    public void setMaxHealth(int maxHealth, boolean heal) {
        this.maxHealth = maxHealth;
        if (heal) setHealth(maxHealth);
    }

    /**
     * Gets the max health of the enemy
     * @return int max health of the enemy
     */
    @Override
    public int getMaxHealth() {
        return maxHealth;
    }

    /**
     * Sets the damage timeout of the enemy
     * @param timeout float timeout of the enemy
     */
    @Override
    public void setDamageTimeout(float timeout) {
        this.damageTimeout = timeout;
    }

    /**
     * Gets the damage timeout of the enemy
     * @return float damage timeout of the enemy
     */
    @Override
    public float getDamageTimeout() {
        return damageTimeout;
    }

    /**
     * Gets the AI of the enemy
     * @return IHasAi AI of the enemy
     */
    public IHasAi getAi() {
        return ai;
    }

    /**
     * Sets the AI of the enemy
     * @param ai IHasAi AI of the enemy
     */
    public void setAi(IHasAi ai) {
        this.ai = ai;
    }

    /**
     * Gets the damage of the enemy
     * @return int damage of the enemy
     */
    public int getContactDamage(){
        return 0;
    }

    /**
     * Sets the room of the enemy
     * @param room RoomInstance room of the enemy
     */
    public void setAssignedRoom(RoomInstance room){
        assignedRoom = room.getRoomSpacePos();
    }

    /**
     * Gets the room of the enemy
     * @return Vector2 room of the enemy
     */
    public Vector2 getAssignedRoom(){
        return assignedRoom;
    }

    /**
     * Destroys the entity
     */
    @Override
    public void destroy() {
        super.destroy();
        if (assignedRoom != null) {
            Main.mapMan.getCurrentLevel().getRoom(assignedRoom).removeEnemy(this);
        }
    }
}
