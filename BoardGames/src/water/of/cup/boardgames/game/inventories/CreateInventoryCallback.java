package water.of.cup.boardgames.game.inventories;

import java.util.HashMap;

public interface CreateInventoryCallback {

    void onCreateGame(HashMap<String, Object> gameData);

}
