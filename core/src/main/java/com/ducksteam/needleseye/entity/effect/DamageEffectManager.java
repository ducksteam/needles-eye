package com.ducksteam.needleseye.entity.effect;

import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.batches.ParticleBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.ducksteam.needleseye.Main;

import java.util.ArrayList;

import static com.ducksteam.needleseye.Main.generalBBParticleBatch;
import static com.ducksteam.needleseye.Main.particleSystem;

/**
 * Manages the creation and removal of damage particle effects.
 * @author SkySourced
 */
public class DamageEffectManager {

    /**
     * Represents a damage effect
     * @param effect the copy of the effect
     * @param expiryTime the time at which the effect should be removed
     */
	public record DamageEffect(ParticleEffect effect, Long expiryTime) {}

    /**
     * All effects currently active
     */
	public static ArrayList<DamageEffect> effects = new ArrayList<>();
    /**
     * Effects to be removed in the next update
     */
	public static ArrayList<DamageEffect> effectsForDisposal = new ArrayList<>();

    /**
     * The address of the static effect
     */
	private static final String STATIC_EFFECT_ADDRESS = "particles/bleed.pfx";
    /**
     * The static effect copied by new instances
     */
	private static ParticleEffect staticEffect;
    /**
     * The lifetime of the effect in milliseconds
     */
	private static final int LIFETIME = 1000;
    /**
     * A list of particle batches used by effects using the general_particle.png image as the texture
     */
	private static Array<ParticleBatch<?>> generalBatches;

    /**
     * A temporary matrix for effect positioning
     */
	private static final Matrix4 tmpMat = new Matrix4();

	/**
	 * Load the static effect from the asset manager
	 */
	public static void loadStaticEffect(){ // load the effect from the asset manager
		generalBatches = new Array<>();
		generalBatches.add(generalBBParticleBatch);
		staticEffect = Main.assMan.get(getStaticEffectAddress());
	}

	/**
	 * Create a new copy of the effect
	 * @param position the position where the effect should be created
	 */
	public static void create(Vector3 position) {
		// generate copy of effect
		ParticleEffect tmpEffect = staticEffect.copy();
		tmpEffect.setBatch(generalBatches);
		tmpEffect.setTransform(tmpMat.setToTranslation(position));
		tmpEffect.init();
		tmpEffect.start();

		effects.add(new DamageEffect(tmpEffect, Main.getTime() + LIFETIME));
		particleSystem.add(tmpEffect);
	}

	/**
	 * Remove any expiring particles
	 */
	public static void update(){
		effectsForDisposal.clear();

		for (DamageEffect record : effects) {
			if (Main.getTime() > record.expiryTime()) {
				record.effect().end(); // stop effect playing
				particleSystem.remove(record.effect()); // remove from world
				effectsForDisposal.add(record);
			}
		}

		effects.removeAll(effectsForDisposal);
	}

    /**
     * Gets the address for loading the static copy of the particle effect
     * @return the path to the file containing the effect information
     */
	public static String getStaticEffectAddress() {
		return STATIC_EFFECT_ADDRESS;
	}
}
