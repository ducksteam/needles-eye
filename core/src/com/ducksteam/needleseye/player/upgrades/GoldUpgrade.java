package com.ducksteam.needleseye.player.upgrades;

import com.ducksteam.needleseye.Main;
import com.ducksteam.needleseye.player.Upgrade;

/**
 * Gain dodge chance and a speed boost
 * @author SkySourced
 */

public class GoldUpgrade extends Upgrade {
	public GoldUpgrade() {
		super("Gold", "The nugget warms in your pocket", "ui/icons/gold.png", "models/upgrades/gold.gltf");
	}

	@Override
	public void onPickup() {
		Main.player.setMaxHealth(Main.player.getMaxHealth() + 1, true);
	}
}
