package water.of.cup.boardgames.game.games.connectfour;

import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.storage.BoardGamesStorageType;
import water.of.cup.boardgames.game.storage.GameStorage;
import water.of.cup.boardgames.game.storage.StorageType;

public class ConnectFourStorage extends GameStorage {
    public ConnectFourStorage(Game game) {
        super(game);
    }

    @Override
    protected String getTableName() {
        return "connectfour";
    }

    @Override
    protected StorageType[] getGameStores() {
        return new StorageType[] {
                BoardGamesStorageType.WINS,
                BoardGamesStorageType.LOSSES,
                BoardGamesStorageType.TIES,
                BoardGamesStorageType.CROSS_WINS,
        };
    }
}
