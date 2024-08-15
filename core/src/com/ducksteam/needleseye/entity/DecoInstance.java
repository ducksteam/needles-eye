package com.ducksteam.needleseye.entity;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.ducksteam.needleseye.Main;
import com.ducksteam.needleseye.map.DecoTemplate;

/**
 * Represents an instance of a decoration in the world
 * @author SkySourced
 */
@Deprecated
public class DecoInstance extends Entity {

    private DecoTemplate template;

    public DecoInstance(DecoTemplate template, Vector3 pos) {
        this(template, pos, new Quaternion());
    }

    public DecoInstance(DecoTemplate template, Vector3 pos, Quaternion rotation) {
        super(pos, rotation, new ModelInstance((Model) Main.assMan.get(template.getModelPath())));
        this.template = template;
    }

    public DecoTemplate getTemplate() {
        return template;
    }

    public void setTemplate(DecoTemplate template) {
        this.template = template;
    }


    @Override
    public String toString() {
        return "DecoInstance{" +
                "template=" + template +
                ", pos=" + getPosition() +
                ", rot=" + getRotation() +
                '}';
    }

    @Override
    public String getModelAddress() {
        return template.getModelPath();
    }
}
