package com.ducksteam.needleseye.entity.enemies.ai;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.ducksteam.needleseye.Main;
import com.ducksteam.needleseye.entity.Entity;
import com.ducksteam.needleseye.entity.enemies.EnemyEntity;
import com.ducksteam.needleseye.player.Player;

/**
 * An AI algorithm for melee enemies that chases the player and attacks when in range
 */
public class MeleeAI implements IHasAi {

	Vector3 playerPos; // position of the player
	static final float DETECTION_RANGE = 5; // range of detection
	static final float ATTACK_RANGE = 0.7f; // range of attack
	EnemyEntity target; // the enemy entity this AI is controlling
	boolean chasing = false; // whether the enemy is chasing the player
	float idleSpeed; // speed of the enemy when idling
	float chaseSpeed; // speed of the enemy when chasing

	// temporary variables for calculation
	Matrix4 tmpMat = new Matrix4();

	public MeleeAI(EnemyEntity target, float idleSpeed, float chaseSpeed) {
		setTarget(target);
		this.idleSpeed = idleSpeed;
		this.chaseSpeed = chaseSpeed;
	}

	/**
	 * Updates the AI algorithm
	 * @param dT the time since the last update
	 */
	@Override
	public void update(float dT) {
		if (getTarget() == null) return; // ensure target exists
		playerPos = Main.player.getPosition(); // update player position
		setChasing(playerPos.dst(getTarget().getPosition()) < DETECTION_RANGE); // update chasing status
		if (isChasing()) chase(dT); // run corresponding method
		else idle(dT);

		Main.entities.forEach((Integer id, Entity entity) -> { // repel from other enemies
			if (entity instanceof EnemyEntity || entity instanceof Player && id != getTarget().id) { // push moving entities away from each other
				Vector3 repulsionForce = calculateRepulsionForce(getTarget(), entity, 1);
				getTarget().collider.applyCentralImpulse(repulsionForce.scl(dT)); // apply force
			}
		});

		// rotate towards player
		/*getTarget().motionState.getWorldTransform(tmpMat);
		tmpMat.rotateTowardTarget(playerPos, Vector3.Y);
		getTarget().motionState.setWorldTransform(tmpMat);
		*/
	}

	/**
	 * Idle behaviour
	 * @param dT the time since the last update
	 */
	@Override
	public void idle(float dT) {
		getTarget().setAnimation("idle");
		Vector3 randomDirection = new Vector3().setToRandomDirection();
		randomDirection.y = 0;
		getTarget().collider.applyCentralImpulse(randomDirection.scl(idleSpeed * dT)); // move in random direction
	}

	/**
	 * Chase behaviour
	 * @param dT the time since the last update
	 */
	@Override
	public void chase(float dT) {
		getTarget().setAnimation("walk");
		Vector3 direction = playerPos.cpy().sub(getTarget().getPosition());
		direction.y = 0;
		getTarget().collider.applyCentralImpulse(direction.nor().scl(chaseSpeed * dT));// move in a random direction
		if (playerPos.dst(getTarget().getPosition()) < ATTACK_RANGE) attack(); // attack if within range
	}

	/**
	 * Attack behaviour
	 */
	@Override
	public void attack() {
		getTarget().setAnimation("attack");
		Main.player.damage(1);
	}

	/**
	 * Sets the target of the AI
	 * @param target the target to set
	 */
	@Override
	public void setTarget(EnemyEntity target) {
		this.target = target;
	}

	/**
	 * Gets the target of the AI
	 * @return the target
	 */
	@Override
	public EnemyEntity getTarget() {
		return target;
	}

	@Override
	public void setChasing(boolean chasing) {
		this.chasing = chasing;
	}

	@Override
	public boolean isChasing() {
		return chasing;
	}

	private Vector3 calculateRepulsionForce(Entity entity1, Entity entity2, float repulsionStrength) {
		Vector3 direction = entity1.getPosition().cpy().sub(entity2.getPosition());
		direction.y = 0;
		float distance = direction.len();
		if (distance == 0) return new Vector3(0, 0, 0); // Avoid division by zero
		float forceMagnitude = repulsionStrength / (distance * distance); // Inverse square law
		return direction.nor().scl(forceMagnitude);
	}}
