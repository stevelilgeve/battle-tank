
public class HeavyTank extends EnemyTank {
	
	private int health = 4;

	private static final int[][] ANIMATION = new int[][] {
		{ 12 }, { 13 }, { 14 }, { 15 },
	};

	protected int[][] getAnimation() {
		return ANIMATION;
	}
	
	public void hit() {
		// Strong tanks can suffer 4 hits before they explode.
		health--;
		if (health == 0)
			explode();
	}

	/**
	 * Strong tanks are slow.
	 */
	protected int getSpeed() {
		return 1;
	}

	protected int getBulletSpeed() {
		return Bullet.SLOW;
	}
	
	protected int getBulletStrength(){
		return 2;
	}

	
	protected int getScore(){
		return 500;
	}
	
}
