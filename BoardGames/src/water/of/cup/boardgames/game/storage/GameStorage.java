package water.of.cup.boardgames.game.storage;

import org.bukkit.entity.Player;
import water.of.cup.boardgames.BoardGames;
import water.of.cup.boardgames.game.Game;

import java.util.ArrayList;

public abstract class GameStorage {

    private final BoardGames instance = BoardGames.getInstance();
    private final Game game;

    protected abstract String getTableName();
    protected abstract StorageType[] getGameStores();

    public GameStorage(Game game) {
        this.game = game;

        StorageManager storageManager = instance.getStorageManager();
        if(storageManager != null) {
            storageManager.addGameStorage(this);
        }
    }

    // Adds increment value to storageType
    public void updateData(Player player, StorageType storageType, Object increment) {
        if(!canExecute(storageType)) return;

        instance.getStorageManager().updateColumn(player, getTableName(), storageType, increment, false);
    }

    // Untested
    public void setData(Player player, StorageType storageType, Object newValue) {
        if(!canExecute(storageType)) return;

        instance.getStorageManager().updateColumn(player, getTableName(), storageType, newValue, true);
    }

    private boolean canExecute(StorageType storageType) {
        // Checks to make sure database is initialized and enabled
        if(!game.hasGameStorage()) return false;

        // Checks to make sure the game storage has the storage type
        if(!hasStorageType(storageType)) return false;

        // TODO: Check if tracking is enabled for this storageType in config

        return true;
    }

    private boolean hasStorageType(StorageType storageType) {
        for(StorageType type : getGameStores()) {
            if (type == storageType) {
                return true;
            }
        }

        return false;
    }

}
