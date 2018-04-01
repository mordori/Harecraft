package fi.tamk.tiko.harecraft.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import fi.tamk.tiko.harecraft.GameMain;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1280;
		//config.width = 1920;
		config.height = 800;
		//config.height = 1080;
		config.title = "Harecraft";
		config.samples = 4;
		new LwjglApplication(new GameMain(), config);
	}
}
