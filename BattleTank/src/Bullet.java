import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.LayerManager;
import javax.microedition.lcdui.game.Sprite;

class Bullet extends Sprite {

	public static final int WIDTH = 3;

	public static final int HEIGHT = 5;

	public static final int SLOW = 6;

	public static final int FAST = 8;

	private static final Image BULLET_IMAGE;

	private static final int POOL_SIZE = 20;

	private final int id;

	private int dx, dy;

	private boolean strong;

	private boolean friendly;

	static Battleground battleground;

	private static Bullet[] BULLET_POOL;

	static {
		BULLET_IMAGE = BattleTankMIDlet.createImage("/Bullet.png");
		BULLET_POOL = new Bullet[POOL_SIZE];
		for (int i = 0; i < POOL_SIZE; i++) {
			BULLET_POOL[i] = new Bullet(i);
		}
	}

	Bullet(int id) {
		super(BULLET_IMAGE, WIDTH, HEIGHT);
		this.id = id;
		defineReferencePixel(WIDTH / 2, 0); // Tip of a bullet.
		setVisible(false);
	}

	static Bullet shoot(int x, int y, int direction, int speed, boolean friendly) {
		Bullet bullet = null;
		for (int i = 0; i < POOL_SIZE; i++) {
			if (!BULLET_POOL[i].isVisible()) {
				bullet = BULLET_POOL[i];
				break;
			}
		}

		if (bullet == null)
			return null; // Too many bullets already.

		bullet.friendly = friendly;

		switch (direction) {
		case BattlegroundScreen.NORTH:
			bullet.dx = 0;
			bullet.dy = -speed;
			bullet.setTransform(TRANS_NONE);
			break;
		case BattlegroundScreen.EAST:
			bullet.dx = speed;
			bullet.dy = 0;
			bullet.setTransform(TRANS_ROT90);
			break;
		case BattlegroundScreen.SOUTH:
			bullet.dx = 0;
			bullet.dy = speed;
			bullet.setTransform(TRANS_ROT180);
			break;
		case BattlegroundScreen.WEST:
			bullet.dx = -speed;
			bullet.dy = 0;
			bullet.setTransform(TRANS_ROT270);
			break;
		}
		bullet.setRefPixelPosition(x, y);
		bullet.setVisible(true);

		return bullet;
	}

	static void appendToLayerManager(LayerManager manager) {
		for (int i = 0; i < POOL_SIZE; i++)
			manager.append(BULLET_POOL[i]);
	}

	static void tickBullets() {
		for (int i = 0; i < POOL_SIZE; i++)
			BULLET_POOL[i].tick();
	}

	void tick() {
		if (!isVisible())
			return;

		// Move the bullet.
		move(dx, dy);

		int x = getRefPixelX();
		int y = getRefPixelY();

		if (x < 0 || x >= battleground.getWidth() || y < 0
				|| y >= battleground.getHeight()) {
			explode();
			return;
		}

		// See if it hit a tank.
		if (friendly) {
			// See if it hit an enemy tank.
			for (int i = 1; i <= Tank.POOL_SIZE; i++) {
				EnemyTank enemy = Tank.getEnemy(i);
				if (collidesWith(enemy, false)) {
					enemy.hit();
					explode();
					return;
				}
			}
		} else {
			// See if it hit hero.
			HeroTank hero = Tank.getHero();
			if (collidesWith(hero, false)) {
				hero.hit();
				explode();
				return;
			}
		}

		// See if it hit a wall.
		if (battleground.hitWall(x, y, strong)) {
			explode();
			return;
		}

		// See if it hit another bullet.
		for (int i = id + 1; i < POOL_SIZE; i++) {
			if (collidesWith(BULLET_POOL[i], false)) {
				// NOTE In an original game bullets just disappeared
				// when hit each other.
				explode();
				BULLET_POOL[i].explode();
				return;
			}
		}
	}

	void explode() {
		setVisible(false);
		Explosion.explode(getRefPixelX(), getRefPixelY(), Explosion.SMALL);
	}

	public static void stopAllBullets() {
		for (int i = 0; i < POOL_SIZE; ++i)
			BULLET_POOL[i].setVisible(false);		
	}
}
