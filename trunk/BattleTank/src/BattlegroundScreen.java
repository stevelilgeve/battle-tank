import java.io.InputStream;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.lcdui.game.LayerManager;

class BattlegroundScreen extends GameCanvas implements Runnable,
		CommandListener {

	public static final int NONE = -1;
	public static final int NORTH = 0;
	public static final int EAST = 1;
	public static final int SOUTH = 2;
	public static final int WEST = 3;

	private static final int MILLIS_PER_TICK = 50;

	private final BattleTankMIDlet midlet;

	private final Battleground battleground;

	private final HeroTank hero;

	private final LayerManager layerManager;

	private final Graphics graphics;

	private static final Font bigBoldFont = Font.getFont(Font.FONT_STATIC_TEXT,
			Font.STYLE_BOLD, Font.SIZE_LARGE);

	private volatile Thread animationThread = null;

	/** The direction in which player is driving. */
	private int direction = NONE;

	/** Should the player shoot? */
	private boolean shoot = false;

	private boolean gameOver = false;

	/**
	 * Current level.
	 */
	private int currentLevel = 0;

	BattlegroundScreen(BattleTankMIDlet midlet) {
		super(false);
		this.midlet = midlet;
		setFullScreenMode(true);
		graphics = getGraphics();

		layerManager = new LayerManager();

		battleground = new Battleground();
		Tank.battleground = battleground;
		Bullet.battleground = battleground;

		Explosion.appendToLayerManager(layerManager);
		Bullet.appendToLayerManager(layerManager);
		layerManager.append(battleground);
		Tank.appendToLayerManager(layerManager);
		
		hero = Tank.getHero();

		nextLevel();

		addCommand(new Command("About", Command.HELP, 1));
		addCommand(new Command("Exit", Command.EXIT, 2));
		setCommandListener(this);
	}

	/**
	 * Load next level and initialize.
	 */
	void nextLevel() {
		readLevel(++currentLevel);
		
		Bullet.stopAllBullets();
		Explosion.stopAllExplosions();

		// Spawn a hero.
		Tank.spawnHero();

		// Spawn three enemies.
		Tank.spawnNextEnemy();

		// TODO Spawn two more enemies.
	}

	private boolean readLevel(int lev) {
		if (lev <= 0) {
			// board.screen0(); // Initialize the default zero screen.
		} else {
			InputStream is = null;
			try {
				is = getClass().getResourceAsStream("/Levels/" + lev + ".txt");
				if (is != null) {
					battleground.read(is, lev);
					is.close();
				} else {
					System.out
							.println("Could not find the game board for level "
									+ lev);
					return false;
				}
			} catch (java.io.IOException ex) {
				return false;
			}
		}

		return true;
	}

	public synchronized void start() {
		animationThread = new Thread(this);
		animationThread.start();
	}

	public synchronized void stop() {
		animationThread = null;
	}

	long timeTaken = 0;

	public void run() {
		Thread currentThread = Thread.currentThread();

		try {
			while (currentThread == animationThread) {
				long startTime = System.currentTimeMillis();
				// Don't advance game or draw if canvas is covered by a system
				// screen.
				if (isShown()) {
					// handleInput();
					tick();
					draw();
					flushGraphics();
				}
				timeTaken = System.currentTimeMillis() - startTime;
				if (timeTaken < MILLIS_PER_TICK) {
					synchronized (this) {
						wait(MILLIS_PER_TICK - timeTaken);
						timeTaken = System.currentTimeMillis() - startTime;
					}
				} else {
					Thread.yield();
				}
			}
		} catch (InterruptedException e) {
		}
	}

	private void tick() {
		if (!gameOver) {
			hero.getUserInput(direction, shoot);
			hero.tick();
		}
		Explosion.tickExplosions();
		Bullet.tickBullets();
		Tank.tickEnemies();
		battleground.tick();
	}

	private void draw() {
		int width = getWidth();
		int height = getHeight();

		graphics.setColor(0x00000000);
		graphics.fillRect(0, 0, width, height);

		// clip and translate to center
		int dx = origin(hero.getX() + hero.getWidth() / 2, battleground
				.getWidth(), width);
		int dy = origin(hero.getY() + hero.getHeight() / 2, battleground
				.getHeight(), height);
		graphics.setClip(dx, dy, battleground.getWidth(), battleground
				.getHeight());
		graphics.translate(dx, dy);

		// draw background and sprites
		layerManager.paint(graphics, 0, 0);

		// undo clip & translate
		graphics.translate(-dx, -dy);
		graphics.setClip(0, 0, width, height);
		// *
		// display time & score
		// long time = (System.currentTimeMillis() - startTime) / 1000;
		// int score = numSheepInFold();
		graphics.setColor(0x00FFFFFF); // white
		/*
		 * graphics.drawString(Integer.toString(score), 1, 1, Graphics.TOP |
		 * Graphics.LEFT);
		 */

		drawFPS();

		drawGameOver();
	}

	private void drawFPS() {
		if (timeTaken == 0)
			timeTaken = 1;
		graphics.drawString(Long.toString(1000 / timeTaken) + "FPS",
				getWidth() - 2, 1, Graphics.TOP | Graphics.RIGHT);
	}

	private void drawGameOver() {
		if (gameOver) {
			graphics.setFont(bigBoldFont);
			graphics.setColor(0x00FFFFFF);
			int x = getWidth() / 2;
			int y = getHeight() / 2;
			graphics.drawString("GAME OVER", x-1, y-1,
					Graphics.BASELINE | Graphics.HCENTER);
			graphics.drawString("GAME OVER", x+1, y-1,
					Graphics.BASELINE | Graphics.HCENTER);
			graphics.drawString("GAME OVER", x-1, y+1,
					Graphics.BASELINE | Graphics.HCENTER);
			graphics.drawString("GAME OVER", x+1, y+1,
					Graphics.BASELINE | Graphics.HCENTER);
			graphics.setColor(0x00FF0000);
			graphics.drawString("GAME OVER", x, y,
					Graphics.BASELINE | Graphics.HCENTER);
			graphics.setFont(Font.getDefaultFont());
		} else {
			graphics.setColor(0x00FFFFFF);
			graphics.drawString(String.valueOf(hero.livesLeft), 0, 0, Graphics.TOP | Graphics.LEFT);
		}
	}

	// If the screen is bigger than the field, we center the field
	// in the screen. Otherwise we center the screen on the focus, except
	// that we don't scroll beyond the edges of the field.
	private int origin(int focus, int fieldLength, int screenLength) {
		int origin;
		if (screenLength >= fieldLength) {
			origin = (screenLength - fieldLength) / 2;
		} else if (focus <= screenLength / 2) {
			origin = 0;
		} else if (focus >= (fieldLength - screenLength / 2)) {
			origin = screenLength - fieldLength;
		} else {
			origin = screenLength / 2 - focus;
		}
		return origin;
	}

	protected void keyPressed(int keyCode) {
		int gameAction = getGameAction(keyCode);
		if (gameAction == UP) {
			direction = NORTH;
		} else if (gameAction == RIGHT) {
			direction = EAST;
		} else if (gameAction == LEFT) {
			direction = WEST;
		} else if (gameAction == DOWN) {
			direction = SOUTH;
		} else if (gameAction == FIRE) {
			shoot = true;
		}
	}

	protected void keyReleased(int keyCode) {
		int gameAction = getGameAction(keyCode);
		switch (gameAction) {
		case UP:
		case DOWN:
		case LEFT:
		case RIGHT:
			direction = NONE;
			break;
		case FIRE:
			shoot = false;
		}
	}

	public void commandAction(Command command, Displayable display) {
		// TODO Auto-generated method stub
		if (command.getCommandType() == Command.HELP) {
			About.showAbout(Display.getDisplay(midlet));
		} else if (command.getCommandType() == Command.EXIT) {
			midlet.exitRequested();
		}

	}

	public void gameOver() {
		gameOver = true;
	}

}
