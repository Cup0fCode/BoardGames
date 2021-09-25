package water.of.cup.boardgames.game.games.slots;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import water.of.cup.boardgames.BoardGames;
import water.of.cup.boardgames.game.Button;
import water.of.cup.boardgames.game.Clock;
import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.GamePlayer;
import water.of.cup.boardgames.game.inventories.GameInventory;
import water.of.cup.boardgames.game.maps.MapData;
import water.of.cup.boardgames.game.maps.Screen;
import water.of.cup.boardgames.game.storage.GameStorage;
import water.of.cup.boardgames.config.ConfigUtil;
import water.of.cup.boardgames.game.storage.CasinoGamesStorageType;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class SlotsGame extends Game {
	private BoardGames instance = BoardGames.getInstance();

	private int[] dimensions; // {x , y}
	protected Button[][] slotsButtons;
	private ArrayList<SlotsSymbol> symbols;
	private SlotsSymbol[][] currentSymbols;
	private int initialBet;

	private double winRatio;
	private double payout = .9;

	private Screen screen;

	public SlotsGame(int rotation) {
		super(rotation);

		screen.setGameImage(gameImage);

		dimensions = getDimensions();

		// verify dimensions are accurate
		assert dimensions.length == 2 && dimensions[0] != 0 && dimensions[1] != 0;

		slotsButtons = getSlotsButtons();

		for (Button[] row : slotsButtons)
			for (Button b : row) {
				b.setScreen(screen);
				buttons.add(b);
			}

		// verify slots size is consistent
		assert dimensions[1] == slotsButtons.length && dimensions[0] == slotsButtons[0].length;
		symbols = getSlotsSymbols();

		currentSymbols = new SlotsSymbol[dimensions[1]][dimensions[0]];

		winRatio = calculateWinRatio();

		spin();
		setButtonImages();
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

		instance.getEconomy().withdrawPlayer(teamManager.getTurnPlayer().getPlayer(), initialBet);
		SlotsSpinner spinner = new SlotsSpinner(this, mapManager, 10);
		spinner.runTaskTimer(BoardGames.getInstance(), 2, 2);
	}

	protected void finishSpin() {
		// TODO Auto-generated method stub
		spin();
		givePayout();
		setButtonImages();
		mapManager.renderBoard();

		clearGamePlayers();
		endGame(null);
	}

	protected void spin() {
		boolean win = Math.random() < winRatio;

		do {
			for (int y = 0; y < dimensions[1]; y++)
				for (int x = 0; x < dimensions[0]; x++) {
					int r = (int) (Math.random() * symbols.size());
					currentSymbols[y][x] = symbols.get(r);
				}
		} while (calculatePayout() > 0 != win);
	}

	private void givePayout() {
		// Null check
		if(teamManager.getTurnPlayer() == null) return;

		double payout = calculatePayout() * initialBet;

		// give payout
		Player player = teamManager.getTurnPlayer().getPlayer();
		instance.getEconomy().depositPlayer(player, payout);
		CasinoGamesStorageType.updateGameStorage(this, teamManager.getTurnPlayer(), payout == 0 ? initialBet * -1 : payout);
		player.sendMessage(ConfigUtil.CHAT_SLOTS_WIN.buildString(payout + ""));
//		player.sendMessage("Win Ratio: " + winRatio);
//		player.sendMessage("Average Win Payout: " + getAverageWinPayout());

		// TODO: boolean check send message if player won
		
		// send nearby players win message
		if (payout > 0)
			player.getWorld().getNearbyEntities(player.getLocation(), 10, 10, 10, e -> e instanceof Player && e != player).stream()
					.forEach(e -> e.sendMessage(ConfigUtil.CHAT_SLOTS_WIN_NEARBY.buildString(player.getDisplayName(), getAltName(), payout)));
	}

	private double calculatePayout() {
		// calculate payout
		double payout = 0;

		for (SlotsSymbol[] line : currentSymbols) {

			double tempPayout = 0;
			double multiplier = 1;

			HashMap<SlotsSymbol, Integer> symbolQuantity = new HashMap<SlotsSymbol, Integer>();

			// calculate symbolQuantity:
			for (SlotsSymbol symbol : line)
				if (!symbolQuantity.containsKey(symbol))
					symbolQuantity.put(symbol, 1);
				else
					symbolQuantity.put(symbol, symbolQuantity.get(symbol) + 1);

			// add each symbols payout
			for (SlotsSymbol symbol : symbolQuantity.keySet()) {
				if (symbol.getType() == SymbolType.STANDARD)
					tempPayout += symbol.getPayout(symbolQuantity);
				if (symbol.getType() == SymbolType.MULTIPLIER)
					multiplier = symbol.getPayout(symbolQuantity);
			}

			payout += multiplier * tempPayout;
		}
		return payout;
	}

	protected void setButtonImages() {
		for (int y = 0; y < dimensions[1]; y++)
			for (int x = 0; x < dimensions[0]; x++)
				slotsButtons[y][x].setImage(currentSymbols[y][x].getGameImage());
	}

	protected abstract int[] getDimensions();

	protected abstract Button[][] getSlotsButtons();

	protected abstract ArrayList<SlotsSymbol> getSlotsSymbols();

	@Override
	protected void setMapInformation(int rotation) {
		this.mapStructure = new int[][] { { -1 } };
		this.placedMapVal = -1;

		screen = new Screen(this, "LIBERTYBELL_BOARD", 2, new int[] { 0, 0 }, new int[][] { { 1 } }, rotation);
		screens.add(screen);
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

	private double getAverageWinPayout() {
		double totalPayout = 0;
		double totalLikelyhood = 0;
		for (SlotsSymbol symbol : symbols) {
			double[] stats = symbol.getAveragePayoutAndQuantity(symbols.size(), dimensions[0]);
			totalPayout += stats[0] * stats[1];
			totalLikelyhood += stats[1];
		}
		totalPayout *= dimensions[1];

		if (totalLikelyhood == 0)
			return 0;
		return totalPayout / totalLikelyhood;
	}

	private double calculateWinRatio() {
		return payout / getAverageWinPayout();
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
		return new SlotsInventory(this);
	}

	@Override
	protected GameStorage getGameStorage() {
		// TODO Auto-generated method stub
		return new SlotsStorageType(this);
	}

	@Override
	public ArrayList<String> getTeamNames() {
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
}
