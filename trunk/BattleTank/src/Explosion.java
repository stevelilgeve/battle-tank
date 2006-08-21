import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.LayerManager;
import javax.microedition.lcdui.game.Sprite;

class Explosion extends Sprite {

	public static final int SMALL = 0;
	public static final int BIG = 1;

	public static final int WIDTH = 32;
	public static final int HEIGHT = 32;
	
	private static final int[][] FRAME_SEQ = new int[][] {
		{ 0, 1, 1 },
		{ 0, 1, 1, 2, 2, 3, 3, 1 },
	};

	private static final Image EXPLOSION_IMAGE = BattleTankMIDlet
			.createImage("/Explosion.png");

	private static final int POOL_SIZE = 10;

	private static Explosion[] EXPLOSIONS_POOL;

	static {
		EXPLOSIONS_POOL = new Explosion[POOL_SIZE];
		for (int i = 0; i < POOL_SIZE; ++i)
			EXPLOSIONS_POOL[i] = new Explosion(SMALL);
	}

	private Explosion(int strength) {
		super(EXPLOSION_IMAGE, WIDTH, HEIGHT);
		defineReferencePixel(WIDTH / 2, HEIGHT / 2);
		setVisible(false);
	}

	private void setStrength(int strength) {
		setFrameSequence(FRAME_SEQ[strength]);
	}

	public static void explode(int x, int y, int strength) {
		for (int i = 0; i < POOL_SIZE; ++i) {
			Explosion explosion = EXPLOSIONS_POOL[i];
			if (!explosion.isVisible()) {
				explosion.setRefPixelPosition(x, y);
				explosion.setFrame(0);
				explosion.setStrength(strength);
				explosion.setVisible(true);
				return;
			}
		}
	}

	public static void tickExplosions() {
		for (int i = 0; i < POOL_SIZE; ++i) {
			Explosion explosion = EXPLOSIONS_POOL[i];
			explosion.tick();
		}
	}

	private void tick() {
		// TODO Change frame and hide if necessary.
		if (!isVisible())
			return;
		nextFrame();
		if (getFrame() == 0)
			setVisible(false);
	}

	static void appendToLayerManager(LayerManager manager) {
		for (int i = 0; i < POOL_SIZE; i++)
			manager.append(EXPLOSIONS_POOL[i]);
	}

	public static void stopAllExplosions() {
		for (int i = 0; i < POOL_SIZE; i++)
			EXPLOSIONS_POOL[i].setVisible(false);
	}

}