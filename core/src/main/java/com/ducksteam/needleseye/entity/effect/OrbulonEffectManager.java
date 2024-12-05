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
 * Manages the creation and removal of Orbulon firing particle effects
 * @author SkySourced
 */
public class OrbulonEffectManager {

    /**
     * Represents a particle effect
     * @param effect the copy of the effect
     * @param activationTime the time when the effect should begin drawing
     * @param expiryTime the time at which the effect should be removed
     * @param pos the position of the effect
     */
    public record OrbulonEffect(ParticleEffect effect, Long activationTime, Long expiryTime, Vector3 pos){}

    /** All effects currently active or waiting to be activated */
    public static ArrayList<OrbulonEffect> effects = new ArrayList<>();
    /** Effects to be removed in the next update */
    public static ArrayList<OrbulonEffect> effectsForDisposal = new ArrayList<>();

    /** The address of the static effect */
    private static final String STATIC_EFFECT_ADDRESS = "particles/orbulon.pfx";
    /** The static effect copied by new instances */
    private static ParticleEffect staticEffect;
    /** The lifetime of the effect in ms */
    private static final int LIFETIME = 1300;
    /** A list of particle batches used by effects using the general_particle.png image as the texture */
    private static Array<ParticleBatch<?>> generalBatches;

    /** How far along the beam's path each particle is created */
    public static final float PARTICLE_DENSITY = 0.2f;
    /** The delay in ms that each particle spawns in, to create the 'firing' effect */
    public static final long PARTICLE_DELAY = 10;

    /** A temporary matrix for effect positioning */
    private static final Matrix4 tmpMat = new Matrix4();

    /** Load the static effect from the asset manager */
    public static void loadStaticEffect() {
        generalBatches = new Array<>();
        generalBatches.add(generalBBParticleBatch);
        staticEffect = Main.assMan.get(getStaticEffectAddress());
    }

    /**
     * Create a new copy of the effect
     */
    public static void create(Vector3 position, Long activationTime) {
        // generate copy of effect
        ParticleEffect tmpEffect = staticEffect.copy();

        effects.add(new OrbulonEffect(tmpEffect, activationTime, activationTime + LIFETIME, position));
    }

    /**
     * Start any required particles and remove expired particles
     */
    public static void update(){
        effectsForDisposal.clear();

        for (OrbulonEffect record : effects){
            if (Main.getTime() > record.expiryTime){
                record.effect.end();
                particleSystem.remove(record.effect);
                effectsForDisposal.add(record);
            } else if (Main.getTime() > record.activationTime){
                record.effect.setBatch(generalBatches);
                record.effect.setTransform(tmpMat.setToTranslation(record.pos));
                record.effect.init();
                record.effect.start();

                particleSystem.add(record.effect);
            }
        }

        effects.removeAll(effectsForDisposal);
        effectsForDisposal.forEach((OrbulonEffect e) -> e.effect.dispose());
    }

    /**
     * Gets the address for loading the static copy of the particle effect
     * @return the path to the file containing the effect information
     */
    public static String getStaticEffectAddress() {
        return STATIC_EFFECT_ADDRESS;
    }
}
