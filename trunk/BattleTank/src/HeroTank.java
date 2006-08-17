public class HeroTank extends Tank {

	// TODO Set this to a correct value after debugging.
	private static final int INITIAL_LIVES = 2;

	/**
	 * When this reaches zero, game is over.
	 */
	private int livesLeft;
	
	/**
	 * Current score of the player.
	 */
	private int score;
	
	/**
	 * Hero can own up to two bullets.
	 */
	private Bullet bullet1, bullet2;

	/**
	 * Hero tank is invulnerable at the start of the level,
	 * and can become invulnerable if collects {@see Powerup.SHIELD}.
	 */
	private int invulnerabilityTicks;

	HeroTank() {
		super();
		
		livesLeft = INITIAL_LIVES;

		speed = 2;
		bullet1 = bullet2 = null;
	}

	void tick(int direction, boolean shoot) {
		changeDirection(direction);

		if (direction != BattlegroundScreen.NONE)
			drive();
		
		if (invulnerabilityTicks > 0)
			--invulnerabilityTicks;

		if (shoot) {
			if (!isShooting) {
				isShooting = true;
				shoot();
			}
		} else {
			isShooting = false;
		}

		if (bullet1 != null) {
			if (!bullet1.isVisible())
				bullet1 = null;
		}
		if (bullet2 != null) {
			if (!bullet2.isVisible())
				bullet2 = null;
		}
	}

	void shoot() {
		// FIXME Look at the powerups and shoot fast and strong bullets.
		if (bullet1 != null && bullet2 != null)
			return;

		int x = getRefPixelX();
		int y = getRefPixelY();

		switch (direction) {
		case BattlegroundScreen.NORTH:
			y -= HEIGHT / 2;
			break;
		case BattlegroundScreen.EAST:
			x += WIDTH / 2;
			break;
		case BattlegroundScreen.SOUTH:
			y += HEIGHT / 2;
			break;
		case BattlegroundScreen.WEST:
			x -= WIDTH / 2;
			break;
		}

		if (bullet1 == null) {
			bullet1 = Bullet.shoot(x, y, direction, Bullet.SLOW, true);
			return;
		}
		bullet2 = Bullet.shoot(x, y, direction, Bullet.FAST, true);
	}

	void hit() {
		if (invulnerabilityTicks > 0)
			return;
		explode();
	}

	protected void explode() {
		super.explode();
		if (--livesLeft < 0) {
			BattleTankMIDlet.gameOver();
		} else
			spawn();
	}

	/**
	 * Recieve a {@see Powerup.STAR}.
	 */
	public void upgrade() {
		// TODO Upgrade tank
	}

	/**
	 * Recieve a {@see Powerup.SHIELD}.
	 */
	public void becomeInvulnerable() {
		// TODO Work out optimal value.
		invulnerabilityTicks = 100;
	}

	public void spawn() {
		setPosition(6 * 16, 10 * 16);
		becomeInvulnerable();
		changeDirection(BattlegroundScreen.NORTH);
		setVisible(true);
	}

}
