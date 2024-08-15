package com.ducksteam.needleseye.entity.effect;

import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.ducksteam.needleseye.Config;
import com.ducksteam.needleseye.Main;
import com.ducksteam.needleseye.entity.Entity;
import com.ducksteam.needleseye.entity.enemies.EnemyEntity;

import java.util.concurrent.ConcurrentHashMap;

import static com.ducksteam.needleseye.Main.entities;
import static com.ducksteam.needleseye.Main.particleSystem;

/**
 * Manages the right click ability for soul thread
 * @author SkySourced
 */
public class SoulFireEffectManager {

    // the effects and their expiry times
    public static ConcurrentHashMap<ParticleEffect, Long> times = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<ParticleEffect, Vector3> positions = new ConcurrentHashMap<>();

    private static final String staticEffectAddress = "models/effects/soulfire.pfx"; // file path to the effect
    private static ParticleEffect staticEffect; // the original copy of the effect

	// used for various calculations
    private static final Matrix4 tmpMat = new Matrix4();
    private static Vector3 tmp = new Vector3();
    private static Vector3 tmp2 = new Vector3();

    public static void loadStaticEffect(){ // load the effect from the asset manager
        staticEffect = Main.assMan.get(getStaticEffectAddress());
    }

    public static void create(Vector3 position) {
        // lock y
        position.y = Config.SOUL_FIRE_HEIGHT;

        // generate copy of effect
		ParticleEffect tmpEffect = staticEffect.copy();
        tmpEffect.setBatch(particleSystem.getBatches());
        tmpEffect.setTransform(tmpMat.setToTranslation(position));
        tmpEffect.init();
        tmpEffect.start();

        // add effect to world
        times.put(tmpEffect, Main.getTime() + 3000);
        positions.put(tmpEffect, position);
        particleSystem.add(tmpEffect);
    }

    public static void update(){
         times.forEach((ParticleEffect effect, Long time) -> {
             if (Main.getTime() > time) {
                 times.remove(effect);
                 particleSystem.remove(effect);
                 positions.remove(effect);
                 return;
             }
             entities.values().forEach((Entity e) -> {
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
         });
    }

    public static String getStaticEffectAddress() {
        return staticEffectAddress;
    }

    private static int getContactDamage() {
        return 1;
    }
}
