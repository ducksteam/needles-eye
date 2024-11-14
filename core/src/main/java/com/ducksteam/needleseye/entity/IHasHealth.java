package com.ducksteam.needleseye.entity;

import com.badlogic.gdx.math.Vector3;

/**
 * An interface for entities that have health
 */
public interface IHasHealth {
	void update(float delta);
	void damage(int damage, Entity source);
	float getParalyseTime();
	void setParalyseTime(float paralyseTime);
	void setHealth(int health);
	int getHealth();
	void setMaxHealth(int maxHealth, boolean heal);
	int getMaxHealth();
	void setDamageTimeout(float timeout);
	float getDamageTimeout();
	Vector3 getPosition();
}
