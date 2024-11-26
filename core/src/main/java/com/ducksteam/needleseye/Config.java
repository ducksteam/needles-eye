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
    /**
     * The keys used for player input, see static block for defaults
     * String is the action, Integer is the key code
     */
    public static HashMap<String, Integer> keys = new HashMap<>();

    /**
     * Whether the debug menu is enabled, toggled by default with F9
     */
    public static boolean debugMenu = false;

    /**
     * Whether the bullet debug renderer is enabled, toggled by default with F8
     */
    public static boolean doRenderColliders = false;

    /**
     * The current player move speed, changed in {@link com.ducksteam.needleseye.player.PlayerInput}
     */
    public static float moveSpeed;

    // Global constants
    /**
     * The player's starting position in the central hallway
     */
    public static final Vector3 PLAYER_START_POSITION = new Vector3(-5, 0.52f, -5);
    /**
     * The speed at which the mouse moves the camera
     */
    public static final float ROTATION_SPEED = 0.007F;
    /**
     * The speed of the loading animation (time per frame in seconds)
     */
    public static final float LOADING_ANIM_SPEED = 0.05f;
    /**
     * The speed at which the attack animation plays (time per frame in seconds)
     */
    public static final float ATTACK_ANIM_SPEED = 0.03f;
    /**
     * The ideal aspect ratio of the game
     */
    public static final float ASPECT_RATIO = (float) 16 / 9;
    /**
     * The target width of the game at startup
     */
    public static final int TARGET_WIDTH = 1920;
    /**
     * The target height of the game at startup
     */
    public static final int TARGET_HEIGHT = 1080;
    /**
     * The scale factor from converting from room space to world space
     */
    public static final int ROOM_SCALE = 10;
    /**
     * The mass of the player in kg
     */
    public static final float PLAYER_MASS = 10;
    /**
     * The time in seconds before an entity can be damaged again
     */
    public static final float DAMAGE_TIMEOUT = 1;
    /**
     * The time in seconds the screen flashes red when the player is damaged
     */
    public static final float DAMAGE_SCREEN_FLASH = 0.5f;
    /**
     * The mean height where upgrade entities spawn see {@link com.ducksteam.needleseye.entity.pickups.UpgradeEntity}
     */
    public static final float UPGRADE_HEIGHT = 1.6f;
    /**
     * The height where soul fire particles are spawned see {@link com.ducksteam.needleseye.entity.effect.SoulFireEffectManager}
     */
    public static final float SOUL_FIRE_HEIGHT = 0f;
    /**
     * The AoE range of the soul fire particle effect
     */
    public static final float SOUL_FIRE_RANGE = 2f;
    /**
     * The distance from the player the soul fire particles spawn
     */
    public static final float SOUL_FIRE_THROW_DISTANCE = 2.5f;
    /**
     * The time in seconds enemies are paralysed by jolt thread LMB
     */
    public static final float JOLT_PARALYSE_TIME = 1.5f;
    /**
     * The strength of light effects used in the game
     * This really should be refactored
     */
    public static final float LIGHT_INTENSITY = 0.25f;
    /**
     * The colour that used to be for the players lantern I think?
     * As you can see it is not used
     */
    public static final Color LIGHT_COLOUR = new Color(0.4f* LIGHT_INTENSITY, 0.4f* LIGHT_INTENSITY, 0.4f* LIGHT_INTENSITY, 1f);
    /**
     * The strength of the force applied to entities when they are knocked back
     */
    public static final float KNOCKBACK_FORCE = 200f;
    /**
     * The strength of the force associated with walking at normal speed
     */
    public static final float WALK_SPEED = 220f;
    /**
     * The strength of the force associated with running
     */
    public static final float RUN_SPEED = 300f;
    /**
     * The time in seconds that the upgrade text is displayed
     */
    public static final float UPGRADE_TEXT_DISPLAY_TIMEOUT = 3f;

    static {
        keys.put("forward", Input.Keys.W);
        keys.put("back", Input.Keys.S);
        keys.put("left", Input.Keys.A);
        keys.put("right", Input.Keys.D);
        keys.put("jump", Input.Keys.SPACE);
        keys.put("run", Input.Keys.SHIFT_LEFT);
        keys.put("advance", Input.Keys.R);
        keys.put("ability", Input.Keys.CONTROL_LEFT);
    }
}
