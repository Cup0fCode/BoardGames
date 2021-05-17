package water.of.cup.boardgames.game.games.minesweaper;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import water.of.cup.boardgames.game.BoardItem;
import water.of.cup.boardgames.game.Button;
import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.GameImage;
import water.of.cup.boardgames.game.GamePlayer;
import water.of.cup.boardgames.game.inventories.GameInventory;
import water.of.cup.boardgames.game.storage.GameStorage;

import java.util.ArrayList;

public class MineSweeper extends Game {
	private Button[][] boardButtons;
	private boolean[][] bombLocations;
	private boolean[][] discoveredTiles;
	private boolean[][] flaggedTiles;
	
	private int openedTiles = 0;
	private int numberOfBombs = 32;

	public MineSweeper(int rotation) {
		super(rotation);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void setMapInformation(int rotation) {
		this.mapStructure = new int[][] { { 1 } };
		this.placedMapVal = 1;

	}

	@Override
	protected void startGame() {
		setInGame();
		createBoard();
		mapManager.renderBoard();
	}

	private void createBoard() {
		bombLocations = new boolean[16][16];
		discoveredTiles = new boolean[16][16];
		boardButtons = new Button[16][16];
		flaggedTiles = new boolean[16][16];
		
		// create buttons
		for (int y = 0; y < 16; y++)
			for (int x = 0; x < 16; x++) {
				boardButtons[y][x] = new Button(this, "MINESWEEPER_HIDDEN", new int[] { x * 8, y * 8 }, 0, "HIDDEN");
				boardButtons[y][x].setClickable(true);
				buttons.add(boardButtons[y][x]);
			}
		
		// place bombs
		for (int placed = 0; placed < numberOfBombs; placed++) {
			int r = (int) (Math.random() * 16 * 16);
			if (bombLocations[r % 16][r / 16]) {
				placed--;
				continue;
			} 
			bombLocations[r % 16][r / 16] = true;
		}
	}
	
	private boolean tileOnBoard(int[] loc) {
		return !(loc[0] < 0 || loc[1] < 0 || loc[0] > 15 || loc[1] > 15);
	}
	
	private void toggleFlag(int[] loc) {
		//don't toggle if tile is discovered
		if (discoveredTiles[loc[1]][loc[0]])
			return;
		
		if (flaggedTiles[loc[1]][loc[0]]) {
			flaggedTiles[loc[1]][loc[0]] = false;
			boardButtons[loc[1]][loc[0]].setImage("MINESWEEPER_HIDDEN");
		} else {
			flaggedTiles[loc[1]][loc[0]] = true;
			boardButtons[loc[1]][loc[0]].setImage("MINESWEEPER_FLAG");
		}
		
	}
	
	private boolean openTile(int[] loc) { //returns false if tile is a bomb
		// if this tile is already opened do nothing
		if (discoveredTiles[loc[1]][loc[0]])
			return true;
		
		// don't open if tile is flagged
		if (flaggedTiles[loc[1]][loc[0]])
			return true;
		
		if (bombLocations[loc[1]][loc[0]]) { //check if tile is a bomb
			boardButtons[loc[1]][loc[0]].setImage("MINESWEEPER_MINE");
			return false;
		}
		
		int n = countNearbyBombs(loc);
		boardButtons[loc[1]][loc[0]].setImage("MINESWEEPER_" + n);
		openedTiles++;
		discoveredTiles[loc[1]][loc[0]] = true;
		
		if (n == 0) {
			// open nearby tiles if this tile has no nearby bombs
			for (int y = -1; y < 2; y++)
				for (int x = -1; x < 2; x++) {
					if (x == 0 && y == 0)
						continue; // don't open this square
					if (!tileOnBoard(new int[] {loc[0] + x , loc[1] + y}))
						continue; //skip tiles not on board
					if (!discoveredTiles[loc[1] + y][loc[0] + x]) {
						//if this tile hasn't been discovered open it
						openTile(new int[] {loc[0] + x , loc[1] + y});
					}
				}
		}
		return true;
	}
	
	private int countNearbyBombs(int[] loc) {
		int count = 0;
		for (int y = -1; y < 2; y++)
			for (int x = -1; x < 2; x++) {
				if (x == 0 && y == 0)
					continue; // don't count middle square
				if (!tileOnBoard(new int[] {loc[0] + x , loc[1] + y}))
						continue; //skip tiles not on board
				if (bombLocations[loc[1] + y][loc[0] + x])
					count++;
			}
		return count;
	}

	private int[] getButtonLocation(Button b) {
		for (int y = 0; y < 16; y++) {
			for (int x = 0; x < 16; x++) {
				if (b == boardButtons[y][x])
					return new int[] { x, y };
			}
		}
		return null;
	}

	@Override
	protected void setGameName() {
		this.gameName = "Minesweeper";

	}

	@Override
	protected void setBoardImage() {
		this.gameImage = new GameImage("MINESWEEPER_BOARD");

	}

	@Override
	protected void startClock() {
		// TODO Auto-generated method stub

	}

	@Override
	protected GameInventory getGameInventory() {
		return new MineSweeperInventory(this);
	}

	@Override
	protected GameStorage getGameStorage() {
		return null;
	}

	@Override
	public ArrayList<String> getTeamNames() {
		return null;
	}

	@Override
	public void click(Player player, double[] loc, ItemStack map) {
		GamePlayer gamePlayer = getGamePlayer(player);
		if(!teamManager.getTurnPlayer().equals(gamePlayer)) return;

		int[] clickLoc = mapManager.getClickLocation(loc, map);
		Button b = getClickedButton(gamePlayer, clickLoc);
		int[] position = getButtonLocation(b);
		
		if (position == null)
			return;
		
		if (player.isSneaking()) {
			// toggle flag
			toggleFlag(position);
		} else {
			// open tile
			if (openTile(position)) {
				// safe tile
				if (openedTiles + numberOfBombs >= 16 * 16) {
					// all safe tiles opened
					endGame(gamePlayer);
				}
			} else {
				// bomb opened
				endGame(null);
			}
		}
		mapManager.renderBoard();
		
	}

	public void endGame(GamePlayer gamePlayerWinner) {
		buttons.clear();
		openedTiles = 0;

		String message;
		if(gamePlayerWinner != null) {
			message = gamePlayerWinner.getPlayer().getDisplayName() + " has won the game!";
		} else {
			message = ChatColor.GREEN + "You lost!";
		}

		for(GamePlayer player : teamManager.getGamePlayers()) {
			player.getPlayer().sendMessage(message);
		}

		super.endGame(gamePlayerWinner);
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
