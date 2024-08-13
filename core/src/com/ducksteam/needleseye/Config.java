package com.ducksteam.needleseye;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;

import java.util.HashMap;

/**
 * Configuration class for the game
 * @author SkySourced
 */
public class Config {
	// Modifiable
    public static HashMap<String, Integer> keys = new HashMap<>();
    public static boolean debugMenu = false;
    public static boolean doRenderColliders = false;

    public static float globalLightIntensity = 0.3f;
    public static Color globalLightColour = new Color(0.4f*globalLightIntensity, 0.4f*globalLightIntensity, 0.4f*globalLightIntensity, 1);

    // Global constants
    public static final float ROTATION_SPEED = 0.007F /* * MathUtils.degRad*/;
    public static float MOVE_SPEED;
    public static final float LOADING_ANIM_SPEED = 0.05f;
    public static final float ATTACK_ANIM_SPEED = 0.03f;
    public static final float ASPECT_RATIO = (float) 16 / 9;
    public static final int ROOM_SCALE = 10;
    public static final float PLAYER_MASS = 10;
    public static final float DAMAGE_TIMEOUT = 2.5f; // seconds
    public static final float UPGRADE_HEIGHT = 1.6f;
    public static final float SOUL_FIRE_HEIGHT = 0f;
    public static final float SOUL_FIRE_RANGE = 2f;
    public static final float SOUL_FIRE_THROW_DISTANCE = 2.5f;

    static {
        keys.put("forward", Input.Keys.W);
        keys.put("back", Input.Keys.S);
        keys.put("left", Input.Keys.A);
        keys.put("right", Input.Keys.D);
        keys.put("jump", Input.Keys.SPACE);
        keys.put("run", Input.Keys.SHIFT_LEFT);
        keys.put("advance", Input.Keys.R);
    }
}
