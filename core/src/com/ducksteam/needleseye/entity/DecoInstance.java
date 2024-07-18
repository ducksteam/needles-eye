package com.ducksteam.needleseye.entity;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.ducksteam.needleseye.map.DecoTemplate;

/**
 * Represents an instance of a decoration in the world
 * @author SkySourced
 */
public class DecoInstance extends WorldObject {

    private DecoTemplate template;
    private Vector3 scale;

    public DecoInstance(DecoTemplate template, Vector3 pos, Vector3 scale, Quaternion rotation) {
        super(pos, rotation, scale);
        this.template = template;
        this.scale = scale;
    }

    public DecoInstance() {
        this(null, new Vector3(), new Vector3(), new Quaternion());
    }

    public DecoTemplate getTemplate() {
        return template;
    }
    public Vector3 getScale() {
        return scale;
    }

    public void setTemplate(DecoTemplate template) {
        this.template = template;
    }

    public void setScale(Vector3 scale) {
        this.scale = scale;
    }

    @Override
    public String toString() {
        return "DecoInstance{" +
                "template=" + template +
                ", pos=" + getPosition() +
                ", rot=" + getRotation() +
                ", scale=" + scale +
                '}';
    }

    @Override
    public String getModelAddress() {
        return template.getModelPath();
    }
}
