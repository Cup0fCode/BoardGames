package water.of.cup.boardgames.game.games.sudoku;

import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.storage.GameStorage;
import water.of.cup.boardgames.game.storage.StorageType;

public class SudokuStorage extends GameStorage {
    public SudokuStorage(Game game) {
        super(game);
    }

    @Override
    protected String getTableName() {
        return "sudoku";
    }

    @Override
    protected StorageType[] getGameStores() {
        return new StorageType[] {
                StorageType.BEST_TIME,
                StorageType.WINS,
                StorageType.LOSSES,
        };
    }
}
