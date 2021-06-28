package water.of.cup.boardgames.game.games.tictactoe;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import water.of.cup.boardgames.config.ConfigUtil;
import water.of.cup.boardgames.config.GameRecipe;
import water.of.cup.boardgames.game.*;
import water.of.cup.boardgames.game.inventories.GameInventory;
import water.of.cup.boardgames.game.inventories.GameOption;
import water.of.cup.boardgames.game.storage.GameStorage;
import water.of.cup.boardgames.game.storage.StorageType;

import java.util.ArrayList;
import java.util.HashMap;

public class TicTacToe extends Game {
	private Button[][] board;

	public TicTacToe(int rotation) {
		super(rotation);
	}

	@Override
	protected void gamePlayerOutOfTime(GamePlayer turn) {
		// TODO Auto-generated method stub

		
	}

	@Override
	protected void setGameName() {
		this.gameName = "TicTacToe";

	}

	@Override
	protected void setBoardImage() {
		this.gameImage = new GameImage("TICTACTOE_BOARD");

	}

	@Override
	protected void setMapInformation(int rotation) {
		this.mapStructure = new int[][] { { 1 } };
		this.placedMapVal = 1;
	}

	private void placeButtons() {
		board = new Button[][] { { null, null, null }, { null, null, null }, { null, null, null } };
		for (int x = 0; x < 128; x += 44) {
			for (int y = 0; y < 128; y += 44) {
				Button b = new Button(this, "TICTACTOE_EMPTY", new int[] { x, y }, 0, "empty");
				b.setClickable(true);
				buttons.add(b);
				board[y / 44][x / 43] = b;
			}
		}
	}

	@Override
	protected Clock getClock() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected GameInventory getGameInventory() {
		return new TicTacToeInventory(this);
	}

	@Override
	protected GameStorage getGameStorage() {
		return new TicTacToeStorage(this);
	}

	@Override
	public ArrayList<String> getTeamNames() {
		return new ArrayList<String>() {{
			add("x");
			add("o");
		}};
	}

	@Override
	protected GameConfig getGameConfig() {
		return new TicTacToeConfig(this);
	}

	@Override
	public BoardItem getBoardItem() {
		return new BoardItem(gameName, new ItemStack(Material.OAK_TRAPDOOR, 1));
	}

	@Override
	public void click(Player player, double[] loc, ItemStack map) {
		GamePlayer gamePlayer = getGamePlayer(player);
		if(!teamManager.getTurnPlayer().equals(gamePlayer)) return;

		int[] clickLoc = mapManager.getClickLocation(loc, map);
		Button b = getClickedButton(gamePlayer, clickLoc);

		if (b != null) {
			if(!b.getName().equals("empty")) return;

			this.playGameSound("click");

			if (teamManager.getTurnTeam().equals("x")) {
				b.getImage().setImage("TICTACTOE_X");
				b.setName("x");
			} else {
				b.getImage().setImage("TICTACTOE_O");
				b.setName("o");
			}

			teamManager.nextTurn();

			String s = checkForWinner();
			if (!s.equals("n")) {
				GamePlayer winner = teamManager.getGamePlayerByTeam(s);

				endGame(winner);
			}

		}

		mapManager.renderBoard();
	}

	@Override
	protected void startGame() {
		super.startGame();
		buttons.clear();
		setInGame();
		placeButtons();
		mapManager.renderBoard();
	}

	public void endGame(GamePlayer gamePlayerWinner) {
		//board = null;
		//buttons.clear();

		mapManager.renderBoard();
		super.endGame(gamePlayerWinner);
	}

	private String checkForWinner() { // n: no winner, x: x wins, o: o wins, t: tie
		String[][] stringBoard = new String[][] { { null, null, null }, { null, null, null }, { null, null, null } };
		for (int y = 0; y < 3; y++)
			for (int x = 0; x < 3; x++)
				stringBoard[y][x] = board[y][x].getName();
		for (int p = 0; p < 3; p++) {
			// check columns
			if (!stringBoard[p][0].equals("empty") && stringBoard[p][0].equals(stringBoard[p][1])
					&& stringBoard[p][0].equals(stringBoard[p][2])) {
				buttons.add(new Button(this, "TICTACTOE_HORIZONTAL_" + (stringBoard[p][0]).toUpperCase(), new int[] {0,p * 44}, 0, "win" ));
				return stringBoard[p][0];
			}
			// check rows
			if (!stringBoard[0][p].equals("empty") && stringBoard[0][p].equals(stringBoard[1][p])
					&& stringBoard[0][p].equals(stringBoard[2][p])) {
				buttons.add(new Button(this, "TICTACTOE_VERTICAL_" + (stringBoard[0][p]).toUpperCase(), new int[] {p * 44,0}, 0, "win" ));
				return stringBoard[0][p];
			}
		}
		// check crosses
		if (!stringBoard[0][0].equals("empty") && stringBoard[0][0].equals(stringBoard[1][1])
				&& stringBoard[0][0].equals(stringBoard[2][2])) {
			buttons.add(new Button(this, "TICTACTOE_CROSS_" + (stringBoard[0][0]).toUpperCase(), new int[] {0,0}, 0, "win" ));
			return stringBoard[0][0];
		}
		
		if (!stringBoard[0][2].equals("empty") && stringBoard[0][2].equals(stringBoard[1][1])
				&& stringBoard[0][2].equals(stringBoard[2][0])) {
			buttons.add(new Button(this, "TICTACTOE_CROSS_" + (stringBoard[1][1]).toUpperCase(), new int[] {0,0}, 1, "win" ));
			return stringBoard[0][2];
		}
		
		// check all positions filled
		boolean tie = true;
		outer: for (String[] strings : stringBoard) {
			for (String string : strings) {
				if (string.equals("empty")) {
					tie = false;
					break outer;
				}
			}
		}

		if(tie) return "t";

		return "n";
	}

	@Override
	protected void clockOutOfTime() {
		// TODO Auto-generated method stub
		
	}

}
