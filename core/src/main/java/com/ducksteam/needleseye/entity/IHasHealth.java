package com.ducksteam.needleseye.entity;

import com.badlogic.gdx.math.Vector3;

/**
 * An interface for entities that have health
 */
public interface IHasHealth {
    /**
     * Update the entity
     * @param delta the time since the last update in seconds
     */
	void update(float delta);
    /**
     * Damage the entity
     * @param damage the amount of damage to deal
     * @param source the entity that dealt the damage
     */
	void damage(int damage, Entity source);
    /**
     * Gets the amount of time left this entity is paralysed for
     * @return the time left of paralysis
     */
	float getParalyseTime();
    /**
     * Sets the time the entity is paralysed for
     * @param paralyseTime the time to set
     */
	void setParalyseTime(float paralyseTime);
    /**
     * Sets the health of the entity
     * @param health the health to set
     */
	void setHealth(int health);
    /**
     * Gets the health of the entity
     * @return the health of the entity
     */
	int getHealth();
    /**
     * Sets the maximum health of the entity
     * @param maxHealth the maximum health to set
     * @param heal whether to heal the entity to the new maximum health
     */
	void setMaxHealth(int maxHealth, boolean heal);
    /**
     * Gets the maximum health of the entity
     * @return the maximum health of the entity
     */
	int getMaxHealth();
    /**
     * Sets the time since the entity was last damaged
     * @param timeout the time to set
     */
	void setDamageTimeout(float timeout);
    /**
     * Gets the time since the entity was last damaged
     * @return the time since the entity was last damaged
     */
	float getDamageTimeout();
    /**
     * Gets the position of the entity
     * @return the position of the entity
     */
	Vector3 getPosition();
}
