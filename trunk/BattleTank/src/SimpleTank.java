class SimpleTank extends EnemyTank {
	
	protected int getSpeed() {
		return 2;
	}

	protected int getBulletSpeed() {
		return Bullet.SLOW;
	}

}
