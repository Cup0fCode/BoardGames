package water.of.cup.boardgames.game.games.plinko;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import water.of.cup.boardgames.BoardGames;
import water.of.cup.boardgames.game.*;
import water.of.cup.boardgames.game.inventories.GameInventory;
import water.of.cup.boardgames.game.maps.MapData;
import water.of.cup.boardgames.game.maps.Screen;
import water.of.cup.boardgames.game.storage.GameStorage;
import water.of.cup.boardgames.config.ConfigUtil;
import water.of.cup.boardgames.game.storage.CasinoGamesStorageType;

import java.util.ArrayList;

public class Plinko extends Game {
	private BoardGames instance = BoardGames.getInstance();
	private double initialBet;
	private String risk;
	private Button ball;
	private int[] ballLoc;
	private double[] multipliers;
	private Screen screen;

	public Plinko(int rotation) {
		super(rotation);
		ballLoc = new int[] { 61, 3 };
		ball = new Button(this, "PLINKO_BALL", ballLoc, 0, "ball");
		buttons.add(ball);
		ball.setScreen(screen);
		multipliers = new double[] { 0.5, 1, 1.1, 2.1, 5.6 };
	}

	@Override
	protected void startGame() {
		initialBet = (int) this.gameInventory.getGameData("betAmount"); // TODO: get initial bet
		if (instance.getEconomy().getBalance(teamManager.getTurnPlayer().getPlayer()) < initialBet) {
			teamManager.getTurnPlayer().getPlayer().sendMessage(water.of.cup.boardgames.config.ConfigUtil.CHAT_GUI_GAME_NO_MONEY_CREATE.toString());
			clearGamePlayers();
			endGame(null);
			return;
		}

		risk = (String) getGameData("risk");
		
		if (risk.equals(ConfigUtil.GUI_PLINKO_LOW_RISK.toRawString())) {
			multipliers = new double[] { 0.5, 1, 1.1, 2.1, 5.6 };
			screen.getGameImage().setImage("PLINKO_LOW");
		} else if (risk.equals(ConfigUtil.GUI_PLINKO_NORMAL_RISK.toRawString())) {
			multipliers = new double[] { 0.4, 0.7, 1.3, 3, 13 };
			screen.getGameImage().setImage("PLINKO_NORMAL");
		} else if (risk.equals(ConfigUtil.GUI_PLINKO_HIGH_RISK.toRawString())) {
			multipliers = new double[] { 0.2, 0.3, 1.5, 4, 29 };
			screen.getGameImage().setImage("PLINKO_HIGH");
		}
		ballLoc[0] = 61;
		ballLoc[1] = 3;

		instance.getEconomy().withdrawPlayer(teamManager.getTurnPlayer().getPlayer(), initialBet);
		PlinkoRunnable spinner = new PlinkoRunnable(this, mapManager);
		spinner.runTaskTimer(BoardGames.getInstance(), 10, 10);
	}

	protected void moveDown(int n) {
		ballLoc[1] += n;
	}

	protected void move(boolean right, int n) {
		if (right)
			ballLoc[0] -= 3 + n % 2;
		else
			ballLoc[0] += 3 + n % 2;
		moveDown(n);

	}
	
	@Override
	public boolean canPlaceBoard(Location loc, int rotation) {
		int[] centerLoc = mapManager.getMapValsLocationOnRotatedBoard(placedMapVal);
		int[] mapDimensions = mapManager.getRotatedDimensions();

		// calculate map bounds
		int t1X = -centerLoc[0];
		int t2X = mapDimensions[0] + t1X;

		int t1Y = 0;
		int t2Y = 0; // for future changes

		int t1Z = -centerLoc[1];
		int t2Z = mapDimensions[1] + t1Z;

		// calculate min and max bounds
		int maxX = Math.max(t1X, t2X);
		int minX = Math.min(t1X, t2X);

		int maxY = Math.max(t1Y, t2Y);
		int minY = Math.min(t1Y, t2Y);

		int maxZ = Math.max(t1Z, t2Z);
		int minZ = Math.min(t1Z, t2Z);

		// check if blocks are empty
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				for (int z = minZ; z <= maxZ; z++) {
					if (!loc.getWorld().getBlockAt(loc.getBlockX() + x, loc.getBlockY() + y, loc.getBlockZ() + z)
							.isEmpty())
						return false;

					// check that place on is not empty
					for (MapData mapData : mapManager.getMapDataAtLocationOnRotatedBoard(x - t1X, z - t1Z, y - t1Y)) {
						if (mapData.getMapVal() <= 0)
							continue;
						Location frameLoc = new Location(loc.getWorld(), loc.getBlockX() + x, loc.getBlockY() + y,
								loc.getBlockZ() + z);
						Block placedOn = frameLoc.getBlock().getRelative(mapData.getBlockFace().getOppositeFace());
						if (placedOn.getType() == Material.AIR)
							return false;
					}
				}
			}
		}

		// check that bottom blocks are not empty
//		for (int x = minX; x <= maxX; x++)
//			for (int z = minZ; z <= maxZ; z++) {
//				if (loc.getWorld().getBlockAt(loc.getBlockX() + x, loc.getBlockY() - 1, loc.getBlockZ() + z).isEmpty())
//					return false;
//			}

		return true;
	}

	@Override
	protected void setMapInformation(int rotation) {
		this.mapStructure = new int[][] { { -1 } };
		this.placedMapVal = -1;
		screen = new Screen(this, "PLINKO_LOW", 2, new int[] { 0, 0 }, new int[][] { { 1 } }, rotation);
		screens.add(screen);
	}

	@Override
	protected void setGameName() {
		this.gameName = "Plinko";

	}

	@Override
	protected void setBoardImage() {
		this.gameImage = new GameImage("PLINKO_LOW");
	}

	@Override
	protected void clockOutOfTime() {
		// TODO Auto-generated method stub

	}

	@Override
	protected Clock getClock() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected GameInventory getGameInventory() {
		// TODO Auto-generated method stub
		return new PlinkoInventory(this);
	}

	@Override
	protected GameStorage getGameStorage() {
		// TODO Auto-generated method stub
		return new PlinkoStorage(this);
	}

	@Override
	public ArrayList<String> getTeamNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected GameConfig getGameConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void click(Player player, double[] loc, ItemStack map) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void gamePlayerOutOfTime(GamePlayer turn) {
		// TODO Auto-generated method stub

	}

	@Override
	public ItemStack getBoardItem() {
		return new BoardItem(gameName, new ItemStack(Material.ACACIA_TRAPDOOR, 1));
	}

	public void endGame() {
		double multiplier = multipliers[Math.abs(ballLoc[0] - 61) / 14];
		double bet = initialBet * multiplier;
		// TODO get bet multiplier
		teamManager.getTurnPlayer().getPlayer().sendMessage(ConfigUtil.CHAT_PLINKO_WIN.buildString(bet + ""));
		instance.getEconomy().depositPlayer(teamManager.getTurnPlayer().getPlayer(), bet);
		CasinoGamesStorageType.updateGameStorage(this, teamManager.getTurnPlayer(), bet);
		endGame(teamManager.getTurnPlayer());

	}
}
