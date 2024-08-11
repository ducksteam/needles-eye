package com.ducksteam.needleseye.entity.enemies.ai;

import com.ducksteam.needleseye.entity.Entity;
import com.ducksteam.needleseye.entity.enemies.EnemyEntity;

public interface IHasAi {
	void update(float dT);

	void idle(float dT);
	void chase(float dT);
	void attack();

	void setTarget(EnemyEntity target);
	Entity getTarget();

	void setChasing(boolean chasing);
	boolean isChasing();
}
