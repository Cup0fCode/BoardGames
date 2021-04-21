package water.of.cup.boardgames.game.games.tictactoe;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.GamePlayer;

public class TicTacToe extends Game {

	public TicTacToe(int rotation) {
		super(rotation);
	}

	@Override
	public void click(Player player, int[] location) {
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void setMapInformation(int rotation) {
		this.mapStructure = new int[][] {{1}};
		this.placedMapVal = 1;
	}

	@Override
	protected void startClock() {
		// TODO Auto-generated method stub
		
	}

}
