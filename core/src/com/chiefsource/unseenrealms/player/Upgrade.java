package com.chiefsource.unseenrealms.player;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import org.w3c.dom.Text;

public abstract class Upgrade {
    String name;
    String description;
    Texture icon;
    Model model;

    public Upgrade (String name, String description, Texture icon, Model model) {
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.model = model;
    }

    public void onPickup(){

    }

    public void onAttack(){

    }

    public void onThrow(){

    }

    public void onDamage(){

    }
}
