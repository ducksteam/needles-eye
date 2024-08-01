package com.ducksteam.needleseye;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;

import static com.ducksteam.needleseye.Main.player;

public class GlobalInput implements InputProcessor {
    @Override
    public boolean keyDown(int i) {
        if (i == Input.Keys.F8) {
            Config.doRenderColliders = !Config.doRenderColliders;
            return true;
        }
        if (i == Input.Keys.F9) {
            Config.debugMenu = !Config.debugMenu;
            return true;
        }
        if (i == Input.Keys.ESCAPE) {
            if(!(Main.gameState == Main.GameState.IN_GAME || Main.gameState == Main.GameState.PAUSED_MENU)) return true;
            if(Main.gameState == Main.GameState.PAUSED_MENU) Main.gameState = Main.GameState.IN_GAME;
            else Main.setGameState(Main.GameState.PAUSED_MENU);
            Gdx.app.debug("GameState", Main.gameState.toString());
            return true;
        }

        if (i == Input.Keys.F10) {
            float gravity = Main.dynamicsWorld.getGravity().y;
            Main.dynamicsWorld.setGravity(new Vector3(0, gravity == 0 ? -10 : 0, 0));
        }

        if (i == Input.Keys.F11) player.collider.translate(Vector3.Y.cpy().scl(50));

        if (i == Input.Keys.NUMPAD_ADD) {
            Main.player.damage(-1);
            return true;
        }
        if (i == Input.Keys.NUMPAD_SUBTRACT) {
            Main.player.damage(1);
            return true;
        }
        return false;
    }

    @Override
    public boolean touchDown(int i, int i1, int i2, int i3) {
        if (Main.gameState == Main.GameState.IN_GAME) Gdx.input.setCursorCatched(true);
        return false;
    }

    @Override
    public boolean keyUp(int i) {
        return false;
    }

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
    public boolean touchDragged(int i, int i1, int i2) {
        return false;
    }

    @Override
    public boolean mouseMoved(int i, int i1) {
        return false;
    }

    @Override
    public boolean scrolled(float v, float v1) {
        return false;
    }
}
