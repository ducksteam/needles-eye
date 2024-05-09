package com.waddleworks.unseenrealms.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.math.Vector3;
import com.waddleworks.unseenrealms.Main;

public class PlayerInput implements InputProcessor, ControllerListener {
    @Override
    public boolean keyDown(int i) {
        Vector3 rot = Main.player.getRot();
        Main.player.getVel().set(Vector3.Zero);
        return switch (i) {
            case Input.Keys.DOWN -> {
                Main.player.getVel().add(new Vector3((float) Math.sin(rot.y), 0, (float) Math.cos(rot.y)));
                yield true;
            }
            case Input.Keys.UP -> {
                Main.player.getVel().add(new Vector3((float) -Math.sin(rot.y), 0, (float) -Math.cos(rot.y)));
                yield true;
            }
            case Input.Keys.RIGHT -> {
                Main.player.getVel().add(new Vector3((float) Math.sin(rot.y + Math.PI / 2), 0, (float) Math.cos(rot.y + Math.PI / 2)));
                yield true;
            }
            case Input.Keys.LEFT -> {
                Main.player.getVel().add(new Vector3((float) Math.sin(rot.y - Math.PI / 2), 0, (float) Math.cos(rot.y - Math.PI / 2)));
                yield true;
            }
            case Input.Keys.SPACE -> {
                if (Main.player.getVel().y == 0) Main.player.getVel().y = 5;
                yield true;
            }
            case Input.Keys.ESCAPE -> {
                // TODO: Update to use new gamestate
                Main.menu = !Main.menu;
                yield true;
            }
            default -> false;
        };
    }

    @Override
    public boolean keyUp(int i) {
        Vector3 rot = Main.player.getRot();
        return switch (i) {
            case Input.Keys.DOWN -> {
                Main.player.getVel().sub(new Vector3((float) Math.sin(rot.y), 0, (float) Math.cos(rot.y)));
                yield true;
            }
            case Input.Keys.UP -> {
                Main.player.getVel().sub(new Vector3((float) -Math.sin(rot.y), 0, (float) -Math.cos(rot.y)));
                yield true;
            }
            case Input.Keys.RIGHT -> {
                Main.player.getVel().sub(new Vector3((float) Math.sin(rot.y + Math.PI / 2), 0, (float) Math.cos(rot.y + Math.PI / 2)));
                yield true;
            }
            case Input.Keys.LEFT -> {
                Main.player.getVel().sub(new Vector3((float) Math.sin(rot.y - Math.PI / 2), 0, (float) Math.cos(rot.y - Math.PI / 2)));
                yield true;
            }
            default -> false;
        };
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
        return false;
    }

    @Override
    public boolean mouseMoved(int mouseX, int mouseY) {
        mouseX -= Gdx.graphics.getWidth()/2;
        mouseY -= Gdx.graphics.getHeight()/2;
        if (Main.menu) return false;

        if (mouseX != 0 && mouseY != 0) Gdx.app.debug("Mouse", "Moved" + mouseX + " " + mouseY);

        Vector3 rot = Main.player.getRot();

        rot.y += -mouseX * 100f;
        rot.x += (float) Math.sin(-mouseY * 100f);
        rot.z += (float) Math.cos(-mouseY * 100f);

        Main.player.setRot(rot);

        Gdx.input.setCursorPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
        Main.camera.update();
        return true;
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
