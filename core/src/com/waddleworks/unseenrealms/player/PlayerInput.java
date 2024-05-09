package com.waddleworks.unseenrealms.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.math.Vector3;
import com.waddleworks.unseenrealms.Config;
import com.waddleworks.unseenrealms.Main;

import java.util.HashMap;

import static com.waddleworks.unseenrealms.Main.camera;
import static com.waddleworks.unseenrealms.Main.player;

public class PlayerInput implements InputProcessor, ControllerListener {

    private static HashMap<Integer, Boolean> keys = new HashMap<>();
    private static boolean skipNextMouseMoved = false;
    private int degreesPerPixel;

    protected final Vector3 tmp = new Vector3();

    public void update() {
        update(Gdx.graphics.getDeltaTime());
    }

    public void update(float dT) {
        player.setVel(Vector3.Zero);
        if(keys.containsKey(Config.keys.get("forward"))){
            player.getVel().add(player.getRot());
        }
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
    public boolean touchDragged(int mouseX, int mouseY, int pointer) {
        float deltaX = -Gdx.input.getDeltaX() * degreesPerPixel;
        float deltaY = -Gdx.input.getDeltaY() * degreesPerPixel;
        camera.direction.rotate(camera.up, deltaX);
        tmp.set(camera.direction).crs(camera.up).nor();
        camera.direction.rotate(tmp, deltaY);
        return true;
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
