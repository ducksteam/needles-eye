package com.ducksteam.needleseye.entity.effect;

import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.batches.ParticleBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.ducksteam.needleseye.Main;
import com.ducksteam.needleseye.entity.IHasHealth;
import com.ducksteam.needleseye.entity.enemies.EnemyEntity;

import java.util.ArrayList;

import static com.ducksteam.needleseye.Main.paralyseBBParticleBatch;
import static com.ducksteam.needleseye.Main.particleSystem;

public class ParalysisEffectManager {
    public record ParalysisEffect(ParticleEffect effect, IHasHealth entity) {}

    // the effects and their expiry times
    public static ArrayList<ParalysisEffect> effects = new ArrayList<>();
    public static ArrayList<ParalysisEffect> effectsForDisposal = new ArrayList<>();

    private static final String STATIC_EFFECT_ADDRESS = "particles/paralyse.pfx"; // file path to the effect
    private static ParticleEffect staticEffect; // the original copy of the effect
    private static Array<ParticleBatch<?>> paralyseBatches; // the batches the effect will be rendered in

    private static final Matrix4 tmpMat = new Matrix4(); // temporary matrix for effect positioning

    public static void loadStaticEffect() {
        staticEffect = Main.assMan.get(getStaticEffectAddress());
        paralyseBatches = new Array<>();
        paralyseBatches.add(paralyseBBParticleBatch);
    }

    public static void create(EnemyEntity enemy) {
        // generate copy of effect
        ParticleEffect tmpEffect = staticEffect.copy();
        tmpEffect.setBatch(paralyseBatches);
        tmpEffect.setTransform(tmpMat.setToTranslation(enemy.getPosition()));
        tmpEffect.init();
        tmpEffect.start();

        effects.add(new ParalysisEffect(tmpEffect, enemy));
        particleSystem.add(tmpEffect);
    }

    public static void update() {
        effectsForDisposal.clear();

		for (ParalysisEffect record : effects) {
            if(record.entity().getParalyseTime() > 0 && record.entity().getHealth() > 0) {
                record.effect().setTransform(tmpMat.setToTranslation(record.entity().getPosition()));
            } else {
                record.effect().end(); // stop effect playing
                particleSystem.remove(record.effect()); // remove from world
                effectsForDisposal.add(record);
            }
		}

        effects.removeAll(effectsForDisposal);
    }

    public static String getStaticEffectAddress() {
        return STATIC_EFFECT_ADDRESS;
    }
}
