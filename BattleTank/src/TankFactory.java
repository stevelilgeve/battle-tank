class TankFactory {

	public static final int HERO_TANK = 0;

	public static final int SIMPLE_TANK = 1;

	public static final int FAST_TANK = 2;

	public static final int SMART_TANK = 3;

	public static final int HEAVY_TANK = 4;

	public static Tank createTank(int tankClass) {
		switch (tankClass) {
		case HERO_TANK:
			return new HeroTank();
		case HEAVY_TANK:
			return new HeavyTank();
		default:
			// For unknown types, just create a simple tank.
		case SIMPLE_TANK:
			return new SimpleTank();
		}
	}
	
}