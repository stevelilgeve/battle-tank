import java.io.IOException;

import javax.microedition.lcdui.game.TiledLayer;

class Battleground extends TiledLayer {
	static final int WIDTH_IN_TILES = 13;

	static final int HEIGHT_IN_TILES = 13;

	static final int TILE_WIDTH = 16;

	static final int TILE_HEIGHT = 16;

	static final int BRICK_WALL = 21;

	static final int CONCRETE_WALL = 22;

	static final int FOREST = 29;

	private static int[][] waterFrames = { { 23, 24 }, { 24, 23 } };

	private int tickCount = 0;

	public Battleground() {
		super(WIDTH_IN_TILES, HEIGHT_IN_TILES, BattleTankMIDlet
				.createImage("/Objects.png"), TILE_WIDTH, TILE_HEIGHT);

		createAnimatedTile(waterFrames[0][0]); // tile -1
		createAnimatedTile(waterFrames[1][0]); // tile -2
	}

	public synchronized void read(java.io.InputStream is, int l) {
		try {
			readBattleground(is);
			readEnemies(is);
		} catch (java.io.IOException ex) {
			ex.printStackTrace();
		}
	}

	private void readBattleground(java.io.InputStream is) throws IOException {
		int c = -1, x = 0, y = 0;
		while ((c = is.read()) != -1 && y < HEIGHT_IN_TILES) {
			switch (c) {
			case '\n':
				y++;
				x = 0;
				break;

			case '+':
				setCell(x++, y, BRICK_WALL);
				break;

			case '#':
				setCell(x++, y, CONCRETE_WALL);
				break;

			case '*':
				setCell(x++, y, FOREST);
				break;

			case '~':
				setCell(x++, y, -1 - ((x ^ y) & 1));
				break;

			case '$':
			case '@':
			case '!':
			case '.':
			case ' ':
				x++;
				break;
			default:
			}
		}
	}

	private void readEnemies(java.io.InputStream is) throws IOException {
		int c;
		StringBuffer buffer = new StringBuffer();
		while ((c = is.read()) != -1) {
			if (c > '0')
				buffer.append(c - '0');
		}
		
		String enemiesDescr = buffer.toString();
		System.out.println(enemiesDescr);
		Tank.initEnemyPool(enemiesDescr);
	}


	void tick() {
		int tickState = (tickCount++ >> 3); // slow down x8
		int tile = tickState % 2;
		setAnimatedTile(-1 - tile, waterFrames[tile][(tickState % 4) / 2]);
	}

	boolean containsImpassableArea(int x, int y, int width, int height) {
		int rowMin = y / TILE_HEIGHT;
		int rowMax = (y + height - 1) / TILE_HEIGHT;
		int columnMin = x / TILE_WIDTH;
		int columnMax = (x + width - 1) / TILE_WIDTH;

		for (int row = rowMin; row <= rowMax; ++row) {
			for (int column = columnMin; column <= columnMax; ++column) {
				int cell = getCell(column, row);
				if ((cell < 0) || (cell == BRICK_WALL)
						|| (cell == CONCRETE_WALL)) {
					return true;
				}
			}
		}
		return false;
	}

	boolean hitWall(int x, int y, boolean strong) {
		int col = x / TILE_WIDTH;
		int row = y / TILE_HEIGHT;
		int cell = getCell(col, row);
		if (cell == BRICK_WALL) {
			// TODO Break walls slowly
			setCell(col, row, 0);
			return true;
		} else if (cell == CONCRETE_WALL) {
			if (strong)
				setCell(col, row, 0);
			return true;
		}
		return false;
	}
}
