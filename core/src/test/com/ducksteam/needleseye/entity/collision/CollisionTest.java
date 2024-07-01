package com.ducksteam.needleseye.entity.collision;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.ducksteam.needleseye.map.MapManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CollisionTest {
    @Test
    public void testCollision() {
        ColliderBox box1 = new ColliderBox(new Vector3(-0.5f, -0.5f, -0.5f), new Vector3(0.5f, 1, 1));
        ColliderBox box2 = new ColliderBox(new Vector3(-1, -2, -1.5f), Vector3.Zero);
        ColliderBox box3 = new ColliderBox(new Vector3(-1.46f, -0.95f, -0.019f), new Vector3(-0.46f, 1.045f, 0.98f));
        ColliderBox plane = new ColliderBox(Vector3.Zero, new Vector3(-10, 0, -10));
        ColliderBox boxFar = new ColliderBox(new Vector3(15, 15, 15), new Vector3(16, 16, 16));
        ColliderSphere sphere1 = new ColliderSphere(Vector3.Zero, 1);
        ColliderSphere sphere2 = new ColliderSphere( new Vector3(1.5f, 1.5f, 1.5f), 2);
        ColliderSphere sphereFar = new ColliderSphere(new Vector3(80, 80, 80), 0.1f);
        ColliderRay ray1 = new ColliderRay(new Vector3(2, 2, 2), (float) Math.PI/4, (float) Math.PI/4);

        assertTrue(box1.collidesWith(box2));
        assertTrue(box3.collidesWith(plane));
        assertFalse(box1.collidesWith(boxFar));
        assertTrue(box1.collidesWith(sphere1));
        assertTrue(box2.collidesWith(sphere2));
        assertFalse(sphere1.collidesWith(sphereFar));
        assertTrue(sphere1.collidesWith(ray1));
    }

    @Test
    public void centreTest() {
        ColliderBox box = new ColliderBox(new Vector3(-0.5f, -0.5f, -0.5f), new Vector3(0.5f, 0.5f, 0.5f));
        Vector3 centre = box.getCentre();

        assertEquals(Vector3.Zero, centre);

        box.setCentre(new Vector3(1, 1, 1), false);
        assertEquals(new Vector3(1, 1, 1), box.getCentre());

        box.setCentre(new Vector3(0, 0, 0), true);
        assertEquals(new Vector3(0, 1, 0), box.getCentre());
    }

    @Test
    public void positionTest() {
        Vector3 pos = new Vector3(1, 1, 1);
        Vector2 roomSpacePos = MapManager.getRoomSpacePos(pos);
        Vector3 pos2 = MapManager.getRoomPos(roomSpacePos);

        assertEquals(roomSpacePos, new Vector2(1, 1));
        assertEquals(pos2, new Vector3(10, 0, 10));

        pos = new Vector3(1000, -12, 1004);
        roomSpacePos = MapManager.getRoomSpacePos(pos);
        pos2 = MapManager.getRoomPos(roomSpacePos);

        assertEquals(roomSpacePos, new Vector2(100, 101));
        assertEquals(pos2, new Vector3(1000, 0, 1010));

        pos = new Vector3(-195, 12, 1234);
        roomSpacePos = MapManager.getRoomSpacePos(pos);
        pos2 = MapManager.getRoomPos(roomSpacePos);

        assertEquals(roomSpacePos, new Vector2(-19, 124));
        assertEquals(pos2, new Vector3(-190, 0, 1240));
    }

    @Test
    public void contactNormalTest(){
        ColliderBox box1 = new ColliderBox(new Vector3(-0.5f, -0.5f, -0.5f), new Vector3(0.5f, 0.5f, 0.5f));
        ColliderBox target = new ColliderBox(new Vector3(1, -1, 1), new Vector3(2, 1, 2));
        System.out.println("Normal: " + Collider.contactNormal(box1, target));
        assertEquals(Collider.contactNormal(box1, target), new Vector3());
    }
}