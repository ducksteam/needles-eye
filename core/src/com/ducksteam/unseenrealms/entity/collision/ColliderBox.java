package com.ducksteam.unseenrealms.entity.collision;

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
        this.min = pos.add(min);
        this.max = pos.add(max);
    }

    public ColliderBox(float width, float height, float depth) {
        this.min = new Vector3(-width / 2, -height / 2, -depth / 2);
        this.max = new Vector3(width / 2, height / 2, depth / 2);
    }

    @Override
    public ModelInstance getRenderable() {
        ModelBuilder modelBuilder = new ModelBuilder();
        Material mat = new Material(new ColorAttribute(ColorAttribute.Diffuse, new Color(min.hashCode())));
        Model box = modelBuilder.createBox(max.x - min.x, max.y - min.y, max.z - min.z, mat, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        ModelInstance instance = new ModelInstance(box, max.sub(min).scl(0.5f).add(min));
        box.dispose();
        return instance;
    }
}
