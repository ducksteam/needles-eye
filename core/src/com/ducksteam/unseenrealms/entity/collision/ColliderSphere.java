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
 * Collider for a sphere
 * @author SkySourced
 */
public class ColliderSphere implements IHasCollision {
    public float radius;
    public Vector3 centre;

    public ColliderSphere(Vector3 centre, float radius) {
        this.radius = radius;
        this.centre = centre;
    }

    @Override
    public ModelInstance render() {
        ModelBuilder modelBuilder = new ModelBuilder();
        Material mat = new Material(new ColorAttribute(ColorAttribute.Diffuse, new Color(centre.hashCode())));
        Model sphere = modelBuilder.createSphere(radius, radius, radius, 12, 12, mat, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        ModelInstance instance = new ModelInstance(sphere, centre);
        sphere.dispose();
        return instance;
    }
}
