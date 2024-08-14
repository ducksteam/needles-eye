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
		config.setTitle("Needle's Eye");
        if (fullscreen) {
            config.setFullscreenMode(primaryDesktopMode);
        } else {
//            config.setWindowedMode(640*2, 360*2);
        	config.setWindowedMode(1920, 1080);
		}
        config.setDecorated(true);
		config.useVsync(false);
		config.setHdpiMode(HdpiMode.Pixels);
		config.setWindowIcon("icon/icon_128.png", "icon/icon_64.png", "icon/icon_32.png", "icon/icon_16.png");

		Main main = new Main();
		main.setSplashWorker(new DesktopSplashWorker());

		new Lwjgl3Application(main, config);
	}
}
