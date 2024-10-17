package com.ducksteam.needleseye;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

import java.util.HashMap;

/**
 * Configuration class for the game
 * @author SkySourced
 * @author thechiefpotatopeeler
 */
public class Config {
    // Modifiable
    public static HashMap<String, Integer> keys = new HashMap<>();
    public static boolean debugMenu = false;
    public static boolean doRenderColliders = false;
    public static float moveSpeed;

    // Global constants
    public static final Vector3 PLAYER_START_POSITION = new Vector3(-5, 0.52f, -5);
    public static final float ROTATION_SPEED = 0.007F /* * MathUtils.degRad*/;
    public static final float LOADING_ANIM_SPEED = 0.05f;
    public static final float ATTACK_ANIM_SPEED = 0.03f;
    public static final float ASPECT_RATIO = (float) 16 / 9;
    public static final int TARGET_WIDTH = 1920;
    public static final int TARGET_HEIGHT = 1080;
    public static final int ROOM_SCALE = 10;
    public static final float PLAYER_MASS = 10;
    public static final float DAMAGE_TIMEOUT = 1; // seconds
    public static final float DAMAGE_SCREEN_FLASH = 0.5f; // seconds
    public static final float UPGRADE_HEIGHT = 1.6f;
    public static final float SOUL_FIRE_HEIGHT = 0f;
    public static final float SOUL_FIRE_RANGE = 2f;
    public static final float SOUL_FIRE_THROW_DISTANCE = 2.5f;
    private static final float LIGHT_INTENSITY = 1f;
    public static final Color LIGHT_COLOUR = new Color(0.4f* LIGHT_INTENSITY, 0.4f* LIGHT_INTENSITY, 0.4f* LIGHT_INTENSITY, 1);
    public static final float KNOCKBACK_FORCE = 1000;
    public static final float WALK_SPEED = 220f;
    public static final float RUN_SPEED = 300f;
    public static final float UPGRADE_TEXT_DISPLAY_TIMEOUT = 3f;

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
