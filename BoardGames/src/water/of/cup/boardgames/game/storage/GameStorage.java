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

    public void updateData(Player player, StorageType storageType, Object value) {
        // check if tracking is enabled for this storageType
        // check if gamestores includes storagetype
        // update getTableName with storageType and value
        // think about increment/decrement int values, might want to add to enum
        if(!game.hasGameStorage()) return;

    }

}
