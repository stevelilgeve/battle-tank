import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Image;

/**
 * Typical about box with a string and an image.
 */
public class About {

	/** Copyright notice */
	private static final String copyright = "Copyright © 2006 Stepan Stolyarov.\nAll rights reserved.\n";

	private static final String ICON = "/BattleTank.png";

	/**
	 * Do not allow anyone to create this class
	 */
	private About() {
	};

	/**
	 * Put up the About box and when the user click ok return to the previous
	 * screen.
	 * 
	 * @param display
	 *            The <code>Display</code> to return to when the about screen
	 *            is dismissed.
	 */
	public static void showAbout(Display display) {
		Alert alert = new Alert("About BattleTank");
		alert.setTimeout(Alert.FOREVER);

		if (display.numColors() > 2) {
			try {
				Image image = Image.createImage(ICON);
				alert.setImage(image);
			} catch (java.io.IOException x) {
				// just don't append the image.
			}
		}
		// Add the copyright
		alert.setString(copyright);

		display.setCurrent(alert);
	}

}
