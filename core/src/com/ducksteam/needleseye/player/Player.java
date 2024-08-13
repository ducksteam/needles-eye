package com.ducksteam.needleseye.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.ducksteam.needleseye.Config;
import com.ducksteam.needleseye.Main;
import com.ducksteam.needleseye.UpgradeRegistry;
import com.ducksteam.needleseye.entity.Entity;
import com.ducksteam.needleseye.entity.IHasHealth;
import com.ducksteam.needleseye.entity.bullet.EntityMotionState;
import com.ducksteam.needleseye.entity.effect.SoulFireEffectManager;
import com.ducksteam.needleseye.entity.enemies.EnemyEntity;
import com.ducksteam.needleseye.player.Upgrade.BaseUpgrade;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

import java.util.ArrayList;

import static com.ducksteam.needleseye.Main.*;
import static com.ducksteam.needleseye.map.MapManager.getRoomSpacePos;

/**
 * Represents the player in the game
 * @author SkySourced
 */
public class Player extends Entity implements IHasHealth {
    public BaseUpgrade baseUpgrade;

    public ArrayList<Upgrade> upgrades;

    int health;
    int maxHealth;
    private boolean grounded = false;

    private static final float playerBoxHalfSize = 0.25f;
    private static final float playerBoxHalfHeight = 0.5f;

    public static final float attackBoxDepth = 1.7f;

    float damageTimeout = 0;
    float abilityTimeout = 0;
    float attackTimeout = 0;
    float attackLength = 0.2f;

    public Vector3 eulerRotation; // rads

    // Upgrade properties
    public float playerSpeedMultiplier = 1;
    public float dodgeChance = 0f;
    public float damageBoost = 0f;

    Vector3 tmp = new Vector3();

    public Player(Vector3 pos) {
        super(pos, new Quaternion().setEulerAngles(0, 0, 0), Config.PLAYER_MASS, null, Entity.PLAYER_GROUP);
        baseUpgrade = BaseUpgrade.NONE;

        eulerRotation = new Vector3(0,0,1);

        dynamicsWorld.dispose();
        dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, broadphase, constraintSolver, collisionConfig);
        dynamicsWorld.setGravity(new Vector3(0, -14f, 0));
        dynamicsWorld.setDebugDrawer(debugDrawer);

        setModelInstance(null);

        this.upgrades = new ArrayList<>();
        health = -1;
        maxHealth = -1;
    }

    @Override
    public void update(float delta) {
        if (health == -1) health = maxHealth = baseUpgrade.MAX_HEALTH;
        if (maxHealth == -1) maxHealth = baseUpgrade.MAX_HEALTH;
        if (getDamageTimeout() > 0) {
            damageTimeout -= delta;
            if (damageTimeout <= 0) {
                collider.setContactCallbackFilter(ENEMY_GROUP | PROJECTILE_GROUP | PICKUP_GROUP);
                damageTimeout = 0;
            }
        }
        //Floors and regulates boost variables
        if (attackTimeout > 0) attackTimeout -= delta;
        if(attackTimeout<0) attackTimeout = 0;
        if (damageBoost > 0) damageBoost -= (float) (delta * 0.5f * Math.pow(10,-2));
        if(playerSpeedMultiplier > 1) playerSpeedMultiplier -= (float) (delta * 0.5f * Math.pow(10,-2));
        if(playerSpeedMultiplier < 1) playerSpeedMultiplier = 1;

        motionState.getWorldTransform(tmpMat);
        if (tmpMat.getTranslation(tmp).y < -10) setHealth(0);
    }

    @Override
    public void setModelInstance(ModelInstance modelInstance) {
        if (collisionShape != null && !collisionShape.isDisposed()) collisionShape.dispose();
        if (collider != null && !collider.isDisposed()) collider.dispose();

        collisionShape = new btBoxShape(new Vector3(playerBoxHalfSize, playerBoxHalfHeight, playerBoxHalfSize));
        collisionShape.obtain();
        motionState = new EntityMotionState(this);
        Vector3 inertia = new Vector3();
        collisionShape.calculateLocalInertia(Config.PLAYER_MASS, inertia);
        collider = new btRigidBody(Config.PLAYER_MASS, motionState, collisionShape, inertia);
        collider.setCollisionFlags(btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK | PLAYER_GROUP);
        collider.setActivationState(Collision.DISABLE_DEACTIVATION);
        collider.setDamping(0.95f, 1f);
        collider.setAngularFactor(Vector3.Y);
        collider.setUserValue(this.id);

        collider.setContactCallbackFlag(PLAYER_GROUP);
        collider.setContactCallbackFilter(ENEMY_GROUP | PROJECTILE_GROUP | PICKUP_GROUP);

        dynamicsWorld.addRigidBody(collider);
    }

    /**
     * The primary attack of the player's whip
     * */
    public void primaryAttack() {
        if (baseUpgrade == BaseUpgrade.NONE) return;
        if (attackAnimTime != 0 || crackAnimTime != 0) return;
        attackAnimTime = 0.01F;
        player.whipAttack(3 + (int) damageBoost);
        setAttackTimeout(attackLength);
        /*switch (baseUpgrade) {
            case SOUL_THREAD -> {
                player.whipAttack(3 + (int) damageBoost;
            }
            case COAL_THREAD -> {
//                player.whipAttack(); // todo add warming temperature
            }
            case JOLT_THREAD -> {
                player.whipAttack(3 + (int) damageBoost, (Entity target) -> {
                    boolean freeze = Math.random() < 0.2;
                    if (freeze) target.freeze(2);
                });
            }
            case THREADED_ROD -> {

            }
        }*/
    }

    /**
     * The secondary attack of the player's whip
     * */
    public void ability() {
        if (baseUpgrade == BaseUpgrade.NONE) return;
        if (abilityTimeout > 0) return;
        if (attackAnimTime != 0 || crackAnimTime != 0) return;
        crackAnimTime = 0.01F;
        switch (baseUpgrade) {
            case SOUL_THREAD -> {
                SoulFireEffectManager.create(player.getPosition().add(player.eulerRotation.cpy().nor().scl(1.5f)));
            }
            case COAL_THREAD -> {
                damageBoost = 1;
            }
            case JOLT_THREAD -> {
                playerSpeedMultiplier = 1.5f;
            }
        }
    }

    public void spawnSoulFire(Vector3 pos){
    }


    public void whipAttack(int damage){
        whipAttack((Entity target) -> ((IHasHealth) target).damage(damage));
    }

    public void whipAttack(EntityRunnable enemyLogic) {
        if (attackTimeout > 0) return;
        if (mapMan.getCurrentLevel().getRoom(getRoomSpacePos(player.getPosition())) == null) return;

        for (Entity entity : Main.entities.values()) {
            if (entity instanceof EnemyEntity) {
                EnemyEntity enemy = (EnemyEntity) entity;
                tmp = enemy.getPosition().sub(player.getPosition());
                if (tmp.len() < attackBoxDepth) {
                    enemyLogic.run(enemy);
                }
            }
        }
    }

    public void setGrounded(boolean grounded) {
        this.grounded = grounded;
    }

    public boolean isGrounded() {
        return grounded;
    }

    public int getHealth() {
        return health;
    }

    @Override
    public void setMaxHealth(int maxHealth, boolean heal) {
        this.maxHealth = maxHealth;
        if (heal) setHealth(maxHealth);
    }

    public void damage(int damage) {
        if (damageTimeout > 0) return;
        if (Math.random() < dodgeChance) return;
        health -= damage;
        setDamageTimeout(Config.DAMAGE_TIMEOUT);
        if (health > maxHealth) setHealth(maxHealth);
        upgrades.forEach((Upgrade::onDamage));
    }

    @Override
    public void setHealth(int health) {
        this.health = health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    @Override
    public void setDamageTimeout(float timeout) {
        this.damageTimeout = timeout;
        collider.setContactCallbackFilter(PICKUP_GROUP);
    }

    @Override
    public float getDamageTimeout() {
        return damageTimeout;
    }

    public void setAttackTimeout(float timeout) {
        this.attackTimeout = timeout;
    }

    public float getAttackTimeout() {
        return attackTimeout;
    }

    public Vector3 getEulerRotation() {
        return eulerRotation;
    }

    public void setEulerRotation(Vector3 rot) {
        this.eulerRotation = rot;
        transform.getTranslation(tmp);
        transform.setFromEulerAnglesRad(rot.x, rot.y, rot.z);
        transform.setTranslation(tmp);
    }

    public void setBaseUpgrade(BaseUpgrade baseUpgrade) {
        this.baseUpgrade = baseUpgrade;
        this.setMaxHealth(baseUpgrade.MAX_HEALTH, true);
        try {
            this.upgrades.add(UpgradeRegistry.getUpgradeInstance(baseUpgrade.upgradeClass));
        } catch (Exception e) {
            Gdx.app.error("Player", "Base upgrade not found: " + baseUpgrade.name(),e);
        }

    }

    @Override
    public String toString() {
        return "Player{" +
                "baseUpgrade=" + baseUpgrade +
                ", id=" + id +
                ", grounded=" + grounded +
                ", transform=" + transform +
                ", upgrades=" + upgrades +
                ", health=" + health +
                ", maxHealth=" + maxHealth +
                '}';
    }

    @Override
    public String getModelAddress() {
        return null;
    }

    @Override
    public void destroy() {
        super.destroy();
    }


}
