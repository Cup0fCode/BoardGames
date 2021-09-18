package water.of.cup.boardgames.game.games.blackjack;

import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.storage.GameStorage;
import water.of.cup.boardgames.game.storage.StorageType;
import water.of.cup.boardgames.game.storage.CasinoGamesStorageType;

public class BlackjackStorage extends GameStorage {

    public BlackjackStorage(Game game) {
        super(game);
    }
    @Override
    protected String getTableName() {
        return "blackjack";
    }

    @Override
    protected StorageType[] getGameStores() {
        return new StorageType[] {
                CasinoGamesStorageType.MONEY_WON,
                CasinoGamesStorageType.MONEY_LOST
        };
    }
}
