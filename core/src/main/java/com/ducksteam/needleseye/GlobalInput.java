package com.ducksteam.needleseye;

import com.badlogic.gdx.Gdx;
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
        if (Keybind.getKeybind("Toggle Music").keys.contains(i)) {
            if (Main.menuMusic.getVolume() == 0) Main.menuMusic.setVolume(0.3f);
            else Main.menuMusic.setVolume(0);
        }

        if (Keybind.getKeybind("Toggle DebugDrawer").keys.contains(i)) {
            Config.doRenderColliders = !Config.doRenderColliders;
            return true;
        }

        if (Keybind.getKeybind("Toggle Debug Info").keys.contains(i)) {
            Config.debugMenu = !Config.debugMenu;
            return true;
        }

        if (Keybind.getKeybind("Toggle Gravity").keys.contains(i)) {
            float gravity = Main.dynamicsWorld.getGravity().y;
            Main.dynamicsWorld.setGravity(new Vector3(0, gravity == 0 ? -10 : 0, 0));
        }

        if(Keybind.getKeybind("Toggle Room Rendering").keys.contains(i)) {
            if(Main.gameState != Main.GameState.IN_GAME) return false;
            Main.mapMan.getCurrentLevel().getRooms().forEach(room -> room.isRenderable = !room.isRenderable);
            return true;
        }

        if (Keybind.getKeybind("Move Player Up").keys.contains(i)) player.collider.translate(Vector3.Y.cpy().scl(20));

        if (Keybind.getKeybind("Heal").keys.contains(i)) { // heal the player
            player.damage(-1, null);
            return true;
        }

        if (Keybind.getKeybind("Damage").keys.contains(i)) { // damage the player
            player.damage(1, null);
            return true;
        }

        if (Keybind.getKeybind("Pause").keys.contains(i)) { // pause the game
            if (!(Main.gameState == Main.GameState.IN_GAME || Main.gameState == Main.GameState.PAUSED_MENU)) return true;
            if (Main.gameState == Main.GameState.PAUSED_MENU) Main.gameState = Main.GameState.IN_GAME;
            else Main.setGameState(Main.GameState.PAUSED_MENU);
            return true;
        }

        if (Main.mapMan.visualise) {
            if (Keybind.getKeybind("Step Visualiser Forward").keys.contains(i)) Main.mapMan.visualiser.step(-1);
            if (Keybind.getKeybind("Step Visualiser Backward").keys.contains(i)) Main.mapMan.visualiser.step(1);
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
