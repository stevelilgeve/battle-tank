import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
/**
 * Typical about box with a string and an image.
 */
public class About extends Canvas implements CommandListener{

	/** Copyright notice */
	private static final String copyright = "Copyright © 2006";
	private static final String copyright2 = "Stepan Stolyarov";
	private static final String copyright3 = "All rights reserved.";

	private static final String ICON = "/BattleTank.png";
	
	private Displayable lastDisplayable;
	private Display display;
	
	public About(Displayable d, Display dis){ 
		this.addCommand(new Command("OK",Command.OK,1));
		lastDisplayable = d;
		display = dis;
		this.setCommandListener(this);
	}

	public void paint(Graphics g){
		int width = getWidth();
		int height = getHeight();
		int centerx = width/2;
		int centery = width/2;
		Font f = g.getFont();
		Graphics graphics = g;
		graphics.setColor(0xffffffff);
		graphics.fillRect(0, 0, width, height);
		Image image;
		try{
			image = Image.createImage(ICON);
			g.drawImage(image,centerx-8,centery-8,0);
		}catch(java.io.IOException e){}
		graphics.setColor(0x00000000);
		g.drawString(copyright, centerx-(f.stringWidth(copyright)/2),centery+f.getHeight(),0);
		g.drawString(copyright2, centerx-(f.stringWidth(copyright)/2),centery+f.getHeight()*2,0);
		g.drawString(copyright3, centerx-(f.stringWidth(copyright)/2),centery+f.getHeight()*3,0);
	}
	
	/**
	 * Put up the About box and when the user click ok return to the previous
	 * screen.
	 * 
	 * @param display
	 *            The <code>Display</code> to return to when the about screen
	 *            is dismissed.
	 */
	public static void showAbout(Display display) {
		About a = new About(display.getCurrent(),display);
		display.setCurrent(a);
	}
	
	/* Magic key codes for back/clear/C */
	protected void keyPressed(int keyCode) {
		if(keyCode == -11 || keyCode == -8){
			display.setCurrent(lastDisplayable);
		}
	}
	
	public void commandAction(Command command, Displayable adisplay) {
		// TODO Auto-generated method stub
		 if (command.getCommandType() == Command.OK) {
			display.setCurrent(lastDisplayable);
		}

	}


}
