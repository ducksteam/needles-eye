package com.ducksteam.needleseye.entity.effect;

import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.batches.ParticleBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.ducksteam.needleseye.Config;
import com.ducksteam.needleseye.Main;
import com.ducksteam.needleseye.entity.Entity;
import com.ducksteam.needleseye.entity.enemies.EnemyEntity;

import java.util.ArrayList;

import static com.ducksteam.needleseye.Main.*;

/**
 * Manages the right click ability for soul thread
 * @author SkySourced
 */
public class SoulFireEffectManager {

    public record SoulFireEffect(ParticleEffect effect, Vector3 position, Long expiryTime) {}

    // the effects and their expiry times
    public static ArrayList<SoulFireEffect> effects = new ArrayList<>();
    public static ArrayList<SoulFireEffect> effectsForDisposal = new ArrayList<>();

    private static final String STATIC_EFFECT_ADDRESS = "particles/soulfire.pfx"; // file path to the effect
    private static ParticleEffect staticEffect; // the original copy of the effect
    private static final int LIFETIME = 3000; // the lifetime of the effect in milliseconds
	private static Array<ParticleBatch<?>> generalBatches;

	// used for various calculations
    private static final Matrix4 tmpMat = new Matrix4();
    private static Vector3 tmp = new Vector3();
    private static Vector3 tmp2 = new Vector3();

    public static void loadStaticEffect(){ // load the effect from the asset manager
		generalBatches = new Array<>();
		generalBatches.add(generalBBParticleBatch);
        staticEffect = Main.assMan.get(getStaticEffectAddress());
    }

    /**
     * Create copy of effect
     * @param position position to create the effect at
     */
    public static void create(Vector3 position) {
        // lock y to specified height
        position.y = Config.SOUL_FIRE_HEIGHT;

        // generate copy of effect
		ParticleEffect tmpEffect = staticEffect.copy();
        tmpEffect.setBatch(generalBatches);
        tmpEffect.setTransform(tmpMat.setToTranslation(position));
        tmpEffect.init();
        tmpEffect.start();

        effects.add(new SoulFireEffect(tmpEffect, position, Main.getTime() + LIFETIME));
        particleSystem.add(tmpEffect);
    }

    public static void update(){
        effectsForDisposal.clear();

		for (SoulFireEffect record : effects) {
			if (Main.getTime() > record.expiryTime()) {
				record.effect().end(); // stop effect playing
				particleSystem.remove(record.effect()); // remove from world
				effectsForDisposal.add(record);
				continue;
			}
			entities.values().forEach((Entity e) -> { // for each entity
				// only check positions of enemies
				if (!(e instanceof EnemyEntity)) return;

				// get positions to compare
				tmp = e.getPosition();
				tmp2 = record.position();

				// only compare x-z distances
				tmp.y = 0;
				tmp2.y = 0;

				// if within range apply damage
				if (tmp.dst(tmp2) < Config.SOUL_FIRE_RANGE) {
					((EnemyEntity) e).damage(getContactDamage());
				}
			});

			// Damage player
			tmp = player.getPosition().cpy();
			tmp2 = record.position().cpy();

			tmp.y = 0;
			tmp2.y = 0;

			if (tmp.dst(tmp2) < Config.SOUL_FIRE_RANGE) {
				player.damage(1);
			}
		}

        effects.removeAll(effectsForDisposal);
    }

    /**
     * Get the path where the effect is stored
     * @return the path to the effect
     */
    public static String getStaticEffectAddress() {
        return STATIC_EFFECT_ADDRESS;
    }

    /**
     * Get the damage dealt by the effect
     * @return the damage dealt
     */
    private static int getContactDamage() {
        return 2;
    }
}