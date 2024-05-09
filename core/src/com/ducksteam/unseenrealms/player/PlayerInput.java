package com.ducksteam.unseenrealms.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.math.Vector3;
import com.ducksteam.unseenrealms.Main;
import com.ducksteam.unseenrealms.Config;

import java.util.HashMap;

public class PlayerInput implements InputProcessor, ControllerListener {

    private static HashMap<Integer, Boolean> keys = new HashMap<>();
    private static boolean skipNextMouseMoved = false;

    protected final Vector3 tmp = new Vector3();

    public void update() {
        update(Gdx.graphics.getDeltaTime());
    }

    public void update(float dT) {
        Gdx.app.debug("keys", keys.toString());
        Main.player.setVel(Vector3.Zero);

        if(keys.containsKey(Config.keys.get("forward")) && keys.get(Config.keys.get("forward"))){
            Main.player.getVel().add(Main.player.getRot().scl(dT * Config.moveSpeed));
        }
        if(keys.containsKey(Config.keys.get("back")) && keys.get(Config.keys.get("back"))){
            Main.player.getVel().add(Main.player.getRot().scl(-dT * Config.moveSpeed));
        }
        if(keys.containsKey(Config.keys.get("left")) && keys.get(Config.keys.get("left"))){
            tmp.set(Main.player.getRot()).crs(Vector3.Y).nor();
            Main.player.getVel().add(tmp.scl(-dT * Config.moveSpeed));
        }
        if(keys.containsKey(Config.keys.get("right")) && keys.get(Config.keys.get("right"))){
            tmp.set(Main.player.getRot()).crs(Vector3.Y).nor();
            Main.player.getVel().add(tmp.scl(dT * Config.moveSpeed));
        }
        Gdx.app.debug("PlayerInput", "Player velocity: " + Main.player.getVel());
    }

    @Override
    public boolean keyDown(int i) {
        keys.put(i, true);
        return true;
    }

    @Override
    public boolean keyUp(int i) {
        keys.put(i, false);
        return true;
    }

    @Override
    public boolean touchDragged(int mouseX, int mouseY, int pointer) {
        float deltaX = -Gdx.input.getDeltaX() * Config.rotationSpeed;
        float deltaY = -Gdx.input.getDeltaY() * Config.rotationSpeed;
        Main.player.getRot().rotate(Main.camera.up, deltaX);
        tmp.set(Main.camera.direction).crs(Main.camera.up).nor();
        Main.player.getRot().rotate(tmp, deltaY);
        return true;
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
    public boolean mouseMoved(int mouseX, int mouseY) {
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
