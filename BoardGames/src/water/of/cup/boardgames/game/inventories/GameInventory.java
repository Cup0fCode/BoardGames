package water.of.cup.boardgames.game.inventories;

import de.themoep.inventorygui.InventoryGui;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import water.of.cup.boardgames.BoardGames;
import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.GamePlayer;
import water.of.cup.boardgames.game.MathUtils;
import water.of.cup.boardgames.game.inventories.create.CreateInventoryCallback;
import water.of.cup.boardgames.game.inventories.create.GameCreateInventory;
import water.of.cup.boardgames.game.inventories.create.GameWaitPlayersInventory;
import water.of.cup.boardgames.game.inventories.create.WaitPlayersCallback;
import water.of.cup.boardgames.game.inventories.join.GameJoinInventory;
import water.of.cup.boardgames.game.inventories.join.JoinGameCallback;
import water.of.cup.boardgames.game.inventories.ready.GameReadyCallback;
import water.of.cup.boardgames.game.inventories.ready.GameReadyInventory;
import water.of.cup.boardgames.game.inventories.wager.GameWagerCallback;
import water.of.cup.boardgames.game.inventories.wager.GameWagerInventory;
import water.of.cup.boardgames.game.inventories.wager.WagerOption;
import water.of.cup.boardgames.game.wagers.RequestWager;
import water.of.cup.boardgames.game.wagers.WagerManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public abstract class GameInventory {

    private final Game game;
    protected abstract ArrayList<GameOption> getOptions();
    protected abstract int getMaxQueue();
    protected abstract int getMaxGame();
    protected abstract int getMinGame();
    protected abstract boolean hasTeamSelect();
    protected abstract boolean hasGameWagers();
    protected abstract boolean hasWagerScreen();
    protected abstract void onGameCreate(HashMap<String, Object> gameData, ArrayList<GamePlayer> players);

    private final GameCreateInventory gameCreateInventory;
    private final GameWaitPlayersInventory gameWaitPlayersInventory;
    private final GameJoinInventory gameJoinInventory;
    private final GameReadyInventory gameReadyInventory;
    private final GameWagerInventory gameWagerInventory;

    private final BoardGames instance = BoardGames.getInstance();
    private final ArrayList<GameOption> gameOptions;
    private final int maxPlayers;
    private final int minPlayers;
    private final boolean hasWagers;

    // Vars that must be reset
    private final ArrayList<Player> joinPlayerQueue;
    private final ArrayList<Player> acceptedPlayers;
    private final HashMap<Player, Boolean> playerReadyMap;
    private final HashMap<Player, WagerOption> wagerViewPlayers;
    private final WagerManager wagerManager;
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
        this.wagerViewPlayers = new HashMap<>();
        this.wagerManager = game.getWagerManager();
        this.gameOptions = getOptions();
        this.maxPlayers = getMaxGame();
        this.minPlayers = getMinGame();
        this.hasWagers = hasGameWagers() && (instance.getEconomy() != null);

        // Add team option if enabled
        if(hasTeamSelect() && game.getTeamNames() != null) {
            this.gameOptions.add(0, GameOption.getTeamSelectGameOption(game.getTeamNames()));
        }

        // Add wager option if enabled
        if(this.hasWagers) {
            this.gameOptions.add(0, GameOption.getWagerGameOption());
        }

        // When gameData is null, no game has been created
        this.gameData = null;

        this.gameCreateInventory = new GameCreateInventory(this);
        this.gameWaitPlayersInventory = new GameWaitPlayersInventory(this);
        this.gameJoinInventory = new GameJoinInventory(this);
        this.gameReadyInventory = new GameReadyInventory(this);
        this.gameWagerInventory = new GameWagerInventory(this);
    }

    public void build(Player player) {
        // create game -> gameData -> open wait players
        // join queue -> accept -> add to queue, waiting for game owner
        // wait players -> accept enough -> move all to ready
        // ready -> onready -> start game

        // TODO: If ingame, display special inv
        if(game.isIngame()) return;

        if(gameData == null) {
            this.gameCreateInventory.build(player, handleCreateGame(player));
            return;
        }

        // If they are in ready screen, show wagers, otherwise don't show anything
        if(playerReadyMap.size() > 0) {
            if(this.hasWagers && hasWagerScreen()) {
                this.wagerViewPlayers.put(player, new WagerOption(game.getGamePlayers().get(0)));
                this.gameWagerInventory.build(player, handleWager());
            }
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

                String preselectedTeam = null;
                if(gameDataResult.containsKey("team") && hasTeamSelect()) {
                    preselectedTeam = gameDataResult.get("team") + "";
                }

                // Check if they have enough money
                if(hasWagers) {
                    double wagerAmount = Double.parseDouble(gameDataResult.get("wager") + "");
                    if(instance.getEconomy().getBalance(player) < wagerAmount) {
                        player.sendMessage(ChatColor.RED + "Not enough money to create game.");
                        return;
                    }

                    GamePlayer gamePlayer = game.addPlayer(player, preselectedTeam);

                    // Add game wagers
                    wagerManager.initGameWager(gamePlayer, wagerAmount);
                } else {
                    game.addPlayer(player, preselectedTeam);
                }

                player.sendMessage("Creating game with gameData");

                // Set game data, open wait players
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
                // Check if they have enough money
                if(hasWagers && (instance.getEconomy().getBalance(player) < getGameWagerAmount())) {
                    gameCreator.sendMessage(ChatColor.RED + "Player no longer has enough money.");
                    player.sendMessage(ChatColor.RED + "You do not have enough money!");

                    joinPlayerQueue.remove(player);
                    closeInventory(player);

                    updateWaitPlayersInventory();
                    return;
                }

                gameCreator.sendMessage("Accepting " + player.getDisplayName());

                joinPlayerQueue.remove(player);
                acceptedPlayers.add(player);

                // Add player to game
                game.addPlayer(player);

                // Add to wager (Keep in mind wagers really only work for 2 players)
                if(hasWagers) {
                    wagerManager.addGameWagerPlayer(player, gameCreator);
                }

                // Move players to ready screen
                if(acceptedPlayers.size() == getMaxPlayers() - 1) {
                    moveToReady();
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
            public void onStart() {
                moveToReady();
            }

            @Override
            public void onLeave() {
                resetGameInventory("Game owner has left", true);
            }
        };
    }

    private JoinGameCallback handleJoinGame() {
        return new JoinGameCallback() {
            @Override
            public void onJoin(Player player) {
                // If game already started etc, don't let them join
                if(gameData == null || playerReadyMap.size() > 0) {
                    closeInventory(player);
                    player.sendMessage("No available game to join.");
                    return;
                }

                // Check if they have enough money
                if(hasWagers && (instance.getEconomy().getBalance(player) < getGameWagerAmount())) {
                    player.sendMessage(ChatColor.RED + "Not enough money to join game.");
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

                // Remove player from game
                game.removePlayer(player);

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
                    // simultaneous click check
                    if(gameCreator != null) {
                        // Everyone is ready, close invs, reset, give data
                        onGameCreate(gameData, game.getGamePlayers());
                        resetGameInventory(null, false);
                    }
                } else {
                    updateReadyInventory();
                }
            }

            @Override
            public void onLeave(Player player) {
                resetGameInventory("Player left ready screen. Game cancelled.", true);
            }
        };
    }

    private GameWagerCallback handleWager() {
        return new GameWagerCallback() {
            @Override
            public void onCreate(RequestWager requestWager) {
                wagerManager.addRequestWager(requestWager);

                updateWagerViewInventories();
            }

            @Override
            public void onCancel(RequestWager requestWager) {
                wagerManager.cancelRequestWager(requestWager);

                updateWagerViewInventories();
            }

            @Override
            public void onAccept(Player wagerOpponent, RequestWager requestWager) {
                wagerManager.acceptRequestWager(wagerOpponent, requestWager);

                Player wagerOwner = requestWager.getOwner();

                wagerOwner.sendMessage(wagerOpponent.getDisplayName() + " has accepted your wager!");
                wagerOpponent.sendMessage("You have accepted " + wagerOwner.getDisplayName() + "'s wager!");

                updateWagerViewInventories();
            }

            @Override
            public void onLeave(Player player) {
                wagerViewPlayers.remove(player);
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

    private void updateWagerViewInventories() {
        for(Player player : wagerViewPlayers.keySet()) {
            gameWagerInventory.build(player, handleWager());
        }
    }

    private void moveToReady() {
        // Kick players still in queue
        closePlayers(joinPlayerQueue, "Game has started, you have been kicked.");

        // Init all player ready
        playerReadyMap.put(gameCreator, false);
        for(Player player1 : acceptedPlayers) {
            playerReadyMap.put(player1, false);
            gameReadyInventory.build(player1, handleReady());
        }

        gameReadyInventory.build(gameCreator, handleReady());
    }

    // TODO: Reset method, kicks everyone out, called when create game or game owner leaves
    private void resetGameInventory(String message, boolean clearGamePlayer) {
        // Close players out of inventory
        closePlayers(joinPlayerQueue, message);

        closePlayers(acceptedPlayers, message);

        closePlayers(new ArrayList<>(wagerViewPlayers.keySet()), message);

        closeInventory(gameCreator);
        if(message != null)
            gameCreator.sendMessage(message);

        // Send money back to all request wagers
        wagerManager.endAllRequestWagers();

        // Clear arrays
        joinPlayerQueue.clear();
        acceptedPlayers.clear();
        playerReadyMap.clear();
        wagerViewPlayers.clear();
        gameCreator = null;
        gameData = null;

        if(clearGamePlayer) {
            game.clearGamePlayers();

            // Send money back to game wagers
            wagerManager.endAllWagers();
        }
    }

    private void closeInventory(Player player) {
        InventoryGui gui = InventoryGui.get(player);
        if(gui != null)
            gui.close(true);
    }

    private void closePlayers(ArrayList<Player> players, String message) {
        for(Player player : players) {
            closeInventory(player);
            if(message != null)
                player.sendMessage(message);
        }
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

    public WagerManager getWagerManager() {
        return this.wagerManager;
    }

    public WagerOption getWagerOption(Player player) {
        return wagerViewPlayers.get(player);
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

    public int getMinPlayers() {
        return this.minPlayers;
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

    private double getGameWagerAmount() {
        if(hasWagers && gameData != null) {
            String gameWagerData = gameData.get("wager") + "";
            if(MathUtils.isNumeric(gameWagerData)) {
                return Double.parseDouble(gameWagerData);
            }
        }

        return 0;
    }

}
