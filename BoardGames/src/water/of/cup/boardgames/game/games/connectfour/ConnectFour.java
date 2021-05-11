package water.of.cup.boardgames.game.games.connectfour;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import water.of.cup.boardgames.game.BoardItem;
import water.of.cup.boardgames.game.Button;
import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.GameImage;
import water.of.cup.boardgames.game.GamePlayer;
import water.of.cup.boardgames.game.maps.Screen;

public class ConnectFour extends Game {
	Screen redScreen;
	Screen blueScreen;
	String turn; // {RED, BLUE, OVER}
	String[][] chipLocations;
	Button[][] redBoardButtons;
	Button[][] blueBoardButtons;

	public ConnectFour(int rotation) {
		super(rotation);
		turn = "OVER";
	}

	@Override
	protected void setMapInformation(int rotation) {
		this.mapStructure = new int[][] { { 0 }, { -1 } };
		this.placedMapVal = -1;
		redScreen = new Screen(this, "CONNECTFOUR_BOARD", 0, new int[] { 0, 0 }, new int[][] { { 1 } }, rotation);
		screens.add(redScreen);
		blueScreen = new Screen(this, "CONNECTFOUR_BOARD", 2, new int[] { 0, 1 }, new int[][] { { 2 } }, rotation);
		screens.add(blueScreen);
	}

	@Override
	protected void startGame() {
		turn = "RED";
		mapManager.renderBoard();
		resetChips();
		createChipButtons();
		setChipImages();
	}

	private void resetChips() {
		chipLocations = new String[6][7];
		for (int y = 0; y < 6; y++) {
			for (int x = 0; x < 7; x++) {
				chipLocations[y][x] = "EMPTY";
			}
		}
	}

	private void createChipButtons() {
		redBoardButtons = new Button[6][7];
		blueBoardButtons = new Button[6][7];
		for (int y = 0; y < 6; y++) {
			for (int x = 0; x < 7; x++) {
				Button redButton = new Button(this, "CONNECTFOUR_EMPTY", new int[] { 5 + x * 17, 1 + y * 17 }, 0,
						"EMPTY");
				redButton.setScreen(redScreen);
				buttons.add(redButton);
				redBoardButtons[y][x] = redButton;

				Button blueButton = new Button(this, "CONNECTFOUR_EMPTY", new int[] { 124 - (x + 1) * 17, 1 + y * 17 },
						0, "EMPTY");
				blueButton.setScreen(blueScreen);
				buttons.add(blueButton);
				blueBoardButtons[y][x] = blueButton;
			}
		}
	}

	private String checkGameOver() { // empty for no winner
		// check horizontal
		for (int y = 0; y < 6; y++) {
			xloop: for (int x = 0; x < 4; x++) {
				String color = chipLocations[y][x];
				for (int ax = 0; ax < 4; ax++)
					if (chipLocations[y][x + ax].equals("EMPTY") || !chipLocations[y][x + ax].equals(color))
						continue xloop;
				// game over
				return color;
			}
		}
		// check vertical
		for (int x = 0; x < 7; x++) {
			yloop: for (int y = 0; y < 3; y++) {
				String color = chipLocations[y][x];
				for (int ay = 0; ay < 4; ay++)
					if (chipLocations[y + ay][x].equals("EMPTY") || !chipLocations[y + ay][x].equals(color))
						continue yloop;
				// game over
				return color;
			}
		}
		
		// check diagonal 1
		for (int x = 0; x < 4; x++) {
			yloop: for (int y = 0; y < 3; y++) {
				String color = chipLocations[y][x];
				for (int a = 0; a < 4; a++)
					if (chipLocations[y + a][x + a].equals("EMPTY") || !chipLocations[y + a][x + a].equals(color))
						continue yloop;
				// game over
				return color;
			}
		}
		// check diagonal -1
				for (int x = 0; x < 4; x++) {
					yloop: for (int y = 3; y < 6; y++) {
						String color = chipLocations[y][x];
						for (int a = 0; a < 4; a++)
							if (chipLocations[y - a][x + a].equals("EMPTY") || !chipLocations[y - a][x + a].equals(color))
								continue yloop;
						// game over
						return color;
					}
				}

		return "EMPTY";
	}

	private int[] getClickedChipLocation(Button b) {
		for (int y = 0; y < 6; y++) {
			for (int x = 0; x < 7; x++) {
				if (b == redBoardButtons[y][x])
					return new int[] { x, y };
				if (b == blueBoardButtons[y][x])
					return new int[] { x, y };
			}
		}
		return null;
	}

	private void setChipImages() {
		for (int y = 0; y < 6; y++) {
			for (int x = 0; x < 7; x++) {
				String imageName = "CONNECTFOUR_" + chipLocations[y][x];
				redBoardButtons[y][x].setImage(imageName);
				blueBoardButtons[y][x].setImage(imageName);
			}
		}
	}

	private boolean placeChip(int col, String color) {
		int x = col;
		int y = 0;
		while (y < 5 && chipLocations[y + 1][x].equals("EMPTY")) {
			y++;
		}
		if (!chipLocations[y][x].equals("EMPTY"))
			return false;

		chipLocations[y][x] = color;
		setChipImages();

		return true;
	}

	@Override
	protected void setGameName() {
		gameName = "ConnectFour";

	}

	@Override
	protected void setBoardImage() {
		this.gameImage = new GameImage("CONNECTFOUR_EMPTY");

	}

	@Override
	protected void startClock() {
		// TODO Auto-generated method stub

	}

	@Override
	public void click(Player player, double[] loc, ItemStack map) {
		// check if game is over
		if (turn.equals("EMPTY")) {
			player.sendMessage(checkGameOver().toLowerCase() + " won!");
			return;
		}
		
		int[] clickLoc = mapManager.getClickLocation(loc, map);
		Screen screen = mapManager.getClickedScreen(map);
		if (screen == null)
			return;
		Button b = screen.getClickedButton(getGamePlayer(player), clickLoc);
		int[] position = getClickedChipLocation(b);

		if (position == null)
			return;

		if ((turn.equals("RED") && screen == redScreen) || (turn.equals("BLUE") && screen == blueScreen)) {
			player.sendMessage(turn);
			int col = position[0];
			if (placeChip(col, turn) == true) {
				mapManager.renderBoard();
				player.sendMessage("Rendered Board");
				if (turn.equals("RED")) {
					turn = "BLUE";
				} else {
					turn = "RED";
				}
				String winner = checkGameOver();
				if (!winner.equals("EMPTY")) {
					// game over
					player.sendMessage(winner.toLowerCase() + " won!");
					turn = "EMPTY";
				}
			}
		}

		// if (screen == )

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