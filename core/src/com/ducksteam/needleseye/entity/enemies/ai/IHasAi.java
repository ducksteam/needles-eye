package com.ducksteam.needleseye.entity.enemies.ai;

import com.ducksteam.needleseye.entity.Entity;

public interface IHasAi {
	void update(float dT);

	void idle(float dT);
	void chase(float dT);
	void attack();

	void setTarget(Entity target);
	Entity getTarget();

	void setChasing(boolean chasing);
	boolean isChasing();
}
