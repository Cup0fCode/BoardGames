package water.of.cup.boardgames.game.games.battleship;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import water.of.cup.boardgames.game.BoardItem;
import water.of.cup.boardgames.game.Button;
import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.GameImage;
import water.of.cup.boardgames.game.GamePlayer;
import water.of.cup.boardgames.game.inventories.GameInventory;
import water.of.cup.boardgames.game.maps.GameMap;
import water.of.cup.boardgames.game.maps.Screen;

public class Battleship extends Game {
	private boolean turn;
	private Screen p1GameScreen;
	private Screen p2GameScreen;

	public Battleship(int rotation) {
		super(rotation);
		turn = true;
	}

	@Override
	protected void setMapInformation(int rotation) {
		this.mapStructure = new int[][] { { 2 }, { 1 } };
		this.placedMapVal = 1;
		p2GameScreen = new Screen(this, "BATTLESHIP_RADAR", 0, new int[] { 0, 0 }, new int[][] { { 3 } }, rotation);
		screens.add(p2GameScreen);
		p1GameScreen = new Screen(this, "BATTLESHIP_RADAR", 2, new int[] { 0, 1 }, new int[][] { { 4 } }, rotation);
		screens.add(p1GameScreen);
	}

	@Override
	protected void setGameName() {
		this.gameName = "BattleShip";
	}

	@Override
	protected void setBoardImage() {
		this.gameImage = new GameImage("BATTLESHIP_BOARD");

	}

	@Override
	protected void startClock() {
		// TODO Auto-generated method stub

	}

	@Override
	protected GameInventory getGameInventory() {
		return null;
	}

	@Override
	public void click(Player player, double[] loc, ItemStack map) {
		int[] clickLoc = mapManager.getClickLocation(loc, map);
		Screen screen = mapManager.getClickedScreen(map);
		if (screen != null) {
			if (screen.equals(p1GameScreen)) {
				player.sendMessage("p1 game screen clicked");
			}
			player.sendMessage("Screen clicked: " + clickLoc[0] + "," + clickLoc[1]);
		} else {
			player.sendMessage("Board clicked: " + clickLoc[0] + "," + clickLoc[1]);
		}
//		if (turn == true) {
//			buttons.add(new Button(this, "TICTACTOE_X", new int[] { clickLoc[0] - 20, clickLoc[1] - 20 }, 0, "An x"));
//			turn = false;
//		} else {
//			buttons.add(new Button(this, "TICTACTOE_O", new int[] { clickLoc[0] - 20, clickLoc[1] - 20 }, 0, "An x"));
//			turn = true;
//		}

		mapManager.renderBoard();

	}

	@Override
	protected void gamePlayerOutOfTime(GamePlayer turn) {
		// TODO Auto-generated method stub

	}

	@Override
	public ItemStack getBoardItem() {
		return new BoardItem(gameName, new ItemStack(Material.OAK_TRAPDOOR, 1));
	}

	@Override
	protected void startGame() {
		// TODO Auto-generated method stub

	}

}
