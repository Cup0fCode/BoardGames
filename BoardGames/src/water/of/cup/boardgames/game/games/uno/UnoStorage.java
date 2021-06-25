package water.of.cup.boardgames.game.games.uno;

import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.storage.GameStorage;
import water.of.cup.boardgames.game.storage.StorageType;

public class UnoStorage extends GameStorage {
    public UnoStorage(Game game) {
        super(game);
    }

    @Override
    protected String getTableName() {
        return "uno";
    }

    @Override
    protected StorageType[] getGameStores() {
        return new StorageType[] {
                StorageType.WINS,
                StorageType.LOSSES,
        };
    }
}
