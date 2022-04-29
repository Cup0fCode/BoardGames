package water.of.cup.boardgames.game.games.checkers;

import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.storage.BoardGamesStorageType;
import water.of.cup.boardgames.game.storage.GameStorage;
import water.of.cup.boardgames.game.storage.StorageType;

public class CheckersStorage extends GameStorage {
    public CheckersStorage(Game game) {
        super(game);
    }

    @Override
    protected String getTableName() {
        return "checkers";
    }

    @Override
    protected StorageType[] getGameStores() {
        return new StorageType[] {
                BoardGamesStorageType.WINS,
                BoardGamesStorageType.LOSSES,
                BoardGamesStorageType.TIES,
        };
    }
}
