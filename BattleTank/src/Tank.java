import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.LayerManager;
import javax.microedition.lcdui.game.Sprite;

abstract class Tank extends Sprite {

	protected int speed;

	static final int WIDTH = 16;

	static final int HEIGHT = 16;

	static final Image TANKS_IMAGE = BattleTankMIDlet
			.createImage("/Objects.png");

	static final int[][] animation = { { 0 }, { 1 }, { 2 }, { 3 } };

	static final int POOL_SIZE = 20;

	private static Tank TANK_POOL[];

	static {
		TANK_POOL = new Tank[POOL_SIZE + 1];
		TANK_POOL[0] = new HeroTank();
		initEnemyPool("");
	}

	boolean isAlive = true;

	boolean isShooting = false;

	int direction = BattlegroundScreen.NORTH;

	int animationTick;

	/**
	 * 
	 */
	private int spawningTicks = 0;

	static Battleground battleground;

	/**
	 * Enemies could be frozen by Powerup.CLOCK.
	 */
	private static int immobilizedTicks = 0;

	private static int enemiesSpawned;

	private static LayerManager layerManager;

	Tank() {
		super(TANKS_IMAGE, WIDTH, HEIGHT);
		// defineCollisionRectangle(1, 1, WIDTH - 2, HEIGHT - 2);
		defineReferencePixel(WIDTH / 2, HEIGHT / 2);
	}
	
	public void tick() {
		if (!isVisible())
			return;

		animationTick++;
		
		if (spawningTicks > 0) {
			nextFrame();
			spawningTicks--;
			return;
		}		
	}

	void drive() {
		switch (direction) {
		case BattlegroundScreen.NORTH:
			if ((getY() > 0)
					&& !battleground.containsImpassableArea(getX(),
							getY() - speed, getWidth(), speed)) {
				tryMove(0, -speed);
			}
			break;
		case BattlegroundScreen.EAST:
			if ((getX() < battleground.getWidth() - getWidth())
					&& !battleground.containsImpassableArea(
							getX() + getWidth(), getY(), speed, getHeight())) {
				tryMove(speed, 0);
			}
			break;
		case BattlegroundScreen.SOUTH:
			if ((getY() < battleground.getHeight() - getHeight())
					&& !battleground.containsImpassableArea(getX(),
							getY() + getHeight(), getWidth(), speed)) {
				tryMove(0, speed);
			}
			break;
		case BattlegroundScreen.WEST:
			if ((getX() > 0)
					&& !battleground.containsImpassableArea(getX() - speed,
							getY(), speed, getHeight())) {
				tryMove(-speed, 0);
			}
			break;
		}
	}

	void tryMove(int dx, int dy) {
		move(dx, dy);
		if (overlapsTank(this))
			move(-dx, -dy);
	}

	void changeDirection(int direction) {
		if (direction == this.direction) {
			// nextFrame(); // TODO Animate tracks.
			return;
		}

		if (direction == BattlegroundScreen.NONE)
			return;

		this.direction = direction;
		if (direction == BattlegroundScreen.NORTH
				|| direction == BattlegroundScreen.SOUTH) {
			setPosition((getX() + 2) & 0xfffffff8, getY());
		} else {
			setPosition(getX(), (getY() + 2) & 0xfffffff8);
		}

		setFrame(animation[direction][0]); // TODO Animate tracks.
	}

	static void appendToLayerManager(LayerManager manager) {
		layerManager = manager;
		reappendToLayerManager();
	}

	private static void reappendToLayerManager() {
		if (layerManager == null)
			return;
		for (int i = 0; i < POOL_SIZE + 1; i++)
			layerManager.append(TANK_POOL[i]);
	}

	static void tickEnemies() {
		if (immobilizedTicks > 0) {
			--immobilizedTicks;
			return;
		}
		for (int i = 1; i < POOL_SIZE + 1; i++) {
			getEnemy(i).tick();
		}
	}

	static boolean overlapsTank(Sprite sprite) {
		for (int i = 0; i < POOL_SIZE + 1; i++) {
			if (sprite.collidesWith(TANK_POOL[i], false)
					&& sprite != TANK_POOL[i])
				return true;
		}
		return false;
	}

	static HeroTank getHero() {
		return (HeroTank) TANK_POOL[0];
	}

	static EnemyTank getEnemy(int i) {
		return (EnemyTank) TANK_POOL[i];
	}

	/**
	 * Explode a tank.
	 */
	protected void explode() {
		Explosion.explode(getX() + WIDTH / 2, getY() + HEIGHT / 2, Explosion.BIG);
		setVisible(false);
	}

	/**
	 * Shoot the bullet in the given direction. Subclasses
	 * can override this method to shoot different bullets
	 * of different strength and speed.
	 */
	abstract void shoot();

	/**
	 * Be hit by a bullet.
	 */
	abstract void hit();

	/**
	 * Player has collected {@see Powerup.CLOCK}. Enemies should freeze for a few seconds.
	 */
	public static void immobilizeEnemies() {
		immobilizedTicks = 100;	// TODO Work out optimal value.		
	}

	/**
	 * Player has collected {@see Powerup.BOMB}. Enemies should explode immediately.
	 */
	public static void explodeAllEmenies() {
		for (int i = 1; i < POOL_SIZE + 1; ++i) {
			Tank tank = TANK_POOL[i];
			if (tank.isVisible())
				tank.explode();
		}
	}
	
	public static void initEnemyPool(String descr) {
		if (descr.length() < POOL_SIZE) {
			descr += "11111111111111111111";
		}
		for (int i = 1; i < POOL_SIZE + 1; ++i) {
			TANK_POOL[i] = TankFactory.createTank(Character.digit(descr.charAt(i - 1), 10));
		}
		enemiesSpawned = 0;
		reappendToLayerManager();
	}

	public static void spawnNextEnemy() {
		// TODO Auto-generated method stub
		if (++enemiesSpawned < POOL_SIZE) {
			Tank enemy = TANK_POOL[enemiesSpawned];
			enemy.setPosition(0, 0);
			enemy.changeDirection(BattlegroundScreen.SOUTH);
			enemy.spawn();
		} else {
			BattleTankMIDlet.nextLevel();
		}
	}
	
	public void spawn() {
//		spawningTicks = 10;
		setVisible(true);
	}

	public static void spawnHero() {
		getHero().spawn();
	}

}
