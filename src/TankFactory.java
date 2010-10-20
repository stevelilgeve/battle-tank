class TankFactory {

	public static final char SIMPLE_TANK = 's';

	public static final char FAST_TANK = 'f';

	public static final char SMART_TANK = 'S';

	public static final char HEAVY_TANK = 'H';

	public static Tank createTank(char tankClass) {
		System.out.println("making a new tank of type : " +tankClass);
		switch (tankClass) {
		case HEAVY_TANK:
			return new HeavyTank();
		case SMART_TANK:
			return new SmartTank();
		default:
			// For unknown types, just create a simple tank.
		case SIMPLE_TANK:
			return new SimpleTank();
		}
	}
	
}