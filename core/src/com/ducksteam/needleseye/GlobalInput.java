package com.ducksteam.needleseye;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;

import static com.ducksteam.needleseye.Main.player;

/**
 * An input processor that is always active, regardless of the current game state
 * @author SkySourced
 * @author thechiefpotatopeeler
 */
public class GlobalInput implements InputProcessor {

    /**
     * Called when a key is pressed
     * @param i the key code
     * @return whether the input was handled
     */

    @Override
    public boolean keyDown(int i) {
        if (i == Input.Keys.F8) { // enable/disable collider rendering
            Config.doRenderColliders = !Config.doRenderColliders;
            return true;
        }
        if (i == Input.Keys.F9) { // enable/disable debug menu
            Config.debugMenu = !Config.debugMenu;
            return true;
        }
        if (i == Input.Keys.ESCAPE) { // pause the game
            if (!(Main.gameState == Main.GameState.IN_GAME || Main.gameState == Main.GameState.PAUSED_MENU)) return true;
            if (Main.gameState == Main.GameState.PAUSED_MENU) Main.gameState = Main.GameState.IN_GAME;
            else Main.setGameState(Main.GameState.PAUSED_MENU);
            return true;
        }
        return false;
    }

    /**
     * Called when the mouse is clicked
     * @param x the x coordinate of the mouse
     * @param y the y coordinate of the mouse
     * @param pointer the pointer
     * @param button the button pressed
     * @return false, as the event will be finally handled by the player input
     */
    @Override
    public boolean touchDown(int x, int y, int pointer, int button) {
        if (Main.gameState == Main.GameState.IN_GAME) Gdx.input.setCursorCatched(true); // if in game, catch the cursor
        return false;
    }

    // Unused methods overridden from InputProcessor

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
