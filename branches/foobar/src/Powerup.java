import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

public class Powerup extends Sprite {

	public static final int WIDTH = 16;

	public static final int HEIGHT = 16;

	private static final Image IMAGE = BattleTankMIDlet
			.createImage("/Powerups.png");

	/** Tank symbol gives an extra life. */
	public static final int TANK = 0;

	/** Star improves player's tank. */
	public static final int STAR = 1;

	/** Bomb destroys all visible enemy tanks. */
	public static final int BOMB = 2;

	/** Clock freezes all enemy tanks for a period of time. */
	public static final int CLOCK = 3;

	/** Shovel adds steel walls around the base for a period of time. */
	public static final int SHOVEL = 4;

	/** Shield makes player's tank invulnerable to attack for a period of time. */
	public static final int SHIELD = 5;

	private int type;

	Powerup() {
		super(IMAGE, WIDTH, HEIGHT);
		setRefPixelPosition(WIDTH / 2, HEIGHT / 2);
	}

	void collect(HeroTank tank) {
		switch (type) {
		case TANK:
			// TODO Give extra life.
			break;
		case STAR:
			tank.upgrade();
			break;
		case BOMB:
			Tank.explodeAllEmenies();
			break;
		case CLOCK:
			Tank.immobilizeEnemies();
			break;
		case SHOVEL:
			// TODO Add steel walls around base.
			break;
		case SHIELD:
			tank.becomeInvulnerable();
			break;
		}

	}

	public static void issuePowerup() {
		// TODO Auto-generated method stub
		
	}

}
