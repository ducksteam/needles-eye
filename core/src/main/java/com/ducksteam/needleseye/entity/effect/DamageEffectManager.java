package com.ducksteam.needleseye.entity.effect;

import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.batches.ParticleBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.ducksteam.needleseye.Main;

import java.util.ArrayList;

import static com.ducksteam.needleseye.Main.*;

/**
 * Manages the creation and removal of damage effects in the game world.
 * @author SkySourced
 */
public class DamageEffectManager {

	public record DamageEffect(ParticleEffect effect, Long expiryTime) {}

	// the effects and their expiry times
	public static ArrayList<DamageEffect> effects = new ArrayList<>();
	public static ArrayList<DamageEffect> effectsForDisposal = new ArrayList<>();

	private static final String STATIC_EFFECT_ADDRESS = "particles/bleed.pfx"; // file path to the effect
	private static ParticleEffect staticEffect; // the original copy of the effect
	private static final int LIFETIME = 1000; // the lifetime of the effect in milliseconds
	private static Array<ParticleBatch<?>> generalBatches;

	private static final Matrix4 tmpMat = new Matrix4(); // temporary matrix for effect positioning

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

	public static String getStaticEffectAddress() {
		return STATIC_EFFECT_ADDRESS;
	}
}
