package com.ducksteam.needleseye;

import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.glutils.HdpiMode;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		boolean fullscreen = false;

		Graphics.DisplayMode primaryDesktopMode = Lwjgl3ApplicationConfiguration.getDisplayMode();
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("Dust Team");
        if (fullscreen) {
            config.setFullscreenMode(primaryDesktopMode);
        } else {
            config.setWindowedMode(1280, 720);
        }
        config.setDecorated(true);
		config.useVsync(false);
		config.setHdpiMode(HdpiMode.Logical);

		new Lwjgl3Application(new Main(), config);
	}
}
