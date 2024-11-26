package com.ducksteam.needleseye.player.upgrades;

import com.ducksteam.needleseye.Main;
import com.ducksteam.needleseye.player.Upgrade;

/**
 * Increases damage and slightly decreases swing speed
 * @author SkySourced
 */

public class LeadUpgrade extends Upgrade {

    /**
     * Create a new lead upgrade
     */
	public LeadUpgrade() {
		super("Lead", "Add some weight to your swing", "upgrades/lead.png", "upgrades/lead.gltf");
	}

	@Override
	public void onPickup() {
		Main.player.damageBoost += 1; // Increase damage by 1
		Main.player.setAttackTimeout(Main.player.getAttackTimeout() + 0.05f); // slow down attack speed by 0.05 seconds
	}
}
