package water.of.cup.boardgames.game.inventories;

import de.themoep.inventorygui.DynamicGuiElement;
import de.themoep.inventorygui.GuiStateElement;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import water.of.cup.boardgames.BoardGames;
import water.of.cup.boardgames.game.Game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public abstract class GameInventory {

    private final Game game;
    protected abstract ArrayList<GameOption> getOptions();
    protected abstract int getMaxQueue();
    protected abstract int getMaxGame();

    private final GameCreateInventory gameCreateInventory;
    private final GameWaitPlayersInventory gameWaitPlayersInventory;

    /*
        - Create game screen
            - define options
            - open if game is idle
        - Waiting players screen
        - Join players screen
        - Confirm players screen
        - Spectator screen
     */

    public GameInventory(Game game) {
        this.game = game;

        // Note: Might want to make these inventories take in one GameInventory instead
        this.gameCreateInventory = new GameCreateInventory(this.game, getOptions());
        this.gameWaitPlayersInventory = new GameWaitPlayersInventory(this.game, getOptions());
    }

    public void build(Player player, GameInventoryCallback callback) {
        // TODO: if idle, build create game, else join or wager

        // create game -> gameData -> open wait players
        // join queue -> accept -> add to queue, waiting for game owner
        // wait players -> accept enough -> move all to ready
        // ready -> onready -> start game

        this.gameCreateInventory.build(player, new CreateInventoryCallback() {
            @Override
            public void onCreateGame(HashMap<String, Object> gameData) {
                // check if gameData has been set, if it has, don't overwrite.

                // Game has been created with gameData
                if(gameData == null) {
                    player.sendMessage("Not creating game");
                    return;
                }

                // Set game data, open wait players
                player.sendMessage("Creating game with gameData");
                gameWaitPlayersInventory.build(player);
            }
        });

        // this.gameJoinInventory.build(player, onJoin -> add, update waitplayers
    }

    // TODO: Reset method, kicks everyone out, called when create game or game owner leaves

    public Game getGame() {
        return this.game;
    }

}
