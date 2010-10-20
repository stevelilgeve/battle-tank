import java.io.InputStream;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.game.LayerManager;

class BattlegroundScreen extends Canvas implements Runnable,
		CommandListener {

	public static final int NONE = -1;
	public static final int NORTH = 0;
	public static final int EAST = 1;
	public static final int SOUTH = 2;
	public static final int WEST = 3;

	private static final int MILLIS_PER_TICK = 1; 

	private final BattleTankMIDlet midlet;

	private final Battleground battleground;

	private  HeroTank hero;

	private final LayerManager layerManager;

	private static final Font bigBoldFont = Font.getFont(Font.FONT_STATIC_TEXT,
			Font.STYLE_BOLD, Font.SIZE_LARGE);

	private volatile Thread animationThread = null;

	/** The direction in which player is driving. */
	private int direction = NONE;

	/** Should the player shoot? */
	private boolean shoot = false;

	private boolean gameOver = false;
	private int countdown = 10;
	private boolean levelSplash = false;

	/**
	 * Current level.
	 */
	private int currentLevel = 0;
	private int liveenemies = 0;

	BattlegroundScreen(BattleTankMIDlet midlet) {
		this.midlet = midlet;
		setFullScreenMode(true);
		
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
		for(int i = 1; i < Tank.POOL_SIZE;i++){
			Tank.spawnNextEnemy(i);
		}
		levelSplash = true;
		countdown = 10;
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
					System.out.println("Could not find the game board for level " + lev);
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
					tick();
					repaint();
				}
				timeTaken = System.currentTimeMillis() - startTime;
				if (timeTaken < MILLIS_PER_TICK) {
					synchronized (this) {
						if(MILLIS_PER_TICK > timeTaken){
							wait(MILLIS_PER_TICK - timeTaken);
							timeTaken = System.currentTimeMillis() - startTime;
						}
					}
				} else {
					Thread.yield();
				}
			}
		} catch (InterruptedException e) {
		}
	}

	private void tick() {
		if (!gameOver && !levelSplash) {
			hero.getUserInput(direction, shoot);
			hero.tick();
		}else{
			countdown--;
		}
		if(!levelSplash){
			Explosion.tickExplosions();
			Bullet.tickBullets();
			Tank.tickEnemies();
			battleground.tick();
		}
	}

	public void paint(Graphics g) {
		int width = getWidth();
		int height = getHeight();
		Graphics graphics = g;
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
		
		 graphics.drawString(Integer.toString(hero.score), getWidth() -2, 1, Graphics.TOP | Graphics.RIGHT);
		

		//drawFPS(graphics);

		drawGameOver(graphics);
	}

	private void drawFPS(Graphics graphics) {
		if (timeTaken == 0)
			timeTaken = 1;
		graphics.drawString(Long.toString(1000 / timeTaken) + "SPF",
				getWidth() - 2, 1, Graphics.TOP | Graphics.RIGHT);
	}

	private void drawGameOver(Graphics graphics) {
		if (gameOver) {
			graphics.setFont(bigBoldFont);
			int x = getWidth() / 2;
			int y = getHeight() / 2;
			int height = graphics.getFont().getHeight();
			graphics.drawString("GAME OVER", x-1, y-1,
					Graphics.BASELINE | Graphics.HCENTER);
			graphics.drawString("GAME OVER", x+1, y-1,
					Graphics.BASELINE | Graphics.HCENTER);
			graphics.drawString("GAME OVER", x-1, y+1,
					Graphics.BASELINE | Graphics.HCENTER);
			graphics.drawString("GAME OVER", x+1, y+1,
					Graphics.BASELINE | Graphics.HCENTER);
			graphics.drawString("Press 5 To Continue",x-1,(y+height*3)-1,Graphics.BASELINE|Graphics.HCENTER);
			graphics.drawString("Press 5 To Continue",x+1,(y+height*3)-1,Graphics.BASELINE|Graphics.HCENTER);
			graphics.drawString("Press 5 To Continue",x-1,(y+height*3)+1,Graphics.BASELINE|Graphics.HCENTER);
			graphics.drawString("Press 5 To Continue",x+1,(y+height*3)+1,Graphics.BASELINE|Graphics.HCENTER);
			graphics.setColor(0x00FF0000);
			graphics.drawString("GAME OVER", x, y,
					Graphics.BASELINE | Graphics.HCENTER);
			graphics.drawString("Press 5 To Continue",x,y+height*3,Graphics.BASELINE|Graphics.HCENTER);
			graphics.setFont(Font.getDefaultFont());
		} else if(levelSplash){
			graphics.setFont(bigBoldFont);
			int x = getWidth() / 2;
			int y = getHeight() / 2;
			int height = graphics.getFont().getHeight();
			String levelString = "LEVEL " + currentLevel;
			graphics.drawString(levelString, x-1, y-1,
					Graphics.BASELINE | Graphics.HCENTER);
			graphics.drawString(levelString, x+1, y-1,
					Graphics.BASELINE | Graphics.HCENTER);
			graphics.drawString(levelString, x-1, y+1,
					Graphics.BASELINE | Graphics.HCENTER);
			graphics.drawString(levelString, x+1, y+1,
					Graphics.BASELINE | Graphics.HCENTER);
			graphics.setColor(0x000000FF);
			graphics.drawString(levelString, x, y,
					Graphics.BASELINE | Graphics.HCENTER);
		}else{
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
		if(gameOver && countdown < 0){restartGame();return;}
		if(levelSplash && countdown < 0){levelSplash = false;return;}
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
		}else if(keyCode == -8){
			midlet.exitRequested();
		}
	}
	
	

	protected void keyReleased(int keyCode) {
		int gameAction = getGameAction(keyCode);
		switch (gameAction) {
		case UP:
			if(direction == NORTH){direction = NONE; break;}
		case DOWN:
			if(direction == SOUTH){direction = NONE; break;}
		case LEFT:
			if(direction == WEST){direction = NONE; break;}
		case RIGHT:
			if(direction == EAST){direction = NONE; break;}
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
		countdown = 10;
	}

	public HeroTank getHero(){ return hero;}
	
	private void restartGame(){
		gameOver = false;
		direction = NONE;
		shoot = false;
		currentLevel = 0;
		liveenemies = 0;
		Tank.restart();
		hero = Tank.getHero();
		nextLevel();
	}
}
