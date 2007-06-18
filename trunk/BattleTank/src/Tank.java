import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.LayerManager;
import javax.microedition.lcdui.game.Sprite;
import java.util.Random;

abstract class Tank extends Sprite {

	protected int speed;

	public static final int WIDTH = 16;

	public static final int HEIGHT = 16;

	private static final Image TANKS_IMAGE = BattleTankMIDlet
			.createImage("/Objects.png");

	private final int[][] animation = getAnimation();

	static final int POOL_SIZE = 4;   // 1 hero + 3 others

	private static Tank TANK_POOL[];
	private static int enemiesToSpawn;
	private static String enemyString;
	protected int pool_pos;
	
	static {
		TANK_POOL = new Tank[POOL_SIZE];
		TANK_POOL[0] = new HeroTank();
		initEnemyPool("");
	}

	protected boolean isAlive = true;

	protected boolean isShooting = false;

	protected int direction = BattlegroundScreen.NORTH;

	protected int animationTick;
	
	/* Places where tanks may spawn - max 8 per level. Co-ords encoded as block number, counting right and down, with top left as 0,0 */
	static protected int[] spawnPoints = new int[8];
	static protected int numSpawns = 1;
	static protected Random rand = new Random();
	static void addSpawnPoint(int x, int y){
		if(numSpawns < 8){
			spawnPoints[numSpawns-1] = y*13 +x;
			numSpawns++;
		}
	}

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

	protected static LayerManager layerManager;

	Tank() {
		super(TANKS_IMAGE, WIDTH, HEIGHT);
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
					&& !battleground.containsImpassableArea(getX(),getY() - speed, getWidth(), speed)) {
				tryMove(0, -speed);
			}
			break;
		case BattlegroundScreen.EAST:
			if ((getX() < battleground.getWidth() - getWidth())
					&& !battleground.containsImpassableArea(getX() + getWidth(), getY(), speed, getHeight())) {
				tryMove(speed, 0);
			}
			break;
		case BattlegroundScreen.SOUTH:
			if ((getY() < battleground.getHeight() - getHeight())
					&& !battleground.containsImpassableArea(getX(),getY() + getHeight(), getWidth(), speed)) {
				tryMove(0, speed);
			}
			break;
		case BattlegroundScreen.WEST:
			if ((getX() > 0)
					&& !battleground.containsImpassableArea(getX() - speed,getY(), speed, getHeight())) {
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

	public void changeDirection(int direction) {
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
		//reappendToLayerManager();
	}

	private static void reappendToLayerManager() {
		if (layerManager == null)
			return;
		for (int i = 0; i < POOL_SIZE; i++)
			layerManager.append(TANK_POOL[i]);
	}

	static void tickEnemies() {
		if (immobilizedTicks > 0) {
			--immobilizedTicks;
			return;
		}
		for (int i = 1; i < POOL_SIZE; i++) {
			if(TANK_POOL[i]!=null)
				((EnemyTank)TANK_POOL[i]).tick();
		}
	}

	static boolean overlapsTank(Sprite sprite) {
		for (int i = 0; i < POOL_SIZE; i++) {
			if (TANK_POOL[i]!=null && sprite != TANK_POOL[i] && sprite.collidesWith(TANK_POOL[i], false))
				return true;
		}
		return false;
	}

	static HeroTank getHero() {
		return (HeroTank) TANK_POOL[0];
	}

	static EnemyTank getEnemy(int i) {
		if(TANK_POOL[i]!=null){
			return (EnemyTank) TANK_POOL[i];
		}else{
			return null;
		}
	}

	/**
	 * Explode a tank.
	 */
	protected void explode() {
		Explosion.explode(getRefPixelX(), getRefPixelY(), Explosion.BIG);
		setVisible(false);
		layerManager.remove(this);
	}
	
	abstract protected int[][] getAnimation();

	/**
	 * Shoot the bullet in the given direction. Subclasses
	 * can override this method to shoot different bullets
	 * of different strength and speed.
	 */
	abstract public void shoot();

	/**
	 * Be hit by a bullet.
	 */
	abstract public void hit();
	
	/* Tank just exploded, but it's done now.*/
	public void doneExploding(){}

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
		for (int i = 1; i < POOL_SIZE; ++i) {
			Tank tank = TANK_POOL[i];
			if (tank.isVisible())
				tank.explode();
		}
	}
	
	public static void removeAllEnemies(){
		for(int i=1; i < POOL_SIZE; ++i){
			if(TANK_POOL[i]!=null)
				layerManager.remove(TANK_POOL[i]);
		}
	}
	
	public static void initEnemyPool(String descr) {
		enemiesToSpawn = descr.length();
		enemyString = descr;
		/*
		if (descr.length() < POOL_SIZE) {
			descr += "11111111111111111111";
		}
		for (int i = 1; i < POOL_SIZE + 1; ++i) {
			TANK_POOL[i] = TankFactory.createTank(Character.digit(descr.charAt(i - 1), 10));
		}
		enemiesSpawned = 0;
		reappendToLayerManager();
		*/
	}

	public static void spawnNextEnemy(int pool) {
		// TODO Auto-generated method stub
		if (enemiesSpawned < enemiesToSpawn) {
			boolean placed = false;
			Tank enemy = TankFactory.createTank(enemyString.charAt(enemiesSpawned++));
			int tries = 20;
			while(!placed && tries > 0){
				tries--;
				int position = spawnPoints[rand.nextInt(numSpawns)];
				int spx = (position % 13) * Battleground.TILE_WIDTH;
				int spy = ((int)position/13)*Battleground.TILE_HEIGHT;
				enemy.setPosition(spx,spy);
				enemy.setVisible(true); // silly but it makes the overlaps tank thing work
				if(!overlapsTank(enemy)){
					placed = true;
					enemy.changeDirection(BattlegroundScreen.SOUTH);
					enemy.spawn();
				}else{
					System.out.println("tank overlapped, trying to place again");
				}					
			}
			if(placed){
				enemy.pool_pos = pool;
				TANK_POOL[pool] = enemy;
				layerManager.append(enemy);
			}
		} else {
			System.out.println("enemies all spawned...");
			for(int i = 1; i < POOL_SIZE; i++){
				if(TANK_POOL[i].isVisible()){return;}
			}
			enemiesSpawned = 0;
			BattleTankMIDlet.nextLevel();
		}
	}
	
	public void spawn() {
//		spawningTicks = 10;
		setVisible(true);
	}

	public static void spawnHero() {
		getHero().spawn();
		layerManager.append(getHero());
	}
	
	public static void restart(){
		removeAllEnemies();
		enemiesSpawned = 0;
		TANK_POOL = new Tank[POOL_SIZE];
		TANK_POOL[0] = new HeroTank();
		initEnemyPool("");
	}

}
