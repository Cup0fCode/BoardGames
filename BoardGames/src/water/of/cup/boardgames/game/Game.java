package water.of.cup.boardgames.game;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;

import water.of.cup.boardgames.BoardGames;
import water.of.cup.boardgames.game.maps.GameMap;
import water.of.cup.boardgames.game.maps.MapManager;
import water.of.cup.boardgames.game.wagers.WagerManager;

public abstract class Game {
	private static NamespacedKey gameIdKey;

	protected int gameId;
	protected String gameName;
	protected MapManager mapManager;
	protected HashMap<Player, GamePlayer> players;
	protected int turn;
	protected BufferedImage boardImage;
	protected ArrayList<Button> buttons;
	protected WagerManager wagerManager;
	protected Clock clock;

	protected int[][] mapStructure; // the structure of mapVals, 0 for missing map
	protected int placedMapVal; // the value of the map at the placed board location

	protected abstract void setMapInformation(int rotation); // set mapStructure and placedMapVal
	private void setGameId() {
		gameId = 1;
	}
	protected abstract void setGameName(); // set gameName
	protected abstract void setBoardImage(); // set board Image
	protected abstract void startClock();
	
	public Game(int rotation) {
		setMapInformation(rotation);
		createMapManager();
		setGameId();
		assert gameId != 0;
		setGameName();
		assert gameName != null;
		setBoardImage();
		//assert boardImage != null;
		wagerManager = new WagerManager();
		players = new HashMap<Player, GamePlayer>();
		turn = 0;
		buttons = new ArrayList<Button>();
	}

	abstract public void click(Player player, double[] loc, ItemStack map);

	public boolean canPlaceBoard(Location loc, int rotation) {
		int[] centerLoc = mapManager.getMapValsLocationOnRotatedBoard(placedMapVal);
		int[] mapDimensions = mapManager.getRotatedDimensions();
		
		// calculate map bounds
		int minX = 1 - mapDimensions[0] + centerLoc[0];
		int maxX = mapDimensions[0] - centerLoc[0] - 1;

		int maxY = 0;
		int minY = 0; // for future changes

		int minZ = 1 - mapDimensions[1] + centerLoc[1];
		int maxZ = mapDimensions[1] - centerLoc[1] - 1;

		// check if blocks are empty
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				for (int z = minZ; z <= maxZ; z++) {
					if (!loc.getWorld().getBlockAt(loc.getBlockX() + x, loc.getBlockY() + y, loc.getBlockZ() + z).isEmpty())
						return false;
				}
			}
		}

		return true;
	}

	public void placeBoard(Location loc, int rotation) {
		// can place board should always be called first

		World world = loc.getWorld();

		int[] centerLoc = mapManager.getMapValsLocationOnRotatedBoard(placedMapVal);
		int[] mapDimensions = mapManager.getRotatedDimensions();
		
		// calculate map bounds
		int minX = 1 - mapDimensions[0] + centerLoc[0];
		int maxX = mapDimensions[0] - centerLoc[0] - 1;

		int maxY = 0;
		int minY = 0; // for future changes

		int minZ = 1 - mapDimensions[1] + centerLoc[1];
		int maxZ = mapDimensions[1] - centerLoc[1] - 1;

		// spawn ItemFrames
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				for (int z = minZ; z <= maxZ; z++) {
					int mapVal = mapManager.getMapValAtLocationOnRotatedBoard(centerLoc[0] + x, centerLoc[0] + z);
					// skip maps with value 0
					if (mapVal == 0)
						continue;

					// create the map
					ItemStack map = new GameMap(this, mapVal, new ItemStack(Material.FILLED_MAP, 1));

					// spawn itemFrame
					Location frameLoc = new Location(loc.getWorld(), loc.getBlockX() + x, loc.getBlockY() + y, loc.getBlockZ() + z);

					ItemFrame frame = world.spawn(frameLoc, ItemFrame.class);
					frame.setItem(map);
					frame.setFacingDirection(BlockFace.UP, true);
					frame.setInvulnerable(true);
					frame.setFixed(true);
					frame.setVisible(true);
					frameLoc.getBlock().setType(Material.BARRIER);
				}
			}
		}
		BoardGames.getInstance().getGameManager().addGame(this);
	}

	public void cancelGame() {
		wagerManager.endAll();
	}

	protected Button getClickedButton(GamePlayer gamePlayer, int[] loc) { // returns null if no button is clicked
		for (Button button : buttons) {
			if (button.clicked(gamePlayer, loc))
				return button;
		}
		return null;
	}

	public void renderMaps() {
		// TODO: render maps using mapManager
	}

	public boolean hasPlayer(Player player) {
		return players.containsKey(player);
	}

	public ArrayList<Game> getPlayerQueue() {
		return null;
	}

	public ArrayList<Game> getPlayerDecideQueue() {
		return null;
	}

	public int getGameId() {
		return gameId;
	}

	public GamePlayer getTurn() {
		return ((List<GamePlayer>) players.values()).get(turn);
	}

	public void setTurn(int turn) {
		this.turn = turn;
	}

	public void setTurn(GamePlayer gamePlayer) {
		turn = ((List<GamePlayer>) players.values()).indexOf(gamePlayer);
	}

	public void setTurn(Player player) {
		setTurn(players.get(player));
	}

	public Player getPlayer(GamePlayer player) {
		for (Player p : players.keySet()) {
			if (players.get(p).equals(player))
				return p;
		}
		return null;
	}

	public ArrayList<GamePlayer> getGamePlayers() {
		return (ArrayList<GamePlayer>) players.values();
	}

	protected abstract void gamePlayerOutOfTime(GamePlayer turn);

	public static NamespacedKey getGameIdKey() {
		return gameIdKey;
	}

	public static void setGameIdKey(NamespacedKey gameIdKey) {
		Game.gameIdKey = gameIdKey;
	}

	protected void createMapManager() {
		mapManager = new MapManager(mapStructure, gameId, this);
	}
	
	public void delete() {
		// TODO: add delete method
	}
	public abstract ItemStack getBoardItem();
}
