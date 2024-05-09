package com.ducksteam.unseenrealms.entity.collision;

import com.badlogic.gdx.math.Vector3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CollisionTest {
    @Test
    public void testCollision() {
        ColliderBox box1 = new ColliderBox(new Vector3(-0.5f, -0.5f, -0.5f), new Vector3(0.5f, 1, 1));
        ColliderBox box2 = new ColliderBox(new Vector3(-1, -2, -1.5f), Vector3.Zero);
        ColliderBox boxFar = new ColliderBox(new Vector3(15, 15, 15), new Vector3(16, 16, 16));
        ColliderSphere sphere1 = new ColliderSphere(1, Vector3.Zero);
        ColliderSphere sphere2 = new ColliderSphere(2, new Vector3(1.5f, 1.5f, 1.5f));
        ColliderSphere sphereFar = new ColliderSphere(0.1f, new Vector3(80, 80, 80));
        ColliderRay ray1 = new ColliderRay(new Vector3(2, 2, 2), new Vector3((float) Math.PI/4, (float) Math.PI/4, 0));

        assertTrue(box1.collidesWith(box2));
        assertFalse(box1.collidesWith(boxFar));
        assertTrue(box1.collidesWith(sphere1));
        assertTrue(box2.collidesWith(sphere2));
        assertFalse(sphere1.collidesWith(sphereFar));
        assertTrue(sphere1.collidesWith(ray1));
    }
}