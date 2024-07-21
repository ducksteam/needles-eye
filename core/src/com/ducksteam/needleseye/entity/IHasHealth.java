package com.ducksteam.needleseye.entity;

public interface IHasHealth {
	void update(float delta);
	void damage(int damage);
	void setHealth(int health);
	int getHealth();
	void setMaxHealth(int maxHealth, boolean heal);
	int getMaxHealth();
	void setDamageTimeout(float timeout);
	float getDamageTimeout();
}
