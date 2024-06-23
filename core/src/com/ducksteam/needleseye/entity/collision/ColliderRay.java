package com.ducksteam.needleseye.entity.collision;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

/**
 * Collider for a ray
 * @author SkySourced
 */
public class ColliderRay implements IHasCollision {
    public Vector3 origin;
    public float polar;
    public float azimuthal;
    public boolean infinite;

    public ColliderRay(Vector3 origin, float polar, float azimuthal) {
        this.origin = origin;
        this.polar = polar;
        this.azimuthal = azimuthal;
        if (origin == null) {
            throw new IllegalArgumentException("Origin cannot be null");
        }
        this.infinite = false;
    }

    public Vector3 getPoint(float distance) {
        float x = distance * (float)Math.sin(polar) * (float)Math.cos(azimuthal);
        float y = distance * (float)Math.sin(polar) * (float)Math.sin(azimuthal);
        float z = distance * (float)Math.cos(polar);

        return new Vector3(x, y, z).add(origin);
    }

    @Override
    public ModelInstance getRenderable() {
        ModelBuilder modelBuilder = new ModelBuilder();
        Material mat = new Material(new ColorAttribute(ColorAttribute.Diffuse, new Color(polar, azimuthal, 0, 1)));
        Model arrow = modelBuilder.createArrow(origin, getPoint(1), mat, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        ModelInstance instance = new ModelInstance(arrow, origin);
        arrow.dispose();
        return instance;
    }

    @Override
    public Vector3 getCentre() {
        return origin;
    }

    @Override
    public void setCentre(Vector3 centre, boolean lockY) {
        if (lockY) centre.y = origin.y;
        this.origin = centre;
    }

    @Override
    public ColliderRay copy() {
        return new ColliderRay(origin.cpy(), polar, azimuthal);
    }

    @Override
    public String toString() {
        return "ColliderRay{" +
                "origin=" + origin +
                ", polar=" + polar +
                ", azimuthal=" + azimuthal +
                '}';
    }
}
