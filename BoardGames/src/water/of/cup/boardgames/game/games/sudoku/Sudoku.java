package water.of.cup.boardgames.game.games.sudoku;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import water.of.cup.boardgames.game.BoardItem;
import water.of.cup.boardgames.game.Button;
import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.GameImage;
import water.of.cup.boardgames.game.GamePlayer;
import water.of.cup.boardgames.game.inventories.GameInventory;

public class Sudoku extends Game {
	private SudokuPuzzle puzzle;
	private Button[][] boardButtons;
	private Button[] numberButtons;
	private int[] selected;

	public Sudoku(int rotation) {
		super(rotation);
	}

	@Override
	protected void setMapInformation(int rotation) {
		this.mapStructure = new int[][] { { 1 } };
		this.placedMapVal = 1;

	}

	@Override
	protected void startGame() {
		puzzle = new SudokuPuzzle();
		selected = null;
		setInGame();
		createBoard();
		updateBoard();

	}

	private void createBoard() {
		boardButtons = new Button[9][9];
		for (int y = 0; y < 9; y++)
			for (int x = 0; x < 9; x++) {
				boardButtons[y][x] = new Button(this, "SUDOKU_0",
						new int[] { 9 + x * 12 + (x / 3) * 2, 3 + y * 12 + (y / 3) * 2 }, 0, "TILE");
				boardButtons[y][x].setClickable(true);
				buttons.add(boardButtons[y][x]);
			}

		numberButtons = new Button[9];
		for (int n = 0; n < 9; n++) {
			numberButtons[n] = new Button(this, "SUDOKU_" + (n + 1), new int[] { 3 + n * 14, 117 }, 0, "" + (n + 1));
			numberButtons[n].setClickable(true);
			buttons.add(numberButtons[n]);
		}

	}

	private void updateBoard() {
		int[][] vals = puzzle.getKnownStructure();
		int n = -1;
		if (selected != null)
			n = vals[selected[1]][selected[0]];
		if (n == 0)
			n = -1;

		for (int y = 0; y < 9; y++)
			for (int x = 0; x < 9; x++) {
				if (vals[y][x] == n || selected != null && (x == selected[0] && y == selected[1])) {
					boardButtons[y][x].setImage("SUDOKU_HIGHLIGHTED");
				} else {
					boardButtons[y][x].setImage("SUDOKU_0");
				}
				boardButtons[y][x].getImage().addGameImage(new GameImage("SUDOKU_" + vals[y][x]), new int[] { 0, 0 });
			}

		mapManager.renderBoard();
	}
	
	private void removeFinishedNumberButtons() {
		for (int n : puzzle.getFinishedNumbers()) {
			if (numberButtons[n - 1] != null) {
				buttons.remove(numberButtons[n - 1]);
				numberButtons[n - 1] = null;
				updateBoard();
			}
		}
	}
	
	private void checkGameOver() {
		if (puzzle.checkGameWon())
			endGame(teamManager.getTurnPlayer());
	}

	private int[] getButtonLocation(Button b) {
		for (int y = 0; y < 9; y++) {
			for (int x = 0; x < 9; x++) {
				if (b == boardButtons[y][x])
					return new int[] { x, y };
			}
		}
		return null;
	}

	@Override
	protected void setGameName() {
		this.gameName = "Sudoku";

	}

	@Override
	protected void setBoardImage() {
		this.gameImage = new GameImage("SUDOKU_BOARD");

	}

	@Override
	protected void startClock() {
		// TODO Auto-generated method stub

	}

	@Override
	protected GameInventory getGameInventory() {
		return new SudokuInventory(this);
	}

	@Override
	public ArrayList<String> getTeamNames() {
		return null;
	}

	@Override
	public void click(Player player, double[] loc, ItemStack map) {
		GamePlayer gamePlayer = getGamePlayer(player);
		if (!teamManager.getTurnPlayer().equals(gamePlayer))
			return;

		int[] clickLoc = mapManager.getClickLocation(loc, map);
		Button b = getClickedButton(gamePlayer, clickLoc);

		if (b == null)
			return;

		if (b.getName().equals("TILE")) {
			int[] position = getButtonLocation(b);
			selected = position;
			updateBoard();
		} else {
			if (selected == null)
				return;
			for (Button but : numberButtons) {
				if (b == but) {
					int i = Integer.parseInt(but.getName());
					if (!puzzle.check(selected, i)) {
						this.endGame(null);
						player.sendMessage("You lost.");
					} else {
						removeFinishedNumberButtons();
						checkGameOver();
					}
					break;
				}
			}

			updateBoard();
		}

	}

	@Override
	protected void gamePlayerOutOfTime(GamePlayer turn) {
		// TODO Auto-generated method stub

	}

	@Override
	public ItemStack getBoardItem() {
		// TODO Auto-generated method stub
		return new BoardItem(gameName, new ItemStack(Material.BIRCH_TRAPDOOR, 1));
	}

}
