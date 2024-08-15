package com.ducksteam.needleseye.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;
import com.ducksteam.needleseye.Config;
import com.ducksteam.needleseye.Main;
import com.ducksteam.needleseye.entity.enemies.EnemyEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

import static com.ducksteam.needleseye.Main.*;

/**
 * Handles player input
 * @author SkySourced
 * @author thechiefpotatopeeler
 */
public class PlayerInput implements InputProcessor {

    // The keys that are currently pressed
    public static final HashMap<Integer, Boolean> KEYS = new HashMap<>();

    static Vector3 tmp = new Vector3(); // used for various calculations
    static Vector3 tmp2 = new Vector3();

    /**
     * Updates the player's velocity based on the keys pressed.
     */
    public static void update(float delta) {
        // update player speed by sprinting & multiplier
        Config.moveSpeed = KEYS.containsKey(Config.keys.get("run")) && KEYS.get(Config.keys.get("run")) ? Config.RUN_SPEED : Config.WALK_SPEED;
        Config.moveSpeed *= player.playerSpeedMultiplier;

        Vector3 forceDir = new Vector3(); // the direction the player should move in

        Vector3 moveVec = player.eulerRotation.cpy().nor().scl(Config.moveSpeed); // the direction the player is facing, scaled to speed

        if(KEYS.containsKey(Config.keys.get("forward")) && KEYS.get(Config.keys.get("forward"))){
            forceDir.add(moveVec);
        }
        if(KEYS.containsKey(Config.keys.get("back")) && KEYS.get(Config.keys.get("back"))){
            forceDir.sub(moveVec);
        }
        if(KEYS.containsKey(Config.keys.get("left")) && KEYS.get(Config.keys.get("left"))){
            tmp.set(moveVec).nor().crs(Vector3.Y); // cross product with up vector to get left vector
            forceDir.sub(tmp); // subtract left vector
        }
        if(KEYS.containsKey(Config.keys.get("right")) && KEYS.get(Config.keys.get("right"))){
            tmp.set(moveVec).nor().crs(Vector3.Y); // cross product with up vector to get left vector
            forceDir.add(tmp); // add left vector
        }

        // jumping logic
        if(KEYS.containsKey(Config.keys.get("jump")) && KEYS.get(Config.keys.get("jump")) && Math.abs(player.getVelocity().y) < 0.5 && !player.isJumping){
            player.collider.applyCentralImpulse(new Vector3(0, 50, 0));
        }

        // advance to next level
        if(KEYS.containsKey(Config.keys.get("advance")) && KEYS.get(Config.keys.get("advance"))){
            // if all enemies in current level are dead
            if (entities.values().stream().filter(e -> e instanceof EnemyEntity).map(e -> (EnemyEntity) e).collect(Collectors.toCollection(ArrayList::new)).isEmpty()) {
                Main.advanceLevel();
            }
        }

        forceDir.y = 0; // remove y component from walking as gravity
        forceDir.nor().scl(Config.moveSpeed * delta); // normalize and scale to speed
        player.collider.applyCentralImpulse(forceDir); // apply to the collider
    }

    /**
     * Rotates the camera based on the mouse movement.
     */
    private void rotateCamera() {
        // scale mouse values
        float deltaX = Gdx.input.getDeltaX() * Config.ROTATION_SPEED;
        float deltaY = Gdx.input.getDeltaY() * Config.ROTATION_SPEED;

        // reset cursor position
        Gdx.input.setCursorPosition(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);

        tmp2 = player.getEulerRotation().cpy().rotateRad(camera.up, -deltaX); // rotate player rotation by x movement
        tmp2.rotateRad(camera.direction.cpy().crs(camera.up).nor(), -deltaY); // rotate player rotation by y movement

        // lock the player from looking up or down too far
        if (tmp2.y > 0.95) tmp2.y = 0.95f;
        if (tmp2.y < -0.95) tmp2.y = -0.95f;
        tmp2.nor();

        if (Math.abs(tmp2.y) > 0.95) return; // don't set if the rotation is invalid

        player.setEulerRotation(tmp2); // set the player rotation
    }

    /**
     * Adds a key to the keys map when it is pressed.
     * @param i the key code
     * @return true if the event is handled
     */
    @Override
    public boolean keyDown(int i) {
        KEYS.put(i, true);
        return true;
    }

    /**
     * Removes a key from the keys map when it is released.
     * @param i the key code
     * @return true if the event is handled
     */
    @Override
    public boolean keyUp(int i) {
        KEYS.put(i, false);
        return true;
    }

    /**
     * Rotates the camera when the mouse is dragged.
     * @param mouseX the x coordinate of the mouse
     * @param mouseY the y coordinate of the mouse
     * @param pointer the button held down
     * @return true if the event is handled
     */
    @Override
    public boolean touchDragged(int mouseX, int mouseY, int pointer) {
        rotateCamera();
        return true;
    }

    /**
     * Rotates the camera when the mouse is moved without a button held down.
     * @param mouseX the x coordinate of the mouse
     * @param mouseY the y coordinate of the mouse
     * @return true if the event is handled
     */
    @Override
    public boolean mouseMoved(int mouseX, int mouseY) {
        rotateCamera();
        return true;
    }

    /**
     * Handles mouse clicks.
     * @param x the x coordinate of the mouse
     * @param y the y coordinate of the mouse
     * @param pointer the pointer
     * @param button the button pressed
     * @return true as the event has been handled
     */
    @Override
    public boolean touchDown(int x, int y, int pointer, int button) {
        if (button == Input.Buttons.LEFT && player.getAttackTimeout() <= 0) player.primaryAttack();
        if (button == Input.Buttons.RIGHT) player.ability();
        return true;
    }

    // Unused methods from InputProcessor

    @Override
    public boolean keyTyped(char c) {
        return false;
    }

    @Override
    public boolean touchUp(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchCancelled(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean scrolled(float v, float v1) {
        return false;
    }
}
