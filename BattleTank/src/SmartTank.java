/* A nominally 'smart' tank. But not really.
*  Tries to sort of patrol around rather than randomly moving. Fires at the player if it sees the player.
*/
public class SmartTank extends EnemyTank {
	
	private int health = 2;
	private boolean justshot = false;
	protected static int value = 600;
	private int timetilturn = 5;
	
	private static final int[][] ANIMATION = new int[][] {
		{ 8 }, { 9 }, { 10 }, { 11 },
	};

	protected int[][] getAnimation() {
		return ANIMATION;
	}
	
	public void hit() {
		health--;
		if (health == 0)
			explode();
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
		return 200;
	}
	
	private boolean lookforhero(int dir){
		Tank hero = getHero();
		int herox = hero.getRefPixelX();
		int heroy = hero.getRefPixelY();
		int dx=0;int dy=0;
		if(dir == BattlegroundScreen.NORTH){dy = -8;}
		else if(dir == BattlegroundScreen.SOUTH){dy = 8;}
		else if(dir == BattlegroundScreen.EAST){dx = 8;}
		else if(dir == BattlegroundScreen.WEST){dx = -8;}
		else{return false;}
		int myx = this.getRefPixelX();
		int myy = this.getRefPixelY();
		int width =  Tank.battleground.WIDTH_IN_TILES * Tank.battleground.TILE_WIDTH;
		int height = Tank.battleground.HEIGHT_IN_TILES * Tank.battleground.TILE_HEIGHT;
		while(myx > 0 && myx < width && myy > 0 && myy < height){
			if(Tank.battleground.hitWall(myx,myy,0)){return false;}
			if(myx > herox - 8 && myx < herox + 8 && myy > heroy - 8 && myy < heroy +8){
				return true;
			}
			myx+=dx;
			myy+=dy;
		}
		return false;
	}
	
	protected void think(){
		if ((animationTick & 0x07) == 0) {
			Tank hero = getHero();
			int newdir = -1;
			if(timetilturn == 0){
				boolean blockedeast = battleground.containsImpassableArea(getX() + getWidth(), getY(), speed, getHeight());
				boolean blockedsouth = battleground.containsImpassableArea(getX(),getY() + getHeight(), getWidth(), speed);
				boolean blockedwest = battleground.containsImpassableArea(getX()-speed,getY(),speed, getHeight());
				boolean blockednorth = battleground.containsImpassableArea(getX(),getY() - speed, getWidth(), speed);
				if(direction == BattlegroundScreen.NORTH){
					if(!blockedwest){newdir = BattlegroundScreen.WEST;
					}else if(blockednorth){newdir = BattlegroundScreen.EAST;}
				}else if(direction == BattlegroundScreen.WEST){
					if(!blockedsouth){newdir = BattlegroundScreen.SOUTH;
					}else if(blockedwest){newdir = BattlegroundScreen.NORTH;}
				}else if(direction == BattlegroundScreen.SOUTH){
					if(!blockedwest){newdir= BattlegroundScreen.WEST;
					}else if(blockedsouth){newdir = BattlegroundScreen.EAST;}
				}else if(direction == BattlegroundScreen.EAST){
					if(!blockednorth){newdir = BattlegroundScreen.NORTH;
					}else if(blockedeast){newdir = BattlegroundScreen.SOUTH;}
				}
				if(BattleTankMIDlet.random(10) >5){
					//every so often, go crazy
					newdir = BattleTankMIDlet.random(4);
				}
				changeDirection(newdir);
				timetilturn = 3;
			}else{
				timetilturn--;
			}
		}
		if(lookforhero(direction)){shoot();}
	}
	
}