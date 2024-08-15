package com.ducksteam.needleseye.player.upgrades;

import com.ducksteam.needleseye.player.Upgrade;

/**
 * One of the four base upgrades the player can select between
 * @author SkySourced
 */
public class CoalThread extends Upgrade {
	public CoalThread() {
		super("Coal Thread", "Increases maximum health by 5", "ui/icons/coal_thread.png", null, true);
	}
}