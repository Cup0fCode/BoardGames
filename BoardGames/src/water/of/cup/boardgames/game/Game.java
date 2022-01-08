package water.of.cup.boardgames.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.UUID;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;

import water.of.cup.boardgames.BoardGames;
import water.of.cup.boardgames.config.ConfigUtil;
import water.of.cup.boardgames.config.GameRecipe;
import water.of.cup.boardgames.config.GameSound;
import water.of.cup.boardgames.game.glicko2.Rating;
import water.of.cup.boardgames.game.glicko2.RatingCalculator;
import water.of.cup.boardgames.game.glicko2.RatingPeriodResults;
import water.of.cup.boardgames.game.inventories.GameInventory;
import water.of.cup.boardgames.game.maps.GameMap;
import water.of.cup.boardgames.game.maps.MapData;
import water.of.cup.boardgames.game.maps.MapManager;
import water.of.cup.boardgames.game.maps.Screen;
import water.of.cup.boardgames.game.npcs.GameNPC;
import water.of.cup.boardgames.game.storage.BoardGamesStorageType;
import water.of.cup.boardgames.game.storage.GameStorage;
import water.of.cup.boardgames.game.storage.StorageType;
import water.of.cup.boardgames.game.teams.TeamManager;
import water.of.cup.boardgames.game.wagers.WagerManager;

import javax.annotation.Nullable;

public abstract class Game {
	private static NamespacedKey gameIdKey;
	private static NamespacedKey gameNameKey;

	protected int gameId;
	protected String gameName;
	protected MapManager mapManager;
	protected ArrayList<Screen> screens;
//	protected HashMap<Player, GamePlayer> players;
//	private int turn;
	private boolean ingame;
	protected GameImage gameImage;
	protected ArrayList<Button> buttons;
	protected WagerManager wagerManager;
	protected Clock clock;
	protected GameInventory gameInventory;
	protected TeamManager teamManager;
	protected GameStorage gameStorage;
	private Location placedMapLoc;
	private final GameNPC gameNPC;

	protected ArrayList<GameMap> gameMaps; // game maps for the game
	// public HashMap<Integer, Integer> mapValMapIds; // <mapval, mapid>
	protected int[][] mapStructure; // the structure of mapVals, 0 for missing map
	protected int placedMapVal; // the value of the map at the placed board location

	private HashMap<String, Object> gameData;

	private final GameConfig gameConfig;

	protected abstract void setMapInformation(int rotation); // set mapStructure and placedMapVal

	private void setGameId() {
		gameId = BoardGames.getInstance().getGameManager().nextGameId();
	}

	protected void startGame() {
		clock = getClock();
		if (clock != null)
			clock.runTaskTimer(BoardGames.getInstance(), 1, 1);
		renderInitial();
	}

	protected abstract void setGameName(); // set gameName

	protected abstract void setBoardImage(); // set board Image

	protected abstract void clockOutOfTime();

	protected abstract Clock getClock();

	protected abstract GameInventory getGameInventory();

	protected abstract GameStorage getGameStorage();

	public abstract ArrayList<String> getTeamNames();

	protected abstract GameConfig getGameConfig();

	public Game(int rotation) {
		screens = new ArrayList<Screen>();
		setMapInformation(rotation);
		assert placedMapVal != 0;
		createMapManager(rotation);
		setGameId();
		assert gameId != 0;
		setGameName();
		assert gameName != null;
		setBoardImage();
		// assert boardImage != null;
		wagerManager = new WagerManager();
		// turn = 0;
		buttons = new ArrayList<Button>();
		teamManager = new TeamManager(this);

		// mapValMapIds = new HashMap<Integer, Integer>();
		gameMaps = new ArrayList<GameMap>();

		gameInventory = getGameInventory();
		gameStorage = getGameStorage();
		gameConfig = getGameConfig();
		gameNPC = BoardGames.hasCitizens() ? getGameNPC() : null;

		gameData = new HashMap<>();
	}

	abstract public void click(Player player, double[] loc, ItemStack map);

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
				}
			}
		}

		// check that bottom blocks are not empty
		for (int x = minX; x <= maxX; x++)
			for (int z = minZ; z <= maxZ; z++) {
				
				// don't check positions where no map is placed
				boolean hasBoard = false;
				for (MapData mapData : mapManager.getMapDataAtLocationOnRotatedBoard(x - t1X, z - t1Z, 0)) 
					if (mapData.getMapVal() > 0) {
						hasBoard = true;
						break;
					}
				if (!hasBoard)
					continue;
				
				if (loc.getWorld().getBlockAt(loc.getBlockX() + x, loc.getBlockY() - 1, loc.getBlockZ() + z).isEmpty())
					return false;
				
			}

		return true;
	}

	public void placeBoard(Location loc, int rotation) {
		placeBoard(loc, rotation, placedMapVal);
	}

	public void placeBoard(Location loc, int rotation, int placedMapVal) {
		// can place board should always be called first

		World world = loc.getWorld();

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
		
		int[] npcLoc = {0,0,0};

////		// remove frames:
//		for (int x = minX; x <= maxX; x++)
//			for (int y = minY; y <= maxY; y++)
//				for (int z = minZ; z <= maxZ; z++) {
//					Location barrierLoc = new Location(loc.getWorld(), loc.getBlockX() + x, loc.getBlockY() + y,
//							loc.getBlockZ() + z);
//					//for (Entity e : barrierLoc.get)
//				}

		// spawn ItemFrames
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				for (int z = minZ; z <= maxZ; z++) {
//					Bukkit.getLogger().info("placed");
					for (MapData mapData : mapManager.getMapDataAtLocationOnRotatedBoard(x - t1X, z - t1Z, y - t1Y)) {

						int mapVal = mapData.getMapVal();
						
						if (mapVal == getPlacedMapVal())
							npcLoc = new int[]{x,y,z};
						
						// skip maps with value 0
						if (mapVal <= 0)
							continue;

						// create the map
						GameMap map = new GameMap(this, mapVal, new ItemStack(Material.FILLED_MAP, 1));

						// set the mapView
						MapView mapView = Bukkit.createMap(world);
						MapMeta mapMeta = (MapMeta) map.getItemMeta();
						mapMeta.setMapView(mapView);
						map.setItemMeta(mapMeta);
						// mapValMapIds.put(mapVal, mapView.getId());
						gameMaps.add(map);

						// spawn itemFrame
						Location frameLoc = new Location(loc.getWorld(), loc.getBlockX() + x, loc.getBlockY() + y,
								loc.getBlockZ() + z);

						// set world preconditions so the item frame can spawn
						frameLoc.getBlock().setType(Material.AIR);
						Block placedOn = frameLoc.getBlock().getRelative(mapData.getBlockFace().getOppositeFace());
						boolean removeBlock = false;
						if (placedOn.getType() == Material.AIR) {
							placedOn.setType(Material.BARRIER);
							removeBlock = true;
						}

						ItemFrame frame = world.spawn(frameLoc, ItemFrame.class);
						frame.setItem(map);
						frame.setFacingDirection(mapData.getBlockFace(), true);
						frame.setInvulnerable(true);
						frame.setFixed(true);
						frame.setVisible(true);
						if (removeBlock)
							placedOn.setType(Material.AIR);

						frameLoc.getBlock().setType(Material.BARRIER);
					}

				}
			}
		}

		//create npc
		if (hasGameNPC())
			gameNPC.setMapValLoc(new Location(loc.getWorld(), loc.getBlockX() + npcLoc[0], loc.getBlockY() + npcLoc[1],
					loc.getBlockZ() + npcLoc[2]), rotation);

		placedMapLoc = loc.clone();

		// Debug
		if (!hasGameInventory()) {
			startGame();
		} else {
			renderInitial();
		}
	}

	public void renderInitial() {
		mapManager.resetRenderers();
	}

	public void endGame(GamePlayer winner) {
		ingame = false;
		wagerManager.completeWagers(winner);
		sendGameWinMoney(winner);
		sendEndGameMessage(winner);
		updateGameStorage(winner);
		clearGamePlayers();

		if (clock != null)
			clock.cancel();

		if(hasGameNPC())
			gameNPC.removeNPC();

		// Ensure map gets cleared
		// renderInitial();
	}

	public void setInGame() {
		ingame = true;
	}
	
	protected void setInGame(boolean inGame) {
		this.ingame = inGame;
	}

	public boolean isIngame() {
		return ingame;
	}

	protected Button getClickedButton(GamePlayer gamePlayer, int[] loc) { // returns null if no button is clicked
		for (Button button : buttons) {
			if (button.clicked(gamePlayer, loc))
				return button;
		}
		return null;
	}

	public boolean hasPlayer(Player player) {
		return teamManager.getGamePlayer(player) != null;
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

	public GamePlayer getGamePlayer(Player player) {
		return teamManager.getGamePlayer(player);
	}

	public ArrayList<GamePlayer> getGamePlayers() {
		return teamManager.getGamePlayers();
	}

	public GamePlayer addPlayer(Player player, String team) {
		if (teamManager.getGamePlayer(player) != null) {
			return teamManager.getGamePlayer(player);
		}

		GamePlayer newPlayer = new GamePlayer(player);
		if (team == null) {
			teamManager.addTeam(newPlayer);
		} else {
			teamManager.addTeam(newPlayer, team);
		}

		return newPlayer;
	}

	public GamePlayer addPlayer(Player player) {
		return addPlayer(player, null);
	}

	public void removePlayer(Player player) {
		teamManager.removeTeamByPlayer(player);
	}

	public void clearGamePlayers() {
		teamManager.resetTeams();
	}

	public GamePlayer getTurn() {
		return teamManager.getTurnPlayer();
	}

	protected abstract void gamePlayerOutOfTime(GamePlayer turn);

	public static NamespacedKey getGameIdKey() {
		return gameIdKey;
	}

	public static void setGameIdKey(NamespacedKey gameIdKey) {
		Game.gameIdKey = gameIdKey;
	}

	protected void createMapManager(int rotation) {
		mapManager = new MapManager(mapStructure, rotation, this);
	}

	public boolean destroy(ItemFrame gameFrame) {
		if (!GameMap.isGameMap(gameFrame.getItem()))
			return false;

		// destroy board
		GameMap gameMap = new GameMap(gameFrame.getItem());
		int mapVal = gameMap.getMapVal();
		destroyBoard(gameFrame.getLocation(), mapVal);

		delete();
		return true;
	}

	private void destroyBoard(Location loc, int startMapVal) {
		int[] centerLoc = mapManager.getMapValsLocationOnRotatedBoard(startMapVal);
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

		// destroy blocks
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				for (int z = minZ; z <= maxZ; z++) {
					Block block = loc.getWorld().getBlockAt(loc.getBlockX() + x, loc.getBlockY() + y,
							loc.getBlockZ() + z);
					for (Entity entity : loc.getWorld().getNearbyEntities(block.getBoundingBox())) {
						if (entity instanceof ItemFrame && GameMap.isGameMap(((ItemFrame) entity).getItem())) {
							ItemFrame frame = (ItemFrame) entity;
							frame.remove();
						}
					}
					block.setType(Material.AIR);
				}
			}
		}
	}

	public void delete() {
		// TODO: game deletion code
	}

//	public int getMapValsId(int mapVal) {
//		if (mapValMapIds.containsKey(mapVal))
//			return mapValMapIds.get(mapVal);
//		return 0;
//				
//	}
	public String getGameName() {
		return gameName;
	}

	public ArrayList<GameMap> getGameMaps() {
		return gameMaps;
	}

	public GameMap getGameMapByMapVal(int mapVal) {
		for (GameMap map : gameMaps) {
			if (map.getMapVal() == mapVal)
				return map;
		}
		return null;
	}

	public GameImage getGameImage() {
		return gameImage;
	}

	public abstract ItemStack getBoardItem();

	public ArrayList<Button> getButtons() {
		return buttons;
	}

	public int getRotation() {
		return mapManager.getRotation();
	}

	public String getName() {
		return gameName + "";
	}

	public String getAltName() {
		String configLoc = "settings.games." + getName() + ".altName";
		if(BoardGames.getInstance().getConfig().getString(configLoc) == null)
			return getName();

		return BoardGames.getInstance().getConfig().getString(configLoc);
	}

	public int getMaxWager() {
		String configLoc = "settings.games." + getName() + ".maxWager";
		Object maxWager = BoardGames.getInstance().getConfig().get(configLoc);

		if(maxWager == null || !MathUtils.isNumeric(maxWager + ""))
			return Integer.MAX_VALUE;

		int maxWagerNum = Integer.parseInt(maxWager + "");

		if(maxWagerNum < 0) return Integer.MAX_VALUE;

		return maxWagerNum;
	}

	public static NamespacedKey getGameNameKey() {
		return gameNameKey;
	}

	public static void setGameNameKey(NamespacedKey gameNameKey) {
		Game.gameNameKey = gameNameKey;
	}

	public ArrayList<Screen> getScreens() {
		return screens;
	}

	public WagerManager getWagerManager() {
		return wagerManager;
	}

	public void displayGameInventory(Player player) {
		if (gameInventory != null) {
			gameInventory.build(player);
		}
	}

	public boolean hasGameInventory() {
		return gameInventory != null;
	}

	public int getPlacedMapVal() {
		return placedMapVal;
	}

	public void replace(Location location, int rotation, int mapVal) { // location must be same as placedMapVal
		this.destroyBoard(location, mapVal);
		final Location loc = location;
		final int frotation = rotation;
		final int fmapVal = mapVal;
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(BoardGames.getInstance(), new Runnable() {
			@Override
			public void run() {
				try {
					placeBoard(loc, frotation, fmapVal);
				}
				catch (java.lang.IllegalArgumentException e) {
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(BoardGames.getInstance(), this, 20);
				}
			}
		}, 20);
	}

	public boolean hasGameStorage() {
		return gameStorage != null && BoardGames.getInstance().getStorageManager() != null;
	}

	public void setGameData(HashMap<String, Object> gameData) {
		this.gameData = new HashMap<>(gameData);
	}

	public boolean hasGameData(String key) {
		return this.gameData.containsKey(key);
	}

	public Object getGameData(String key) {
		if (this.gameData.get(key) instanceof String)
			return ChatColor.stripColor((String) this.gameData.get(key));

		return this.gameData.get(key);
	}

	public GameStorage getGameStore() {
		return this.gameStorage;
	}

	public boolean hasGameConfig() {
		return gameConfig != null;
	}

	public GameRecipe getGameRecipe() {
		if (!hasGameConfig())
			return null;

		return gameConfig.getGameRecipe();
	}

	public ArrayList<GameSound> getGameSounds() {
		if (!hasGameConfig())
			return null;

		return gameConfig.getGameSounds();
	}

	public HashMap<String, Object> getCustomValues() {
		if (!hasGameConfig())
			return null;

		return gameConfig.getCustomValues();
	}

	public int getGameWinAmount() {
		if (!hasGameConfig())
			return 0;

		return gameConfig.getWinAmount();
	}

	public boolean isEnabled() {
		String configLoc = "settings.games." + getName() + ".enabled";
		if(BoardGames.getInstance().getConfig().getString(configLoc) == null)
			return true;

		return ConfigUtil.getBoolean(configLoc);
	}

	public void sendGameWinMoney(GamePlayer winner) {
		if (winner == null)
			return;
		if (!hasGameConfig())
			return;

		String configLoc = "settings.games." + getName() + ".winAmount";
		int winAmount = BoardGames.getInstance().getConfig().getInt(configLoc);

		if (BoardGames.getInstance().getEconomy() != null && winAmount != 0) {
			BoardGames.getInstance().getEconomy().depositPlayer(winner.getPlayer(), winAmount);
		}
	}

	@Nullable
	public Sound getGameSound(String key) {
		if (gameConfig.getGameSounds() == null)
			return null;

		String configLoc = "settings.games." + getName() + ".sounds";
		if (!ConfigUtil.getBoolean(configLoc + ".enabled"))
			return null;

		String soundName = BoardGames.getInstance().getConfig().getString(configLoc + "." + key);

		try {
			return Sound.valueOf(soundName);
		} catch (IllegalArgumentException e) {
			return null;
		}

	}

	@Nullable
	public Object getConfigValue(String key) {
		if (gameConfig.getCustomValues() == null)
			return null;

		String configLoc = "settings.games." + getName() + ".misc." + key;

		return BoardGames.getInstance().getConfig().get(configLoc);
	}

	protected void playGameSound(String soundKey) {
		Sound sound = getGameSound(soundKey);
		if (sound == null)
			return;

		for (GamePlayer player : teamManager.getGamePlayers()) {
			player.getPlayer().playSound(player.getPlayer().getLocation(), sound, (float) 5.0, (float) 1.0);
		}
	}

	public void exitPlayer(Player player) {
		for (GamePlayer gamePlayer : teamManager.getGamePlayers()) {
			if (gamePlayer.getPlayer().isOnline()) {
				gamePlayer.getPlayer().sendMessage(
						ConfigUtil.CHAT_GAME_PLAYER_LEAVE.buildStringPlayerGame(player.getDisplayName(), getAltName()));
			}
		}

		if (teamManager.getGamePlayers().size() == 1) {
			this.endGame(null);
			return;
		}
		
		if (teamManager.getGamePlayers().size() == 2) {
			if (teamManager.getTurnPlayer().getPlayer() == player)
				teamManager.nextTurn();
			this.endGame(teamManager.getTurnPlayer());
			return;
		}

		teamManager.removeTeamByPlayer(player);

		if (hasGameStorage()) {
			if (gameStorage.canExecute(BoardGamesStorageType.LOSSES)) {
				gameStorage.updateData(player, BoardGamesStorageType.LOSSES, 1);
			}
		}

		if (teamManager.getGamePlayers().size() == 1) {
			this.endGame(teamManager.getGamePlayers().get(0));
			return;
		}
	}

	public void rerender(Player player) {
		mapManager.renderBoard(player);

	}

	private void updateGameStorage(GamePlayer gamePlayerWinner) {
		if (!hasGameStorage())
			return;

		// update ratings
		if (gameStorage.canExecute(BoardGamesStorageType.Rating)) {
			if (teamManager.getGamePlayers().size() == 2) {

				GamePlayer winner = teamManager.getGamePlayers().get(0);
				GamePlayer loser = teamManager.getGamePlayers().get(1);
				if (gamePlayerWinner != winner && gamePlayerWinner != null) {
					loser = winner;
					winner = gamePlayerWinner;
				}

				updateRatings(winner, loser, gamePlayerWinner == null);
			}
		}

		if (gamePlayerWinner == null) {
			if (teamManager.getGamePlayers().size() == 1) {
				gameStorage.updateData(teamManager.getGamePlayers().get(0).getPlayer(), BoardGamesStorageType.LOSSES, 1);
				return;
			}

			for (GamePlayer player : teamManager.getGamePlayers()) {
				gameStorage.updateData(player.getPlayer(), BoardGamesStorageType.TIES, 1);
			}
			return;
		}

		gameStorage.updateData(gamePlayerWinner.getPlayer(), BoardGamesStorageType.WINS, 1);

		if (gameStorage.canExecute(BoardGamesStorageType.BEST_TIME)) {
			LinkedHashMap<StorageType, Object> playerStats = BoardGames.getInstance().getStorageManager()
					.fetchPlayerStats(gamePlayerWinner.getPlayer(), getGameStore(), false);
			Double time = clock.getPlayerTimes().get(gamePlayerWinner);

			double bestTime = 0;
			if(playerStats != null && playerStats.containsKey(BoardGamesStorageType.BEST_TIME)) bestTime = (Double) playerStats.get(BoardGamesStorageType.BEST_TIME);
			if (bestTime <= 0 || bestTime > time)
				gameStorage.setData(gamePlayerWinner.getPlayer(), BoardGamesStorageType.BEST_TIME, time);
		}

		for (GamePlayer player : teamManager.getGamePlayers()) {
			if (player.getPlayer().getName().equals(gamePlayerWinner.getPlayer().getName()))
				continue;

			gameStorage.updateData(player.getPlayer(), BoardGamesStorageType.LOSSES, 1);
		}
	}

	private void sendEndGameMessage(GamePlayer gamePlayerWinner) {
		String message;
		if (gamePlayerWinner == null) {
			if (teamManager.getGamePlayers().size() == 1) {
				message = ConfigUtil.CHAT_GAME_PLAYER_LOSE.buildString(getAltName());
			} else {
				message = ConfigUtil.CHAT_GAME_TIE.buildString(getAltName());
			}
		} else {
			message = ConfigUtil.CHAT_GAME_PLAYER_WIN
					.buildStringPlayerGame(gamePlayerWinner.getPlayer().getDisplayName(), getAltName());
		}

		for (GamePlayer player : teamManager.getGamePlayers()) {
			player.getPlayer().sendMessage(message);
		}
	}

	private void updateRatings(GamePlayer winner, GamePlayer loser, boolean tie) {
		// get winner / loser
		if (!getGameData("ranked").equals(ConfigUtil.GUI_RANKED_OPTION_TEXT.toRawString()))
			return;
		RatingCalculator rc = new RatingCalculator();

		String winnerUUID = winner.getPlayer().getUniqueId().toString();
		String loserUUID = loser.getPlayer().getUniqueId().toString();

		Rating ratingWinner;
		Rating ratingLoser;

		LinkedHashMap<StorageType, Object> winnerStats = BoardGames.getInstance().getStorageManager()
				.fetchPlayerStats(winner.getPlayer(), getGameStore(), false);
		LinkedHashMap<StorageType, Object> loserStats = BoardGames.getInstance().getStorageManager()
				.fetchPlayerStats(loser.getPlayer(), getGameStore(), false);

		boolean isFirstWinner = winnerStats == null || winnerStats.get(BoardGamesStorageType.Rating) == null;
		if (isFirstWinner || (double) winnerStats.get(BoardGamesStorageType.Rating) <= 0.1) {
			ratingWinner = new Rating(winnerUUID, rc);
		} else {
			ratingWinner = new Rating(winnerUUID, rc, (double) winnerStats.get(BoardGamesStorageType.Rating),
					(double) winnerStats.get(BoardGamesStorageType.RatingDeviation),
					(double) winnerStats.get(BoardGamesStorageType.RatingVolatility));
		}

		boolean isFirstLoser = loserStats == null || loserStats.get(BoardGamesStorageType.Rating) == null;
		if (isFirstLoser || (double) loserStats.get(BoardGamesStorageType.Rating) <= 0.1) {
			ratingLoser = new Rating(loserUUID, rc);
		} else {
			ratingLoser = new Rating(loserUUID, rc, (double) loserStats.get(BoardGamesStorageType.Rating),
					(double) loserStats.get(BoardGamesStorageType.RatingDeviation),
					(double) loserStats.get(BoardGamesStorageType.RatingVolatility));
		}

		water.of.cup.boardgames.game.glicko2.RatingPeriodResults rpr = new RatingPeriodResults();

		if (tie) {
			// Tied game
			rpr.addDraw(ratingWinner, ratingLoser);
		} else {
			// Won game
			rpr.addResult(ratingWinner, ratingLoser);
		}
		rc.updateRatings(rpr);

		gameStorage.setData(winner.getPlayer(), BoardGamesStorageType.Rating, ratingWinner.getRating());
		gameStorage.setData(winner.getPlayer(), BoardGamesStorageType.RatingDeviation, ratingWinner.getRatingDeviation());
		gameStorage.setData(winner.getPlayer(), BoardGamesStorageType.RatingVolatility, ratingWinner.getVolatility());

		gameStorage.setData(loser.getPlayer(), BoardGamesStorageType.Rating, ratingLoser.getRating());
		gameStorage.setData(loser.getPlayer(), BoardGamesStorageType.RatingDeviation, ratingLoser.getRatingDeviation());
		gameStorage.setData(loser.getPlayer(), BoardGamesStorageType.RatingVolatility, ratingLoser.getVolatility());

	}

	public boolean allowOutsideClicks() {
		return false;
	}

	public GameNPC getGameNPC() {
		return null;
	}

	public boolean hasGameNPC() {
		return gameNPC != null && BoardGames.hasCitizens();
	}

	protected void spawnNPC() {
		if(!hasGameNPC()) return;
		gameNPC.spawnNPC();
	}

	protected void npcLookAt(Player player) {
		if(!hasGameNPC()) return;
		gameNPC.lookAt(player);
	}

	public Location getPlacedMapLoc() {
		return placedMapLoc;
	}
}