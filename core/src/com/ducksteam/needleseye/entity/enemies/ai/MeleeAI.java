package com.ducksteam.needleseye.entity.enemies.ai;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.ducksteam.needleseye.Main;
import com.ducksteam.needleseye.entity.Entity;
import com.ducksteam.needleseye.entity.enemies.EnemyEntity;
import com.ducksteam.needleseye.map.MapManager;
import com.ducksteam.needleseye.player.Player;

public class MeleeAI implements IHasAi {

	Vector3 playerPos;
	float detectionRange = 5;
	float attackRange = 0.7f;
	EnemyEntity target;
	boolean chasing = false;
	float idleSpeed;
	float chaseSpeed;
	Matrix4 tmpMat = new Matrix4();
	Vector3 tmp = new Vector3();

	public MeleeAI(EnemyEntity target, float idleSpeed, float chaseSpeed) {
		setTarget(target);
		this.idleSpeed = idleSpeed;
		this.chaseSpeed = chaseSpeed;
	}

	@Override
	public void update(float dT) {
		if (getTarget() == null) return;
		playerPos = Main.player.getPosition();
		setChasing(playerPos.dst(getTarget().getPosition()) < detectionRange/* && MapManager.getRoomSpacePos(getTarget().getPosition()).equals(MapManager.getRoomSpacePos(playerPos))*/);
		if (isChasing()) chase(dT);
		else idle(dT);

		Main.entities.forEach((Integer id, Entity entity) -> {
			if (entity instanceof EnemyEntity || entity instanceof Player && id != getTarget().id) {
				Vector3 repulsionForce = calculateRepulsionForce(getTarget(), entity, 1);
				getTarget().collider.applyCentralImpulse(repulsionForce.scl(dT));
			}
		});

		getTarget().motionState.getWorldTransform(tmpMat);
		tmpMat.rotateTowardTarget(playerPos, Vector3.Y);
		getTarget().motionState.setWorldTransform(tmpMat);
	}

	@Override
	public void idle(float dT) {
		getTarget().setAnimation("idle");
		Vector3 randomDirection = new Vector3().setToRandomDirection();
		randomDirection.y = 0;
		getTarget().collider.applyCentralImpulse(randomDirection.scl(idleSpeed * dT));
	}

	@Override
	public void chase(float dT) {
		getTarget().setAnimation("walk");
		Vector3 direction = playerPos.cpy().sub(getTarget().getPosition());
		direction.y = 0;
		getTarget().collider.applyCentralImpulse(direction.nor().scl(chaseSpeed * dT));
		if (playerPos.dst(getTarget().getPosition()) < attackRange) attack();
	}

	@Override
	public void attack() {
		getTarget().setAnimation("attack");
		Main.player.damage(1);
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

	private Vector3 calculateRepulsionForce(Entity entity1, Entity entity2, float repulsionStrength) {
		Vector3 direction = entity1.getPosition().cpy().sub(entity2.getPosition());
		direction.y = 0;
		float distance = direction.len();
		if (distance == 0) return new Vector3(0, 0, 0); // Avoid division by zero
		float forceMagnitude = repulsionStrength / (distance * distance); // Inverse square law
		return direction.nor().scl(forceMagnitude);
	}}
