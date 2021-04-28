package water.of.cup.boardgames.game.games.battleship;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import water.of.cup.boardgames.game.Button;
import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.GameImage;
import water.of.cup.boardgames.game.GamePlayer;
import water.of.cup.boardgames.game.maps.GameMap;

public class Battleship extends Game {
	boolean turn;

	public Battleship(int rotation) {
		super(rotation);
		turn = true;
	}

	@Override
	protected void setMapInformation() {
		this.mapStructure = new int[][] { { 2 }, { 1 } };
		this.placedMapVal = 1;
		this.gameImage = new GameImage("TICTACTOE_BOARD");
	}

	@Override
	protected void setGameName() {
		this.gameName = "Battle Ship";

	}

	@Override
	protected void setBoardImage() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void startClock() {
		// TODO Auto-generated method stub

	}

	@Override
	public void click(Player player, double[] loc, ItemStack map) {
		int[] clickLoc = mapManager.getClickLocation(loc, map);
		if (turn == true) {
			buttons.add(new Button(this, "TICTACTOE_X", new int[] { clickLoc[0] - 20, clickLoc[1] - 20 }, 0, "An x"));
			turn = false;
		} else {
			buttons.add(new Button(this, "TICTACTOE_O", new int[] { clickLoc[0] - 20, clickLoc[1] - 20 }, 0, "An x"));
			turn = true;
		}

		mapManager.renderBoard();

	}

	@Override
	protected void gamePlayerOutOfTime(GamePlayer turn) {
		// TODO Auto-generated method stub

	}

	@Override
	public ItemStack getBoardItem() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void startGame() {
		// TODO Auto-generated method stub
		
	}

}
