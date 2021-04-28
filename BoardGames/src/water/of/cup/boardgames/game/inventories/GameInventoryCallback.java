package water.of.cup.boardgames.game.inventories;

import java.util.HashMap;

public interface GameInventoryCallback {

    void onGameCreated(HashMap<String, Object> gameData);

    void onCancel(HashMap<String, Object> gameData);

}
