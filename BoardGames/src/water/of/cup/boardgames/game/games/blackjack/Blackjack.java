package water.of.cup.boardgames.game.games.blackjack;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import water.of.cup.boardgames.BoardGames;
import water.of.cup.boardgames.game.*;
import water.of.cup.boardgames.game.games.gameutils.EconomyUtils;
import water.of.cup.boardgames.game.games.gameutils.cards.Card;
import water.of.cup.boardgames.game.games.gameutils.cards.Deck;
import water.of.cup.boardgames.game.games.gameutils.cards.Hand;
import water.of.cup.boardgames.game.inventories.GameInventory;
import water.of.cup.boardgames.game.inventories.GameOption;
import water.of.cup.boardgames.game.inventories.GameOptionType;
import water.of.cup.boardgames.game.inventories.number.GameNumberInventory;
import water.of.cup.boardgames.game.npcs.GameNPC;
import water.of.cup.boardgames.game.storage.GameStorage;
import water.of.cup.boardgames.config.ConfigUtil;

import java.util.ArrayList;

public class Blackjack extends Game {
	boolean inRound;

	Deck deck;
	ArrayList<Hand>[] playerHands;
	int[] activePlayerHands;
	int[] playerBets;
	ArrayList<ArrayList<Button>>[] cardButtons;
	Hand dealerHand;
	ArrayList<Button> dealerCardButtons;
	boolean[] insuredPlayers;
	ArrayList<Boolean>[] doubledPlayerHands;
	GamePlayer[] gamePlayerAtLocation;

	Button[] chipHolderButtons;
	Button[] joinButtons;
	Button currentHandButton;

	// action buttons:
	Button[] hitButtons;
	Button[] standButtons;
	Button[] doubleButtons;
	Button[] splitButtons;
	Button[] insuranceButtons;

	// runnables:
	BlackjackNextGameTimer nextGameTimer;
	BlackjackTurnTimer turnTimer;

	@SuppressWarnings("unchecked")
	public Blackjack(int rotation) {
		super(rotation);
		playerHands = new ArrayList[7];
		dealerHand = new Hand();
		cardButtons = new ArrayList[7];
		dealerCardButtons = new ArrayList<Button>();
		gamePlayerAtLocation = new GamePlayer[7];
		playerBets = new int[7];
		inRound = false;

		createGameButtons();
		prepareNextRound();
	}

	private void setGameButtonVisiblePlayers() {
		for (int n = 0; n < 7; n++) {
			hitButtons[n].clearVisablePlayers();
			standButtons[n].clearVisablePlayers();
			doubleButtons[n].clearVisablePlayers();
			splitButtons[n].clearVisablePlayers();
			insuranceButtons[n].clearVisablePlayers();
			if (gamePlayerAtLocation[n] != null) {
				hitButtons[n].addVisiblePlayer(gamePlayerAtLocation[n]);
				standButtons[n].addVisiblePlayer(gamePlayerAtLocation[n]);
				doubleButtons[n].addVisiblePlayer(gamePlayerAtLocation[n]);
				splitButtons[n].addVisiblePlayer(gamePlayerAtLocation[n]);
				insuranceButtons[n].addVisiblePlayer(gamePlayerAtLocation[n]);
			}
		}
	}

	private void createGameButtons() {
		hitButtons = new Button[7];
		standButtons = new Button[7];
		doubleButtons = new Button[7];
		chipHolderButtons = new Button[7];
		splitButtons = new Button[7];
		insuranceButtons = new Button[7];
		joinButtons = new Button[7];

		currentHandButton = new Button(this, "BLACKJACK_CURRENTHAND", new int[] { 0, 0 }, 0, "double");
		currentHandButton.setVisibleForAll(false);
		buttons.add(currentHandButton);

		for (int n = 0; n < 7; n++) {
			int x = 0;
			int y = 0;
			int rotation = 0;
			if (n < 2) {
				y = 128 * (2 - n);
				rotation = 1;
			} else if (n > 4) {
				y = 128 * (n - 4);
				x = 128 * 4;
				rotation = 3;
			} else {
				x = 128 * (n - 1);
				rotation = 2;
			}
			int[] hitLoc = new int[] { 128 - 10, 15 };
			int[] standLoc = new int[] { 128 - 40, 15 };
			int[] doubleLoc = new int[] { 128 - 80, 15 };
			int[] chipHolderLoc = new int[] { 128 - 54, 60 };
			int[] joinLoc = new int[] { 128 - 54 + 1, 60 + 1 };
			int[] splitLoc = new int[] { 128 - 80, 25 };
			int[] insurancLoc = new int[] { 128 - 10, 25 };

			double[] rotater = new double[] { 63.5, 63.5 };
			if (rotation != 2)
				for (int i = rotation; i < 4; i++) {
					hitLoc = MathUtils.rotatePointAroundPoint90Degrees(rotater, hitLoc);
					standLoc = MathUtils.rotatePointAroundPoint90Degrees(rotater, standLoc);
					doubleLoc = MathUtils.rotatePointAroundPoint90Degrees(rotater, doubleLoc);
					chipHolderLoc = MathUtils.rotatePointAroundPoint90Degrees(rotater, chipHolderLoc);
					splitLoc = MathUtils.rotatePointAroundPoint90Degrees(rotater, splitLoc);
					insurancLoc = MathUtils.rotatePointAroundPoint90Degrees(rotater, insurancLoc);
					joinLoc = MathUtils.rotatePointAroundPoint90Degrees(rotater, joinLoc);
				}

			hitButtons[n] = new Button(this, "BLACKJACK_HIT", new int[] { hitLoc[0] + x, hitLoc[1] + y }, rotation,
					"hit");
			hitButtons[n].setVisibleForAll(false);
			hitButtons[n].changeLocationByRotation();
			hitButtons[n].setClickable(true);
			buttons.add(hitButtons[n]);

			standButtons[n] = new Button(this, "BLACKJACK_STAND", new int[] { standLoc[0] + x, standLoc[1] + y },
					rotation, "stand");
			standButtons[n].changeLocationByRotation();
			standButtons[n].setVisibleForAll(false);
			standButtons[n].setClickable(true);
			buttons.add(standButtons[n]);

			doubleButtons[n] = new Button(this, "BLACKJACK_X2", new int[] { doubleLoc[0] + x, doubleLoc[1] + y },
					rotation, "double");
			doubleButtons[n].changeLocationByRotation();
			doubleButtons[n].setVisibleForAll(false);
			doubleButtons[n].setClickable(true);
			buttons.add(doubleButtons[n]);

			splitButtons[n] = new Button(this, "BLACKJACK_SPLIT", new int[] { splitLoc[0] + x, splitLoc[1] + y },
					rotation, "split");
			splitButtons[n].changeLocationByRotation();
			splitButtons[n].setVisibleForAll(false);
			splitButtons[n].setClickable(true);
			buttons.add(splitButtons[n]);

			insuranceButtons[n] = new Button(this, "BLACKJACK_INSURANCE",
					new int[] { insurancLoc[0] + x, insurancLoc[1] + y }, rotation, "insurance");
			insuranceButtons[n].changeLocationByRotation();
			insuranceButtons[n].setVisibleForAll(false);
			insuranceButtons[n].setClickable(true);
			buttons.add(insuranceButtons[n]);

			chipHolderButtons[n] = new Button(this, "BLACKJACK_CHIPHOLDER",
					new int[] { chipHolderLoc[0] + x, chipHolderLoc[1] + y }, rotation, "chip");
			chipHolderButtons[n].setVisibleForAll(true);
			chipHolderButtons[n].changeLocationByRotation();
			buttons.add(chipHolderButtons[n]);

			joinButtons[n] = new Button(this, "BLACKJACK_JOIN", new int[] { joinLoc[0] + x, joinLoc[1] + y }, rotation,
					"join");
			joinButtons[n].setVisibleForAll(true);
			joinButtons[n].setClickable(true);
			joinButtons[n].changeLocationByRotation();
			buttons.add(joinButtons[n]);
		}
	}

	private void toggleJoinButtons() {
		for (int n = 0; n < 7; n++)
			joinButtons[n].setVisibleForAll(gamePlayerAtLocation[n] == null);
	}

	private void prepareNextRound() {
		resetPlayersBets();
		inRound = false;
		takeBets();

		if (nextGameTimer != null)
			nextGameTimer.cancel();

		nextGameTimer = new BlackjackNextGameTimer(this);
		nextGameTimer.runTaskTimer(BoardGames.getInstance(), 5, 5);

		if (turnTimer != null)
			turnTimer.cancel();
	}

	@SuppressWarnings("unchecked")
	protected void startRound() {
		if (nextGameTimer != null)
			nextGameTimer.cancel();

		inRound = true;
		resetCardButtons();
		setGameButtonVisiblePlayers();

		insuredPlayers = new boolean[7];
		doubledPlayerHands = new ArrayList[7];

		deck = new Deck(1);

		initialDeal();

		updatePlayerHandButtons();
		updateDealerHandButtons(true);

		if (turnTimer != null)
			turnTimer.cancel();

		turnTimer = new BlackjackTurnTimer(this);
		turnTimer.runTaskTimer(BoardGames.getInstance(), 5, 5);

		nextTurn(-1);
		toggleActionButtons();

		mapManager.renderBoard();
	}

	@Override
	public boolean allowOutsideClicks() {
		return true;
	}

	@Override
	protected void startGame() {
		clearGamePlayers();
		spawnNPC();
		super.setInGame();
		this.prepareNextRound();
	}

	private void dealerSendMessage(Player player, String message) {
		player.sendMessage(ConfigUtil.CHAT_BLACKJACK_DEALER.toString() + message);
	}

	private void dealerSendMessage(int n, String message) {
		Player player = gamePlayerAtLocation[n].getPlayer();
		if (player == null)
			return;
		dealerSendMessage(player, message);
	}

	private void dealerSendAll(String message) {
		for (GamePlayer player : teamManager.getGamePlayers())
			player.getPlayer().sendMessage(ConfigUtil.CHAT_BLACKJACK_DEALER.toString() + message);
	}

	private void takeBets() {
		for (GamePlayer gamePlayer : teamManager.getGamePlayers()) {
			// askForBet(gamePlayer);
			dealerSendMessage(gamePlayer.getPlayer(), ConfigUtil.CHAT_BLACKJACK_PLACEBET.toString());
		}
	}

	private void askForBet(GamePlayer gamePlayer) {
		Player player = gamePlayer.getPlayer();
		GameOption betOption = new GameOption("bet", Material.GOLD_INGOT, GameOptionType.COUNT, ConfigUtil.GUI_BET_AMOUNT_LABEL.toString(), "1",
				false);
		new GameNumberInventory(gameInventory).build(player, (s, betAmount) -> {
			takeBet(gamePlayer, betAmount);
		}, betOption, 1);
	}

	private void takeBet(GamePlayer gamePlayer, int amount) {
		if (!EconomyUtils.playerTakeMoney(gamePlayer.getPlayer(), amount)) {
			dealerSendMessage(gamePlayer.getPlayer(), ConfigUtil.CHAT_BLACKJACK_NOMONEY.toString());
			return;
		}
		int n = this.getPlayerBoardLocation(gamePlayer);
		playerBets[n] = amount;
		dealerSendMessage(gamePlayer.getPlayer(), ConfigUtil.CHAT_BLACKJACK_BETAMOUNT.buildString(amount + ""));
	}

	private void resetPlayersBets() {
		playerBets = new int[7];
	}

	private void toggleActionButtons() {
		boolean canInsure = (dealerHand.getCards().get(0).getBlackJackValue(false) == 1);

		for (int n = 0; n < 7; n++) {
			if (gamePlayerAtLocation[n] == null || playerBets[n] == 0)
				continue;

			Player player = gamePlayerAtLocation[n].getPlayer();

			boolean turn = inRound && gamePlayerAtLocation[n] == teamManager.getTurnPlayer();

			Hand currentHand = playerHands[n].get(activePlayerHands[n]);
			ArrayList<Card> currentCards = currentHand.getCards();

			hitButtons[n].setVisible(turn && currentHand.getHandBlackJackTotal() < 21);

			standButtons[n].setVisible(turn && currentHand.getHandBlackJackTotal() <= 21);

			doubleButtons[n].setVisible(
					turn && currentHand.getAmountOfCards() == 2 && !doubledPlayerHands[n].get(activePlayerHands[n]));

			splitButtons[n].setVisible(turn && currentCards.size() == 2
					&& currentCards.get(0).getBlackJackValue(true) == currentCards.get(1).getBlackJackValue(true)
					&& EconomyUtils.playerHasFunds(player, playerBets[n]));

			insuranceButtons[n].setVisible(turn && canInsure && !insuredPlayers[n]
					&& EconomyUtils.playerHasFunds(player, playerBets[n] / 2.0));
		}
	}

	@SuppressWarnings("unchecked")
	private void initialDeal() {
		activePlayerHands = new int[7];
		doubledPlayerHands = new ArrayList[7];
		playerHands = new ArrayList[7];
		dealerHand = new Hand();
		// int n = 0;
		for (GamePlayer gamePlayer : teamManager.getGamePlayers()) {
			if (gamePlayer == null)
				continue;
			int n = this.getPlayerBoardLocation(gamePlayer);
			if (playerBets[n] == 0)
				continue;
			playerHands[n] = new ArrayList<Hand>();
			playerHands[n].add(new Hand());
			playerHands[n].get(0).draw(deck, 2);
			activePlayerHands[n] = 0;
			doubledPlayerHands[n] = new ArrayList<Boolean>();
			doubledPlayerHands[n].add(false);
		}
		dealerHand.draw(deck, 2);
		String dealerCardName = dealerHand.getCards().get(0).getShortName();
		dealerSendAll(ConfigUtil.CHAT_BLACKJACK_DRAWCARD.toString() + (dealerCardName.charAt(0) == 'A' ? "n " : " ") + dealerCardName + ".");
	}

	private void updateDealerHandButtons(boolean cardDown) {
		// if card down then only one card is shown to players

		int cardNum = -1;
		int rotation = 2;
		int x = 2 * 128 + 64 + 7;
		int y = 2 * 128 - 64;

		for (Card card : dealerHand.getCards()) {
			cardNum++;
			if (dealerCardButtons.size() > cardNum)
				continue;
			if (cardDown && cardNum == 1)
				break;

			// set button location
			int[] cardLoc = new int[] { -cardNum * 14, cardNum * 20 };
			GameImage cardImage = card.getGameImage();
			cardImage.resize(2);

			Button cardButton = new Button(this, cardImage, new int[] { cardLoc[0] + x, cardLoc[1] + y }, rotation,
					"dealercard");
			cardButton.setVisibleForAll(true);
			cardButton.changeLocationByRotation();
			dealerCardButtons.add(cardButton);
			buttons.add(cardButton);

		}

	}

	private void updatePlayerHandButtons() {

		for (int n = 0; n < 7; n++) {
			if (gamePlayerAtLocation[n] == null)
				continue;
			if (playerBets[n] == 0)
				continue;

			int x = 0;
			int y = 0;
			int rotation = 0;
			if (n < 2) {
				y = 128 * (2 - n);
				rotation = 1;
			} else if (n > 4) {
				y = 128 * (n - 4);
				x = 128 * 4;
				rotation = 3;
			} else {
				x = 128 * (n - 1);
				rotation = 2;
			}

			int amountOfHands = playerHands[n].size();

			if (cardButtons[n] == null) {
				cardButtons[n] = new ArrayList<ArrayList<Button>>();
			}

			// if amount of hands > amount of handButtons reset all cardButtons
			if (amountOfHands > cardButtons[n].size())
				for (ArrayList<Button> cardsButtons : cardButtons[n]) {
					buttons.removeAll(cardsButtons);
					cardsButtons.clear();
				}

			int handNum = 0;
			for (Hand hand : playerHands[n]) {

				if (cardButtons[n].size() <= handNum || cardButtons[n].get(handNum) == null)
					cardButtons[n].add(new ArrayList<Button>());

				int cardNum = -1;
				for (Card card : hand.getCards()) {
					cardNum++;
					if (cardButtons[n].get(handNum).size() > cardNum)
						continue;

					// set button location
					int[] cardLoc = new int[] { 64 + 7 - cardNum * 7 + (amountOfHands - 1) * 24 / 2 - handNum * 24,
							94 + cardNum * 10 };

					double[] rotater = new double[] { 63.5, 63.5 };
					if (rotation != 2)
						for (int i = rotation; i < 4; i++) {
							cardLoc = MathUtils.rotatePointAroundPoint90Degrees(rotater, cardLoc);
						}

					Button cardButton = new Button(this, card.getGameImage(),
							new int[] { cardLoc[0] + x, cardLoc[1] + y }, rotation, "card");
					cardButton.setVisibleForAll(true);
					cardButton.changeLocationByRotation();
					cardButtons[n].get(handNum).add(cardButton);
					buttons.add(cardButton);
				}
				handNum++;
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void resetCardButtons() {
		for (ArrayList<ArrayList<Button>> line : cardButtons)
			if (line != null)
				for (ArrayList<Button> handsButtons : line)
					buttons.removeAll(handsButtons);
		cardButtons = new ArrayList[7];

		buttons.removeAll(dealerCardButtons);
		dealerCardButtons = new ArrayList<Button>();
	}

	@Override
	protected void setMapInformation(int i) {
		this.mapStructure = new int[][] { { 0, 2, 3, 4, 0 }, { 6, 7, 8, 9, 10 }, { 11, 12, 13, 14, 15 } };
		this.placedMapVal = 13;
	}

	@Override
	protected void setGameName() {
		this.gameName = "Blackjack";

	}

	@Override
	protected void setBoardImage() {
		this.gameImage = new GameImage("BLACKJACK_BOARD");
	}

	@Override
	protected void clockOutOfTime() {
		// TODO Auto-generated method stub

	}

	@Override
	protected Clock getClock() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected GameInventory getGameInventory() {
		return new BlackjackInventory(this);
	}

	@Override
	protected GameStorage getGameStorage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<String> getTeamNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected GameConfig getGameConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	private int getPlayerBoardLocation(GamePlayer gamePlayer) {
		if (gamePlayer == null)
			return -1;
		for (int n = 0; n < 7; n++) {
			if (gamePlayerAtLocation[n] == gamePlayer)
				return n;
		}
		return -1;
	}

	@Override
	public void click(Player player, double[] loc, ItemStack map) {
		GamePlayer gamePlayer = getGamePlayer(player);
		// player.sendMessage("clicked");

		int n = getPlayerBoardLocation(gamePlayer);

		int[] clickLoc = mapManager.getClickLocation(loc, map);

		// ask for bet if not in round and player has not bet while in game
		if (gamePlayer != null && !this.inRound && playerBets[n] == 0) {
			askForBet(gamePlayer);
			return;
		}

		Button b = getClickedButton(gamePlayer, clickLoc);
		if (b == null)
			return;

		// player.sendMessage("clicked: " + b.getName());

		switch (b.getName()) {
		case "hit":
			hit(n);
			break;
		case "double":
			doubleDown(n);
			break;
		case "split":
			split(n);
			break;
		case "stand":
			stand(n);
			break;
		case "insurance":
			insurance(n);
			break;
		case "join":
			int c = 0;
			for (Button button : joinButtons) {
				if (button == b)
					break;
				c++;
			}
			join(player, c);
			return;
		}

		updatePlayerHandButtons();
		toggleActionButtons();
		mapManager.renderBoard();
	}

	private void nextHand() {
		int n = getPlayerBoardLocation(teamManager.getTurnPlayer());
		if (activePlayerHands[n] < playerHands[n].size() - 1) {
			activePlayerHands[n]++;
			moveCurrentHandButton();
		} else
			nextTurn();

	}

	private void nextTurn(int n) {
		do {
			n++;
		} while (n < 7 && (gamePlayerAtLocation[n] == null || (playerBets[n] == 0)));

		if (n < 7) {
			teamManager.setTurn(gamePlayerAtLocation[n]);
			moveCurrentHandButton();
			this.toggleActionButtons();
			this.dealerSendMessage(n, ConfigUtil.CHAT_BLACKJACK_TURN.toString());
			turnTimer.start(gamePlayerAtLocation[n].getPlayer());
			mapManager.renderBoard();
			npcLookAt(gamePlayerAtLocation[n].getPlayer());
		} else {
			currentHandButton.setVisibleForAll(false);
			completeDealersTurn();
		}

	}

	protected void nextTurn() {
		int n = getPlayerBoardLocation(teamManager.getTurnPlayer());
		nextTurn(n);
	}

	private void moveCurrentHandButton() {
		int n = getPlayerBoardLocation(teamManager.getTurnPlayer());
		Button cardButton = cardButtons[n].get(activePlayerHands[n]).get(0);
		int[] cardLoc = cardButton.getLocation();
		int rotation = cardButton.getRotation();
		int[] locChange = new int[] { 0, 0 };

//		double[] rotater = new double[] { 0, 0 };
//		if (rotation != 2)
//			for (int i = rotation; i < 4; i++)
//				locChange = MathUtils.rotatePointAroundPoint90Degrees(rotater, locChange);
		switch (rotation) {
		case 1:
			locChange[0]--;
			locChange[1]++;
			break;
		case 2:
			locChange[0] += 15 - 2;
			locChange[1]--;
			break;
		case 3:
			locChange[0] += 21;
			locChange[1] += 15 - 2;
			break;
		}

		int[] loc = new int[] { cardLoc[0] + locChange[0], cardLoc[1] + locChange[1] };
		int[] bloc = currentHandButton.getLocation();
		bloc[0] = loc[0];
		bloc[1] = loc[1];
		currentHandButton.setRotation(rotation);
		currentHandButton.changeLocationByRotation();
		currentHandButton.setVisibleForAll(true);

	}

	private void completeDealersTurn() {
		String dealerCardName = dealerHand.getCards().get(1).getShortName();
		dealerSendAll("I flipped a" + (dealerCardName.charAt(0) == 'A' ? "n " : " ") + dealerCardName + ".");

		while (dealerHand.getHandBlackJackTotal() < 17) {
			dealerHand.draw(deck);
			dealerCardName = dealerHand.getCards().get(dealerHand.getAmountOfCards() - 1).getShortName();
			dealerSendAll("I drew a" + (dealerCardName.charAt(0) == 'A' ? "n " : " ") + dealerCardName + ".");
		}
		int dealerScore = dealerHand.getHandBlackJackTotal();
		boolean dealerBlackjack = dealerScore == 21;
		boolean dealerNaturalBlackjack = dealerBlackjack && dealerHand.getAmountOfCards() == 2;

		// loop through players decks
		for (int n = 0; n < 7; n++) {
			if (gamePlayerAtLocation[n] == null)

				continue;
			if (playerBets[n] == 0)
				continue;

			double payout = 0;

			int amountOfHands = playerHands[n].size();
			for (int h = 0; h < amountOfHands; h++) {
				Hand hand = playerHands[n].get(h);
				int score = hand.getHandBlackJackTotal();
				boolean blackjack = false;
				boolean naturalBlackjack = false;
				boolean doubleDown = doubledPlayerHands[n].get(h);

				if (score == 21 && !(amountOfHands == 2 && hand.getCards().get(0).getBlackJackValue(false) == 1)) {
					blackjack = true;
					if (hand.getAmountOfCards() == 2)
						naturalBlackjack = true;
				}

				if (score > 21) {
					this.dealerSendMessage(n, ConfigUtil.CHAT_BLACKJACK_BUST.buildString(score + ""));
					continue;
				}

				if (dealerNaturalBlackjack && !naturalBlackjack) {
					// dealer wins with naturalBlackJack
					this.dealerSendMessage(n, ConfigUtil.CHAT_BLACKJACK_DEALERWIN.toString());
					continue;
				}

				if (dealerScore > 21 || score > dealerScore) {
					if (dealerScore <= 21)
						this.dealerSendMessage(n, ConfigUtil.CHAT_BLACKJACK_PLAYERWIN.buildString(score, dealerScore));
					else
						this.dealerSendMessage(n, ConfigUtil.CHAT_BLACKJACK_DEALERBUST.toString());

					double won = 2;

					if (blackjack)
						won += .5;

					if (doubleDown)
						won *= 2;

					payout += playerBets[n] * won;

					continue;
				}

				if (dealerNaturalBlackjack && naturalBlackjack || dealerScore == score) {
					// push
					this.dealerSendMessage(n, ConfigUtil.CHAT_BLACKJACK_TIE.buildString(score, dealerScore));
					double won = 1;
					if (doubleDown)
						won *= 2;

					payout += playerBets[n] * won;
					continue;
				}

				if (dealerScore > score) {
					// dealer wins
					this.dealerSendMessage(n, ConfigUtil.CHAT_BLACKJACK_PLAYERLOSE.buildString(score, dealerScore));

					continue;
				}
			}
			EconomyUtils.playerGiveMoney(gamePlayerAtLocation[n].getPlayer(), payout);
			this.dealerSendMessage(n, ConfigUtil.CHAT_BLACKJACK_PLAYERWINBET.buildString(payout + ""));

		}
		this.inRound = false;
		updatePlayerHandButtons();
		this.toggleActionButtons();
		this.updateDealerHandButtons(false);
		mapManager.renderBoard();
		this.prepareNextRound();

	}

	private void hit(int n) {
		Hand hand = playerHands[n].get(activePlayerHands[n]);
		hand.draw(deck);
		int total = hand.getHandBlackJackTotal();
		if (total < 21)
			dealerSendMessage(n, ConfigUtil.CHAT_BLACKJACK_PLAYERHIT.buildString(total + ""));
		else if (total == 21) {
			dealerSendMessage(n, ConfigUtil.CHAT_BLACKJACK_BLACKJACK.toString());
			nextHand();
		} else {
			dealerSendMessage(n, ConfigUtil.CHAT_BLACKJACK_PLAYERBUST.buildString(total + ""));
			nextHand();
		}
	}

	private void doubleDown(int n) {
		if (!EconomyUtils.playerTakeMoney(this.gamePlayerAtLocation[n].getPlayer(), playerBets[n])) {
			dealerSendMessage(n, ConfigUtil.CHAT_BLACKJACK_NOMONEYDOUBLE.toString());
			return;
		}

		doubledPlayerHands[n].set(activePlayerHands[n], true);
		dealerSendMessage(n, ConfigUtil.CHAT_BLACKJACK_DOUBLEDOWN.buildString((playerBets[n] * 2) + ""));
		Hand currentHand = playerHands[n].get(activePlayerHands[n]);
		hit(n);

		// check if hit resulted in a hand change, had change if not
		n = getPlayerBoardLocation(teamManager.getTurnPlayer());
		if (currentHand == playerHands[n].get(activePlayerHands[n]))
			nextHand();

	}

	@SuppressWarnings("unchecked")
	private void split(int n) {
		if (!EconomyUtils.playerTakeMoney(this.gamePlayerAtLocation[n].getPlayer(), playerBets[n])) {
			dealerSendMessage(n, ConfigUtil.CHAT_BLACKJACK_NOMONEYSPLIT.toString());
			return;
		}

		Hand hand = playerHands[n].get(activePlayerHands[n]);
		ArrayList<Card> cards = (ArrayList<Card>) hand.getCards().clone();
		hand.clear();
		hand.addCard(cards.get(0));
		hand.draw(deck);
		Hand newHand = new Hand();
		newHand.addCard(cards.get(1));
		newHand.draw(deck);

		playerHands[n].add(newHand);
		doubledPlayerHands[n].add(false);

		if (cards.get(0).getBlackJackValue(false) == 1) {
			dealerSendMessage(n, ConfigUtil.CHAT_BLACKJACK_SPLITACE.toString());
			nextTurn();
		} else {
			this.updatePlayerHandButtons();
			moveCurrentHandButton();
		}

	}

	private void stand(int n) {
		nextHand();
	}

	private void insurance(int n) {
		if (!EconomyUtils.playerTakeMoney(this.gamePlayerAtLocation[n].getPlayer(), playerBets[n] / 2)) {
			dealerSendMessage(n, ConfigUtil.CHAT_BLACKJACK_NOMONEYINSURACE.toString());
			return;
		}
		insuredPlayers[n] = true;

	}

	@Override
	public void exitPlayer(Player player) {
		int n = this.getPlayerBoardLocation(teamManager.getGamePlayer(player));
		gamePlayerAtLocation[n] = null;
		if (!inRound && playerBets[n] != 0) {
			EconomyUtils.playerGiveMoney(player, playerBets[n]);
			this.dealerSendMessage(player, ConfigUtil.CHAT_BLACKJACK_BETRETURN.buildString(playerBets[n] + ""));
		}

		playerBets[n] = 0;

		if (inRound && teamManager.getGamePlayer(player) == teamManager.getTurnPlayer())
			nextTurn();
		teamManager.removeTeamByPlayer(player);

		if (inRound)
			toggleActionButtons();

		this.toggleJoinButtons();
		if (this.getAmountOfBets() == 0)
			this.endGame(null);

		mapManager.renderBoard();
	}

	private void join(Player player, int n) {
		gamePlayerAtLocation[n] = this.addPlayer(player);
		teamManager.addTeam(gamePlayerAtLocation[n]);
		if (!inRound)
			askForBet(gamePlayerAtLocation[n]);
		this.toggleJoinButtons();
		mapManager.renderBoard();
	}

	@Override
	protected void gamePlayerOutOfTime(GamePlayer turn) {
		// TODO Auto-generated method stub

	}

	@Override
	public ItemStack getBoardItem() {
		return new BoardItem(gameName, new ItemStack(Material.BLACK_CARPET, 1));
	}

	public int getAmountOfBets() {
		int bets = 0;
		for (int b : playerBets)
			if (b > 0)
				bets++;
		return bets;
	}

	@Override
	public GameNPC getGameNPC() {
		return new BlackjackNPC(new double[] { 0.5, -1, 1.5 });
	}

}
