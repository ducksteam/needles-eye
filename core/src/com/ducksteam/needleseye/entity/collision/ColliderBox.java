package com.ducksteam.needleseye.entity.collision;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

/**
 * Collider for an axis aligned bounding box (AABB)
 * @author SkySourced
 */
public class ColliderBox implements IHasCollision {
    public Vector3 min;
    public Vector3 max;

    public ColliderBox(Vector3 min, Vector3 max) {
        this.min = min;
        this.max = max;
    }

    public ColliderBox(Vector3 pos, Vector3 min, Vector3 max) {
        this.min = pos.cpy().add(min);
        this.max = pos.cpy().add(max);
    }

    public ColliderBox(float width, float height, float depth) {
        this.min = new Vector3(-width / 2, -height / 2, -depth / 2);
        this.max = new Vector3(width / 2, height / 2, depth / 2);
    }

    @Override
    public ModelInstance getRenderable() {
        ModelBuilder modelBuilder = new ModelBuilder();
        Material mat = new Material(new ColorAttribute(ColorAttribute.Diffuse, Color.CHARTREUSE));
        Model box = modelBuilder.createBox(max.x - min.x, max.y - min.y, max.z - min.z, mat, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        ModelInstance instance = new ModelInstance(box, max.sub(min).scl(0.5f).add(min));
        box.dispose();
        return instance;
    }

    @Override
    public Vector3 getCentre() {
        return max.cpy().sub(min).scl(0.5f).add(min);
    }

    @Override
    public void setCentre(Vector3 centre, boolean lockY) {
        Vector3 oldCentre = getCentre();
        Vector3 dMin = oldCentre.cpy().sub(min);
        Vector3 dMax = oldCentre.cpy().sub(max);
        if (lockY) dMin.y = dMax.y = 0;
        min = centre.cpy().add(dMin);
        max = centre.cpy().add(dMax);
    }

    @Override
    public IHasCollision copy() {
        return new ColliderBox(min.cpy(), max.cpy());
    }

    @Override
    public String toString() {
        return "ColliderBox{" +
                "min=" + min +
                ", max=" + max +
                '}';
    }
}
