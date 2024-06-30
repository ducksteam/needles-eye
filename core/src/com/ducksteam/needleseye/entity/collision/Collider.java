package com.ducksteam.needleseye.entity.collision;

import com.badlogic.gdx.math.Vector3;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Utility class for collisions
 * @author SkySourced
 */
public class Collider {
    public static boolean collidesWith(IHasCollision a, IHasCollision b) {
        if (a instanceof ColliderSphere aSphere && b instanceof ColliderSphere bSphere) { // Sphere-Sphere collision
            return aSphere.radius + bSphere.radius > aSphere.centre.dst(bSphere.centre);
        } else if (a instanceof ColliderBox aBox && b instanceof ColliderBox bBox) { // Box-Box collision
            return !(aBox.min.x > bBox.max.x || aBox.max.x < bBox.min.x ||
                    aBox.min.y > bBox.max.y || aBox.max.y < bBox.min.y ||
                    aBox.min.z > bBox.max.z || aBox.max.z < bBox.min.z);
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

            float tmin = max(max(min(t1, t2), min(t3, t4)), min(t5, t6));
            float tmax = min(min(max(t1, t2), max(t3, t4)), max(t5, t6));

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
        } else if (a instanceof ColliderGroup aGroup) {
            for (IHasCollision collider : aGroup.colliders) {
                boolean collides = collider.collidesWith(b);
                if (collides) return true;
            }
        } else if (b instanceof ColliderGroup bGroup) {
            for (IHasCollision collider : bGroup.colliders) {
                boolean collides = collider.collidesWith(b);
                if (collides) return true;
            }
        } else {
            throw new IllegalArgumentException("Unknown collision types");
        }
        return false;
    }

    public static Vector3 contactNormal(IHasCollision a, IHasCollision b) {
        if (a instanceof ColliderSphere aSphere && b instanceof ColliderSphere bSphere) { // Sphere-Sphere collision
            return aSphere.centre.cpy().sub(bSphere.centre).nor();
        } else if (a instanceof ColliderBox aBox && b instanceof ColliderBox bBox) { // Box-Box collision
            Vector3 aCentre = aBox.getCentre();
            Vector3 bCentre = bBox.getCentre();
            Vector3 diff = aCentre.cpy().sub(bCentre);
            Vector3 minDiff = aBox.min.cpy().sub(bBox.min);
            Vector3 maxDiff = aBox.max.cpy().sub(bBox.max);
            if (Math.abs(diff.x) < Math.abs(minDiff.x) || Math.abs(diff.x) < Math.abs(maxDiff.x)) {
                return new Vector3(Math.signum(diff.x), 0, 0);
            } else if (Math.abs(diff.y) < Math.abs(minDiff.y) || Math.abs(diff.y) < Math.abs(maxDiff.y)) {
                return new Vector3(0, Math.signum(diff.y), 0);
            } else if (Math.abs(diff.z) < Math.abs(minDiff.z) || Math.abs(diff.z) < Math.abs(maxDiff.z)) {
                return new Vector3(0, 0, Math.signum(diff.z));
            }
        } else if (a instanceof ColliderSphere aSphere && b instanceof ColliderBox bBox) { // Sphere-Box collision
            Vector3 aCentre = aSphere.centre;
            Vector3 bCentre = bBox.getCentre();
            Vector3 diff = aCentre.cpy().sub(bCentre);
            Vector3 minDiff = aSphere.centre.cpy().sub(bBox.min);
            Vector3 maxDiff = aSphere.centre.cpy().sub(bBox.max);
            if (Math.abs(diff.x) < Math.abs(minDiff.x) || Math.abs(diff.x) < Math.abs(maxDiff.x)) {
                return new Vector3(Math.signum(diff.x), 0, 0);
            } else if (Math.abs(diff.y) < Math.abs(minDiff.y) || Math.abs(diff.y) < Math.abs(maxDiff.y)) {
                return new Vector3(0, Math.signum(diff.y), 0);
            } else if (Math.abs(diff.z) < Math.abs(minDiff.z) || Math.abs(diff.z) < Math.abs(maxDiff.z)) {
                return new Vector3(0, 0, Math.signum(diff.z));
            }
        } else if (a instanceof ColliderBox aBox && b instanceof ColliderSphere bSphere) {
            return contactNormal(bSphere, aBox).scl(-1);
        } else if (a instanceof ColliderRay aRay && b instanceof ColliderBox bBox){
            Vector3 direction = aRay.getPoint(1).sub(aRay.origin);
            Vector3 dirFrac = new Vector3(1.0f / direction.x, 1.0f / direction.y, 1.0f / direction.z);

            // slabs
            float t1 = (bBox.min.x - aRay.origin.x) * dirFrac.x;
            float t2 = (bBox.max.x - aRay.origin.x) * dirFrac.x;
            float t3 = (bBox.min.y - aRay.origin.y) * dirFrac.y;
            float t4 = (bBox.max.y - aRay.origin.y) * dirFrac.y;
            float t5 = (bBox.min.z - aRay.origin.z) * dirFrac.z;
            float t6 = (bBox.max.z - aRay.origin.z) * dirFrac.z;

            float tmin = max(max(min(t1, t2), min(t3, t4)), min(t5, t6));
            float tmax = min(min(max(t1, t2), max(t3, t4)), max(t5, t6));

            if (tmax > 0 && tmin < tmax) {
                Vector3 contactPoint = aRay.getPoint(tmin);
                Vector3 normal = new Vector3();
                if (Math.abs(contactPoint.x - bBox.min.x) < 0.0001f) {
                    normal.x = -1;
                } else if (Math.abs(contactPoint.x - bBox.max.x) < 0.0001f) {
                    normal.x = 1;
                } else if (Math.abs(contactPoint.y - bBox.min.y) < 0.0001f) {
                    normal.y = -1;
                } else if (Math.abs(contactPoint.y - bBox.max.y) < 0.0001f) {
                    normal.y = 1;
                } else if (Math.abs(contactPoint.z - bBox.min.z) < 0.0001f) {
                    normal.z = -1;
                } else if (Math.abs(contactPoint.z - bBox.max.z) < 0.0001f) {
                    normal.z = 1;
                }
                return normal;
            }
        } else if (a instanceof ColliderBox aBox && b instanceof ColliderRay bRay){
            return contactNormal(bRay, aBox).scl(-1);
        } else if (a instanceof ColliderRay aRay && b instanceof ColliderSphere bSphere){
            Vector3 direction = aRay.getPoint(1).sub(aRay.origin);
            Vector3 originDist = aRay.origin.cpy().sub(bSphere.centre);

            float closestApproach = originDist.dot(direction);
            if (closestApproach < 0) return new Vector3();

            float d2 = originDist.len2() - closestApproach * closestApproach;
            if (d2 <= bSphere.radius * bSphere.radius) {
                Vector3 normal = bSphere.centre.cpy().sub(aRay.getPoint(closestApproach));
                return normal.nor();
            }
        } else if (a instanceof ColliderSphere aSphere && b instanceof ColliderRay bRay){
            return contactNormal(bRay, aSphere).scl(-1);
        } else if (a instanceof ColliderRay aRay && b instanceof ColliderRay bRay) {
            Vector3 aDir = aRay.getPoint(1).sub(aRay.origin);
            Vector3 bDir = bRay.getPoint(1).sub(bRay.origin);
            Vector3 cross = aDir.crs(bDir);

            if (cross.isZero(0.0001f)) { // parallel
                return aRay.origin.epsilonEquals(bRay.origin) ? new Vector3(1, 0, 0) : new Vector3();
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

                return dP.nor();
            }
        } else {
            throw new IllegalArgumentException("Unknown collision types");
        }
        return null;
    }
}
