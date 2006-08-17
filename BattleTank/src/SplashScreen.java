import javax.microedition.lcdui.*;

class SplashScreen extends Canvas implements Runnable {
	private final BattleTankMIDlet midlet;

	private Image splashImage;

	private volatile boolean dismissed = false;

	SplashScreen(BattleTankMIDlet midlet) {
		this.midlet = midlet;
		setFullScreenMode(true);
		splashImage = BattleTankMIDlet.createImage("/Splash.png");
		new Thread(this).start();
	}

	public void run() {
		synchronized (this) {
			try {
				wait(3000L); // 3 seconds
			} catch (InterruptedException e) {
			}
			dismiss();
		}
	}

	public void paint(Graphics g) {
		int width = getWidth();
		int height = getHeight();
		g.setColor(0x00000000);
		g.fillRect(0, 0, width, height);

		g.setColor(0x00FF0000); // red
		g.drawRect(1, 1, width - 3, height - 3);
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
