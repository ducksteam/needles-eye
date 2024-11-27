package com.ducksteam.needleseye.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.ducksteam.needleseye.Config;
import com.ducksteam.needleseye.Main;
import com.ducksteam.needleseye.UpgradeRegistry;
import com.ducksteam.needleseye.entity.Entity;
import com.ducksteam.needleseye.entity.IHasHealth;
import com.ducksteam.needleseye.entity.bullet.EntityMotionState;
import com.ducksteam.needleseye.entity.effect.DamageEffectManager;
import com.ducksteam.needleseye.entity.effect.SoulFireEffectManager;
import com.ducksteam.needleseye.entity.enemies.EnemyEntity;
import com.ducksteam.needleseye.player.Upgrade.BaseUpgrade;
import net.mgsx.gltf.scene3d.scene.Scene;

import java.util.ArrayList;

import static com.ducksteam.needleseye.Config.JOLT_PARALYSE_TIME;
import static com.ducksteam.needleseye.Main.*;
import static com.ducksteam.needleseye.map.MapManager.getRoomSpacePos;

/**
 * Represents the player in the game
 * @author SkySourced
 * @author thechiefpotatopeeler
 */
public class Player extends Entity implements IHasHealth {
    /**
     * The {@link BaseUpgrade} of the player
     */
    public BaseUpgrade baseUpgrade;
    /**
     * The upgrades the player has collected
     */
    public ArrayList<Upgrade> upgrades;
    /**
     * The player's health
     */
    int health;
    /**
     * The player's maximum health
     */
    int maxHealth;

    // collision constants
    private static final float PLAYER_BOX_HALF_SIZE = 0.25f;
    private static final float PLAYER_BOX_HALF_HEIGHT = 0.5f;
    /**
     * The attack radius of the player's LMB attack
     */
    public static final float ATTACK_RANGE = 1.7f;

    // timeout variables
    float damageTimeout = 0;
    float abilityTimeout = 0;
    float attackTimeout = 0;
    /**
     * The time since the last upgrade was picked up. This is initially set to the upgrade text display timeout so that no text is displayed until an upgrade is collected
     */
    public static float timeSincePickup = Config.UPGRADE_TEXT_DISPLAY_TIMEOUT;

    /**
     * The player's euler rotation, only used for the camera. Quaternions are hard.
     * measured in radians
     */
    public Vector3 eulerRotation;

    // Upgrade properties
    /**
     * The player's speed multiplier
     */
    public float playerSpeedMultiplier = 1;
    /**
     * The player's dodge chance.
     * This should be capped/we should make more upgrades so that the threat of a dodge chance >= 100% is very low
     */
    public float dodgeChance = 0f;
    /**
     * The player's damage boost, always applied
     */
    public float damageBoost = 0f;
    /**
     * The player's coal damage boost, decays over time
     */
    public float coalDamageBoost = 0f;
    /**
     * The player's jolt speed boost, decays over time
     */
    public float joltSpeedBoost = 0f;
    /**
     * The player's attack animation time, used for the attack timeout
     */
    public float attackLength = 0.2f;

    /**
     * Whether the player is currently jumping
     */
    public boolean isJumping;
    /**
     * Flags for vertical movement, 0 is up (y' greater than 0), 1 is down (y' less than 0). Used for jumping calculations
     */
    public boolean[] jumpFlags = new boolean[2];

    /**
     * The player's walking sound id.
     * Unsure.
     */
    public long walkingSoundId;

    Vector3 tempVec = new Vector3(); // temporary vector for calculations

    /**
     * Temporary: used for the player's model until we have a proper one
     */
    public static Scene sceneModel;

    /**
     * Creates a new player at the given position
     * @param pos the position to spawn the player at
     */
    public Player(Vector3 pos) {
        super(pos, new Quaternion().setEulerAngles(0, 0, 0), Config.PLAYER_MASS, sceneModel, Entity.PLAYER_GROUP);
        baseUpgrade = BaseUpgrade.NONE;

        eulerRotation = new Vector3(0,0,1);

        // regenerate physics world for player
        dynamicsWorld.dispose();
        dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, broadphase, constraintSolver, collisionConfig);
        dynamicsWorld.setGravity(new Vector3(0, -14f, 0));

        // build the player's model
        sceneModel = new Scene(new Model());
        setScene(sceneModel);

        // reset other player info
        this.upgrades = new ArrayList<>();
        health = -1;
        maxHealth = -1;
    }

    /**
     * Update the player's timeouts, jumping flags, boosts, and checks for any illegal states
     * @param delta the time since the last update in seconds
     */
    @Override
    public void update(float delta) {
        if (health == -1) health = maxHealth = baseUpgrade.MAX_HEALTH; // this will only be called on the first in-game frame
        if (maxHealth == -1) maxHealth = baseUpgrade.MAX_HEALTH;
        if (damageTimeout > 0) { // update damage timeout
            damageTimeout -= delta;
            if (damageTimeout <= 0) { // reset collision filter
                collider.setContactCallbackFilter(ENEMY_GROUP | PROJECTILE_GROUP | PICKUP_GROUP);
                damageTimeout = 0;
            }
        }

        timeSincePickup += delta; // update time since last pickup

        //Floors and regulates boost variables
        if (attackTimeout > 0) attackTimeout -= delta;
        if (attackTimeout < 0) attackTimeout = 0;
        if (coalDamageBoost > 0) coalDamageBoost -= (float) (0.43 * Math.pow(Math.E, coalDamageBoost/2) * delta);
        if (joltSpeedBoost > 0) joltSpeedBoost -= (float) (0.43 * Math.pow(Math.E, joltSpeedBoost/2) * delta);

        // calculate jumping flags
        float velY = Math.round(getVelocity().y);

        if(!jumpFlags[0] && !jumpFlags[1]) {
            jumpFlags[0] = velY > 0;
            jumpFlags[1] = velY < 0;
        }
        if(jumpFlags[0] && velY<0){
            jumpFlags[0] = false;
            jumpFlags[1] = true;
        }
        if(jumpFlags[1] && velY==0){
            jumpFlags[1] = false;
        }
        isJumping = jumpFlags[0] || jumpFlags[1];

        // kill player if they have fallen out of the map
        motionState.getWorldTransform(tmpMat);
        if (tmpMat.getTranslation(tempVec).y < -10) setHealth(0);
    }

    /**
     * Sets the player's scene asset, updating the collision shape and rigid body
     * @param scene the scene asset to set
     */
    @Override
    public void setScene(Scene scene) {
        // delete old collision shape and rigid body
        if (collisionShape != null && !collisionShape.isDisposed()) collisionShape.dispose();
        if (collider != null && !collider.isDisposed()) collider.dispose();

        // create new collision shape and motion state
        collisionShape = new btBoxShape(new Vector3(PLAYER_BOX_HALF_SIZE, PLAYER_BOX_HALF_HEIGHT, PLAYER_BOX_HALF_SIZE));
        motionState = new EntityMotionState(this);
        // calculate inertia
        Vector3 inertia = new Vector3();
        collisionShape.calculateLocalInertia(Config.PLAYER_MASS, inertia);
        // create rigid body
        collider = new btRigidBody(Config.PLAYER_MASS, motionState, collisionShape, inertia);
        collider.setCollisionFlags(btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK | PLAYER_GROUP); // the cf custom material callback flag is required for custom collision
        collider.setActivationState(Collision.DISABLE_DEACTIVATION); // player should never deactivate
        collider.setDamping(0.95f, 1f); // set damping
        collider.setAngularFactor(Vector3.Y); // lock x/z rotation
        collider.setUserValue(this.id); // set user value to entity id

        // set filters for custom collision
        collider.setContactCallbackFlag(PLAYER_GROUP);
        collider.setContactCallbackFilter(ENEMY_GROUP | PROJECTILE_GROUP | PICKUP_GROUP);

        // add rigid body to the physics world
        dynamicsWorld.addRigidBody(collider);
    }

    /**
     * The primary attack of the player's whip
     * */
    public void primaryAttack() {
        if (baseUpgrade == BaseUpgrade.NONE) return;
        if (attackAnimTime != 0 || crackAnimTime != 0) return;

        // start animation
        attackAnimTime = 0.01F;
        // run damage logic
        if (baseUpgrade == BaseUpgrade.JOLT_THREAD) player.whipAttack((entity -> {
            ((IHasHealth) entity).damage(baseUpgrade.BASE_DAMAGE + (int) damageBoost, this);
            if (Math.random()<0.2) ((IHasHealth) entity).setParalyseTime(JOLT_PARALYSE_TIME);
        }));
        else player.whipAttack(baseUpgrade.BASE_DAMAGE + (int) damageBoost + (int) coalDamageBoost);

        // play sounds
        if(sounds.get("audio/sfx/whip_lash_1.mp3")!=null) {
            sounds.get("audio/sfx/whip_lash_1.mp3").stop(walkingSoundId);
            walkingSoundId = sounds.get("audio/sfx/whip_lash_1.mp3").play();
            sounds.get("audio/sfx/whip_lash_1.mp3").setVolume(walkingSoundId,0.5f);
        }

        setAttackTimeout(attackLength);
    }

    /**
     * The secondary attack of the player's whip
     * */
    public void ability() {
        if (baseUpgrade == BaseUpgrade.NONE || baseUpgrade == BaseUpgrade.THREADED_ROD) return; // no upgrade for trod
        if (abilityTimeout > 0) return;
        if (attackAnimTime != 0 || crackAnimTime != 0) return; // if an animation is currently playing

        crackAnimTime = 0.01F; // begin animation

        // run ability logic
        switch (baseUpgrade) {
            case SOUL_THREAD -> SoulFireEffectManager.create(player.getPosition().add(player.eulerRotation.cpy().nor().scl(Config.SOUL_FIRE_THROW_DISTANCE))); // create a new effect
            case COAL_THREAD -> coalDamageBoost = 3;
            case JOLT_THREAD -> joltSpeedBoost = 1.5f;
        }

        // play sounds
        if(sounds.get("audio/sfx/whip_crack_1.mp3")!=null) {
            long id = sounds.get("audio/sfx/whip_crack_1.mp3").play();
            sounds.get("audio/sfx/whip_crack_1.mp3").setVolume(id,0.5f);
        }
    }

    /**
     * Run a whip attack with no custom logic, only damage
     * @param damage the damage value
     */
    public void whipAttack(int damage){
        whipAttack((Entity target) -> ((IHasHealth) target).damage(damage, this));
    }

    /**
     * Run a whip attack with custom logic
     * @param enemyLogic the logic to run on each enemy hit
     */
    public void whipAttack(EntityRunnable enemyLogic) {
        if (attackTimeout > 0) return; // if already attacking, don't
        if (mapMan.getCurrentLevel().getRoom(getRoomSpacePos(player.getPosition(), true)) == null) return; // don't attack if not in a room

        for (Entity entity : Main.entities.values()) {
            if (entity instanceof EnemyEntity enemy) { // for each enemy
                tempVec = enemy.getPosition().sub(player.getPosition()); // get distance
                if (tempVec.len() < ATTACK_RANGE) { // if within attack radius, run logic
                    enemyLogic.run(enemy);
                }
            }
        }
    }

    /**
     * Gets player health
     * @return player health
     */
    public int getHealth() {
        return health;
    }

    /**
     * Set the player's maximum health
     * @param maxHealth new maximum health
     * @param heal if true, sets the health to new max health
     */
    @Override
    public void setMaxHealth(int maxHealth, boolean heal) {
        this.maxHealth = maxHealth;
        if (heal) setHealth(maxHealth);
    }

    /**
     * Damages the player
     * @param damage the amount of damage
     * @param source the entity that caused the damage
     */
    @Override
    public void damage(int damage, Entity source) {
        if (damage < 0) {
            heal(-damage);
            return;
        }
        if (damage == 0) return;

        if (damageTimeout > 0) return; // if damaged recently, don't
        setDamageTimeout(Config.DAMAGE_TIMEOUT);

        if (Math.random() < dodgeChance) return; // if player has dodged, skip damage

        DamageEffectManager.create(getPosition()); // create particle effect
        health -= damage;

        if (health > maxHealth) setHealth(maxHealth); // if negative damage, cap health at max
        upgrades.forEach((Upgrade::onDamage)); // run upgrade logic for after damage
    }
    @Override
    public float getParalyseTime(){
        return 0;
    }
    @Override
    public void setParalyseTime(float paralyseTime){

    }

    /**
     * Heals  the  player
     * @param damage the amount of health to heal
     */
    public void heal(int damage) {
        if (damage < 0) {
            damage(-damage, this);
            return;
        }
        if (damage == 0) return;

        if (health + damage <= maxHealth) setHealth(getHealth() + damage);
        if (health + damage > maxHealth) setHealth(maxHealth);
    }

    /**
     * Sets the player's health
     * @param health the new health value
     */
    @Override
    public void setHealth(int health) {
        this.health = health;
    }

    /**
     * Gets the player's maximum health
     * @return the player's maximum health
     */
    public int getMaxHealth() {
        return maxHealth;
    }

    /**
     * Sets the player's damage timeout
     * @param timeout the new timeout value
     */
    @Override
    public void setDamageTimeout(float timeout) {
        this.damageTimeout = timeout;
        collider.setContactCallbackFilter(PICKUP_GROUP); // set collision filter to prevent damage
    }

    /**
     * Gets the player's damage timeout
     * @return the player's damage timeout
     */
    @Override
    public float getDamageTimeout() {
        return damageTimeout;
    }

    /**
     * Sets the player's attack timeout
     * @param timeout the new timeout value
     */
    public void setAttackTimeout(float timeout) {
        this.attackTimeout = timeout;
    }

    /**
     * Gets the player's attack timeout
     * @return the player's attack timeout
     */
    public float getAttackTimeout() {
        return attackTimeout;
    }

    /**
     * Gets the player's camera rotation
     * @return the player's camera rotation
     */
    public Vector3 getEulerRotation() {
        return eulerRotation;
    }

    /**
     * Sets the player's camera rotation
     * @param rot the new camera rotation
     */
    public void setEulerRotation(Vector3 rot) {
        this.eulerRotation = rot;
        // update the transformation matrix
        transform.getTranslation(tempVec);
        transform.setFromEulerAnglesRad(rot.x, rot.y, rot.z);
        transform.setTranslation(tempVec);
    }

    /**
     * Sets the player's base upgrade
     * @param baseUpgrade the new base upgrade
     */
    public void setBaseUpgrade(BaseUpgrade baseUpgrade) {
        Gdx.app.log("Player", "Setting base upgrade to " + baseUpgrade.name());
        if(this.baseUpgrade != BaseUpgrade.NONE) { // removes any old base upgrade
            this.upgrades.removeIf(upgrade -> upgrade.getName().equals(this.baseUpgrade.NAME));
        }
        this.baseUpgrade = baseUpgrade;
        this.setMaxHealth(baseUpgrade.MAX_HEALTH, true); // set max health to that determined by the upgrade
        try { // add the upgrade to the player's list
            this.upgrades.add(UpgradeRegistry.getUpgradeInstance(baseUpgrade.UPGRADE_CLASS));
        } catch (Exception e) {
            Gdx.app.error("Player", "Base upgrade not found: " + baseUpgrade.name(),e);
        }

    }

    /**
     * toString for debug
     * @return a string with important information about the player
     */
    @Override
    public String toString() {
        return "Player{" +
                "baseUpgrade=" + baseUpgrade +
                ", id=" + id +
                ", transform=" + transform +
                ", upgrades=" + upgrades +
                ", health=" + health +
                ", maxHealth=" + maxHealth +
                '}';
    }

    /**
     * Gets the player's model address, which is null because they don't have a model
     * This is just from extending entity
     * @return null
     */
    @Override
    public String getModelAddress() {
        return null;
    }

    /**
     * Serializes the player's data
     * @return a string with the player's data
     */
    public String serialize(){
        StringBuilder sb = new StringBuilder();
        sb.append(baseUpgrade.name()).append(",");
        sb.append(health).append(",");
        sb.append(maxHealth).append(",");
        for(Upgrade upgrade : upgrades){
            sb.append(upgrade.getName()).append(",");
        }

        return sb.toString();
    }

    /**
     * Deserializes the player's data
     * @param serial the serialized data to be parsed
     */
    public void setFromSerial(String serial){
        String[] parts = serial.split(",");
        baseUpgrade = BaseUpgrade.valueOf(parts[0]);
        for(int i = 3; i < parts.length; i++){
            if (parts[i].isEmpty()) continue;
            try {
                upgrades.add(UpgradeRegistry.getUpgradeInstance(parts[i]));
                UpgradeRegistry.getUpgradeInstance(parts[i]).onPickup();
            } catch (Exception e) {
                Gdx.app.error("Player", "Upgrade not found: " + parts[i],e);
            }
        }
        health = Integer.parseInt(parts[1]);
        maxHealth = Integer.parseInt(parts[2]);
    }
}
