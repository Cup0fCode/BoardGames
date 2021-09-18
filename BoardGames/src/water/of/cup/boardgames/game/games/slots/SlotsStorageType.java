package water.of.cup.boardgames.game.games.slots;

import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.storage.GameStorage;
import water.of.cup.boardgames.game.storage.StorageType;
import water.of.cup.boardgames.game.storage.CasinoGamesStorageType;

public class SlotsStorageType extends GameStorage {

    public SlotsStorageType(Game game) {
        super(game);
    }

    @Override
    protected String getTableName() {
        return "slots";
    }

    @Override
    protected StorageType[] getGameStores() {
        return new StorageType[] {
                CasinoGamesStorageType.MONEY_WON,
                CasinoGamesStorageType.MONEY_LOST
        };
    }

}
