package com.ducksteam.needleseye.player.upgrades;

import com.ducksteam.needleseye.Main;
import com.ducksteam.needleseye.player.Upgrade;

/**
 * Gain dodge chance and a speed boost
 * @author SkySourced
 */

public class MercuryUpgrade extends Upgrade {
	public MercuryUpgrade() {
		super("Mercury", "Your hands become numb", "ui/icons/mercury.png", "models/upgrades/mercury.gltf");
	}

	@Override
	public void onPickup() {
		Main.player.playerSpeedMultiplier += 0.1f; // Increase speed by 0.1x
		Main.player.dodgeChance += 0.1f; // Increase dodge chance by 10%
	}
}
