package com.waddleworks.unseenrealms.player.upgrades;

import com.badlogic.gdx.graphics.Texture;
import com.waddleworks.unseenrealms.Main;
import com.waddleworks.unseenrealms.player.Upgrade;

/**
 * An upgrade that grants a 20% chance to dodge any damage & increases speed by 10%
 */
public class MercuryCharm extends Upgrade {

    final float dodgeChance = 0.2f;
    final float speedIncrease = 1.1f;

    public MercuryCharm(String name, String description, Texture icon, String modelAddress) {
        super("Mercury", "Speed up, dodge up", icon, modelAddress);
    }

    @Override
    public void onPickup() {
        Main.player.setSpeed(Main.player.getSpeed() * speedIncrease);
    }

    @Override
    public void onDamage(int damage) {
        if (Math.random() <= dodgeChance){
            Main.player.setHealth(Main.player.getHealth() + damage);
        }
    }
}
