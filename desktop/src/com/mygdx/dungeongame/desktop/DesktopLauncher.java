package com.mygdx.dungeongame.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.mygdx.dungeongame.DungeonGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("Dungeon Game");
		config.setWindowedMode(1920, 1080);
		new Lwjgl3Application(new DungeonGame(), config);
	}
}
