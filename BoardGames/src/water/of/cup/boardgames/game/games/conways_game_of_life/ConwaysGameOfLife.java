package water.of.cup.boardgames.game.games.conways_game_of_life;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import water.of.cup.boardgames.game.BoardItem;
import water.of.cup.boardgames.game.Button;
import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.GameImage;
import water.of.cup.boardgames.game.GamePlayer;

public class ConwaysGameOfLife extends Game {
	boolean[][] cells;
	Button[][] cellButtons;
	

	public ConwaysGameOfLife(int rotation) {
		super(rotation);
	}

	@Override
	protected void setMapInformation(int rotation) {
		// TODO Auto-generated method stub
		this.mapStructure = new int[][] { { 1 } };
		this.placedMapVal = 1;
	}

	@Override
	protected void startGame() {
		createCells();
		mapManager.renderBoard();
	}
	
	private void createCells() {
		cells = new boolean[32][32];
		cellButtons = new Button[32][32];
		for (int y = 0; y < 32; y++)
			for (int x = 0; x < 32; x++) {
				Button b = new Button(this, "CONWAYS_DEAD", new int[] { x * 4, y * 4 }, 0, "cell");
				buttons.add(b);
				cellButtons[y][x] = b;
			}
	}
	
	private void renderCells() {
		for (int y = 0; y < 32; y++)
			for (int x = 0; x < 32; x++) {
				cellButtons[y][x].setImage(cells[y][x] ? "CONWAYS_ALIVE" : "CONWAYS_DEAD");
			}
		mapManager.renderBoard();
	}
	
	private int[] getButtonLocation(Button b) {
		for (int y = 0; y < 32; y++) {
			for (int x = 0; x < 32; x++) {
				if (b == cellButtons[y][x])
					return new int[] { x, y };
			}
		}
		return null;
	}
	
	private void toggleCell(Button b) {
		int[] loc = getButtonLocation(b);
		if (loc == null)
			return;
		
		cells[loc[1]][loc[0]] = !cells[loc[1]][loc[0]];
		renderCells();
	}
	
	private int getLiveNeighbors(int[] loc) {
		int count = 0;
		for (int cy = -1; cy <= 1; cy++)
			for (int cx = -1; cx <= 1; cx++) {
				if (cy == 0 && cx == 0)
					continue; // skip middle cell
				int y = loc[1] + cy;
				int x = loc[0] + cx;
				
				if (!(y >= 0 && x >= 0 && y < 32 && x < 32))
					continue; // check cell on board
				
				if (cells[y][x])
					count++; // increment if cell is alive
				
			}
		return count;
	}
	
	private void nextGeneration() {
		boolean[][] newGeneration = new boolean[32][32];
		
		for (int y = 0; y < 32; y++) 
			for (int x = 0; x < 32; x++) {
				int neighbors = getLiveNeighbors(new int[] {x, y});
				if (cells[y][x]) {
					//Any live cell with two or three live neighbours survives.
					if (neighbors == 2 || neighbors == 3)
						newGeneration[y][x] = true;
					
				} else {
					if (neighbors == 3)
						newGeneration[y][x] = true;
					//Any dead cell with three live neighbours becomes a live cell.
				}
				//All other live cells die in the next generation. Similarly, all other dead cells stay dead.	
			}
		cells = newGeneration;
		renderCells();
		
	}

	@Override
	protected void setGameName() {
		gameName = "ConwaysGameOfLife";
		
	}

	@Override
	protected void setBoardImage() {
		this.gameImage = new GameImage("CONWAYS_BOARD");
		
	}

	@Override
	protected void startClock() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void click(Player player, double[] loc, ItemStack map) {
		if (player.isSneaking()) {
			nextGeneration();
			return;
		}
		
		int[] clickLoc = mapManager.getClickLocation(loc, map);
		Button b = getClickedButton(getGamePlayer(player), clickLoc);
		if (b == null)
			return;
		
		toggleCell(b);
		return;
		
	}

	@Override
	protected void gamePlayerOutOfTime(GamePlayer turn) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ItemStack getBoardItem() {
		return new BoardItem(gameName, new ItemStack(Material.OAK_TRAPDOOR, 1));
	}

}
