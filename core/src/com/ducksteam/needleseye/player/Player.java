package com.ducksteam.needleseye.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Matrix4;
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
import com.ducksteam.needleseye.entity.EntityMotionState;
import com.ducksteam.needleseye.entity.GenericMotionState;
import com.ducksteam.needleseye.entity.enemies.EnemyEntity;
import com.ducksteam.needleseye.player.Upgrade.BaseUpgrade;

import java.util.ArrayList;
import java.util.Collection;

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

    public static float attackConeRadius = 0.6f;
    public static float attackConeHeight = 1.5f;
    public static btCollisionShape attackConeShape = new btConeShape(attackConeRadius, attackConeHeight);
    public static Matrix4 conePosition = new Matrix4();

    float damageTimeout = 0;
    float attackTimeout = 0;
    float attackLength = 0.2f;

    public Vector3 eulerRotation; // rads

    Vector3 tmp = new Vector3();

    public Player(Vector3 pos) {
        super(pos, new Quaternion().setEulerAngles(0, 0, 0), Config.PLAYER_MASS, null, Entity.PLAYER_GROUP);
        baseUpgrade = BaseUpgrade.NONE;

        eulerRotation = new Vector3(0,0,1);

        Main.dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, broadphase, constraintSolver, collisionConfig);
        Main.dynamicsWorld.setGravity(new Vector3(0, -10f, 0));
        Main.dynamicsWorld.setDebugDrawer(debugDrawer);

        collisionShape = new btBoxShape(new Vector3(0.25F, 0.5F, 0.25F));
        motionState = new EntityMotionState(this);
        Vector3 inertia = new Vector3();
        collisionShape.calculateLocalInertia(Config.PLAYER_MASS, inertia);
        collider = new btRigidBody(Config.PLAYER_MASS, motionState, collisionShape, inertia);
        collider.setActivationState(Collision.DISABLE_DEACTIVATION);
        collider.setDamping(0.7f, 0.7f);
        collider.setAngularFactor(Vector3.Y);

        Main.dynamicsWorld.addRigidBody(collider);

        this.upgrades = new ArrayList<>();
        health = -1;
        maxHealth = -1;
    }

    public void update(float delta) {
        if (health == -1) health = maxHealth = baseUpgrade.MAX_HEALTH;
        if (maxHealth == -1) maxHealth = baseUpgrade.MAX_HEALTH;
        if (getDamageTimeout() > 0) damageTimeout -= delta;
        if (attackTimeout > 0) attackTimeout -= delta;
    }

    public void primaryAttack() {
        if (baseUpgrade == BaseUpgrade.NONE) return;
        setAttackTimeout(attackLength);
        player.whipAttack(3);
        /*switch (baseUpgrade) {
            case SOUL_THREAD -> {
                player.whipAttack(3);
            }
            case COAL_THREAD -> {
//                player.whipAttack();
            }
            case JOLT_THREAD -> {
                player.whipAttack(3, (Entity target) -> {
                    boolean freeze = Math.random() < 0.2;
                    if (freeze) target.freeze(2);
                });
            }
            case THREADED_ROD -> {

            }
        }*/
    }

//    public void secondaryAttack() {
//        if (baseUpgrade == BaseUpgrade.NONE) return;
//        baseUpgrade.secondaryAttack();
//    }

    public void whipAttack(int damage){
        whipAttack(damage, null);
    }

    public void whipAttack(int damage, EntityRunnable enemyLogic) {
        if (attackTimeout > 0) return;
        if (mapMan.getCurrentLevel().getRoom(getRoomSpacePos(player.getPosition())) == null) return;
        player.motionState.getWorldTransform(conePosition);
        conePosition.translate(0, 0.6f, 0);
        btCollisionObject attackCone = new btRigidBody(0, new GenericMotionState(conePosition), attackConeShape, Vector3.Zero);
        Collection<? extends Entity> activeEntities = mapMan.getCurrentLevel().getRoom(getRoomSpacePos(player.getPosition())).getEnemies().values();
        Entity.checkCollision(attackCone, (ArrayList<Entity>) activeEntities, (Entity target) -> {
            if (target instanceof EnemyEntity) {
                ((IHasHealth) target).damage(damage);
                if (enemyLogic != null) enemyLogic.run(target);
            }
        });
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
        health -= damage;
        setDamageTimeout(Config.DAMAGE_TIMEOUT);
        if (health <= 0) Main.setGameState(Main.GameState.DEAD_MENU);
        if (health > maxHealth) setHealth(maxHealth);
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
    public String getModelAddress() {
        return null;
    }
}
