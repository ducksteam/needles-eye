package com.ducksteam.needleseye.entity.bullet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.ducksteam.needleseye.Main;
import com.ducksteam.needleseye.entity.Entity;
import com.ducksteam.needleseye.entity.RoomInstance;
import com.ducksteam.needleseye.entity.enemies.EnemyEntity;
import com.ducksteam.needleseye.entity.pickups.UpgradeEntity;
import com.ducksteam.needleseye.player.Player;
import com.ducksteam.needleseye.player.Upgrade;

import java.util.ArrayList;

public class CollisionListener extends ContactListener {

	public static final ArrayList<Integer> playerGroundContacts = new ArrayList<>();

	/**
	 * Called when two objects (one of which having the <code>CF_CUSTOM_MATERIAL_CALLBACK</code> flag) collide.
	 *
	 * @param userValue0 the integer key in <code>Main.entities</code> for the first object
	 * @param match0     whether the first entity's customCallbackFilter is met by this collision
	 * @param userValue1 the integer key in <code>Main.entities</code> for the second object
	 * @param match1     whether the second entity's customCallbackFilter is met by this collision
	 */

	@Override
	public void onContactStarted(int userValue0, boolean match0, int userValue1, boolean match1) {
		Entity entity0 = Main.entities.get(userValue0);
		Entity entity1 = Main.entities.get(userValue1);
		if (entity0 == null || entity1 == null) return;
		Gdx.app.debug("CollisionListener", "Contact added between " + entity0.getClass().getSimpleName() + " " + entity0.id + " cf " + match0 + " and " + entity1.getClass().getSimpleName() + " " + entity1.id + " cf " + match1);

		if (entity1 instanceof Player) onContactStarted(userValue1, match1, userValue0, match0); // flip for convenience

		if (entity0 instanceof Player) {
			if (entity1 instanceof EnemyEntity) {
				((Player) entity0).damage(((EnemyEntity) entity1).getContactDamage());
			} else if (entity1 instanceof RoomInstance) {
				if (!playerGroundContacts.contains(userValue1)) {
					playerGroundContacts.add(userValue1);
				}
			} else if (entity1 instanceof UpgradeEntity) {
				entity1.collider.setContactCallbackFilter(0);
				Upgrade pickup = ((UpgradeEntity) entity1).getUpgrade();
				((Player) entity0).upgrades.add(pickup);
				((UpgradeEntity) entity1).getUpgrade().onPickup();
				entity1.destroy();
			}
		}
	}

	/**
	 * Called when two objects (one of which having the <code>CF_CUSTOM_MATERIAL_CALLBACK</code> flag) stop colliding.
	 *
	 * @param userValue0 the integer key in <code>Main.entities</code> for the first object
	 * @param match0     whether the first entity's customCallbackFilter is met by this collision
	 * @param userValue1 the integer key in <code>Main.entities</code> for the second object
	 * @param match1     whether the second entity's customCallbackFilter is met by this collision
	 */
	@Override
	public void onContactEnded(int userValue0, boolean match0, int userValue1, boolean match1) {
		Entity entity0 = Main.entities.get(userValue0);
		Entity entity1 = Main.entities.get(userValue1);
		if (entity0 == null || entity1 == null) return;
		Gdx.app.debug("CollisionListener", "Contact ended between " + entity0.getClass().getSimpleName() + " " + entity0.id + " cf " + match0 + " and " + entity1.getClass().getSimpleName() + " " + entity1.id + " cf " + match1);
	}
}
