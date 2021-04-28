package water.of.cup.boardgames.game.games.tictactoe;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import water.of.cup.boardgames.game.Button;
import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.GameImage;
import water.of.cup.boardgames.game.GamePlayer;

public class TicTacToe extends Game {
	private int turn; // remove when Matt work done
	private Button[][] board;

	public TicTacToe(int rotation) {
		super(rotation);
		turn = 1;
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
		this.mapStructure = new int[][] { { 1 } };
		this.placedMapVal = 1;
	}

	private void placeButtons() {
		board = new Button[][] { { null, null, null }, { null, null, null }, { null, null, null } };
		for (int x = 0; x < 128; x += 44) {
			for (int y = 0; y < 128; y += 44) {
				Button b = new Button(this, "TICTACTOE_EMPTY", new int[] { x, y }, 0, "empty");
				buttons.add(b);
				board[y / 44][x / 43] = b;
			}
		}
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
		Button b = getClickedButton(getGamePlayer(player), clickLoc);
		if (b != null) {
			player.sendMessage("you clicked button " + b.getName());
			if (turn >= 10 || !b.getName().equals("empty"))
				return;
			if (turn % 2 == 1) {
				b.getImage().setImage("TICTACTOE_X");
				b.setName("x");
			} else {
				b.getImage().setImage("TICTACTOE_O");
				b.setName("o");
			}
			turn++;
			
			String s = checkForWinner();
			if (s.equals("x"))
				player.sendMessage("X won");
			if (s.equals("o"))
				player.sendMessage("O won");
			if (s.equals("t"))
				player.sendMessage("Tie game");
			if (!s.equals("n"))
				turn = 10;
		}
		player.sendMessage("you clicked: " + clickLoc[0] + "," + clickLoc[1]);
		mapManager.renderBoard();

	}

	@Override
	protected void startGame() {
		placeButtons();
		mapManager.renderBoard();
	}

	private String checkForWinner() { // n: no winner, x: x wins, o: o wins, t: tie
		String[][] stringBoard = new String[][] { { null, null, null }, { null, null, null }, { null, null, null } };
		for (int y = 0; y < 3; y++)
			for (int x = 0; x < 3; x++)
				stringBoard[y][x] = board[y][x].getName();
		for (int p = 0; p < 3; p++) {
			// check columns
			if (!stringBoard[p][0].equals("empty") && stringBoard[p][0].equals(stringBoard[p][1])
					&& stringBoard[p][0].equals(stringBoard[p][2]))
				return stringBoard[p][0];
			// check rows
			if (!stringBoard[0][p].equals("empty") && stringBoard[0][p].equals(stringBoard[1][p])
					&& stringBoard[0][p].equals(stringBoard[2][p]))
				return stringBoard[0][p];
		}
		// check crosses
		if (!stringBoard[0][0].equals("empty") && stringBoard[0][0].equals(stringBoard[1][1])
				&& stringBoard[0][0].equals(stringBoard[2][2]))
			return stringBoard[0][0];
		
		if (!stringBoard[0][2].equals("empty") && stringBoard[0][2].equals(stringBoard[1][1])
				&& stringBoard[0][2].equals(stringBoard[2][0]))
			return stringBoard[0][2];
		
		// check all positions fileld
		if (turn >= 10)
			return "t";

		return "n";
	}

}
