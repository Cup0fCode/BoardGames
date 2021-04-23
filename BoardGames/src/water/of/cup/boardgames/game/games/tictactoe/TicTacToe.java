package water.of.cup.boardgames.game.games.tictactoe;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import water.of.cup.boardgames.game.Button;
import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.GameImage;
import water.of.cup.boardgames.game.GamePlayer;

public class TicTacToe extends Game {

	public TicTacToe(int rotation) {
		super(rotation);
	}

	@Override
	protected void gamePlayerOutOfTime(GamePlayer turn) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void setGameName() {
		this.gameName = "Tic-Tac-Toe";
		
	}

	@Override
	protected void setBoardImage() {
		this.gameImage = new GameImage("TICTACTOE_BOARD");
		
	}

	@Override
	protected void setMapInformation() {
		this.mapStructure = new int[][] {{1}};
		this.placedMapVal = 1;
	}

	@Override
	protected void startClock() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ItemStack getBoardItem() {
		return null;
	}

	@Override
	public void click(Player player, double[] loc, ItemStack map) {
		int[] clickLoc = mapManager.getClickLocation(loc, map);
		buttons.add(new Button(this, "TICTACTOE_X", new int[] {clickLoc[0] - 20, clickLoc[1] - 20}, 0, "An x"));
		player.sendMessage("you clicked: " + clickLoc[0] + "," + clickLoc[1]);
		mapManager.renderBoard();
		
	}

}
