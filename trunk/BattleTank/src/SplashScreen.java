import javax.microedition.lcdui.*;

class SplashScreen extends Canvas implements Runnable {
	private final BattleTankMIDlet midlet;

	private Image splashImage;

	private volatile boolean dismissed = false;

	SplashScreen(BattleTankMIDlet midlet) {
		this.midlet = midlet;
		setFullScreenMode(true);
		splashImage = BattleTankMIDlet.createImage("/Splash.png");
		this.setTitle("BattleTanks");
		new Thread(this).start();
	}

	public void run() {
		synchronized (this) {
			try {
				wait(1000L); // 3 seconds
			} catch (InterruptedException e) {
			}
			dismiss();
		}
	}

	public void paint(Graphics g) {
		int width = getWidth();
		int height = getHeight();
		String s = "Loading...";
		String b = "Battletanks";
		g.setColor(0x00000000);
		g.fillRect(0, 0, width, height);
		g.setColor(0x00FF0000); // red
		g.drawRect(1, 1, width - 3, height - 3);
		g.setColor(0xFFFFFFFF);
		g.drawString(s,width/2 - (g.getFont().stringWidth(s)/2),height/2+g.getFont().getHeight()*2,0);
		g.drawString(b,width/2 - (g.getFont().stringWidth(b)/2),height/2-g.getFont().getHeight()*4,0);
		if (splashImage != null) {
			g.drawImage(splashImage, width / 2, height / 2, Graphics.VCENTER
					| Graphics.HCENTER);
			splashImage = null;
			midlet.splashScreenPainted();
		}
	}

	public synchronized void keyPressed(int keyCode) {
		dismiss();
	}

	private void dismiss() {
		if (!dismissed) {
			dismissed = true;
			midlet.splashScreenDone();
		}
	}
}
