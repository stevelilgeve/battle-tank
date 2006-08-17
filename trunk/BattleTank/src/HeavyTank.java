
public class HeavyTank extends EnemyTank {
	
	private int health = 4;

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

}
