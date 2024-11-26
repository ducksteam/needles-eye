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

/**
 * Manages the creation and removal of paralysis particle effects from the jolt thread attack.
 * @author skysourced
 */
public class ParalysisEffectManager {
    /**
     * Represents a paralysis effect
     * @param effect the copy of the effect
     * @param entity the entity the effect is attached to
     */
    public record ParalysisEffect(ParticleEffect effect, IHasHealth entity) {}

    /**
     * All effects currently active
     */
    public static ArrayList<ParalysisEffect> effects = new ArrayList<>();
    /**
     * Effects to be removed in the next update
     */
    public static ArrayList<ParalysisEffect> effectsForDisposal = new ArrayList<>();

    /**
     * The address of the static effect
     */
    private static final String STATIC_EFFECT_ADDRESS = "particles/paralyse.pfx";
    /**
     * The static effect copied by new instances
     */
    private static ParticleEffect staticEffect;
    /**
     * The array of particle batches using the paralyse_particle.png image as the texture
     */
    private static Array<ParticleBatch<?>> paralyseBatches;

    private static final Matrix4 tmpMat = new Matrix4(); // temporary matrix for effect positioning

    /**
     * Load the static effect from the asset manager
     */
    public static void loadStaticEffect() {
        staticEffect = Main.assMan.get(getStaticEffectAddress());
        paralyseBatches = new Array<>();
        paralyseBatches.add(paralyseBBParticleBatch);
    }

    /**
     * Create a new paralysis effect on an enemy
     * @param enemy the enemy to have the particle effect attached to
     */
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

    /**
     * Update positions and clear effects if the entity has moved or is no longer paralysed
     */
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

    /**
     * Get the address of the static effect
     * @return the address of the static effect
     */
    public static String getStaticEffectAddress() {
        return STATIC_EFFECT_ADDRESS;
    }
}
