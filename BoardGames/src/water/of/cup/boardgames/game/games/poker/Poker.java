package water.of.cup.boardgames.game.games.poker;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import water.of.cup.boardgames.BoardGames;
import water.of.cup.boardgames.config.ConfigUtil;
import water.of.cup.boardgames.game.*;
import water.of.cup.boardgames.game.games.gameutils.cards.Card;
import water.of.cup.boardgames.game.games.gameutils.cards.Deck;
import water.of.cup.boardgames.game.games.gameutils.cards.Hand;
import water.of.cup.boardgames.game.inventories.GameInventory;
import water.of.cup.boardgames.game.inventories.GameOption;
import water.of.cup.boardgames.game.inventories.GameOptionType;
import water.of.cup.boardgames.game.inventories.number.GameNumberInventory;
import water.of.cup.boardgames.game.npcs.GameNPC;
import water.of.cup.boardgames.game.storage.CasinoGamesStorageType;
import water.of.cup.boardgames.game.storage.GameStorage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Poker extends Game {

    private Deck pokerDeck;
    private LinkedHashMap<GamePlayer, Hand> playerHands;
    private HashMap<GamePlayer, ArrayList<Button>> playerButtons;
    private HashMap<GamePlayer, Button> playerSelectedButtons;
    private LinkedHashMap<GamePlayer, Integer> playerBets;
    private Button flopButton;
    private HashMap<GamePlayer, Button> playerHandButtons;
    private LinkedHashMap<GamePlayer, Integer> playersAllIn;
    private ArrayList<SidePot> sidePots;
    private HashMap<GamePlayer, Integer> spotsTaken;
    private Hand flopCards;
    private PokerGameTimer pokerTimer;
    private PokerTurnTimer pokerTurnTimer;
    private int firstBetIndex;
    private int currentBet;
    private int gamePot;
    private int BIG_BLIND;
    private int RAISE_LIMIT;

    private static final int AMOUNT_OF_DECKS = 1;
    private final BoardGames instance = BoardGames.getInstance();

    // TODO: poker chips, translate messages
    public Poker(int rotation) {
        super(rotation);
    }

    @Override
    protected void setMapInformation(int i) {
        this.mapStructure = new int[][] { { 1, 2, 3, 4, 5 }, { 6, 7, 8, 9, 10 }, { 11, 12, 0, 13, 14 } };
        this.placedMapVal = 8;
    }

    @Override
    protected void startGame() {
        // Reorder team manager
        this.setTeamOrder();

        boolean canStart = canStartNextGame();
        if(!canStart)
            return;

        // Clear renderers
        buttons.clear();
        super.startGame();

        // Initiate poker deck
        this.pokerDeck = new Deck(AMOUNT_OF_DECKS);

        // Deal cards to players, init flop cards
        this.dealCards();

        // Render in available spots
        this.renderAvailableSpots();

        // Render in game buttons
        this.setPokerButtons();

        this.takeBlindBets();

        mapManager.renderBoard();
    }

    private void setTeamOrder() {
        teamManager.resetTeams();
        for(int i = 0; i < 7; i++) {
            if(spotsTaken.containsValue(i)) {
                teamManager.addTeam(getPlayerBySpot(i));
            }
        }
    }

    private GamePlayer getPlayerBySpot(int spot) {
        for(GamePlayer gamePlayer : spotsTaken.keySet()) {
            if(spotsTaken.get(gamePlayer) == spot)
                return gamePlayer;
        }

        return null;
    }

    // Call inside poker inv
    protected void initGame() {
        if(hasGameData("minEntry") && hasGameData("raiseLimit")) {
            this.BIG_BLIND = (int) getGameData("minEntry");
            this.RAISE_LIMIT = (int) getGameData("raiseLimit");
        } else {
            this.BIG_BLIND = 0;
            this.RAISE_LIMIT = 0;
        }

        // Clear old buttons
        buttons.clear();

        super.setInGame();

        // init array of spots taken
        this.spotsTaken = new HashMap<>();

        this.firstBetIndex = -1;

        // show player avail spots, keep track of spots taken
        this.renderAvailableSpots();

        spawnNPC();

        mapManager.renderBoard();
    }

    private void renderAvailableSpots() {
        buttons.removeIf(button -> button.getName().startsWith("JOIN_GAME"));

        for(int i = 0; i < 7; i++) {
            if(spotsTaken.containsValue(i)) continue;

            int rotation = getPokerButtonRotation(i);
            int[] loc = getPokerButtonPos(i);

            transformCords(loc, 37, 111, i);

            Button b = new Button(this, "POKER_JOIN_GAME", loc, rotation, "JOIN_GAME_" + i);
            b.changeLocationByRotation();
            b.setClickable(true);
            buttons.add(b);
        }
    }

    private void dealCards() {
        this.playerHands = new LinkedHashMap<>();
        this.flopCards = new Hand();
        this.playerSelectedButtons = new HashMap<>();
        this.playerBets = new LinkedHashMap<>();
        this.playersAllIn = new LinkedHashMap<>();
        this.sidePots = new ArrayList<>();
        this.currentBet = 0;
        this.gamePot = 0;
        this.flopButton = null;

        for(GamePlayer gamePlayer : teamManager.getGamePlayers()) {
            ArrayList<Card> cards = this.pokerDeck.draw(2);
            Hand playerHand = new Hand();
            playerHand.addCards(cards);
            playerHands.put(gamePlayer, playerHand);
            playerBets.put(gamePlayer, -1);
        }
    }

    private void setPokerButtons() {
        // Sets player buttons
        this.playerButtons = new HashMap<>();
        this.playerHandButtons = new HashMap<>();
        for(GamePlayer gamePlayer : teamManager.getGamePlayers()) {
            this.playerButtons.put(gamePlayer, new ArrayList<>());

            int spot = spotsTaken.get(gamePlayer);
            ArrayList<Button> playerButtons = setPokerPlayerButtons(gamePlayer, spot);
            this.playerButtons.get(gamePlayer).addAll(playerButtons);

            Hand hand = playerHands.get(gamePlayer);
            int rotation = getPokerButtonRotation(spot);
            int[] loc = getPokerButtonPos(spot);

            GameImage handImage = hand.getGameImage(false);
            Button handButton = new Button(this, handImage, loc, rotation, "HAND");
            handButton.changeLocationByRotation();
            handButton.setVisibleForAll(false);
            handButton.addVisiblePlayer(gamePlayer);
            handButton.setClickable(false);

            playerHandButtons.put(gamePlayer, handButton);
            buttons.add(handButton);
        }

        // Sets flop cards
        this.setFlopCards();
    }

    private ArrayList<Button> setPokerPlayerButtons(GamePlayer gamePlayer, int spot) {
        ArrayList<Button> playerButtons = new ArrayList<>();
        for(PokerButton pokerButton : PokerButton.values()) {
            if(pokerButton.getxDisplacement() == 0) continue;

            int rotation = getPokerButtonRotation(spot);
            int[] loc = getPokerButtonPos(spot);

            transformCords(loc, pokerButton.getxDisplacement(), pokerButton.getyDisplacement(), spot);

            Button b = new Button(this, pokerButton.getImageName(true), loc, rotation, pokerButton.toString());
            b.changeLocationByRotation();
            b.setVisibleForAll(false);
            b.addVisiblePlayer(gamePlayer);
            b.setClickable(true);

            buttons.add(b);
            playerButtons.add(b);
        }
        return playerButtons;
    }

    private void setFlopCards() {
        int[] flopCord = new int[] { 128 * 3 + 64, 128 * 2 + 64};

        GameImage flopImage = flopCards.getGameImage(false);
        flopImage.resize(2);
        flopImage.setRotation(2);

        if(this.flopButton == null) {
            this.flopButton = new Button(this, flopImage, flopCord, 2, "FLOP");
            this.flopButton.changeLocationByRotation();
            buttons.add(this.flopButton);
            return;
        }

        this.flopButton.setImage(flopImage);
    }

    private int[] getPokerButtonPos(int posCounter) {
        int[] loc = new int[] { 0, 0 };
        if(posCounter > 4) {
            loc[0] = (128 * 4);
            loc[1] = 256 * (posCounter - 5) + 128;
        } else if(posCounter > 1) {
            loc[0] = (256 * (posCounter - 2)) + 128;
            loc[1] = 128;
        } else {
            loc[0] = 128;
            loc[1] = 256 * (1 - posCounter);
        }
        return loc;
    }

    private int getPokerButtonRotation(int posCounter) {
        if(posCounter > 4) {
            return 3;
        } else if(posCounter > 1) {
            return 2;
        } else {
            return 1;
        }
    }

    private void transformCords(int[] loc, int xDisp, int yDisp, int spot) {
        if(spot > 4) {
            loc[0] = loc[0] + yDisp;
            loc[1] = loc[1] - xDisp;;
        } else if(spot > 1){
            loc[0] = loc[0] - xDisp;
            loc[1] = loc[1] - yDisp;
        } else  {
            loc[0] = loc[0] - yDisp;
            loc[1] = loc[1] + xDisp;
        }
    }

    private boolean placeBet(GamePlayer gamePlayer, int amount) {
        int playerBalance = (int) instance.getEconomy().getBalance(gamePlayer.getPlayer());
        boolean allIn = playerBalance <= amount;

        if(amount < currentBet) return false;

        // if a player has gone all in, start counting new pot
        // new pot is smallest stack
        if(allIn) {
            sendGameMessage(ConfigUtil.CHAT_POKER_ALL_IN.buildString(gamePlayer.getPlayer().getDisplayName()));

            instance.getEconomy().withdrawPlayer(gamePlayer.getPlayer(), playerBalance);

            playersAllIn.put(gamePlayer, playerBalance);
            playerBets.remove(gamePlayer);

            this.disablePokerButtons(gamePlayer);

            if(playerBalance > currentBet)
                currentBet = playerBalance;
            return true;
        }

        int withdrawAmount = playerBets.get(gamePlayer) == -1 ? amount : amount - playerBets.get(gamePlayer);
        instance.getEconomy().withdrawPlayer(gamePlayer.getPlayer(), withdrawAmount);

        this.playerBets.put(gamePlayer, amount);

        currentBet = amount;
        return true;
    }

    private void takeBlindBets() {
        GamePlayer smallBlindPlayer = teamManager.getTurnPlayer();
        GamePlayer bigBlindPlayer = teamManager.nextTurn();

        this.firstBetIndex = teamManager.getGamePlayers().indexOf(smallBlindPlayer);

        this.placeBet(smallBlindPlayer, BIG_BLIND/2);
        this.sendGameMessage(ConfigUtil.CHAT_POKER_BET_SMALL_BLIND.buildString(smallBlindPlayer.getPlayer().getDisplayName(), BIG_BLIND/2));

        this.placeBet(bigBlindPlayer, BIG_BLIND);
        this.sendGameMessage(ConfigUtil.CHAT_POKER_BET_BIG_BLIND.buildString(bigBlindPlayer.getPlayer().getDisplayName(), BIG_BLIND));

        this.nextTurn();
    }

    private void sendGameMessage(String message) {
        teamManager.getGamePlayers().forEach((GamePlayer player) -> {
           player.getPlayer().sendMessage(message);
        });
    }

    private void reRenderPokerButtons() {
        for(GamePlayer gamePlayer : playerBets.keySet()) {
            this.renderPokerPlayerButtons(gamePlayer);
        }
    }

    private void renderPokerPlayerButtons(GamePlayer gamePlayer) {
        this.disablePokerButtons(gamePlayer);

        boolean canCheck = playerBets.get(gamePlayer) - currentBet == 0 || currentBet == 0;

        // if its their turn, bet
        if(teamManager.getTurnPlayer().equals(gamePlayer)) {
            this.setPokerButton(playerButtons.get(gamePlayer), "BET", true);
            this.setPokerButton(playerButtons.get(gamePlayer), "CALL", true);
            this.setPokerButton(playerButtons.get(gamePlayer), "FOLD", true);

            if(canCheck) {
                this.setPokerButton(playerButtons.get(gamePlayer), "CALL", "POKER_CHECK");
            }
        }

        this.setPokerButton(playerButtons.get(gamePlayer), "CHECK_FOLD", true);

        this.setPokerButton(playerButtons.get(gamePlayer), "CALL_ANY", true);
    }

    protected void nextTurn() {
        if(playerBets.size() >= 1 && !checkRoundOver()) {
            GamePlayer nextPlayer = teamManager.nextTurn();

            if(!playerBets.containsKey(nextPlayer)) {
                nextTurn();
                return;
            }

            // If they have already selected a button
            if(this.playerSelectedButtons.containsKey(nextPlayer)) {
                this.playPokerMove(nextPlayer, this.playerSelectedButtons.get(nextPlayer).getName());
                this.playerSelectedButtons.remove(nextPlayer);
                return;
            }

            pokerTurnTimer.start(nextPlayer.getPlayer());
        }

        boolean roundOver = checkRoundOver();
        if(roundOver) {
            this.startNextRound();
            return;
        }

        this.reRenderPokerButtons();
    }

    protected void reRenderBoard() {
        mapManager.renderBoard();
    }

    protected void lookAtPlayer(Player player) {
        npcLookAt(player);
    }

    private void setPokerButton(ArrayList<Button> buttons, String buttonName, boolean isToggled) {
        buttons.forEach((Button button) -> {
            if(button.getName().equals(buttonName)) {
                button.getImage().setImage(isToggled ? "POKER_" + buttonName : "POKER_" + buttonName + "_DARK");
            }
        });
    }

    private void setPokerButton(ArrayList<Button> buttons, String buttonName, String imageName) {
        buttons.forEach((Button button) -> {
            if(button.getName().equals(buttonName)) {
                button.getImage().setImage(imageName);
            }
        });
    }

    @Override
    protected void setGameName() {
        this.gameName = "Poker";
    }

    @Override
    protected void setBoardImage() {
        this.gameImage = new GameImage("POKER_BOARD");
    }

    @Override
    protected void clockOutOfTime() {

    }

    @Override
    protected Clock getClock() {
        return null;
    }

    @Override
    protected GameInventory getGameInventory() {
        return new PokerInventory(this);
    }

    @Override
    protected GameStorage getGameStorage() {
        return new PokerStorageType(this);
    }

    @Override
    public ArrayList<String> getTeamNames() {
        return null;
    }

    @Override
    protected GameConfig getGameConfig() {
        return new PokerConfig(this);
    }

    @Override
    public void click(Player player, double[] loc, ItemStack map) {
        GamePlayer gamePlayer = getGamePlayer(player);

        boolean outsideClick = false;
        if(gamePlayer == null) {
            gamePlayer = new GamePlayer(player);
            outsideClick = true;
        } else if(!spotsTaken.containsKey(gamePlayer)) {
            outsideClick = true;
        }

        int[] clickLoc = mapManager.getClickLocation(loc, map);

        Button b = getClickedButton(gamePlayer, clickLoc);
        if(b == null) return;

        if(outsideClick) {
            if(b.getName().startsWith("JOIN_GAME")) {
                String spot = b.getName().substring(b.getName().lastIndexOf('_') + 1);
                int spotNum = Integer.parseInt(spot);

                if(!spotsTaken.containsValue(spotNum)) {
                    this.addPokerPlayer(gamePlayer, spotNum);
                }
            }
            return;
        }

        // Game has not started
        if(playerBets == null) return;

        // If they are out of the round, ignore clicks
        if(!playerBets.containsKey(gamePlayer)) return;

        if(!teamManager.getTurnPlayer().equals(gamePlayer)) {
            this.selectPokerButton(gamePlayer, b);
            return;
        }

        this.playPokerMove(gamePlayer, b.getName());
    }

    private void addPokerPlayer(GamePlayer player, int spot) {
        if (instance.getEconomy().getBalance(player.getPlayer()) < this.BIG_BLIND) {
            player.getPlayer().sendMessage(ConfigUtil.CHAT_POKER_JOIN.toString());
            return;
        }

        player.getPlayer().sendMessage(ConfigUtil.CHAT_POKER_JOIN.toString());

        teamManager.addTeam(player);
        spotsTaken.put(player, spot);

        renderAvailableSpots();

        setPokerPlayerButtons(player, spot);

        // Start game
        if(spotsTaken.size() == 2 && playerBets == null) {
            sendGameMessage(ConfigUtil.CHAT_POKER_GAME_STARTING.toString());

            prepareNextGame();
        }

        mapManager.renderBoard();
    }

    private void playPokerMove(GamePlayer gamePlayer, String move) {
        Player player = gamePlayer.getPlayer();
        switch (move) {
            case "BET": {
                this.handlePokerBet(gamePlayer);
                return;
            }
            case "CALL_ANY":
            case "CALL": {
                if(currentBet > 0) {
                    sendGameMessage(ConfigUtil.CHAT_POKER_PLAYER_CALL.buildString(player.getDisplayName(), currentBet));

                    this.placeBet(gamePlayer, currentBet);
                } else {
                    // Check takes place of call when available
                    this.checkGamePlayer(gamePlayer);
                }
                this.nextTurn();
                break;
            }
            case "CHECK_FOLD": {
                if(playerBets.get(gamePlayer) == currentBet || currentBet == 0) {
                    this.checkGamePlayer(gamePlayer);
                } else {
                    this.foldGamePlayer(gamePlayer);
                }
                this.nextTurn();
                break;
            }
            case "FOLD": {
                this.foldGamePlayer(gamePlayer);
                this.nextTurn();
                break;
            }
        }

        mapManager.renderBoard();
    }

    private void handlePokerBet(GamePlayer gamePlayer) {
        Player player = gamePlayer.getPlayer();
        GameOption betOption = new GameOption("bet", Material.GOLD_INGOT, GameOptionType.COUNT, ConfigUtil.GUI_BET_AMOUNT_LABEL.toString(), currentBet + "", false, Math.max(1, currentBet), RAISE_LIMIT + currentBet);
        new GameNumberInventory(gameInventory).build(player, (s, betAmount) -> {
            if(betAmount > currentBet) {
                int playerBalance = (int) instance.getEconomy().getBalance(gamePlayer.getPlayer());
                if(betAmount >= playerBalance)
                    betAmount = playerBalance;

                sendGameMessage(ConfigUtil.CHAT_POKER_PLAYER_RAISE.buildString(player.getDisplayName(), betAmount));

                this.placeBet(gamePlayer, betAmount);
                this.nextTurn();

                mapManager.renderBoard();
            }
        }, betOption, currentBet);
    }

    private void selectPokerButton(GamePlayer gamePlayer, Button selectedButton) {
        String buttonName = selectedButton.getName();

        // check if which one they select
        if(this.playerSelectedButtons.containsKey(gamePlayer)) {
            if(!selectedButton.getName().equals(this.playerSelectedButtons.get(gamePlayer).getName())) return;

            this.renderPokerPlayerButtons(gamePlayer);
            this.playerSelectedButtons.remove(gamePlayer);

            mapManager.renderBoard(gamePlayer.getPlayer());
            return;
        }

        if(!buttonName.equals("CALL_ANY") && !buttonName.equals("CHECK_FOLD")) return;

        // Set their buttons to black
        this.disablePokerButtons(gamePlayer);

        this.setPokerButton(playerButtons.get(gamePlayer), buttonName, "POKER_" + buttonName + "_SELECTED");

        this.playerSelectedButtons.put(gamePlayer, selectedButton);

        mapManager.renderBoard(gamePlayer.getPlayer());
    }

    private void checkGamePlayer(GamePlayer gamePlayer) {
        sendGameMessage(ConfigUtil.CHAT_POKER_PLAYER_CHECK.buildString(gamePlayer.getPlayer().getDisplayName()));

        if(this.playerBets.get(gamePlayer) < 0)
            this.playerBets.put(gamePlayer, 0);
    }

    protected void foldGamePlayer(GamePlayer gamePlayer) {
        sendGameMessage(ConfigUtil.CHAT_POKER_PLAYER_FOLD.buildString(gamePlayer.getPlayer().getDisplayName()));

        // Add their bet to the pot
        if(this.playerBets.containsKey(gamePlayer) && this.playerBets.get(gamePlayer) > 0) {
            gamePot += this.playerBets.get(gamePlayer);

            CasinoGamesStorageType.updateGameStorage(this, gamePlayer, this.playerBets.get(gamePlayer) * -1);
        }

        this.playerBets.remove(gamePlayer);

        // Set their buttons to black
        if(playerButtons != null && playerButtons.containsKey(gamePlayer))
            this.disablePokerButtons(gamePlayer);
    }

    private void disablePokerButtons(GamePlayer gamePlayer) {
        for(Button button : playerButtons.get(gamePlayer)) {
            button.getImage().setImage("POKER_" + button.getName() + "_DARK");
        }
    }

    private void startNextRound() {
        if(this.playersAllIn.size() > 0) {
            // Find smallest all in
            ArrayList<Integer> allInBets = new ArrayList<>(this.playersAllIn.values());
            allInBets.sort(Comparator.naturalOrder());

            int smallestBet = allInBets.get(0);
            // all players can win roundPot
            int roundPot = smallestBet * (this.playerBets.size() + this.playersAllIn.size());

            SidePot firstPot = new SidePot();
            firstPot.addPotPlayers(new ArrayList<>(this.playerBets.keySet()));
            firstPot.addPotPlayers(new ArrayList<>(this.playersAllIn.keySet()));
            firstPot.setPotAmount(roundPot + gamePot);
            this.sidePots.add(firstPot);

            // other side pots
            if(this.playersAllIn.size() > 1) {
                for(int i = 1; i < this.playersAllIn.size(); i++) {
                    int bet = allInBets.get(i) - allInBets.get(i - 1);
                    int sidePot = bet * ((this.playersAllIn.size() - i) + this.playerBets.size());
                    // can be won by i-playersAllIn.size() + playerBets

                    // if everyone went all in and is last side pot, return money (no one can match)
                    if(playerBets.size() == 0 && i == this.playersAllIn.size() - 1) {
                        if(bet > 0) {
                            GamePlayer returnPlayer = getLastElement(this.playersAllIn.keySet());
                            returnPlayer.getPlayer().sendMessage(ConfigUtil.CHAT_POKER_MONEY_BACK.buildString(bet + ""));
                            instance.getEconomy().depositPlayer(returnPlayer.getPlayer(), bet);
                        }
                    } else {
                        SidePot newSidePot = new SidePot();
                        newSidePot.addPotPlayers(new ArrayList<>(this.playerBets.keySet()));
                        newSidePot.setPotAmount(sidePot);
                        for(int j = i; j < this.playersAllIn.size(); j++) {
                            newSidePot.addPotPlayer(new ArrayList<>(this.playersAllIn.keySet()).get(j));
                        }

                        this.sidePots.add(newSidePot);
                    }
                }
            }

            // new side pot with playerBets (becomes new game pot)
            gamePot = (currentBet - allInBets.get(allInBets.size() - 1)) * this.playerBets.size();
            this.playersAllIn.clear();
        } else {
            int roundPot = 0;
            for(int bet : this.playerBets.values()) {
                if(bet >= 0)
                    roundPot += bet;
            }

            this.gamePot += roundPot;
        }

        // If its the last betting round or everyone has folded
        if((this.flopCards.getAmountOfCards() == 5 || playerBets.size() <= 1)) {
            // End game
            this.endRound(false);
            return;
        }

        // Draw flop card(s)
        int drawFlopNum = (this.flopCards.getAmountOfCards() == 0) ? 3 : 1;
        ArrayList<Card> flopCards = this.pokerDeck.draw(drawFlopNum);
        this.flopCards.addCards(flopCards);

        this.setFlopCards();

        // Find player
        GamePlayer startingPlayer = getStartingPlayer();
        teamManager.setTurn(startingPlayer);
        pokerTurnTimer.start(startingPlayer.getPlayer());

        this.currentBet = 0;
        this.playerBets.replaceAll((p, v) -> -1);
        this.playerSelectedButtons.clear();
        this.reRenderPokerButtons();

        sendGameMessage(ConfigUtil.CHAT_POKER_NEXT_ROUND.buildString(gamePot + ""));
        for(SidePot sidePot : this.sidePots) {
            sendGameMessage(ConfigUtil.CHAT_POKER_SIDE_POT.buildString(sidePot.getPotAmount(),  sidePot.getPotPlayers().size()));
        }
        sendGameMessage(ConfigUtil.CHAT_POKER_FLOP.toString());
        for(Card card : this.flopCards.getCards()) {
            sendGameMessage(card.getName());
        }
    }

    private GamePlayer getStartingPlayer() {
        int playerIndex = firstBetIndex >= teamManager.getGamePlayers().size() ? 0 : firstBetIndex;
        while (!playerBets.containsKey(teamManager.getGamePlayers().get(playerIndex))) {
            playerIndex++;
            if(playerIndex >= teamManager.getGamePlayers().size())
                playerIndex = 0;
        }
        return teamManager.getGamePlayers().get(playerIndex);
    }

    private void endRound(boolean endGame) {
        sendGameMessage(ConfigUtil.CHAT_POKER_GAME_OVER.toString());

        // Draw the remaining flop cards
        for(int i = this.flopCards.getAmountOfCards(); i < 5; i++) {
            Card flopCard = this.pokerDeck.draw();
            this.flopCards.addCard(flopCard);
        }

        this.setFlopCards();

        HashMap<GamePlayer, Integer> finalPlayers = new HashMap<>();

        // Only playerBets can win gamePot
        if(playerBets.size() > 0) {
            GamePlayer winner = getBestHand(new ArrayList<>(playerBets.keySet()));
            int winAmount = gamePot;

            sendGameMessage(ConfigUtil.CHAT_POKER_WIN_POT.buildString(winner.getPlayer().getDisplayName(), winAmount));
            finalPlayers.put(winner, winAmount);
        }

        for(SidePot sidePot : this.sidePots) {
            GamePlayer winner = getBestHand(sidePot.getPotPlayers());
            int winAmount = sidePot.getPotAmount();

            sendGameMessage(ConfigUtil.CHAT_POKER_WIN_SIDE_POT.buildString(winner.getPlayer().getDisplayName(), winAmount));
            finalPlayers.put(winner, winAmount);
        }

        // Send money to players
        for(GamePlayer inGamePlayer : finalPlayers.keySet()) {
            instance.getEconomy().depositPlayer(inGamePlayer.getPlayer(), finalPlayers.get(inGamePlayer));
            CasinoGamesStorageType.updateGameStorage(this, inGamePlayer, finalPlayers.get(inGamePlayer));
        }

        // Everyone that lost money
        for(GamePlayer gamePlayer : playerBets.keySet()) {
            if(!finalPlayers.containsKey(gamePlayer)) {
                CasinoGamesStorageType.updateGameStorage(this, gamePlayer, playerBets.get(gamePlayer) * -1);
            }
        }

        // Show cards
        for(GamePlayer gamePlayer : playerHandButtons.keySet()) {
            playerHandButtons.get(gamePlayer).setVisibleForAll(true);
        }

        mapManager.renderBoard();

        if(!endGame) {
            sendGameMessage(ConfigUtil.CHAT_POKER_NEXT_GAME.toString());

            prepareNextGame();
        }
    }

    private void prepareNextGame() {
        playerBets = null;

        if (pokerTimer != null)
            pokerTimer.cancel();

        if(pokerTurnTimer != null)
            pokerTurnTimer.cancel();

        pokerTimer = new PokerGameTimer(this);
        pokerTimer.runTaskTimer(BoardGames.getInstance(), 5, 5);
    }

    private boolean canStartNextGame() {
        // Make sure players still have enough money
        for(GamePlayer gamePlayer : teamManager.getGamePlayers()) {
            double playerBalance = instance.getEconomy().getBalance(gamePlayer.getPlayer());
            if(playerBalance < this.BIG_BLIND) {
                sendGameMessage(ConfigUtil.CHAT_POKER_PLAYER_REMOVE.buildString(gamePlayer.getPlayer().getDisplayName()));
                teamManager.removeTeamByPlayer(gamePlayer.getPlayer());
            }
        }

        if(teamManager.getGamePlayers().size() <= 1) {
            sendGameMessage(ConfigUtil.CHAT_POKER_NOT_ENOUGH_PLAYERS.toString());
            resetGame();
            return false;
        }

        // Open up spots
        spotsTaken.keySet().removeIf(gamePlayer -> !teamManager.getGamePlayers().contains(gamePlayer));

        int playerIndex = firstBetIndex + 1 >= teamManager.getGamePlayers().size() ? 0 : firstBetIndex + 1;
        teamManager.setTurn(teamManager.getGamePlayers().get(playerIndex));

        if (pokerTurnTimer != null)
            pokerTurnTimer.cancel();

        pokerTurnTimer = new PokerTurnTimer(this);
        pokerTurnTimer.runTaskTimer(BoardGames.getInstance(), 5, 5);
        return true;
    }

    private boolean checkRoundOver() {
        // Everyone has folded
        if(playerBets.size() <= 1 && this.playersAllIn.size() == 0) return true;

        // Everyone has gone all in
        if(playerBets.size() == 0) return true;

        boolean roundOver = true;
        for(int playerBet : playerBets.values()) {
            if(playerBet != currentBet) {
                roundOver = false;
                break;
            }
        }

        return roundOver;
    }

    private GamePlayer getBestHand(ArrayList<GamePlayer> gamePlayers) {
        HashMap<Hand, GamePlayer> potHands = new HashMap<>();

        for(GamePlayer sidePotPlayer : gamePlayers) {
            potHands.put(playerHands.get(sidePotPlayer), sidePotPlayer);
        }

        Hand winningHand = Hand.getBestHand(new ArrayList<>(potHands.keySet()), flopCards.getCards());

        return potHands.get(winningHand);
    }

    @Override
    public void exitPlayer(Player player) {
        // Game has not "started"
        if(playerBets == null && spotsTaken.size() <= 2) {
            sendGameMessage(ConfigUtil.CHAT_POKER_NOT_ENOUGH_PLAYERS.toString());

            // Reset the board
            resetGame();
            return;
        }

        // Render in available spots
        spotsTaken.remove(teamManager.getGamePlayer(player));
        this.renderAvailableSpots();

        if(playerBets != null)
            this.foldGamePlayer(teamManager.getGamePlayer(player));

        super.exitPlayer(player);

        if (!this.isIngame()) {
            return;
        }

        if(playerBets == null) {
            mapManager.renderBoard();
            return;
        }

        boolean roundOver = checkRoundOver();
        if(roundOver) {
            this.startNextRound();
            return;
        }

        while (!playerBets.containsKey(teamManager.getTurnPlayer())) {
            teamManager.nextTurn();
        }

        this.reRenderPokerButtons();

        mapManager.renderBoard();
    }

    @Override
    public void endGame(GamePlayer gamePlayer) {
        this.endRound(true);

        resetGame();
    }


    private void resetGame() {
        // Remove join buttons, game over
        buttons.removeIf(button -> button.getName().startsWith("JOIN_GAME"));

        if (pokerTimer != null)
            pokerTimer.cancel();

        if (pokerTurnTimer != null)
            pokerTurnTimer.cancel();

        playerBets = null;
        clearGamePlayers();
        super.endGame(null);

        mapManager.renderBoard();
    }

    @Override
    protected void gamePlayerOutOfTime(GamePlayer gamePlayer) {

    }

    @Override
    public ItemStack getBoardItem() {
        return new BoardItem(gameName, new ItemStack(Material.ACACIA_TRAPDOOR, 1));
    }

    @Override
    public boolean allowOutsideClicks() {
        return true;
    }

    private <T> T getLastElement(final Iterable<T> elements) {
        T lastElement = null;

        for (T element : elements) {
            lastElement = element;
        }

        return lastElement;
    }

    @Override
    public GameNPC getGameNPC() {
        return new PokerNPC(new double[] { 0.5, -1, 1.5 });
    }
}
