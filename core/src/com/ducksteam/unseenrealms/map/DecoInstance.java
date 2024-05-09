package com.ducksteam.unseenrealms.map;

import com.badlogic.gdx.math.Vector3;

public class DecoInstance {

    private DecoTemplate template;
    private Vector3 pos;
    private Vector3 scale;

    public DecoInstance(DecoTemplate template, Vector3 pos, Vector3 scale) {
        this.template = template;
        this.pos = pos;
        this.scale = scale;
    }

    public DecoInstance(){}

    public DecoTemplate getTemplate() {
        return template;
    }

    public Vector3 getPos() {
        return pos;
    }

    public Vector3 getScale() {
        return scale;
    }

    public void setTemplate(DecoTemplate template) {
        this.template = template;
    }

    public void setPos(Vector3 pos) {
        this.pos = pos;
    }

    public void setScale(Vector3 scale) {
        this.scale = scale;
    }

    @Override
    public String toString() {
        return "DecoInstance{" +
                "template=" + template +
                ", pos=" + pos +
                ", scale=" + scale +
                '}';
    }
}
