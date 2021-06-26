package water.of.cup.boardgames.game.games.minesweaper;

import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.storage.GameStorage;
import water.of.cup.boardgames.game.storage.StorageType;

public class MineSweeperStorage extends GameStorage {
    public MineSweeperStorage(Game game) {
        super(game);
    }

    @Override
    protected String getTableName() {
        return "minesweeper";
    }

    @Override
    protected StorageType[] getGameStores() {
        return new StorageType[] {
                StorageType.WINS,
                StorageType.BEST_TIME,
        };
    }
}
