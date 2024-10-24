package com.ducksteam.needleseye.entity.effect;

import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.math.Matrix4;
import com.ducksteam.needleseye.Main;
import com.ducksteam.needleseye.entity.Entity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.ducksteam.needleseye.Main.particleSystem;

public class ParalysisEffectManager {
    public static ConcurrentHashMap<ParticleEffect, Entity> particles = new ConcurrentHashMap<>();

    private static final String STATIC_EFFECT_ADDRESS = "models/effects/soulfire.pfx";
    private static ParticleEffect staticEffect;

    private static final Matrix4 tmpMat = new Matrix4();

    public static void loadStaticEffect() {
        staticEffect = Main.assMan.get(getStaticEffectAddress());
    }

    public static void create(Entity enemy) {
        ParticleEffect tmpEffect = staticEffect.copy();
        tmpEffect.setBatch(particleSystem.getBatches());
        tmpEffect.setTransform(tmpMat.setToTranslation(enemy.getPosition()));
        tmpEffect.init();
        tmpEffect.start();
    }

    public static void update() {
        for (Map.Entry<ParticleEffect, Entity> entry : particles.entrySet()) {
            if (entry.getValue().getParalyseTime() > 0){
                entry.getKey().setTransform(tmpMat.setToTranslation(entry.getValue().getPosition()));
            } else {
                particles.remove(entry.getKey());
            }
        }
    }

    public static String getStaticEffectAddress() {
        return STATIC_EFFECT_ADDRESS;
    }
}
