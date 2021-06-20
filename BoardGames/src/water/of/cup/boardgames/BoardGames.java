package water.of.cup.boardgames;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;
import water.of.cup.boardgames.commands.DebugCommand;
import water.of.cup.boardgames.commands.bgCommandsTabCompleter;
import water.of.cup.boardgames.config.ConfigUtil;
import water.of.cup.boardgames.config.GameConfigLoader;
import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.GameManager;
import water.of.cup.boardgames.game.games.uno.Uno;
import water.of.cup.boardgames.commands.bgCommands;
import water.of.cup.boardgames.game.games.battleship.Battleship;
import water.of.cup.boardgames.game.games.checkers.Checkers;
import water.of.cup.boardgames.game.games.connectfour.ConnectFour;
import water.of.cup.boardgames.game.games.conways_game_of_life.ConwaysGameOfLife;
import water.of.cup.boardgames.game.games.minesweaper.MineSweeper;
import water.of.cup.boardgames.game.games.tictactoe.TicTacToe;
import water.of.cup.boardgames.game.maps.MapManager;
import water.of.cup.boardgames.game.storage.StorageManager;
import water.of.cup.boardgames.listeners.BlockBreak;
import water.of.cup.boardgames.listeners.BlockPlace;
import water.of.cup.boardgames.listeners.BoardInteract;
import water.of.cup.boardgames.listeners.PlayerJoin;
import water.of.cup.boardgames.metrics.Metrics;

public class BoardGames extends JavaPlugin {
	
	private static BoardGames instance;
	private static GameManager gameManager = new GameManager();
	//private HashMap<Player, ChessCreateGameInventory> createGameManager = new HashMap<>();
	private File configFile;
	private FileConfiguration config;
	private static Economy economy = null;
	//private DataSource dataStore;

	private StorageManager storageManager;

	@SuppressWarnings("unchecked") // for register games
	@Override
	public void onEnable() {
		instance = this;

		// Config loads database settings
		loadConfig();

		// Load database if enabled
		if(ConfigUtil.DB_ENABLED.toBoolean())
			loadStorage();
		
		Game.setGameIdKey(new NamespacedKey(this, "game_id_key"));
		Game.setGameNameKey(new NamespacedKey(this, "game_name_key"));
		MapManager.setMapValsKey(new NamespacedKey(this, "map_vals_key"));


//		Bukkit.getLogger().info("[BoardGames] Successfully loaded piece images");

		// Debug:
//		new TicTacToeInventory(null).build(null, null);

		// Debug:
		getCommand("debug").setExecutor(new DebugCommand());
		//Bukkit.getLogger().info("[ChessBoards] Successfully loaded piece images");
		
		gameManager.registerGames(ConwaysGameOfLife.class, TicTacToe.class, Battleship.class, ConnectFour.class, Checkers.class, MineSweeper.class, Uno.class);
		
		getCommand("bg").setExecutor(new bgCommands());
		getCommand("bg").setTabCompleter(new bgCommandsTabCompleter());
//		getCommand("chessboards").setTabCompleter(new ChessBoardCommandsTabCompleter());

//		registerListeners(new BoardInteract(), new BlockPlace(), new InventoryClose(), new InventoryClick(), new HangingBreakByEntity(), new EntityDamageByEntity(), new HangingBreak(), new ChessPlayerJoin(), new BlockBreak());
		registerListeners(new BlockPlace(), new BoardInteract(), new BlockBreak(), new PlayerJoin());

		// Load recipes after config and games are initialized
		GameConfigLoader.loadRecipes();
		GameConfigLoader.loadGameSounds();
		GameConfigLoader.loadCustomConfigValues();

		if(ConfigUtil.WAGERS_ENABLED.toBoolean()) {
			boolean hasEconomy = setupEconomy();
			if (!hasEconomy) {
				Bukkit.getLogger().info("Server must have Vault in order to place wagers on games.");
			}
		}

		//GameManager.loadGames();

		// Add bStats
		Metrics metrics = new Metrics(this, 10153);
		Bukkit.getLogger().info("[BoardGames] bStats: " + metrics.isEnabled() + " plugin ver: " + getDescription().getVersion());

		metrics.addCustomChart(new Metrics.SimplePie("plugin_version", () -> getDescription().getVersion()));
	}

	@Override
	public void onDisable() {
		boolean databaseEnabled = instance.getConfig().getBoolean("settings.database.enabled");
//		if(databaseEnabled && this.dataStore != null)
//			this.dataStore.closeConnection();

		// TODO: Fix/Add save games
//		gameManager.saveGames();

		// Disconnect from database
		if(storageManager != null)
			storageManager.closeConnection();

		/* Disable all current async tasks */
		Bukkit.getScheduler().cancelTasks(this);
	}

	private void registerListeners(Listener... listeners) {
		Arrays.stream(listeners).forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));
	}

	public static BoardGames getInstance() {
		return instance;
	}
	
	private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }
	
	public void addGameRecipes() {
//		ItemStack chessboard = ChessUtils.getChessBoardItem();
//
//		NamespacedKey key = new NamespacedKey(this, "chess_board");
//		ShapedRecipe recipe = new ShapedRecipe(key, chessboard);
//
//		ArrayList<String> shapeArr = (ArrayList<String>) config.get("settings.chessboard.recipe.shape");
//		recipe.shape(shapeArr.toArray(new String[shapeArr.size()]));
//
//		for(String ingredientKey : config.getConfigurationSection("settings.chessboard.recipe.ingredients").getKeys(false)){
//			recipe.setIngredient(ingredientKey.charAt(0), Material.valueOf((String) config.get("settings.chessboard.recipe.ingredients." + ingredientKey)));
//		}
//
//		Bukkit.addRecipe(recipe);
	}

	public void loadConfig() {
		if (!getDataFolder().exists()) {
			getDataFolder().mkdir();
		}

		configFile = new File(getDataFolder(), "config.yml");
		if (!configFile.exists()) {
			try {
				configFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		config = YamlConfiguration.loadConfiguration(configFile);

		HashMap<String, Object> defaultConfig = new HashMap<>();

		/* Perms:
		boardgames.interact - interact with board
		boardgames.place - place board
		boardgames.destroy - break board
		boardgames.command - use board command
			- boardgames.command.games
			- boardgames.command.board
			- boardgames.command.stats
			- boardgames.command.leaderboard
			- boardgames.command.reload
		 */

		// Load in defaults from ConfigUtil
		for(ConfigUtil configUtil : ConfigUtil.values()) {
			defaultConfig.put(configUtil.getPath(), configUtil.getDefaultValue());
		}

		// settings.games.GAMENAME.database
		// settings.games.GAMENAME.recipe
		// settings.games.GAMENAME.sounds

		for (String key : defaultConfig.keySet()) {
			if(!config.contains(key)) {
				config.set(key, defaultConfig.get(key));
			}
		}

		File savedGamesDir = new File(getDataFolder(), "saved_games");
		if (!savedGamesDir.exists()) {
			savedGamesDir.mkdir();
		}

		this.saveConfig();
	}

	@Override
	public void saveConfig() {
		try {
			config.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addToConfig(HashMap<String, Object> defaultConfig) {
		for (String key : defaultConfig.keySet()) {
			if(!config.contains(key)) {
				config.set(key, defaultConfig.get(key));
			}
		}

		saveConfig();
	}

	@Override
	public FileConfiguration getConfig() {
		return config;
	}

	public GameManager getGameManager() {
		return gameManager;
	}
	
	public Economy getEconomy() {
        return economy;
    }

    public void loadStorage() {
		storageManager = new StorageManager();
	}

	public StorageManager getStorageManager() {
		return storageManager;
	}

	public boolean hasStorage() {
		return this.storageManager != null && ConfigUtil.DB_ENABLED.toBoolean();
	}
	
//	public DataSource getDataStore() {
//		return dataStore;
//	}

//	public ChessCreateGameInventory getCreateGameInventory(Player player) {
//		return this.createGameManager.get(player);
//	}
//
//	public void removeCreateGamePlayer(Player player) {
//		this.createGameManager.remove(player);
//	}
//
//	public void addCreateGamePlayer(Player player, ChessCreateGameInventory inventory) {
//		this.createGameManager.put(player, inventory);
//	}
//
//	public boolean playerHasCreateGame(Player player) {
//		return this.createGameManager.containsKey(player);
//	}
//
//	public Set<Player> getCreateGamePlayers() {
//		return this.createGameManager.keySet();
//	}
}
