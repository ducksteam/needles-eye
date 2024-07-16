package com.ducksteam.needleseye.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import com.ducksteam.needleseye.Config;
import com.ducksteam.needleseye.entity.Entity;

import static com.ducksteam.needleseye.Main.camera;
import static com.ducksteam.needleseye.Main.player;

import java.util.HashMap;
import java.util.Vector;

/**
 * Handles player input
 * @author SkySourced
 */
public class PlayerInput implements InputProcessor, ControllerListener {

    // The keys that are currently pressed
    private static final HashMap<Integer, Boolean> KEYS = new HashMap<>();

    static Vector3 tmp = new Vector3();

    float mouseX = 0;
    float mouseY = 0;

    /**
     * Updates the player's velocity based on the keys pressed.
     */
    public static void update() {
        player.setVelocity(Vector3.Zero);

        Config.MOVE_SPEED = KEYS.containsKey(Input.Keys.SHIFT_LEFT) && KEYS.get(Input.Keys.SHIFT_LEFT) ? 5 : 1;

        Vector3 moveVec = player.eulerRotation.cpy().scl(Config.MOVE_SPEED);
        //moveVec.y = 0;

        if(KEYS.containsKey(Config.keys.get("forward")) && KEYS.get(Config.keys.get("forward"))){
            player.getVelocity().add(moveVec);
        }
        if(KEYS.containsKey(Config.keys.get("back")) && KEYS.get(Config.keys.get("back"))){
            player.getVelocity().sub(moveVec);
        }
        if(KEYS.containsKey(Config.keys.get("left")) && KEYS.get(Config.keys.get("left"))){
            tmp.set(player.eulerRotation).cpy().crs(Vector3.Y).nor();
            Gdx.app.debug("PlayerInput", tmp.toString());
            player.getVelocity().sub(tmp.scl(Config.MOVE_SPEED));
        }
        if(KEYS.containsKey(Config.keys.get("right")) && KEYS.get(Config.keys.get("right"))){
            tmp.set(player.eulerRotation).cpy().crs(Vector3.Y).nor();
            player.getVelocity().add(tmp.scl(Config.MOVE_SPEED));
        }
        if(KEYS.containsKey(Config.keys.get("disappear")) && KEYS.get(Config.keys.get("disappear"))){
            tmp.set(player.eulerRotation).cpy().crs(Vector3.Y).nor();
            player.getVelocity().sub(tmp.scl(Config.MOVE_SPEED));
        }

        if (!player.getVelocity().isZero()) Gdx.app.debug("PlayerInput", player.getVelocity().toString());
    }

    /**
     * Rotates the camera based on the mouse movement.
     * @return true if the camera was rotated
     */
    private boolean rotateCamera() {
        float deltaX = Gdx.input.getDeltaX() * Config.ROTATION_SPEED;
        float deltaY = Gdx.input.getDeltaY() * Config.ROTATION_SPEED;

        mouseX -= deltaX;
        mouseY -= deltaY;

        Gdx.input.setCursorPosition(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);

        tmp = player.getEulerRotation().cpy().rotateRad(camera.up, -deltaX);
        tmp.rotateRad(camera.direction.cpy().crs(camera.up).nor(), -deltaY);

        player.setEulerRotation(tmp);

        return true;
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
        return rotateCamera();
    }

    /**
     * Rotates the camera when the mouse is moved without a button held down.
     * @param mouseX the x coordinate of the mouse
     * @param mouseY the y coordinate of the mouse
     * @return true if the event is handled
     */
    @Override
    public boolean mouseMoved(int mouseX, int mouseY) {
        return rotateCamera();
    }

    @Override
    public boolean keyTyped(char c) {
        return false;
    }

    @Override
    public boolean touchDown(int i, int i1, int i2, int i3) {
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

    @Override
    public void connected(Controller controller) {

    }

    @Override
    public void disconnected(Controller controller) {

    }

    @Override
    public boolean buttonDown(Controller controller, int i) {
        return false;
    }

    @Override
    public boolean buttonUp(Controller controller, int i) {
        return false;
    }

    @Override
    public boolean axisMoved(Controller controller, int i, float v) {
        return false;
    }
}
