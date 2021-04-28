package water.of.cup.boardgames.game.inventories;

import org.bukkit.entity.Player;
import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.inventories.create.CreateInventoryCallback;
import water.of.cup.boardgames.game.inventories.create.GameCreateInventory;
import water.of.cup.boardgames.game.inventories.create.GameWaitPlayersInventory;
import water.of.cup.boardgames.game.inventories.create.WaitPlayersCallback;
import water.of.cup.boardgames.game.inventories.join.GameJoinInventory;
import water.of.cup.boardgames.game.inventories.join.JoinGameCallback;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class GameInventory {

    private final Game game;
    protected abstract ArrayList<GameOption> getOptions();
    protected abstract int getMaxQueue();
    protected abstract int getMaxGame();

    private final GameCreateInventory gameCreateInventory;
    private final GameWaitPlayersInventory gameWaitPlayersInventory;
    private final GameJoinInventory gameJoinInventory;

    private final ArrayList<GameOption> gameOptions;
    private final int maxPlayers;

    // Vars that must be reset
    private final ArrayList<Player> joinPlayerQueue;
    private final ArrayList<Player> acceptedPlayers;
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
        this.gameOptions = getOptions();
        this.maxPlayers = getMaxGame();

        // When gameData is null, no game has been created
        this.gameData = null;

        this.gameCreateInventory = new GameCreateInventory(this);
        this.gameWaitPlayersInventory = new GameWaitPlayersInventory(this);
        this.gameJoinInventory = new GameJoinInventory(this);
    }

    public void build(Player player, GameInventoryCallback callback) {
        // create game -> gameData -> open wait players
        // join queue -> accept -> add to queue, waiting for game owner
        // wait players -> accept enough -> move all to ready
        // ready -> onready -> start game

        if(gameData == null) {
            this.gameCreateInventory.build(player, handleCreateGame(player));
            return;
        }

        this.gameJoinInventory.build(player, handleJoinGame());



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
                // TODO: Move to accept screen if maxPlayers
                gameCreator.sendMessage("Accepting " + player.getDisplayName());

                joinPlayerQueue.remove(player);
                acceptedPlayers.add(player);

                updateWaitPlayersInventory();
                updateJoinGameInventory(player);
            }

            @Override
            public void onDecline(Player player) {
                gameCreator.sendMessage("Declining " + player.getDisplayName());

                joinPlayerQueue.remove(player);

                // Close inventory for player waiting
                player.closeInventory();

                updateWaitPlayersInventory();
            }
        };
    }

    private JoinGameCallback handleJoinGame() {
        return new JoinGameCallback() {
            @Override
            public void onJoin(Player player) {
                if(joinPlayerQueue.size() < getMaxQueue()) {
                    joinPlayerQueue.add(player);

                    // update waitplayers
                    updateWaitPlayersInventory();
                    updateJoinGameInventory(player);
                } else {
                    player.closeInventory();
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

    private void updateWaitPlayersInventory() {
        gameWaitPlayersInventory.build(gameCreator, handleWaitPlayers());
    }

    private void updateJoinGameInventory(Player player) {
        gameJoinInventory.build(player, handleJoinGame());
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

}
