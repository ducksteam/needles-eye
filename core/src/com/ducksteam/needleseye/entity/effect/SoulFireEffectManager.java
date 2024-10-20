package com.ducksteam.needleseye.entity.effect;

import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.ducksteam.needleseye.Config;
import com.ducksteam.needleseye.Main;
import com.ducksteam.needleseye.entity.Entity;
import com.ducksteam.needleseye.entity.enemies.EnemyEntity;

import java.util.concurrent.ConcurrentHashMap;

import static com.ducksteam.needleseye.Main.*;

/**
 * Manages the right click ability for soul thread
 * @author SkySourced
 */
public class SoulFireEffectManager {

    // the effects and their expiry times
    public static ConcurrentHashMap<ParticleEffect, Long> times = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<ParticleEffect, Vector3> positions = new ConcurrentHashMap<>();

    private static final String STATIC_EFFECT_ADDRESS = "models/effects/soulfire.pfx"; // file path to the effect
    private static ParticleEffect staticEffect; // the original copy of the effect

	// used for various calculations
    private static final Matrix4 tmpMat = new Matrix4();
    private static Vector3 tmp = new Vector3();
    private static Vector3 tmp2 = new Vector3();

    public static void loadStaticEffect(){ // load the effect from the asset manager
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
        tmpEffect.setBatch(particleSystem.getBatches());
        tmpEffect.setTransform(tmpMat.setToTranslation(position));
        tmpEffect.init();
        tmpEffect.start();

        // add effect to world
        times.put(tmpEffect, Main.getTime() + 1000);
        positions.put(tmpEffect, position);
        particleSystem.add(tmpEffect);
    }

    public static void update(){
         times.forEach((ParticleEffect effect, Long time) -> {
             if (Main.getTime() > time) { // remove the effect if its expiry time has passed
                 times.remove(effect);
                 particleSystem.remove(effect);
                 positions.remove(effect);
                 return;
             }
             entities.values().forEach((Entity e) -> { // for each entity
                 // only check positions of enemies
                 if (!(e instanceof EnemyEntity)) return;

                 // get positions to compare
                 tmp = e.getPosition();
                 tmp2 = positions.get(effect);

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
             tmp2 = positions.get(effect);

             tmp.y = 0;
             tmp2.y = 0;

             if (tmp.dst(tmp2) < Config.SOUL_FIRE_RANGE) {
                 player.damage(1);
             }
         });
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
