package water.of.cup.boardgames.game.games.chess;

import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.storage.BoardGamesStorageType;
import water.of.cup.boardgames.game.storage.GameStorage;
import water.of.cup.boardgames.game.storage.StorageType;

public class ChessStorage extends GameStorage {

    public ChessStorage(Game game) {
        super(game);
    }

    @Override
    protected String getTableName() {
        return "chess";
    }

    @Override
    protected StorageType[] getGameStores() {
        return new StorageType[] {
                BoardGamesStorageType.WINS,
                BoardGamesStorageType.LOSSES,
                BoardGamesStorageType.TIES,
                BoardGamesStorageType.Rating,
                BoardGamesStorageType.RatingDeviation,
                BoardGamesStorageType.RatingVolatility,
        };
    }
}
