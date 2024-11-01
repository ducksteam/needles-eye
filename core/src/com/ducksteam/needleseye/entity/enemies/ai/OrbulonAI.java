package com.ducksteam.needleseye.entity.enemies.ai;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.ducksteam.needleseye.Main;
import com.ducksteam.needleseye.entity.Entity;
import com.ducksteam.needleseye.entity.enemies.EnemyEntity;

public class OrbulonAI implements IHasAi {

	Vector3 playerPos; // position of the player
	float currentAngle;

	static final float DETECTION_RANGE = 8; // range of detection
	static final float ROTATION_SPEED = 25f; // speed of rotation (deg s^-1)
	static final float ATTACK_ACCURACY = 2; // accuracy of attack (deg)

	EnemyEntity target; // the enemy entity this AI is controlling
	boolean chasing = false; // whether the enemy is chasing the player
	boolean windup = false; // whether the enemy is winding up an attack
	boolean idling = false; // if the idle animation is playing
	float idleTargetAngle = 0;

	// temporary variables for calculation
	Matrix4 tmpMat = new Matrix4();
	Quaternion tmpQuat = new Quaternion();
	Vector3 tmpVec = new Vector3();

	public OrbulonAI(EnemyEntity target) {
		setTarget(target);
	}

	/**
	 * Updates the AI algorithm
	 * @param dT the time since the last update
	 */
	@Override
	public void update(float dT) {
		if (getTarget() == null) return; // ensure target exists
		playerPos = Main.player.getPosition(); // update player position
		currentAngle = getTarget().getRotation().nor().getYaw(); // get the current angle of the target

		if (isWindup()) return; // if winding up, do nothing

		setChasing(playerPos.dst(getTarget().getPosition()) < DETECTION_RANGE); // update chasing status
		if (isChasing()) chase(dT); // run corresponding method
		else idle(dT);
	}

	/**
	 * Idle behaviour
	 * @param dT the time since the last update
	 */
	@Override
	public void idle(float dT) {
		if (Math.abs(idleTargetAngle - currentAngle) < 0.2 && !isIdling()){
			switch ((int) (Math.random() * 3)) {
				case 0:
					idleTargetAngle = (float) ((Math.random() - 0.5f) * 360);
					break;
				case 1:
					getTarget().setAnimation("idle1", 0);
					setIdling(true);
					break;
				case 2:
					getTarget().setAnimation("idle2", 0);
					setIdling(true);
					break;
			}
		}
	}

	@Override
	public void chase(float dT) {
		getTarget().motionState.getWorldTransform(tmpMat); // get the world transform of the target
		tmpMat.getRotation(tmpQuat); // get the rotation of the target
		tmpMat.getTranslation(tmpVec); // get the translation of the target
		float xDst = tmpVec.x - playerPos.x; // get the x distance between the player and the target
		float zDst = tmpVec.z - playerPos.z; // get the z distance between the player and the target
		float playerAngle = (float) Math.toDegrees(Math.atan2(xDst, zDst)); // calculate the angle between the player and the target

		if (Math.abs(Math.min(360 - Math.abs(playerAngle - currentAngle), Math.abs(playerAngle - currentAngle))) < ATTACK_ACCURACY) windup();
		else rotateToAngle(playerAngle, dT); // rotate the target towards the player

	}

	public void windup() {
		setWindup(true);
		getTarget().blendAnimation("windup", 1, 0.2f);
	}

	@Override
	public void attack() {
		getTarget().setAnimation("shoot", 1);
	}

	private void rotateToAngle(float angle, float dT) {
		while (angle < -180) angle += 360; // ensure angle is greater than -180
		while (angle > 180) angle -= 360; // ensure angle is less than 180

		float difference = angle - currentAngle; // calculate the difference between the angles
		if (difference > 180) difference -= 360; // ensure the difference is the shortest path
		if (difference < -180) difference += 360; // ensure the difference is the shortest path
		Gdx.app.debug("OrbulonAI", "target: " + angle + ", current: " + currentAngle + ", difference: " + difference);

		getTarget().motionState.getWorldTransform(tmpMat); // get the world transform of the target


		if (difference > 0) tmpMat.rotate(Vector3.Y, ROTATION_SPEED * dT); // rotate the target towards the player
		else tmpMat.rotate(Vector3.Y, -ROTATION_SPEED * dT); // rotate the target towards the player

		getTarget().transform.set(tmpMat); // set the transform of the target
	}

	@Override
	public void setTarget(EnemyEntity target) {
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

	public void setWindup(boolean windup) {
		this.windup = windup;
	}

	public boolean isWindup() {
		return windup;
	}

	public void setIdling(boolean idling) {
		this.idling = idling;
	}

	public boolean isIdling() {
		return idling;
	}
}
