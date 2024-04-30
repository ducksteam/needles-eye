package com.chiefsource.unseenrealms.player.upgrades;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.chiefsource.unseenrealms.Main;
import com.chiefsource.unseenrealms.player.Upgrade;

/**
 * An upgrade that grants a 20% chance to dodge any damage
 */
public class LuckyCharm extends Upgrade {

    public LuckyCharm(String name, String description, Texture icon, Model model) {
        super("Lucky Charm", "Gives a small chance to dodge when attacked", icon, model);
    }

    @Override
    public void onDamage() {
        if (Math.random() <= 0.2){
            Main.player.setHealth(Main.player.getHealth()+1);
        }
    }
}
