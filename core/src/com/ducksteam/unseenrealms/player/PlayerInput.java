package com.ducksteam.unseenrealms.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.math.Vector3;
import com.ducksteam.unseenrealms.Main;
import com.ducksteam.unseenrealms.Config;

import java.util.HashMap;

/**
 * Handles player input
 * @author SkySourced
 */
public class PlayerInput implements InputProcessor, ControllerListener {

    // The keys that are currently pressed
    private static final HashMap<Integer, Boolean> keys = new HashMap<>();

    protected final Vector3 tmp = new Vector3();

    /**
     * Updates the player's velocity based on the keys pressed.
     */
    public void update() {
        Gdx.app.debug("keys", keys.toString());
        Main.player.getVel().set((Vector3.Zero));

        if(keys.containsKey(Config.keys.get("forward")) && keys.get(Config.keys.get("forward"))){
            Main.player.getVel().add(Main.player.getRot().scl(Config.moveSpeed));
        }
        if(keys.containsKey(Config.keys.get("back")) && keys.get(Config.keys.get("back"))){
            Main.player.getVel().sub(Main.player.getRot().scl(Config.moveSpeed));
        }
        if(keys.containsKey(Config.keys.get("left")) && keys.get(Config.keys.get("left"))){
            tmp.set(Main.player.getRot()).crs(Vector3.Y).nor();
            Main.player.getVel().sub(tmp.scl(Config.moveSpeed));
        }
        if(keys.containsKey(Config.keys.get("right")) && keys.get(Config.keys.get("right"))){
            tmp.set(Main.player.getRot()).crs(Vector3.Y).nor();
            Main.player.getVel().add(tmp.scl(Config.moveSpeed));
        }
        Gdx.app.debug("PlayerInput", "Player velocity: " + Main.player.getVel());
    }

    /**
     * Rotates the camera based on the mouse movement.
     * @return true if the camera was rotated
     */
    private boolean rotateCamera() {
        float deltaX = -Gdx.input.getDeltaX() * Config.rotationSpeed;
        float deltaY = -Gdx.input.getDeltaY() * Config.rotationSpeed;
        Main.player.getRot().rotate(Main.camera.up, deltaX);
        tmp.set(Main.camera.direction).crs(Main.camera.up).nor();
        Main.player.getRot().rotate(tmp, deltaY);

        Gdx.input.setCursorPosition(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);

        if (deltaX != 0 || deltaY != 0) Gdx.app.debug("PlayerInput", "Player rotation: " + Main.player.getRot());
        return true;
    }

    /**
     * Adds a key to the keys map when it is pressed.
     * @param i the key code
     * @return true if the event is handled
     */
    @Override
    public boolean keyDown(int i) {
        keys.put(i, true);
        return true;
    }

    /**
     * Removes a key from the keys map when it is released.
     * @param i the key code
     * @return true if the event is handled
     */
    @Override
    public boolean keyUp(int i) {
        keys.put(i, false);
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
