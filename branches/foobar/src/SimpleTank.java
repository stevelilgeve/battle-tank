class SimpleTank extends EnemyTank {
	
	protected static int value = 100;
	
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
	
	protected int getBulletStrength(){
		return 1;
	}
	
	protected int getScore(){
		return 100;
	}

}
