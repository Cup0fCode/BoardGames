package water.of.cup.boardgames.game.inventories;

import de.themoep.inventorygui.InventoryGui;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import water.of.cup.boardgames.BoardGames;
import water.of.cup.boardgames.config.ConfigUtil;
import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.GamePlayer;
import water.of.cup.boardgames.game.MathUtils;
import water.of.cup.boardgames.game.inventories.create.CreateInventoryCallback;
import water.of.cup.boardgames.game.inventories.create.GameCreateInventory;
import water.of.cup.boardgames.game.inventories.create.GameWaitPlayersInventory;
import water.of.cup.boardgames.game.inventories.create.WaitPlayersCallback;
import water.of.cup.boardgames.game.inventories.ingame.GameForfeitCallback;
import water.of.cup.boardgames.game.inventories.ingame.GameForfeitInventory;
import water.of.cup.boardgames.game.inventories.join.GameJoinInventory;
import water.of.cup.boardgames.game.inventories.join.JoinGameCallback;
import water.of.cup.boardgames.game.inventories.number.GameNumberInventory;
import water.of.cup.boardgames.game.inventories.number.GameNumberInventoryCallback;
import water.of.cup.boardgames.game.inventories.ready.GameReadyCallback;
import water.of.cup.boardgames.game.inventories.ready.GameReadyInventory;
import water.of.cup.boardgames.game.inventories.trade.GameTrade;
import water.of.cup.boardgames.game.inventories.trade.GameTradeCallback;
import water.of.cup.boardgames.game.inventories.trade.GameTradeInventory;
import water.of.cup.boardgames.game.inventories.wager.GameWagerCallback;
import water.of.cup.boardgames.game.inventories.wager.GameWagerInventory;
import water.of.cup.boardgames.game.inventories.wager.WagerOption;
import water.of.cup.boardgames.game.wagers.ItemWager;
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
    protected abstract boolean hasGameWagers(); // 1v1 only
    protected abstract boolean hasWagerScreen(); // 1v1 only
    protected abstract boolean hasForfeitScreen(); // 1v1 only
    protected abstract void onGameCreate(HashMap<String, Object> gameData, ArrayList<GamePlayer> players);

    private final GameCreateInventory gameCreateInventory;
    private final GameWaitPlayersInventory gameWaitPlayersInventory;
    private final GameJoinInventory gameJoinInventory;
    private final GameReadyInventory gameReadyInventory;
    private final GameWagerInventory gameWagerInventory;
    private final GameForfeitInventory gameForfeitInventory;

    private final BoardGames instance = BoardGames.getInstance();
    private final ArrayList<GameOption> gameOptions;
    private final int maxPlayers;
    private final int minPlayers;
    private final boolean hasWagers;
    private final boolean hasItemWagers;

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

        this.gameOptions = getOptions() == null ? new ArrayList<>() : getOptions();
        this.maxPlayers = getMaxGame();
        this.minPlayers = getMinGame();
        this.hasWagers = hasGameWagers() && (instance.getEconomy() != null);
        this.hasItemWagers = this.maxPlayers == 2 && ConfigUtil.ITEM_WAGERS_ENABLED.toBoolean();

        // Add team option if enabled
        if(hasTeamSelect() && game.getTeamNames() != null) {
            this.gameOptions.add(0, GameOption.getTeamSelectGameOption(game.getTeamNames()));
        }

        // Add item wager option if enabled
        if(this.hasItemWagers) {
            this.gameOptions.add(0, GameOption.getTradeItemsOption());
        }

        // Add wager option if enabled
        if(this.hasWagers) {
            this.gameOptions.add(0, GameOption.getWagerGameOption(game.getMaxWager()));
        }

        // Remove economy required options if no economy is found
        ArrayList<GameOption> toBeRemoved = new ArrayList<>();
        for(GameOption gameOption : this.gameOptions) {
            if(gameOption.requiresEconomy() && instance.getEconomy() == null) {
                toBeRemoved.add(gameOption);
            }
        }

        this.gameOptions.removeAll(toBeRemoved);

        // When gameData is null, no game has been created
        this.gameData = null;

        this.gameCreateInventory = new GameCreateInventory(this);
        this.gameWaitPlayersInventory = new GameWaitPlayersInventory(this);
        this.gameJoinInventory = new GameJoinInventory(this);
        this.gameReadyInventory = new GameReadyInventory(this);
        this.gameWagerInventory = new GameWagerInventory(this);
        this.gameForfeitInventory = new GameForfeitInventory(this);
    }

    public void build(Player player) {
        // create game -> gameData -> open wait players
        // join queue -> accept -> add to queue, waiting for game owner
        // wait players -> accept enough -> move all to ready
        // ready -> onready -> start game

        if(hasForfeitScreen() && game.isIngame() && game.hasPlayer(player)) {
            this.gameForfeitInventory.build(player, handleForfeit());
            return;
        }

        if(hasCustomInGameInventory() && game.isIngame() && game.hasPlayer(player)) {
            openCustomInGameInventory(player);
            return;
        }

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
                if(gameData != null || game.isIngame()) {
                    player.sendMessage(ConfigUtil.CHAT_GUI_GAME_ALREADY_CREATED.toString());
                    return;
                }

                // Game has been created with gameData
                if(gameDataResult == null) {
//                    player.sendMessage("Exited creating game.");
                    return;
                }

                String preselectedTeam = null;
                if(gameDataResult.containsKey("team") && hasTeamSelect()) {
                    preselectedTeam = gameDataResult.get("team") + "";
                }

                // Check if they have enough money
                for(GameOption gameOption : gameOptions) {
                    if (gameOption.requiresEconomy()) {
                        String stringVal = gameDataResult.get(gameOption.getKey()) + "";
                        if(!MathUtils.isNumeric(stringVal)) continue;

                        double value = Double.parseDouble(stringVal);
                        if(instance.getEconomy().getBalance(player) < value) {
                            player.sendMessage(ConfigUtil.CHAT_GUI_GAME_NO_MONEY_CREATE.toString());
                            return;
                        }
                    }
                }

                if(hasWagers) {
                    double wagerAmount = Double.parseDouble(gameDataResult.get("wager") + "");
                    GamePlayer gamePlayer = game.addPlayer(player, preselectedTeam);

                    // Add game wagers
                    wagerManager.initGameWager(gamePlayer, wagerAmount);
                } else {
                    game.addPlayer(player, preselectedTeam);
                }

//                player.sendMessage("Creating game with gameData");

                // Set game data, open wait players
                gameCreator = player;
                gameData = new HashMap<>(gameDataResult);

                game.setGameData(gameData);

                if(maxPlayers == 1) {
                    onGameCreate(gameData, game.getGamePlayers());
                    resetGameInventory(null, false);
                } else {
                    gameWaitPlayersInventory.build(player, handleWaitPlayers());
                }
            }
        };
    }

    private WaitPlayersCallback handleWaitPlayers() {
        return new WaitPlayersCallback() {
            @Override
            public void onAccept(Player player) {
                // Check if they have enough money
                if(!hasMoneyToAccept(player)) {
                    gameCreator.sendMessage(ConfigUtil.CHAT_GUI_GAME_NO_MONEY_ACCEPT.toString());
                    player.sendMessage(ConfigUtil.CHAT_GUI_GAME_NO_MONEY_JOIN.toString());

                    joinPlayerQueue.remove(player);
                    closeInventory(player);

                    updateWaitPlayersInventory();
                    return;
                }

                gameCreator.sendMessage(ConfigUtil.CHAT_GUI_GAME_ACCEPT.buildString(player.getDisplayName()));

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
                    if(hasItemWagers && gameData.get("trade").equals(ConfigUtil.GUI_WAGERITEMS_ENABLED_LABEL.toString())) {
                        moveToItemWager();
                    } else {
                        moveToReady();
                    }
                } else {
                    updateWaitPlayersInventory();
                    updateJoinGameInventory(player);
                }
            }

            @Override
            public void onDecline(Player player) {
                gameCreator.sendMessage(ConfigUtil.CHAT_GUI_GAME_DECLINE.buildString(player.getDisplayName()));

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
                resetGameInventory(ConfigUtil.CHAT_GUI_GAME_OWNER_LEFT.toString(), true);
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
                    player.sendMessage(ConfigUtil.CHAT_GUI_GAME_NO_AVAIL_GAME.toString());
                    return;
                }

                // Check if they have enough money
                if(!hasMoneyToAccept(player)) {
                    player.sendMessage(ConfigUtil.CHAT_GUI_GAME_NO_MONEY_JOIN.toString());
                    return;
                }

                if(joinPlayerQueue.size() < getMaxQueue()) {
                    joinPlayerQueue.add(player);

                    // update waitplayers
                    updateWaitPlayersInventory();
                    updateJoinGameInventory(player);
                } else {
                    closeInventory(player);
                    player.sendMessage(ConfigUtil.CHAT_GUI_GAME_FULL_QUEUE.toString());
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
                resetGameInventory(ConfigUtil.CHAT_GUI_GAME_PLAYER_LEFT.toString(), true);
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

                wagerOwner.sendMessage(ConfigUtil.CHAT_GUI_WAGER_ACCEPT.buildString(wagerOpponent.getDisplayName()));
                wagerOpponent.sendMessage(ConfigUtil.CHAT_GUI_WAGER_ACCEPTED.buildString(wagerOwner.getDisplayName()));

                updateWagerViewInventories();
            }

            @Override
            public void onLeave(Player player) {
                wagerViewPlayers.remove(player);
            }
        };
    }

    private GameForfeitCallback handleForfeit() {
        return new GameForfeitCallback() {
            @Override
            public void onForfeit(Player player) {
                if(game.isIngame() && game.hasPlayer(player)) {
                    game.exitPlayer(player);
                }
            }
        };
    }

    private GameTradeCallback handleItemWager() {
        return new GameTradeCallback() {
            @Override
            public void onAccept(GameTrade gameTrade) {
                wagerManager.addWager(new ItemWager(gameTrade));

                moveToReady();
            }

            @Override
            public void onLeave(GameTrade gameTrade) {
                // Wager not yet created, so still need to send items back
                gameTrade.sendBackItems();
                gameTrade.cancelTimer();

                resetGameInventory(ConfigUtil.CHAT_GUI_GAME_PLAYER_LEFT.toString(), true);
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
        closePlayers(joinPlayerQueue, ConfigUtil.CHAT_GUI_GAME_ALREADY_CREATED.toString());

        // Init all player ready
        playerReadyMap.put(gameCreator, false);
        for(Player player1 : acceptedPlayers) {
            playerReadyMap.put(player1, false);
            gameReadyInventory.build(player1, handleReady());
        }

        gameReadyInventory.build(gameCreator, handleReady());
    }

    private void moveToItemWager() {
        // Kick players still in queue
        closePlayers(joinPlayerQueue, ConfigUtil.CHAT_GUI_GAME_ALREADY_CREATED.toString());

        if(game.getGamePlayers().size() != 2) return;

        Player player1 = game.getGamePlayers().get(0).getPlayer();
        Player player2 = game.getGamePlayers().get(1).getPlayer();

        GameTradeCallback gameTradeCallback = handleItemWager();
        GameTrade gameTrade = new GameTrade(player1, player2, game, gameTradeCallback);
        new GameTradeInventory(gameTrade, gameTradeCallback).build(player1);
        new GameTradeInventory(gameTrade, gameTradeCallback).build(player2);
    }

    // Reset method, kicks everyone out, called when create game or game owner leaves
    private void resetGameInventory(String message, boolean clearGamePlayer) {
        if(gameCreator == null)
            return;

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

    private double getGameDataNum(String key) {
        if(gameData != null && gameData.containsKey(key)) {
            String value = gameData.get(key) + "";
            if(MathUtils.isNumeric(value)) {
                return Double.parseDouble(value);
            }
        }

        return 0;
    }

    public String getCreateGameText() {
        return ConfigUtil.GUI_CREATE_GAME.toString();
    }

    public boolean hasCustomInGameInventory() {
        return false;
    }

    public void openCustomInGameInventory(Player player) {
    }

    private boolean hasMoneyToAccept(Player player) {
        if(hasWagers && (instance.getEconomy().getBalance(player) < getGameWagerAmount())) {
            return false;
        }

        for(GameOption gameOption : gameOptions) {
            if(gameOption.requiresEconomy()) {
                double num = getGameDataNum(gameOption.getKey());
                if(instance.getEconomy().getBalance(player) < num) return false;
            }
        }

        return true;
    }
}
