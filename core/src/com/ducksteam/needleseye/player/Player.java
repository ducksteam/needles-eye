package com.ducksteam.needleseye.player;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.ducksteam.needleseye.Config;
import com.ducksteam.needleseye.Main;
import com.ducksteam.needleseye.entity.Entity;
import com.ducksteam.needleseye.entity.MotionState;
import com.ducksteam.needleseye.player.Upgrade.BaseUpgrade;

import java.util.ArrayList;

import static com.ducksteam.needleseye.Main.*;

/**
 * Represents the player in the game
 * @author SkySourced
 */
public class Player extends Entity {
    public BaseUpgrade baseUpgrade;

    ArrayList<Upgrade> upgrades;

    int health;
    int maxHealth;

    public Vector3 eulerRotation; // rads

    Vector3 tmp = new Vector3();

    public Player(Vector3 pos) {
        super(pos, new Quaternion().setEulerAngles(0, 0, 0), Config.PLAYER_MASS, null, Entity.PLAYER_GROUP);
        baseUpgrade = BaseUpgrade.NONE;

        eulerRotation = new Vector3(0,0,1);

        Main.dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, broadphase, constraintSolver, collisionConfig);
        Main.dynamicsWorld.setGravity(new Vector3(0, -10f, 0));

        collisionShape = new btBoxShape(new Vector3(0.25F, 0.5F, 0.25F));
        motionState = new MotionState(this);
        Vector3 inertia = new Vector3();
        collisionShape.calculateLocalInertia(Config.PLAYER_MASS, inertia);
        collider = new btRigidBody(Config.PLAYER_MASS, motionState, collisionShape, inertia);
        collider.setActivationState(Collision.DISABLE_DEACTIVATION);
        collider.setDamping(0.8f, 0.8f);

        Main.dynamicsWorld.addRigidBody(collider);

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
    }

    @Override
    public String getModelAddress() {
        return null;
    }
}
