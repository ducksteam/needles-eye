package com.ducksteam.needleseye.player.upgrades;

import com.ducksteam.needleseye.Main;
import com.ducksteam.needleseye.player.Upgrade;

/**
 * Increases damage and slightly decreases swing speed
 * @author SkySourced
 */

public class LeadUpgrade extends Upgrade {

	public LeadUpgrade() {
		super("Lead", "Add some weight to your swing", "ui/icons/lead.png", "models/upgrades/lead.gltf");
	}

	@Override
	public void onPickup() {
		Main.player.damageBoost += 1;
		Main.player.setAttackTimeout(Main.player.getAttackTimeout() + 0.05f);
	}
}
