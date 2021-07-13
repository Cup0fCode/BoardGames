package water.of.cup.boardgames.game.games.connectfour;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import water.of.cup.boardgames.config.ConfigUtil;
import water.of.cup.boardgames.config.GameRecipe;
import water.of.cup.boardgames.game.*;
import water.of.cup.boardgames.game.games.tictactoe.TicTacToeStorage;
import water.of.cup.boardgames.game.inventories.GameInventory;
import water.of.cup.boardgames.game.maps.Screen;
import water.of.cup.boardgames.game.storage.GameStorage;
import water.of.cup.boardgames.game.storage.StorageType;

import java.util.ArrayList;

public class ConnectFour extends Game {
	Screen redScreen;
	Screen blueScreen;
	String[][] chipLocations;
	Button[][] redBoardButtons;
	Button[][] blueBoardButtons;

	public ConnectFour(int rotation) {
		super(rotation);
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
		buttons.clear();
		resetChips();
		createChipButtons();
		setChipImages();
		setInGame();
		mapManager.renderBoard();
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
				redButton.setClickable(true);
				redButton.setScreen(redScreen);
				buttons.add(redButton);
				redBoardButtons[y][x] = redButton;

				Button blueButton = new Button(this, "CONNECTFOUR_EMPTY", new int[] { 124 - (x + 1) * 17, 1 + y * 17 },
						0, "EMPTY");
				blueButton.setClickable(true);
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
				return color + "_CROSS";
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
				return color + "_CROSS";
			}
		}

		// check all positions filled
		boolean tie = true;
		outer: for (String[] strings : chipLocations) {
			for (String string : strings) {
				if (string.equals("EMPTY")) {
					tie = false;
					break outer;
				}
			}
		}

		if(tie) return "t";

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
	protected Clock getClock() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected GameInventory getGameInventory() {
		return new ConnectFourInventory(this);
	}

	@Override
	protected GameStorage getGameStorage() {
		return new ConnectFourStorage(this);
	}

	@Override
	public ArrayList<String> getTeamNames() {
		return new ArrayList<String>() {{
			add("RED");
			add("BLUE");
		}};
	}

	@Override
	protected GameConfig getGameConfig() {
		return new ConnectFourConfig(this);
	}

	@Override
	public void click(Player player, double[] loc, ItemStack map) {
		GamePlayer gamePlayer = getGamePlayer(player);
		if(!teamManager.getTurnPlayer().equals(gamePlayer)) return;

		int[] clickLoc = mapManager.getClickLocation(loc, map);
		Screen screen = mapManager.getClickedScreen(map);
		if (screen == null)
			return;

		Button b = screen.getClickedButton(gamePlayer, clickLoc);
		int[] position = getClickedChipLocation(b);

		if (position == null)
			return;

		String teamTurn = teamManager.getTurnTeam();

		int col = position[0];
		if (placeChip(col, teamTurn)) {
			this.playGameSound("click");

			mapManager.renderBoard();

			teamManager.nextTurn();

			String winner = checkGameOver();

			if (!winner.equals("EMPTY")) {
				// game over
				boolean crossWin = false;

				if(winner.indexOf("_CROSS") > -1) {
					winner = winner.substring(0, winner.indexOf("_CROSS"));
					crossWin = true;
				}

				GamePlayer playerWinner = teamManager.getGamePlayerByTeam(winner);
				endGame(playerWinner, crossWin);
			}
		}
	}

	public void endGame(GamePlayer gamePlayer) {
		endGame(gamePlayer, false);
	}

	public void endGame(GamePlayer gamePlayerWinner, boolean crossWin) {
		//buttons.clear();

		updateGameStorage(gamePlayerWinner, crossWin);

		super.endGame(gamePlayerWinner);
	}

	private void updateGameStorage(GamePlayer gamePlayerWinner, boolean crossWin) {
		if(!hasGameStorage()) return;

		if(gamePlayerWinner != null && crossWin) {
			gameStorage.updateData(gamePlayerWinner.getPlayer(), StorageType.CROSS_WINS, 1);
		}
	}

	@Override
	protected void gamePlayerOutOfTime(GamePlayer turn) {
		// TODO Auto-generated method stub

	}

	@Override
	public ItemStack getBoardItem() {
		return new BoardItem(getAltName(), new ItemStack(Material.OAK_TRAPDOOR, 1));
	}

	@Override
	protected void clockOutOfTime() {
		// TODO Auto-generated method stub
		
	}

}
