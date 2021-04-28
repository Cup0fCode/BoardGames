package water.of.cup.boardgames.game.inventories;

import de.themoep.inventorygui.InventoryGui;
import org.bukkit.entity.Player;
import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.inventories.create.CreateInventoryCallback;
import water.of.cup.boardgames.game.inventories.create.GameCreateInventory;
import water.of.cup.boardgames.game.inventories.create.GameWaitPlayersInventory;
import water.of.cup.boardgames.game.inventories.create.WaitPlayersCallback;
import water.of.cup.boardgames.game.inventories.join.GameJoinInventory;
import water.of.cup.boardgames.game.inventories.join.JoinGameCallback;
import water.of.cup.boardgames.game.inventories.ready.GameReadyCallback;
import water.of.cup.boardgames.game.inventories.ready.GameReadyInventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public abstract class GameInventory {

    private final Game game;
    protected abstract ArrayList<GameOption> getOptions();
    protected abstract int getMaxQueue();
    protected abstract int getMaxGame();
    protected abstract void onGameCreate(HashMap<String, Object> gameData, ArrayList<Player> players);

    private final GameCreateInventory gameCreateInventory;
    private final GameWaitPlayersInventory gameWaitPlayersInventory;
    private final GameJoinInventory gameJoinInventory;
    private final GameReadyInventory gameReadyInventory;

    private final ArrayList<GameOption> gameOptions;
    private final int maxPlayers;

    // Vars that must be reset
    private final ArrayList<Player> joinPlayerQueue;
    private final ArrayList<Player> acceptedPlayers;
    private final HashMap<Player, Boolean> playerReadyMap;
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
        this.acceptedPlayers = new ArrayList<>();
        this.playerReadyMap = new HashMap<>();
        this.gameOptions = getOptions();
        this.maxPlayers = getMaxGame();

        // When gameData is null, no game has been created
        this.gameData = null;

        this.gameCreateInventory = new GameCreateInventory(this);
        this.gameWaitPlayersInventory = new GameWaitPlayersInventory(this);
        this.gameJoinInventory = new GameJoinInventory(this);
        this.gameReadyInventory = new GameReadyInventory(this);
    }

    public void build(Player player) {
        // create game -> gameData -> open wait players
        // join queue -> accept -> add to queue, waiting for game owner
        // wait players -> accept enough -> move all to ready
        // ready -> onready -> start game

        if(gameData == null) {
            this.gameCreateInventory.build(player, handleCreateGame(player));
            return;
        }

        this.gameJoinInventory.build(player, handleJoinGame());
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

                // TODO: if gameDataResult contains gameSize, set getMaxGame

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
                gameCreator.sendMessage("Accepting " + player.getDisplayName());

                joinPlayerQueue.remove(player);
                acceptedPlayers.add(player);

                // Move players to ready screen
                if(acceptedPlayers.size() == getMaxPlayers() - 1) {
                    // Init all player ready
                    playerReadyMap.put(gameCreator, false);
                    for(Player player1 : acceptedPlayers) {
                        playerReadyMap.put(player1, false);
                        gameReadyInventory.build(player1, handleReady());
                    }

                    gameReadyInventory.build(gameCreator, handleReady());
                } else {
                    updateWaitPlayersInventory();
                    updateJoinGameInventory(player);
                }
            }

            @Override
            public void onDecline(Player player) {
                gameCreator.sendMessage("Declining " + player.getDisplayName());

                joinPlayerQueue.remove(player);

                // Close inventory for player waiting
                closeInventory(player);

                updateWaitPlayersInventory();
            }

            @Override
            public void onLeave() {
                resetGameInventory("Game owner has left");
            }
        };
    }

    private JoinGameCallback handleJoinGame() {
        return new JoinGameCallback() {
            @Override
            public void onJoin(Player player) {
                // If game already started etc, don't let them join
                if(gameData == null) {
                    closeInventory(player);
                    player.sendMessage("No available game to join.");
                    return;
                }

                if(joinPlayerQueue.size() < getMaxQueue()) {
                    joinPlayerQueue.add(player);

                    // update waitplayers
                    updateWaitPlayersInventory();
                    updateJoinGameInventory(player);
                } else {
                    closeInventory(player);
                    player.sendMessage("Too many players are queuing!");
                }

            }

            @Override
            public void onLeave(Player player) {
                boolean shouldUpdate = joinPlayerQueue.contains(player) || acceptedPlayers.contains(player);
                joinPlayerQueue.remove(player);
                acceptedPlayers.remove(player);

                // update waitplayers
                if(shouldUpdate)
                    updateWaitPlayersInventory();
            }
        };
    }

    private GameReadyCallback handleReady() {
        return new GameReadyCallback() {
            @Override
            public void onReady(Player player) {
                playerReadyMap.put(player, true);

                // Check if everyone is ready
                boolean allReady = true;
                for(Player player1 : playerReadyMap.keySet()) {
                    if(!playerReadyMap.get(player1)) {
                        allReady = false;
                        break;
                    }
                }

                if(allReady) {
                    // Everyone is ready, close invs, reset, give data
                    onGameCreate(gameData, new ArrayList<>(playerReadyMap.keySet()));
                    resetGameInventory(null);
                } else {
                    updateReadyInventory();
                }
            }

            @Override
            public void onLeave(Player player) {
                resetGameInventory("Player left ready screen. Game cancelled.");
            }
        };
    }

    private void updateWaitPlayersInventory() {
        gameWaitPlayersInventory.build(gameCreator, handleWaitPlayers());
    }

    private void updateJoinGameInventory(Player player) {
        gameJoinInventory.build(player, handleJoinGame());
    }

    private void updateReadyInventory() {
        for(Player player : playerReadyMap.keySet()) {
            gameReadyInventory.build(player, handleReady());
        }
    }

    // TODO: Reset method, kicks everyone out, called when create game or game owner leaves
    private void resetGameInventory(String message) {
        for(Player player : joinPlayerQueue) {
            closeInventory(player);
            if(message != null)
                player.sendMessage(message);
        }

        for(Player player : acceptedPlayers) {
            closeInventory(player);
            if(message != null)
                player.sendMessage(message);
        }

        closeInventory(gameCreator);
        if(message != null)
            gameCreator.sendMessage(message);

        joinPlayerQueue.clear();
        acceptedPlayers.clear();
        playerReadyMap.clear();
        gameCreator = null;
        gameData = null;
    }

    private void closeInventory(Player player) {
        InventoryGui gui = InventoryGui.get(player);
        if(gui != null)
            gui.close(true);
    }

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

    public ArrayList<Player> getAcceptedPlayers() {
        return new ArrayList<>(this.acceptedPlayers);
    }

    public Object getGameData(String key) {
        return this.gameData.get(key);
    }

    public Player getGameCreator() {
        return this.gameCreator;
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    // Use this inside readyinventory to make sure game creator is included
    public Set<Player> getReadyPlayers() {
        return playerReadyMap.keySet();
    }

    public boolean getReadyStatus(Player player) {
        return playerReadyMap.get(player);
    }

    public int getNumReady() {
        int numReady = 0;
        for(Player player1 : playerReadyMap.keySet()) {
            if(playerReadyMap.get(player1)) numReady++;
        }
        return numReady;
    }

}
