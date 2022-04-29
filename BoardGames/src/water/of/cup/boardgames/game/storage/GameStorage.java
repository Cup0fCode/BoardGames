package water.of.cup.boardgames.game.storage;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import water.of.cup.boardgames.BoardGames;
import water.of.cup.boardgames.config.ConfigUtil;
import water.of.cup.boardgames.game.Game;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class GameStorage {

    private final BoardGames instance = BoardGames.getInstance();
    private final Game game;

    protected abstract String getTableName();
    protected abstract StorageType[] getGameStores();

    private final StorageType[] storageTypes;

    public GameStorage(Game game) {
        this.game = game;
        this.storageTypes = getGameStores();

        StorageManager storageManager = instance.getStorageManager();
        if(storageManager != null) {
            storageManager.addGameStorage(this);
        }
    }

    // Adds increment value to storageType (Only use num types ex int,double)
    public void updateData(Player player, StorageType storageType, Object increment) {
        if(!canExecute(storageType)) return;

        instance.getStorageManager().updateColumn(player, getTableName(), storageType, increment, false);
    }

    // Sets value directly
    public void setData(Player player, StorageType storageType, Object newValue) {
        if(!canExecute(storageType)) return;

        instance.getStorageManager().updateColumn(player, getTableName(), storageType, newValue, true);
    }

    public boolean canExecute(StorageType storageType) {
        // Checks to make sure database is initialized and enabled
        if(!game.hasGameStorage()) return false;

        // Checks to make sure the game storage has the storage type
        if(!hasStorageType(storageType)) return false;

        // Check if tracking is enabled for this storageType in config
        String path = "settings.games." + game.getName() + ".database";

        String enabledPath = path + ".enabled";
        if(!ConfigUtil.getBoolean(enabledPath)) return false;

        String storagePath = path + "." + storageType.getKey();
        return ConfigUtil.getBoolean(storagePath);
    }

    private boolean hasStorageType(StorageType storageType) {
        for(StorageType type : getGameStores()) {
            if (type == storageType) {
                return true;
            }
        }

        return false;
    }

    public boolean hasStorageType(String columnName) {
        for(StorageType type : getGameStores()) {
            if (type.getKey().equals(columnName)) {
                return true;
            }
        }

        return false;
    }

    protected void initializeConfig() {
        FileConfiguration config = instance.getConfig();

        HashMap<String, Object> gameConfig = new HashMap<>();
        String path = "settings.games." + game.getName() + ".database";

        for(StorageType type : getGameStores()) {
            gameConfig.put(path + "." + type.getKey(), "true");
        }

        gameConfig.put(path + ".enabled", "true");

        for (String key : gameConfig.keySet()) {
            if(!config.contains(key)) {
                config.set(key, gameConfig.get(key));
            }
        }

        instance.saveConfig();
    }

    public ArrayList<StorageType> getStorageTypes() {
        ArrayList<StorageType> storageTypes = new ArrayList<>();
        for(StorageType storageType : getGameStores()) {
            if(canExecute(storageType)) storageTypes.add(storageType);
        }

        return storageTypes;
    }

}
