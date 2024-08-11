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
	 * Called when two objects (one of which having the <code>CF_CUSTOM_MATERIAL_CALLBACK</code> flag) collide, specifically before any physics is applied.
	 *
	 * @param userValue0 the integer key in <code>Main.entities</code> for the first object
	 * @param partId0    the part id of the first object (no idea, it's just in the constructor)
	 * @param index0     the index of the first object (similar)
	 * @param userValue1 the integer key in <code>Main.entities</code> for the second object
	 * @param partId1    the part id of the second object
	 * @param index1     the index of the second object
	 * @return some boolean? this part is not documented well. assuming it's like an input processor, true means it's handled here
	 */

	@Override
	public boolean onContactAdded(int userValue0, int partId0, int index0, boolean match0, int userValue1, int partId1, int index1, boolean match1) {
		Entity entity0 = Main.entities.get(userValue0);
		Entity entity1 = Main.entities.get(userValue1);
		if (entity0 == null || entity1 == null) return false;
		Gdx.app.debug("CollisionListener", "Contact added between " + entity0.getClass().getSimpleName() + " " + entity0.id + " cf " + match0 + " and " + entity1.getClass().getSimpleName() + " " + entity1.id + " cf " + match1);

		if (entity1 instanceof Player) onContactAdded(userValue1, partId1, index1, match1, userValue0, partId0, index0, match0); // flip for convenience

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
		} else if (entity0 instanceof EnemyEntity) {

		}
		return true;
	}

	@Override
	public void onContactEnded(int userValue0, boolean match0, int userValue1, boolean match1) {
		Entity entity0 = Main.entities.get(userValue0);
		Entity entity1 = Main.entities.get(userValue1);
		if (entity0 == null || entity1 == null) return;
		Gdx.app.debug("CollisionListener", "Contact ended between " + entity0.getClass().getSimpleName() + " " + entity0.id + " cf " + match0 + " and " + entity1.getClass().getSimpleName() + " " + entity1.id + " cf " + match1);
	}
}
