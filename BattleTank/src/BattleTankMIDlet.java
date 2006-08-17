import java.util.Random;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;
import javax.microedition.midlet.MIDlet;

public class BattleTankMIDlet extends MIDlet {
	private static final Random random = new Random();

	private static BattlegroundScreen battleground;

	public BattleTankMIDlet() {
	}

	public void startApp() {
		Displayable current = Display.getDisplay(this).getCurrent();
		if (current == null) {
			// FIXME A splash screen should be created instead.
			battleground = new BattlegroundScreen(this);
			Display.getDisplay(this).setCurrent(battleground);
			battleground.start();
		}
	}

	public void pauseApp() {
		Displayable current = Display.getDisplay(this).getCurrent();
		if (current == battleground) {
			battleground.stop();
		}
	}

	public void destroyApp(boolean unconditional) {
		if (battleground != null) {
			battleground.stop();
		}
	}

	void exitRequested() {
		destroyApp(false);
		notifyDestroyed();
	}

	static int random(int size) {
		return (random.nextInt() & 0x7FFFFFFF) % size;
	}

	static Image createImage(String filename) {
		Image image = null;
		try {
			image = Image.createImage(filename);
		} catch (java.io.IOException ex) {
			// just let return value be null
		}
		return image;
	}

	public void splashScreenPainted() {
		// TODO Auto-generated method stub
		
	}

	public void splashScreenDone() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * End the game when player has no lives left or the bird is killed. 
	 */
	public static void gameOver() {
		battleground.gameOver();
	}

	public static void nextLevel() {
		// TODO Auto-generated method stub
		battleground.stop();
		battleground.nextLevel();
		battleground.start();
	}
}
