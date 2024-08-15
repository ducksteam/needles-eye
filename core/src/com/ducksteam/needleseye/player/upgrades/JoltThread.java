package com.ducksteam.needleseye.player.upgrades;

import com.ducksteam.needleseye.player.Upgrade;

/**
 * One of the four base upgrades the player can select between
 * @author SkySourced
 */
public class JoltThread extends Upgrade {
	public JoltThread() {
		super("Jolt Thread", "Increases maximum health by 4", "ui/icons/jolt_thread.png", null, true);
	}
}