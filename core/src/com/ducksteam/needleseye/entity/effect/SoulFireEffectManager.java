package com.ducksteam.needleseye.entity.effect;

import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.ducksteam.needleseye.Main;
import com.ducksteam.needleseye.entity.Entity;
import com.ducksteam.needleseye.entity.enemies.EnemyEntity;

import java.util.concurrent.ConcurrentHashMap;

import static com.ducksteam.needleseye.Main.entities;
import static com.ducksteam.needleseye.Main.particleSystem;

public class SoulFireEffectManager {

    public static ConcurrentHashMap<ParticleEffect, Long> effects = new ConcurrentHashMap<>();

    public static String staticEffectAddress = "models/effects/soulfire.pfx";
    private static ParticleEffect staticEffect; // the original copy of the effect
    private static ParticleEffect tmpEffect;
    private static final Matrix4 tmpMat = new Matrix4();
    private static final Vector3 tmp = new Vector3();

    public static void loadStaticEffect(){
        staticEffect = Main.assMan.get(getStaticEffectAddress());
    }

    public static void create(Vector3 position) {
        tmpEffect = staticEffect.copy();
        tmpEffect.setTransform(tmpMat.setToTranslation(position));
        tmpEffect.init();
        tmpEffect.start();
        effects.put(tmpEffect, Main.getTime() + 3);
        particleSystem.add(tmpEffect);
    }

    public static void update(){
         effects.forEach((ParticleEffect effect, Long time) -> {
             if (Main.getTime() > time) {
                 effects.remove(effect);
                 particleSystem.remove(effect);
             }
             entities.values().forEach((Entity e) -> {
                 if (!(e instanceof EnemyEntity)) return;
                 if (e.getPosition().dst(effect.getBoundingBox().getCenter(tmp)) < 1) {
                     ((EnemyEntity) e).damage(getContactDamage());
                 }
             });
         });
    }

    public static String getStaticEffectAddress() {
        return staticEffectAddress;
    }

    private static int getContactDamage() {
        return 1;
    }
}
