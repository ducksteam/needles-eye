package com.ducksteam.needleseye.entity.enemies.ai;

import com.badlogic.gdx.math.Vector3;
import com.ducksteam.needleseye.Main;
import com.ducksteam.needleseye.entity.Entity;
import com.ducksteam.needleseye.map.MapManager;

public class MeleeAI implements IHasAi {

	Vector3 playerPos;
	float detectionRange = 10;
	float attackRange = 1;
	Entity target;
	boolean chasing = false;
	float moveSpeed;

	public MeleeAI(Entity target, float moveSpeed) {
		setTarget(target);
		this.moveSpeed = moveSpeed;
	}

	@Override
	public void update(float dT) {
		if (getTarget() == null) return;
		playerPos = Main.player.getPosition();
		setChasing(playerPos.dst(getTarget().getPosition()) < detectionRange && MapManager.getRoomSpacePos(getTarget().getPosition()).equals(MapManager.getRoomSpacePos(playerPos)));
		if (isChasing()) chase(dT);
		else idle(dT);
	}

	@Override
	public void idle(float dT) {
		getTarget().setAnimation("idle");
		Vector3 randomDirection = new Vector3().setToRandomDirection();
		randomDirection.y = 0;
		getTarget().collider.applyCentralImpulse(randomDirection.scl(moveSpeed * dT));
	}

	@Override
	public void chase(float dT) {
		getTarget().setAnimation("walk");
		Vector3 direction = playerPos.cpy().sub(getTarget().getPosition());
		direction.y = 0;
		getTarget().collider.applyCentralImpulse(direction.nor().scl(moveSpeed * dT));
		if (playerPos.dst(getTarget().getPosition()) < attackRange) attack();
	}

	@Override
	public void attack() {
		getTarget().setAnimation("attack");
		Main.player.damage(1);
	}

	@Override
	public void setTarget(Entity target) {
		this.target = target;
	}

	@Override
	public Entity getTarget() {
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
}
