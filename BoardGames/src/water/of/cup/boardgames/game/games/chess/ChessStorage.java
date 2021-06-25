package water.of.cup.boardgames.game.games.chess;

import water.of.cup.boardgames.game.Game;
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
                StorageType.WINS,
                StorageType.LOSSES,
                StorageType.TIES,
        };
    }
}
