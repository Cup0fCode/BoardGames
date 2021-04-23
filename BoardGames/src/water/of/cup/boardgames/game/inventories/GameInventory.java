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

    private final ArrayList<GameOption> gameOptions;

    // Vars that must be reset
    private final ArrayList<Player> joinPlayerQueue;
    private HashMap<String, Object> gameData;
    private Player gameCreator;

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
        this.joinPlayerQueue = new ArrayList<>();
        this.gameOptions = getOptions();

        // When gameData is null, no game has been created
        this.gameData = null;

        this.gameCreateInventory = new GameCreateInventory(this);
        this.gameWaitPlayersInventory = new GameWaitPlayersInventory(this);
    }

    public void build(Player player, GameInventoryCallback callback) {
        // TODO: if idle, build create game, else join or wager

        // create game -> gameData -> open wait players
        // join queue -> accept -> add to queue, waiting for game owner
        // wait players -> accept enough -> move all to ready
        // ready -> onready -> start game

        this.gameCreateInventory.build(player, handleCreateGame(player));

        // this.gameJoinInventory.build(player, onleave/onJoin -> add, update waitplayers/ check if game has enough players
    }

    private CreateInventoryCallback handleCreateGame(Player player) {
        return new CreateInventoryCallback() {
            @Override
            public void onCreateGame(HashMap<String, Object> gameDataResult) {
                // check if gameData has been set, if it has, don't overwrite.
                if(gameData != null) {
                    player.sendMessage("Game has already been created.");
                    return;
                }

                // Game has been created with gameData
                if(gameDataResult == null) {
                    player.sendMessage("Exited creating game.");
                    return;
                }

                // Set game data, open wait players
                player.sendMessage("Creating game with gameData");

                gameCreator = player;
                gameData = new HashMap<>(gameDataResult);
                gameWaitPlayersInventory.build(player, handleWaitPlayers());
            }
        };
    }

    private WaitPlayersCallback handleWaitPlayers() {
        return new WaitPlayersCallback() {
            @Override
            public void onAccept(Player player) {
                // TODO: Add them to players
            }

            @Override
            public void onDecline(Player player) {
                // TODO: Remove from queue
            }
        };
    }

    // TODO: Reset method, kicks everyone out, called when create game or game owner leaves

    public Game getGame() {
        return this.game;
    }

    // This should be called instead of getOptions()
    public ArrayList<GameOption> getGameOptions() {
        return new ArrayList<>(this.gameOptions);
    }

    public ArrayList<Player> getJoinPlayerQueue() {
        return new ArrayList<>(this.joinPlayerQueue);
    }

    public Object getGameData(String key) {
        return this.gameData.get(key);
    }

}
