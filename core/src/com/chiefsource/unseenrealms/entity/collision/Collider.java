package com.chiefsource.unseenrealms.entity.collision;

import com.badlogic.gdx.math.Vector3;

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
            Vector3 dirfrac = new Vector3(1.0f / aRay.direction.x, 1.0f / aRay.direction.y, 1.0f / aRay.direction.z);

            float t1 = (bBox.min.x - aRay.origin.x) * dirfrac.x;
            float t2 = (bBox.max.x - aRay.origin.x) * dirfrac.x;
            float t3 = (bBox.min.y - aRay.origin.y) * dirfrac.y;
            float t4 = (bBox.max.y - aRay.origin.y) * dirfrac.y;
            float t5 = (bBox.min.z - aRay.origin.z) * dirfrac.z;
            float t6 = (bBox.max.z - aRay.origin.z) * dirfrac.z;

            float tmin = Math.max(Math.max(Math.min(t1, t2), Math.min(t3, t4)), Math.min(t5, t6));
            float tmax = Math.min(Math.min(Math.max(t1, t2), Math.max(t3, t4)), Math.max(t5, t6));

            // if tmax < 0, ray (line) is intersecting box, but the whole box is behind the ray start
            // if the line is infinite we still want this to return true
            if (tmax < 0 && !aRay.infinite) return false;

            // if tmin > tmax, ray doesn't intersect box
            return tmin < tmax;
        } else if (a instanceof ColliderBox aBox && b instanceof ColliderRay bRay) {
            return collidesWith(bRay, aBox);
        } else if (a instanceof ColliderRay aRay && b instanceof ColliderSphere bSphere) {
            Vector3 originDist = aRay.origin.cpy().sub(bSphere.centre);
            float _b = aRay.direction.dot(aRay.direction);
            float _c = originDist.dot(originDist) - bSphere.radius * bSphere.radius;
            float _h = _b * _b - _c;
            return _h >= 0;
        } else if (a instanceof ColliderSphere aSphere && b instanceof ColliderRay bRay) {
            return collidesWith(bRay, aSphere);
        } else if (a instanceof ColliderRay aRay && b instanceof ColliderRay bRay) {
            Vector3 cross = aRay.direction.cpy().crs(bRay.direction);
            return cross.len() > 0;
        } else {
            throw new IllegalArgumentException("Unknown collision types");
        }
    }
}
