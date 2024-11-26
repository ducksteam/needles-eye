package com.ducksteam.needleseye.entity.enemies.ai;

import com.ducksteam.needleseye.entity.enemies.EnemyEntity;

/**
 * An interface containing methods for updating an AI algorithm
 * @author SkySourced
 */
public interface IHasAi {
    /**
     * Update the AI, changing the behavioural state and possibly initiating an attack
     * @param dT the time since the last update in seconds
     */
	void update(float dT);

    /**
     * Perform idle behaviour
     * @param dT the time since the last update in seconds
     */
	void idle(float dT);
    /**
     * Perform chase behaviour
     * @param dT the time since the last update in seconds
     */
	void chase(float dT);
    /**
     * Attack the target
     */
	void attack();

    /**
     * Set the entity that this AI controls
     * @param target the entity to set
     */
	void setTarget(EnemyEntity target);
    /**
     * Get the entity that this AI controls
     * @return the entity that this AI controls
     */
	EnemyEntity getTarget();

    /**
     * Update the state of the AI, used for updating state machines
     * @param chasing whether the AI is chasing the target
     */
	void setChasing(boolean chasing);
    /**
     * Check if the AI is chasing the target
     * @return true if the AI is chasing the target
     */
	boolean isChasing();

    /**
     * Set the windup state of the AI, used for updating state machines
     * @param windup whether the AI is winding up an attack
     */
	void setWindup(boolean windup);
    /**
     * Check if the AI is winding up an attack
     * @return true if the AI is winding up an attack
     */
	boolean isWindup();

    /**
     * Set the idling state of the AI, used for updating state machines
     * @param idling whether the AI is idling
     */
	void setIdling(boolean idling);

    /**
     * Check if the AI is idling
     * @return true if the AI is idling
     */
	boolean isIdling();
}
