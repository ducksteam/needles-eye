package com.ducksteam.needleseye.player.upgrades;

import com.ducksteam.needleseye.Main;
import com.ducksteam.needleseye.player.Upgrade;

/**
 * Gain 1 max health
 * @author SkySourced
 */

public class GoldUpgrade extends Upgrade {
	public GoldUpgrade() {
		super("Gold", "The nugget warms in your pocket", "upgrades/gold.png", "upgrades/gold.gltf");
	}

	@Override
	public void onPickup() {
		Main.player.setMaxHealth(Main.player.getMaxHealth() + 1, true);
	}
}
