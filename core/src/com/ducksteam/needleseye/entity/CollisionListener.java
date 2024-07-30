package com.ducksteam.needleseye.entity;

import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.ducksteam.needleseye.Main;
import com.ducksteam.needleseye.entity.enemies.EnemyEntity;
import com.ducksteam.needleseye.player.Player;

import java.util.ArrayList;

public class CollisionListener extends ContactListener {

	private static ArrayList<Integer> playerGroundContacts = new ArrayList<>();

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
	public boolean onContactAdded(int userValue0, int partId0, int index0, int userValue1, int partId1, int index1) {
		Entity entity0 = Main.entities.get(userValue0);
		Entity entity1 = Main.entities.get(userValue1);
		if (entity0 instanceof Player) {
			if (entity1 instanceof EnemyEntity) {
				((Player) entity0).damage(1);
			} else if (entity1 instanceof RoomInstance) {
				if (!playerGroundContacts.contains(userValue1)) {
					playerGroundContacts.add(userValue1);
				}
			}
		} else if (entity0 instanceof EnemyEntity) {

		}
		return true;
	}

	@Override
	public void onContactEnded(int userValue0, int userValue1) {
		super.onContactEnded(userValue0, userValue1);
	}
}
