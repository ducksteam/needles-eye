package com.ducksteam.unseenrealms.entity.collision;

import com.badlogic.gdx.math.Vector3;

/**
 * Utility class for collisions
 * @author SkySourced
 */
public class Collider {
    public static boolean collidesWith(IHasCollision a, IHasCollision b) {
        if (a instanceof ColliderSphere aSphere && b instanceof ColliderSphere bSphere) { // Sphere-Sphere collision
            return aSphere.radius + bSphere.radius > aSphere.centre.dst(bSphere.centre);
        } else if (a instanceof ColliderBox aBox && b instanceof ColliderBox bBox) { // Box-Box collision
            return aBox.min.x < bBox.max.x && aBox.max.x > bBox.min.x &&
                    aBox.min.y < bBox.max.y && aBox.max.y > bBox.min.y &&
                    aBox.min.z < bBox.max.z && aBox.max.z > bBox.min.z;
        } else if (a instanceof ColliderSphere aSphere && b instanceof ColliderBox bBox) { // Sphere-Box collision
            return aSphere.centre.x + aSphere.radius > bBox.min.x && aSphere.centre.x - aSphere.radius < bBox.max.x &&
                    aSphere.centre.y + aSphere.radius > bBox.min.y && aSphere.centre.y - aSphere.radius < bBox.max.y &&
                    aSphere.centre.z + aSphere.radius > bBox.min.z && aSphere.centre.z - aSphere.radius < bBox.max.z;
        } else if (a instanceof ColliderBox && b instanceof ColliderSphere) {
            return collidesWith(b, a);
        } else if (a instanceof ColliderRay aRay && b instanceof ColliderBox bBox) {
            Vector3 direction = aRay.getPoint(1).sub(aRay.origin);
            Vector3 dirFrac = new Vector3(1.0f / direction.x, 1.0f / direction.y, 1.0f / direction.z);

            // slabs
            float t1 = (bBox.min.x - aRay.origin.x) * dirFrac.x;
            float t2 = (bBox.max.x - aRay.origin.x) * dirFrac.x;
            float t3 = (bBox.min.y - aRay.origin.y) * dirFrac.y;
            float t4 = (bBox.max.y - aRay.origin.y) * dirFrac.y;
            float t5 = (bBox.min.z - aRay.origin.z) * dirFrac.z;
            float t6 = (bBox.max.z - aRay.origin.z) * dirFrac.z;

            float tmin = Math.max(Math.max(Math.min(t1, t2), Math.min(t3, t4)), Math.min(t5, t6));
            float tmax = Math.min(Math.min(Math.max(t1, t2), Math.max(t3, t4)), Math.max(t5, t6));

            return tmax > 0 && tmin < tmax;
        } else if (a instanceof ColliderBox aBox && b instanceof ColliderRay bRay) {
            return collidesWith(bRay, aBox);
        } else if (a instanceof ColliderRay aRay && b instanceof ColliderSphere bSphere) {
            Vector3 direction = aRay.getPoint(1).sub(aRay.origin);
            Vector3 originDist = aRay.origin.cpy().sub(bSphere.centre);

            float closestApproach = originDist.dot(direction);
            if (closestApproach < 0) return false;

            float d2 = originDist.len2() - closestApproach * closestApproach;
            return d2 <= bSphere.radius * bSphere.radius;
        } else if (a instanceof ColliderSphere aSphere && b instanceof ColliderRay bRay) {
            return collidesWith(bRay, aSphere);
        } else if (a instanceof ColliderRay aRay && b instanceof ColliderRay bRay) {
            Vector3 aDir = aRay.getPoint(1).sub(aRay.origin);
            Vector3 bDir = bRay.getPoint(1).sub(bRay.origin);
            Vector3 cross = aDir.crs(bDir);

            if (cross.isZero(0.0001f)) { // parallel
                return aRay.origin.epsilonEquals(bRay.origin);
            } else {
                Vector3 originDist = aRay.origin.cpy().sub(bRay.origin);
                float aDotA = aDir.dot(aDir);
                float aDotB = aDir.dot(bDir);
                float bDotB = bDir.dot(bDir);
                float aDotDist = aDir.dot(originDist);
                float bDotDist = bDir.dot(originDist);
                float D = aDotA * bDotB - aDotB * aDotB;
                float sc, tc;

                // compute the line parameters of the two closest points
                if (D < 0.000001f) { // the lines are almost parallel
                    sc = 0.0f;
                    tc = (aDotB > bDotB ? aDotDist / aDotB : bDotDist / bDotB); // use the largest denominator
                } else {
                    sc = (aDotB * bDotDist - bDotB * aDotDist) / D;
                    tc = (aDotA * bDotDist - aDotB * aDotDist) / D;
                }

                // get the difference of the two closest points
                Vector3 dP = originDist.add(aDir.scl(sc)).sub(bDir.scl(tc)); // = L1(sc) - L2(tc)

                float norm = dP.len();
                return norm < 0.000001f; // return true if the lines collide
            }
        } else if (a instanceof ColliderGroup aGroup){
            for (IHasCollision collider : aGroup.colliders) {
                boolean collides = collider.collidesWith(b);
                if (collides) return true;
            }
        } else if (b instanceof ColliderGroup bGroup) {
            for (IHasCollision collider : bGroup.colliders) {
                boolean collides = collider.collidesWith(b);
                if (collides) return true;
            }
            // TODO: create plane/quad/tri collider
        } else {
            throw new IllegalArgumentException("Unknown collision types");
        }
        return false;
    }
}
