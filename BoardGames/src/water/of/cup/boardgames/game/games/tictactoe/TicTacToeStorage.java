package water.of.cup.boardgames.game.games.tictactoe;

import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.storage.GameStorage;
import water.of.cup.boardgames.game.storage.StorageType;

public class TicTacToeStorage extends GameStorage {

    public TicTacToeStorage(Game game) {
        super(game);
    }

    @Override
    protected String getTableName() {
        return "tictactoe";
    }

    @Override
    protected StorageType[] getGameStores() {
        return new StorageType[] {
                StorageType.WINS,
                StorageType.LOSSES,
                StorageType.TIES,
                StorageType.CROSS_WINS,
        };
    }
}
