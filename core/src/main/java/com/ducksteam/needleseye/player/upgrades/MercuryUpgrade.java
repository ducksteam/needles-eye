package com.ducksteam.needleseye.player.upgrades;

import com.ducksteam.needleseye.Main;
import com.ducksteam.needleseye.player.Upgrade;

/**
 * Gain dodge chance and a speed boost
 * @author SkySourced
 */

public class MercuryUpgrade extends Upgrade {

    /**
    * Create a new mercury upgrade
    */
	public MercuryUpgrade() {
		super("Mercury", "Your hands become numb", "upgrades/mercury.png", "upgrades/mercury.gltf");
	}

	@Override
	public void onPickup() {
		Main.player.playerSpeedMultiplier += 0.1f; // Increase speed by 0.1x
		Main.player.dodgeChance += 0.1f; // Increase dodge chance by 10%
	}
}
