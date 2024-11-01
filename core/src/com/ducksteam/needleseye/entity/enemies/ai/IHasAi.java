package com.ducksteam.needleseye.entity.enemies.ai;

import com.ducksteam.needleseye.entity.Entity;
import com.ducksteam.needleseye.entity.enemies.EnemyEntity;

/**
 * An interface containing methods for updating an AI algorithm
 * @author SkySourced
 */
public interface IHasAi {
	void update(float dT);

	void idle(float dT);
	void chase(float dT);
	void attack();

	void setTarget(EnemyEntity target);
	Entity getTarget();

	void setChasing(boolean chasing);
	boolean isChasing();

	void setWindup(boolean windup);
	boolean isWindup();

	void setIdling(boolean idling);
	boolean isIdling();
}
