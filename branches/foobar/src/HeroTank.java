public class HeroTank extends Tank {

	// TODO Set this to a correct value after debugging.
	private static final int INITIAL_LIVES = 2;
	/**
	 * When this reaches zero, game is over.
	 */
	protected int livesLeft;
	
	/**
	 * Current score of the player.
	 */
	public int score;
	
	/**
	 * Hero can own up to two bullets.
	 */
	private Bullet bullet1, bullet2;

	/**
	 * Hero tank is invulnerable at the start of the level,
	 * and can become invulnerable if collects {@see Powerup.SHIELD}.
	 */
	private int invulnerabilityTicks;

	private Explosion explodingHero;

	private boolean shouldDrive;
	private boolean shouldShoot;

	private static final int[][] ANIMATION = new int[][] {
		{ 0 }, { 1 }, { 2 }, { 3 }
	};

	HeroTank() {
		super();
		
		livesLeft = INITIAL_LIVES;

		speed = 3;
		bullet1 = bullet2 = null;
	}
	
	protected int[][] getAnimation() {
		return ANIMATION;
	}
	
	public void tick() {
		super.tick();

		if (shouldDrive)
			drive();
		
		if (invulnerabilityTicks > 0)
			--invulnerabilityTicks;

		if (shouldShoot) {
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

	public void shoot() {
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
			bullet1 = Bullet.shoot(x, y, direction, Bullet.SLOW, true,1);
			return;
		}
		bullet2 = Bullet.shoot(x, y, direction, Bullet.FAST, true,1);
	}

	public void hit() {
		if (invulnerabilityTicks > 0)
			return;
		explode();
	}

	protected void explode() {
		explodingHero = Explosion.explode(getRefPixelX(), getRefPixelY(), Explosion.BIG);
		setVisible(false);
		//layerManager.remove(this);
		if (--livesLeft < 0) {
			BattleTankMIDlet.gameOver();
		} else{
			explodingHero.toCallBack = this;
			//spawn();
		}
	}

	
	/* Well, we've finished blowing up, so let's have another go */
	public void doneExploding(){
		spawn();
		layerManager.append(this);
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

	public void getUserInput(int direction, boolean shoot) {
		changeDirection(direction);
		shouldDrive = direction != BattlegroundScreen.NONE;
		shouldShoot = shoot;
	}

}
