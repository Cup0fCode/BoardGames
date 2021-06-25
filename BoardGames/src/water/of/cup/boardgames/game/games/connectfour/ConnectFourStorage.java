package water.of.cup.boardgames.game.games.connectfour;

import water.of.cup.boardgames.game.Game;
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
                StorageType.WINS,
                StorageType.LOSSES,
                StorageType.TIES,
                StorageType.CROSS_WINS,
        };
    }
}
