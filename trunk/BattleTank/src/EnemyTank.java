abstract class EnemyTank extends Tank {
	
	/**
	 * Enemy tanks own a single bullet.
	 */
	protected Bullet bullet = null;
	
	/**
	 * 
	 */
	private boolean hasPrize;

	EnemyTank() {
		super();
		setVisible(false);
		speed = getSpeed();
	}


	public void tick() {
		super.tick();
		
		if (!isVisible())
			return;
		
		think();
		drive();

		// Detach a bullet if it has blown up.
		if (bullet != null) {
			if (!bullet.isVisible()) {
				bullet = null;
			}
		}
	}

	/**
	 * Perform some AI and decide where to go and whether to shoot or not.
	 */
	void think() {
		if ((animationTick & 0x07) == 0) {
			if (BattleTankMIDlet.random(5) == 0) {
				/* Pick direction. Randomly. */
				changeDirection(BattleTankMIDlet.random(4));
			}

		}

		/* Shoot randomly. */
		if (BattleTankMIDlet.random(20) == 0) {
			shoot();
		}
	}

	/**
	 * Enemy tanks explode immediately. Override if you want to add extra logic.
	 */
	void hit() {
		explode();
	}

	/**
	 * Explode enemy, place powerup if it has one, and spawn next enemy.
	 */
	protected void explode() {
		super.explode();
		if (hasPrize) {
			Powerup.issuePowerup();
		}
		Tank.spawnNextEnemy();
	}


	/**
	 * Shoot 
	 */
	void shoot() {
		if (bullet != null)
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
	
		bullet = Bullet.shoot(x, y, direction, getBulletSpeed(), false);
	}
	
	/**
	 * Concrete enemies should return speed at which they are moving.
	 * @return speed in pixels per tick
	 */
	protected abstract int getSpeed();

	/**
	 * Concrete enemies should return speed of the bullets they can shoot.
	 * @return <code>Bullet.SLOW</code> or <code>Bullet.FAST</code>
	 */
	abstract protected int getBulletSpeed();

}
