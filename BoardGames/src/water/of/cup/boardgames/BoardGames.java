package water.of.cup.boardgames;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;
import water.of.cup.boardgames.commands.bgCommandsTabCompleter;
import water.of.cup.boardgames.config.ConfigUtil;
import water.of.cup.boardgames.config.GameConfigLoader;
import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.GameManager;
import water.of.cup.boardgames.game.games.blackjack.Blackjack;
import water.of.cup.boardgames.game.games.hilo.HiLo;
import water.of.cup.boardgames.game.games.mines.Mines;
import water.of.cup.boardgames.game.games.plinko.Plinko;
import water.of.cup.boardgames.game.games.poker.Poker;
import water.of.cup.boardgames.game.games.roulette.Roulette;
import water.of.cup.boardgames.game.games.slots.slotsgames.LibertyBell;
import water.of.cup.boardgames.game.games.slots.slotsgames.MoneyHoney;
import water.of.cup.boardgames.commands.bgCommands;
import water.of.cup.boardgames.game.maps.MapManager;
import water.of.cup.boardgames.game.npcs.GameNPC;
import water.of.cup.boardgames.game.npcs.GameNPCRegistry;
import water.of.cup.boardgames.game.storage.StorageManager;
import water.of.cup.boardgames.image_handling.ImageManager;
import water.of.cup.boardgames.listeners.*;
import water.of.cup.boardgames.metrics.Metrics;
import water.of.cup.boardgames.placeholder.BoardGamesPlaceholder;

public class BoardGames extends JavaPlugin {
	
	private static BoardGames instance;
	private static GameManager gameManager = new GameManager();
	//private HashMap<Player, ChessCreateGameInventory> createGameManager = new HashMap<>();
	private File configFile;
	private FileConfiguration config;
	private static Economy economy = null;
	//private DataSource dataStore;

	private StorageManager storageManager;
	private static ImageManager IMAGE_MANAGER;

	private static boolean hasCitizens;

	@SuppressWarnings("unchecked") // for register games
	@Override
	public void onEnable() {
		instance = this;

		//Init imageManager
		IMAGE_MANAGER = new ImageManager();

		// Config loads database settings
		loadConfig();

		// Load database if enabled
		if(ConfigUtil.DB_ENABLED.toBoolean())
			loadStorage();
		
		Game.setGameIdKey(new NamespacedKey(this, "game_id_key"));
		Game.setGameNameKey(new NamespacedKey(this, "game_name_key"));
		MapManager.setMapValsKey(new NamespacedKey(this, "map_vals_key"));
		MapManager.setRotationKey(new NamespacedKey(this, "rotation_key"));


//		Bukkit.getLogger().info("[BoardGames] Successfully loaded piece images");

		// Debug:
//		new TicTacToeInventory(null).build(null, null);

		// Debug:
//		getCommand("debug").setExecutor(new DebugCommand());
		//Bukkit.getLogger().info("[ChessBoards] Successfully loaded piece images");

		setupCitizens();

		gameManager.registerGames(LibertyBell.class, Mines.class, Poker.class, MoneyHoney.class, HiLo.class, Plinko.class, Blackjack.class, Roulette.class);

		// Register extension board games
//		loadExtensionManager();

		getCommand("bg").setExecutor(new bgCommands());
		getCommand("bg").setTabCompleter(new bgCommandsTabCompleter());
//		getCommand("chessboards").setTabCompleter(new ChessBoardCommandsTabCompleter());

//		registerListeners(new BoardInteract(), new BlockPlace(), new InventoryClose(), new InventoryClick(), new HangingBreakByEntity(), new EntityDamageByEntity(), new HangingBreak(), new ChessPlayerJoin(), new BlockBreak());
		
//		if(config.getBoolean("settings.chessboard.recipe.enabled"))
//			addGameRecipes();
		registerListeners(new PlayerQuit(), new ChunkLoad(), new BlockPlace(), new BoardInteract(), new BlockBreak(), new PlayerJoin(), new PlayerItemCraft());

		// Load recipes after config and games are initialized
		GameConfigLoader.loadGameConfig();

		if(ConfigUtil.WAGERS_ENABLED.toBoolean()) {
			boolean hasEconomy = setupEconomy();
			if (!hasEconomy) {
				Bukkit.getLogger().info("[BoardGames] Server must have Vault in order to place wagers on games.");
			}
		}


		setupPlaceholders();

		// Load in old chess games
//		ChessBoardsUtil.loadGames();

		//GameManager.loadGames();

		// Add bStats
		Metrics metrics = new Metrics(this, 11839);
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

		// Clear image cache
		IMAGE_MANAGER.clearImages();

		// Unload games
		GameConfigLoader.unloadConfig();

		// Disconnect from database
		if(storageManager != null)
			storageManager.closeConnection();

		if(hasCitizens() && GameNPC.REGISTRY != null)
			GameNPC.REGISTRY.deregisterAll();

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

    // TODO: remove old citizens
    private void setupCitizens() {
		if(getServer().getPluginManager().getPlugin("Citizens") == null || !getServer().getPluginManager().getPlugin("Citizens").isEnabled()) {
			getLogger().log(Level.SEVERE, "[BoardGames] Citizens 2.0 not found or not enabled, NPCS disabled.");
			hasCitizens = false;
			return;
		}

		hasCitizens = true;
	}

    private void setupPlaceholders() {
		if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
			new BoardGamesPlaceholder(this).register();
		}
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
		boardgames.recipe.GAMENAME
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

		File customImages = new File(getDataFolder(), "custom_images");
		if (!customImages.exists()) {
			customImages.mkdir();
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

	public static boolean hasCitizens() {
		return hasCitizens;
	}

	public static ImageManager getImageManager() {
		return IMAGE_MANAGER;
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
