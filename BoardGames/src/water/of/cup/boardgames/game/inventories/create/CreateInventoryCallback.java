package water.of.cup.boardgames.game.inventories.create;

import java.util.HashMap;

public interface CreateInventoryCallback {

    void onCreateGame(HashMap<String, Object> gameData);

}
