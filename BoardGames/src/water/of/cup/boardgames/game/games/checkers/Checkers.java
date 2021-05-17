package water.of.cup.boardgames.game.games.checkers;

import org.bukkit.Bukkit;
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

import java.util.ArrayList;

public class Checkers extends Game {
	String[][] board; // {EMPTY, BLACK, BLACK_KING, "RED", "RED_KING"}
	int[] selected;
	Button[][] boardButtons;
	int movesSinceCapture = 0;
	
	boolean canDeSelect;

	public Checkers(int rotation) {
		super(rotation);
	}

	@Override
	protected void setMapInformation(int rotation) {
		this.mapStructure = new int[][] { { 1 } };
		this.placedMapVal = 1;
	}

	@Override
	public void renderInitial() {
		setUpBoard();
		mapManager.renderBoard();
	}

	@Override
	protected void startGame() {
		setInGame();
		setUpBoard();
		mapManager.renderBoard();
	}

	private void setUpBoard() {
		buttons.clear();

		board = new String[8][8];
		boardButtons = new Button[8][8];
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				board[y][x] = "EMPTY";
				if ((x + y) % 2 == 1)
					continue;
				if (y > 5) {
					board[y][x] = "RED";
				} else if (y < 2) {
					board[y][x] = "BLACK";
				}
				Button b = new Button(this, "CHECKERS_EMPTY", new int[] { 4 + x * 15, 4 + y * 15 }, 0, "EMPTY");
				b.setClickable(true);
				boardButtons[y][x] = b;
				buttons.add(b);
			}
		}
		updateButtons();
	}

	private void updateButtons() {
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				if (boardButtons[y][x] == null)
					continue;
				boardButtons[y][x].setImage("CHECKERS_" + board[y][x]);
				if (selected != null && selected[0] == x && selected[1] == y)
					boardButtons[y][x].getImage().addGameImage(new GameImage("CHECKERS_BLACK_HIGHLIGHT"),
							new int[] { 0, 0 });
			}
		}
	}

	private int[] getButtonLocation(Button b) {
		for (int y = 0; y < 8; y++) {
			for (int x = 0; x < 8; x++) {
				if (b == boardButtons[y][x])
					return new int[] { x, y };
			}
		}
		return null;
	}

	private boolean isOnBoard(int[] pos) {
		return !(pos[0] < 0 || pos[1] < 0 || pos[0] > 7 || pos[1] > 7);
	}

	private boolean canJump(int[] position) {
		String color = board[position[1]][position[0]].split("_")[0];
		if (color.equals("EMPTY"))
			return false;

		int[][] moves = new int[0][0];

		if (board[position[1]][position[0]].contains("KING")) {
			moves = new int[][] { { 1, 1 }, { -1, 1 }, { 1, -1 }, { -1, -1 } };
		} else if (color.equals("RED")) {
			moves = new int[][] { { 1, -1 }, { -1, -1 } };
		} else if (color.equals("BLACK")) {
			moves = new int[][] { { 1, 1 }, { -1, 1 } };
		}

		for (int[] move : moves) {
			// check that move is on board
			if (!isOnBoard(new int[] { position[0] + move[0] * 2, position[1] + move[1] * 2 }))
				continue;

			if (!board[position[1] + move[1]][position[0] + move[0]].equals("EMPTY")
					&& !board[position[1] + move[1]][position[0] + move[0]].contains(color)
					&& board[position[1] + move[1] * 2][position[0] + move[0] * 2].equals("EMPTY"))
				return true;
		}

		return false;
	}

	private boolean canAdvance(int[] pos) { // a non-jump move
		String color = board[pos[1]][pos[0]].split("_")[0];
		if (color.equals("EMPTY"))
			return false;

		int[][] moves = new int[0][0];
		if (board[pos[1]][pos[0]].contains("KING")) {
			moves = new int[][] { { 1, 1 }, { -1, 1 }, { 1, -1 }, { -1, -1 } };
		} else if (color.equals("RED")) {
			moves = new int[][] { { 1, -1 }, { -1, -1 } };
		} else if (color.equals("BLACK")) {
			moves = new int[][] { { 1, 1 }, { -1, 1 } };
		}

		// check normals moves
		for (int[] move : moves) {
			// check that move is on board
			if (!isOnBoard(new int[] { pos[0] + move[0], pos[1] + move[1] }))
				continue;

			if (board[pos[1] + move[1]][pos[0] + move[0]].equals("EMPTY"))
				return true;
		}
		return false;
	}

	private boolean canMove(int[] pos1, int[] pos2) {
		String color = board[pos1[1]][pos1[0]].split("_")[0];
		if (color.equals("EMPTY"))
			return false;

		int[][] moves = new int[0][0];
		if (board[pos1[1]][pos1[0]].contains("KING")) {
			moves = new int[][] { { 1, 1 }, { -1, 1 }, { 1, -1 }, { -1, -1 } };
		} else if (color.equals("RED")) {
			moves = new int[][] { { 1, -1 }, { -1, -1 } };
		} else if (color.equals("BLACK")) {
			moves = new int[][] { { 1, 1 }, { -1, 1 } };
		}

		// check jumps
		for (int[] move : moves) {
			// check that move is on board
			if (!isOnBoard(new int[] { pos1[0] + move[0] * 2, pos1[1] + move[1] * 2 }))
				continue;

			if (!board[pos1[1] + move[1]][pos1[0] + move[0]].equals("EMPTY")
					&& !board[pos1[1] + move[1]][pos1[0] + move[0]].contains(color)
					&& board[pos1[1] + move[1] * 2][pos1[0] + move[0] * 2].equals("EMPTY")
					&& pos1[1] + move[1] * 2 == pos2[1] && pos1[0] + move[0] * 2 == pos2[0])
				return true;
		}

		// check if must jump
		if (colorCanJump(color))
			return false;

		// check normals moves
		for (int[] move : moves) {
			// check that move is on board
			if (!isOnBoard(new int[] { pos1[0] + move[0], pos1[1] + move[1] }))
				continue;

			if (board[pos1[1] + move[1]][pos1[0] + move[0]].equals("EMPTY") && pos1[1] + move[1] == pos2[1]
					&& pos1[0] + move[0] == pos2[0])
				return true;
		}

		return false;
	}

	private boolean colorCanJump(String color) {
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				if (board[y][x].contains(color) && canJump(new int[] { x, y }))
					return true;
			}
		}
		return false;
	}
	
	private void promotePieces() {
		for (int x = 0; x < 8; x++) {
			if (board[0][x].equals("RED"))
				board[0][x] = "RED_KING";
			else if (board[7][x].equals("BLACK"))
				board[7][x] = "BLACK_KING";
		}
	}

	@Override
	protected void setGameName() {
		this.gameName = "Checkers";
	}

	@Override
	protected void setBoardImage() {
		this.gameImage = new GameImage("CHECKERS_BOARD");

	}

	@Override
	protected void startClock() {
		// TODO Auto-generated method stub

	}

	@Override
	protected GameInventory getGameInventory() {
		return new CheckersInventory(this);
	}

	@Override
	public ArrayList<String> getTeamNames() {
		return new ArrayList<String>() {{
			add("RED");
			add("BLACK");
		}};
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

		String teamTurn = teamManager.getTurnTeam();

		if (selected == null || (canDeSelect && board[position[1]][position[0]].contains(teamTurn))) {
			// check that selected piece is correct turn
			if (!board[position[1]][position[0]].contains(teamTurn))
				return;

			// check if piece must jump
			if (colorCanJump(teamTurn)) {
				player.sendMessage("You must select a piece that can jump if a jump is possible.");
				// check if piece can jump
				if (!canJump(position))
					return;
			} else if (!canAdvance(position)) // check if piece has normal moves if no jumps available
				return;
			
			selected = position;
			canDeSelect = true;
		} else {
			// check that selected piece is correct turn
			if (!board[selected[1]][selected[0]].contains(teamTurn))
				return;

			// check if can make move
			if (!canMove(selected, position))
				return;

			boolean isJump = (2 == Math.abs(position[1] - selected[1]));

			// move piece
			board[position[1]][position[0]] = board[selected[1]][selected[0]];
			board[selected[1]][selected[0]] = "EMPTY";
			
			movesSinceCapture++; // update capture counter

			// remove jumped pieces & reset capture counter
			if (isJump) {
				board[(position[1] + selected[1]) / 2][(position[0] + selected[0]) / 2] = "EMPTY";
				movesSinceCapture = 0;
			}

			selected = null;
			// swap turn
			
			//promote pieces
			promotePieces();
			
			if (isJump && canJump(position)) {
				selected = position;
				canDeSelect = false;
			} else {
				teamManager.nextTurn();
			}
		}

		String winner = checkGameOver();
		if (!winner.equals("EMPTY")) {
			GamePlayer playerWinner = teamManager.getGamePlayerByTeam(winner);
			endGame(playerWinner);
			return;
		}

		updateButtons();
		mapManager.renderBoard();

	}

	public void endGame(GamePlayer gamePlayerWinner) {
		buttons.clear();
		movesSinceCapture = 0;
		selected = null;
		canDeSelect = false;

		String message;
		if(gamePlayerWinner != null) {
			message = gamePlayerWinner.getPlayer().getDisplayName() + " has won the game!";
		} else {
			message = ChatColor.GREEN + "Tie game!";
		}

		for(GamePlayer player : teamManager.getGamePlayers()) {
			player.getPlayer().sendMessage(message);
		}

		super.endGame(gamePlayerWinner);
	}

	private String checkGameOver() { // returns empty for game not over
		boolean foundRed = false;
		boolean foundBlack = false;
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				if (board[y][x].contains("RED"))
					foundRed = true;
				if (board[y][x].contains("BLACK"))
					foundBlack = true;
			}
		}
		if (foundRed == false)
			return "BLACK";
		if (foundBlack == false)
			return "RED";
		if (movesSinceCapture >= 40)
			return "TIE";
				
		return "EMPTY";
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
