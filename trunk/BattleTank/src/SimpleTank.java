class SimpleTank extends EnemyTank {
	
	private static final int[][] ANIMATION = new int[][] {
		{ 4 }, { 5 }, { 6 }, { 7 }
	};

	protected int[][] getAnimation() {
		return ANIMATION;
	}
	
	protected int getSpeed() {
		return 2;
	}

	protected int getBulletSpeed() {
		return Bullet.SLOW;
	}

}
