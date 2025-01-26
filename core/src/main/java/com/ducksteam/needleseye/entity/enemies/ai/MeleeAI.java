package com.ducksteam.needleseye.entity.enemies.ai;

import com.badlogic.gdx.Gdx;
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
    float chaseAngleSpeed; // speed of the enemy when rotating towards player

    /**
     * Creates a new MeleeAI
     * @param target the entity to control
     * @param idleSpeed the speed of the enemy when idling
     * @param chaseSpeed the speed of the enemy when chasing
     */
	public MeleeAI(EnemyEntity target, float idleSpeed, float chaseSpeed, float chaseAngleSpeed) {
		setTarget(target);
		this.idleSpeed = idleSpeed;
		this.chaseSpeed = chaseSpeed;
        this.chaseAngleSpeed = chaseAngleSpeed;
	}

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
				getTarget().collider.applyCentralForce(repulsionForce); // apply force
			}
		});
	}

	@Override
	public void idle(float dT) {
		//getTarget().setAnimation("idle");
		Vector3 randomDirection = new Vector3().setToRandomDirection();
		randomDirection.y = 0;
		getTarget().collider.applyCentralForce(randomDirection.scl(idleSpeed)); // move in random direction
	}

	@Override
	public void chase(float dT) {
		//getTarget().setAnimation("walk");
        Vector3 direction = playerPos.cpy().sub(getTarget().collider.getCenterOfMassPosition());
		direction.y = 0;
        float playerAngle = calculatePlayerAngle(direction.cpy().nor().x, direction.cpy().nor().z);
        float currentAngle = getTarget().getRotation().getAngleAround(0, 1, 0);

        // rotate towards player
        if(currentAngle > playerAngle) getTarget().collider.applyTorque(new Vector3(0, (currentAngle - playerAngle < 180) ? -chaseAngleSpeed*(currentAngle-playerAngle) : chaseAngleSpeed*(360+playerAngle-currentAngle), 0));
        else getTarget().collider.applyTorque(new Vector3(0, (playerAngle - currentAngle < 180) ? chaseAngleSpeed*(playerAngle-currentAngle) : -chaseAngleSpeed*(360+currentAngle-playerAngle), 0));
        if(Math.signum(getTarget().collider.getTotalTorque().y) != Math.signum(getTarget().collider.getAngularVelocity().y)) getTarget().collider.setAngularVelocity(new Vector3(0, 0, 0)); // if rotating the wrong way, stop

        getTarget().collider.applyCentralForce(direction.nor().scl(chaseSpeed)); // move towards player
		if (playerPos.dst(getTarget().collider.getCenterOfMassPosition()) < ATTACK_RANGE) attack(); // attack if within range
	}

	@Override
	public void attack() {
		//getTarget().setAnimation("attack");
		Main.player.damage(1, getTarget());
	}

	@Override
	public void setTarget(EnemyEntity target) {
		this.target = target;
	}

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

	@Override
	public void setWindup(boolean windup) {
		Gdx.app.debug("MeleeAI", "setWindup() called on MeleeAI");
	}

	@Override
	public boolean isWindup() {
		Gdx.app.debug("MeleeAI", "isWindup() called on MeleeAI");
		return false;
	}

	@Override
	public void setIdling(boolean idling) {

	}

	@Override
	public boolean isIdling() {
		return false;
	}

	private Vector3 calculateRepulsionForce(Entity entity1, Entity entity2, float repulsionStrength) {
		Vector3 direction = entity1.collider.getCenterOfMassPosition().cpy().sub(entity2.collider.getCenterOfMassPosition());
		direction.y = 0;
		float distance = direction.len();
		if (distance == 0) return new Vector3(0, 0, 0); // Avoid division by zero
		float forceMagnitude = repulsionStrength / (distance * distance); // Inverse square law
		return direction.nor().scl(forceMagnitude);
	}

    private float calculatePlayerAngle(float x, float y) {
        if(x>0 && y>0) { // 180 - 270 degrees
            return (float) Math.toDegrees(Math.PI + Math.asin(x));
        } else if(x>0 && y<0) { // 270 - 360 degrees
            return (float) Math.toDegrees(2*Math.PI - Math.asin(x));
        } else if(x<0 && y>0) { // 90-180 degrees
            return (float) Math.toDegrees(Math.PI + Math.asin(x));
        } else { // 0-90 degrees
            return (float) -Math.toDegrees(Math.asin(x));
        }
    }
}
