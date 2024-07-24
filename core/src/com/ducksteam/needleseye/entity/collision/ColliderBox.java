package com.ducksteam.needleseye.entity.collision;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;

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
        balanceVectors();
    }

    public ColliderBox(Vector3 pos, Vector3 min, Vector3 max) {
        this.min = min.cpy().add(pos);
        this.max = max.cpy().add(pos);
        balanceVectors();
    }

    public ColliderBox(float width, float height, float depth) {
        this.min = new Vector3(-width / 2, -height / 2, -depth / 2);
        this.max = new Vector3(width / 2, height / 2, depth / 2);
        balanceVectors();
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
        min = centre.cpy().add(dMin);
        max = centre.cpy().add(dMax);
        if (lockY) {
            min.y = oldCentre.y + dMin.y;
            max.y = oldCentre.y + dMax.y;
        }
        balanceVectors();
    }

    @Override
    public void move(Vector3 delta, boolean lockY) {
        if (lockY) delta.y = 0;
        min.add(delta);
        max.add(delta);
    }

    @Override
    public IHasCollision copy() {
        return new ColliderBox(min.cpy(), max.cpy());
    }

    @Override
    public ArrayList<IHasCollision> getColliders() {
        ArrayList<IHasCollision> colliders = new ArrayList<>();
        colliders.add(this);
        return colliders;
    }

    @Override
    public String toString() {
        return "ColliderBox{" +
                "min=" + min +
                ", max=" + max +
                '}';
    }

    public void balanceVectors(){
        Vector3 newMin = min.cpy();
        Vector3 newMax = max.cpy();

        if (min.x > max.x){
            newMin.x = max.x;
            newMax.x = min.x;
        }
        if (min.y > max.y){
            newMin.y = max.y;
            newMax.y = min.y;
        }
        if (min.z > max.z){
            newMin.z = max.z;
            newMax.z = min.z;
        }

        this.min = newMin;
        this.max = newMax;
    }
}
