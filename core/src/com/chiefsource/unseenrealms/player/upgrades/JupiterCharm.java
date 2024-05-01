package com.chiefsource.unseenrealms.player.upgrades;

import com.badlogic.gdx.graphics.Texture;
import com.chiefsource.unseenrealms.Main;
import com.chiefsource.unseenrealms.player.Upgrade;

/**
 * An upgrade that increases max life by 3 and decreases speed by 20%
 */
public class JupiterCharm extends Upgrade {

    final int maxLifeIncrease = 3;
    final float speedDecrease = 0.8f;

    public JupiterCharm(String name, String description, Texture icon, String modelAddress) {
        super("Jupiter", "Life up, speed down", icon, modelAddress);
    }

    @Override
    public void onPickup() {
        Main.player.setSpeed(Main.player.getSpeed() * speedDecrease);
        Main.player.setMaxHealth(Main.player.getMaxHealth() + maxLifeIncrease, true);
    }
}
