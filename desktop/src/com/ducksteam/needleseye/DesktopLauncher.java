package com.ducksteam.needleseye;

import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		boolean fullscreen = true;

		Graphics.DisplayMode primaryDesktopMode = Lwjgl3ApplicationConfiguration.getDisplayMode();
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("SchoolProject2024");
        if (fullscreen) {
            config.setFullscreenMode(primaryDesktopMode);
        } else {
            config.setWindowedMode(800, 600);
        }
        config.setDecorated(false);
		config.useVsync(false);

		new Lwjgl3Application(new Main(), config);
	}
}
