package com.ducksteam.needleseye;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.glutils.HdpiMode;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

		config.setTitle("Needle's Eye"); // window title
		config.setWindowedMode(1920, 1080); // resolution
        config.setDecorated(true); // enable OS window options
		config.useVsync(false); // disable vsync
		config.setHdpiMode(HdpiMode.Pixels); // use actual pixels for resolution
		config.setWindowIcon("icon/icon_128.png", "icon/icon_64.png", "icon/icon_32.png", "icon/icon_16.png"); // application icon

		Main main = new Main(); // create the main game object
		main.setSplashWorker(new DesktopSplashWorker()); // set the splash screen

		new Lwjgl3Application(main, config); // start the game
	}
}
