package water.of.cup.boardgames.game.games.uno;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import water.of.cup.boardgames.config.ConfigUtil;
import water.of.cup.boardgames.config.GameRecipe;
import water.of.cup.boardgames.game.*;
import water.of.cup.boardgames.game.inventories.GameInventory;
import water.of.cup.boardgames.game.storage.GameStorage;
import water.of.cup.boardgames.game.storage.StorageType;

public class Uno extends Game {
	private boolean isWild;
	private UnoDeck deck;

	private HashMap<GamePlayer, Integer> playerBoardPosition; // [0,7]
	private HashMap<GamePlayer, UnoHand> playerHands;
	private HashMap<GamePlayer, ArrayList<Button>> playerCardButtons;
	private HashMap<GamePlayer, Button> handButtons;

	private ArrayList<Button> colorButtons;

	private UnoCard currentCard;
	private Button currentCardButton;

	private int middleCardSize = 2;

	private static final HashMap<String, String> COLOR_MAP = new HashMap<>();

	static {
		COLOR_MAP.put("RED", ConfigUtil.CHAT_GAME_UNO_COLOR_RED.toString());
		COLOR_MAP.put("BLUE", ConfigUtil.CHAT_GAME_UNO_COLOR_BLUE.toString());
		COLOR_MAP.put("YELLOW", ConfigUtil.CHAT_GAME_UNO_COLOR_YELLOW.toString());
		COLOR_MAP.put("GREEN", ConfigUtil.CHAT_GAME_UNO_COLOR_GREEN.toString());
	}

	public Uno(int rotation) {
		super(rotation);
	}
	
	@Override
	public void exitPlayer(Player player) {
		super.exitPlayer(player);
		if (!this.isIngame())
			return;
		toggleHandButtons();
		for (GamePlayer gamePlayer : teamManager.getGamePlayers()) {
			setCardButtons(gamePlayer);
		}
		mapManager.renderBoard();
		
	}

	@Override
	protected void setMapInformation(int rotation) {
		this.mapStructure = new int[][] { { 3, 4 }, { 1, 2 } };
		this.placedMapVal = 1;
	}

	@Override
	protected void startGame() {
		super.startGame();
		deck = new UnoDeck();
		
		// set current card
		currentCard = deck.drawCards(1).get(0);
		while (currentCard.getColor().equals("ALL"))
			currentCard = deck.drawCards(1).get(0);

		playerBoardPosition = new HashMap<GamePlayer, Integer>();
		playerCardButtons = new HashMap<GamePlayer, ArrayList<Button>>();
		handButtons = new HashMap<GamePlayer, Button>();
		createPlayerHands();

		if(getConfigValue("middle_card_size") != null) {
			String cardSize = getConfigValue("middle_card_size") + "";
			if(MathUtils.isNumeric(cardSize)) {
				int cardSizeNum = Integer.parseInt(cardSize);
				if(cardSizeNum > 0)
					middleCardSize = cardSizeNum;
			}
		}

		GameImage currentCardImage = currentCard.getGameImage().clone();
		currentCardImage.resize(middleCardSize);
		currentCardButton = new Button(this, currentCardImage, new int[] { 128 - (4 * middleCardSize), 128 - (7 * middleCardSize) }, 0,
				"currentCardButton");
		currentCardButton.setClickable(false);
		buttons.add(currentCardButton);

		isWild = false;

		// set color buttons
		colorButtons = new ArrayList<Button>();
		int i = 0;
		double colorImageResize = middleCardSize / 2.0;
		for (String s : new String[] { "RED", "BLUE", "YELLOW", "GREEN" }) {
			GameImage colorImage = new GameImage("UNOCARD_" + s);
			colorImage.resize(colorImageResize);
			Button b = new Button(this, colorImage, new int[] { 128 - (int) (8 * colorImageResize) + (i / 2) * (int) (8 * colorImageResize), 128 -  (int) (14 * colorImageResize) + (i % 2) * (int) (14 * colorImageResize) }, 0, s);
			buttons.add(b);
			colorButtons.add(b);
			toggleColorButtons();
			i++;
		}

		toggleHandButtons();
		mapManager.renderBoard();
		super.setInGame();
	}

	private void toggleColorButtons() {
		currentCardButton.setVisibleForAll(!isWild);
		for (Button b : colorButtons) {
			b.setVisibleForAll(isWild);
			b.setClickable(isWild);
		}
	}

	private void toggleHandButtons() {
		for (Button b : handButtons.values()) {
			b.setImage("UNO_DECK");
		}
		handButtons.get(teamManager.getTurnPlayer()).setImage("UNO_DECK_TURN");
	}

	private void createPlayerHands() {
		int posCounter = 0;
		playerHands = new HashMap<GamePlayer, UnoHand>();
		for (GamePlayer player : super.getGamePlayers()) {
			//player.getPlayer().sendMessage("set");

			playerBoardPosition.put(player, posCounter);
			playerHands.put(player, new UnoHand());
			playerDrawCards(player, 7); // draw 7 cards at game start
			playerCardButtons.put(player, new ArrayList<Button>());

			// pos maths
			int rotation = (posCounter + 1) / 2;
			int[] loc = new int[] { 0, 0 };
//			for (int i = 4; i > rotation; i--) { // rotate initial cords
//				loc = MathUtils.rotatePointAroundPoint90Degrees(new double[] { 63.5, 63.5 }, loc);
//			}
			loc[1] += (1 - (posCounter + 2) / 4) * 128; // set Y cords
			loc[0] += (1 - (posCounter) / 4) * 128; // set X cords

			if(loc[1] < 0) loc[1] = loc[1] * -1;

			// create position image
			Button b = new Button(this, "UNO_DECK", loc, (4 - rotation) % 4, "deck");
			handButtons.put(player, b);
			buttons.add(b);

			setCardButtons(player);
			posCounter++;
		}

	}

	private void playerDrawCards(GamePlayer player, int amount) {
		UnoHand hand = playerHands.get(player);
		hand.draw(deck, amount);
	}

	private void setCardButtons(GamePlayer player) { // TODO: include page
		ArrayList<Button> cardButtons = playerCardButtons.get(player);
		buttons.removeAll(cardButtons);
		cardButtons.clear();

		UnoHand hand = playerHands.get(player);
		int boardPos = playerBoardPosition.get(player);

		// TODO : add loc rotation maths
		int rotation = (boardPos + 1) / 2;

		int[] iLoc = new int[] { 33, 96 };

		for (int i = 4; i > rotation; i--) { // rotate initial cords
			iLoc = MathUtils.rotatePointAroundPoint90Degrees(new double[] { 63.5, 63.5 }, iLoc);
		}

		iLoc[1] += (1 - (boardPos + 2) / 4) * 128; // set Y cords
		iLoc[0] += (1 - (boardPos) / 4) * 128; // set X cords

		if(iLoc[1] < 0) iLoc[1] = 256 + iLoc[1];

		int handPos = 0;
		for (UnoCard card : hand.getCards(currentCard)) {
			int[] cLoc = new int[] { (handPos % 7) * 9, (handPos / 7) * 15 };

			for (int i = 4; i > rotation; i--) { // rotate change cords
				cLoc = MathUtils.rotatePointAroundPoint90Degrees(new double[] { 0, 0 }, cLoc);
			}

			int[] loc = new int[] { iLoc[0] + cLoc[0], iLoc[1] + cLoc[1] };

			//player.getPlayer().sendMessage(loc[0] + "," + loc[1]);
			Button cardButton = new Button(this, card.getGameImage(), loc, (4 - rotation) % 4, "" + handPos);
			cardButton.changeLocationByRotation();
			cardButton.setVisibleForAll(false);
			cardButton.addVisiblePlayer(player);
			cardButtons.add(cardButton);
			buttons.add(cardButton);
			cardButton.setClickable(true);
			handPos++;
		}

	}

	private UnoCard getSelectedCard(GamePlayer player, Button b) {
		ArrayList<Button> cardButtons = playerCardButtons.get(player);
		int index = cardButtons.indexOf(b);
		if (index == -1)
			return null;
		UnoHand hand = playerHands.get(player);
		return hand.getCards(currentCard).get(index);
	}

	private boolean playCard(GamePlayer player, UnoCard card) {
		if (card.matches(currentCard)) {
			UnoHand hand = playerHands.get(player);
			hand.removeCard(card);
			if (hand.cardsLeft() == 1) {
				// say uno
				this.getGamePlayers()
						.forEach((p) -> p.getPlayer().sendMessage(ConfigUtil.CHAT_GAME_UNO_LAST_CARD.buildString(player.getPlayer().getDisplayName())));
			}

			currentCard = card;

			GameImage currentCardImage = currentCard.getGameImage().clone();
			currentCardImage.resize(middleCardSize);
			currentCardButton.setImage(currentCardImage);

			if (hand.cardsLeft() == 0) {
				this.endGame(player);
				return false;
			}

			setCardButtons(player);

			// check if card is wild
			if (card.getColor().equals("ALL")) {
				isWild = true;
				toggleColorButtons();
				return true;
			}

			doCardActions(card); // changes the turn

			return true;
		} else {
			return false;
		}
	}

	private void doCardActions(UnoCard card) {
		teamManager.nextTurn();
		GamePlayer player = teamManager.getTurnPlayer();

		for (String action : card.getActions()) {
			if (action.equals("DRAW2")) {
				playerHands.get(player).draw(deck, 2);
				player.getPlayer().sendMessage(ConfigUtil.CHAT_GAME_UNO_FORCE_2.toString());
				setCardButtons(player);
				continue;
			}
			if (action.equals("DRAW4")) {
				playerHands.get(player).draw(deck, 4);
				player.getPlayer().sendMessage(ConfigUtil.CHAT_GAME_UNO_FORCE_4.toString());
				setCardButtons(player);
				continue;
			}
			if (action.equals("SKIP")) {
				teamManager.nextTurn();
				player.getPlayer().sendMessage(ConfigUtil.CHAT_GAME_UNO_SKIPPED.toString());
				continue;
			}
			if (action.equals("REVERSE")) {
				teamManager.switchTurnDirection();
				teamManager.nextTurn();
				teamManager.nextTurn();
				continue;
			}
		}
	}

	@Override
	protected void setGameName() {
		this.gameName = "Uno";
	}

	@Override
	protected void setBoardImage() {
		// TODO Auto-generated method stub
		this.gameImage = new GameImage("UNO_BOARD");

	}

	@Override
	protected Clock getClock() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected GameInventory getGameInventory() {
		return new UnoInventory(this);
	}

	@Override
	protected GameStorage getGameStorage() {
		return new UnoStorage(this);
	}

	@Override
	public ArrayList<String> getTeamNames() {
		return null;
	}

	@Override
	protected GameConfig getGameConfig() {
		return new UnoConfig(this);
	}

	@Override
	public void click(Player player, double[] loc, ItemStack map) {
		GamePlayer gamePlayer = getGamePlayer(player);
		if (gamePlayer == null) {
			return;
		}

		if (gamePlayer != teamManager.getTurnPlayer()) {
			player.sendMessage(ConfigUtil.CHAT_GAME_UNO_NOT_YOUR_TURN.toString());
			return;
		}

		int[] clickLoc = mapManager.getClickLocation(loc, map);
		Button b = getClickedButton(gamePlayer, clickLoc);

		if (b == null)
			return;

		// select wild color
		if (isWild) {
			if (colorButtons.contains(b)) {
				isWild = false;
				toggleColorButtons();
				currentCard.setColor(b.getName());
				doCardActions(currentCard);
				toggleHandButtons();
				setCardButtons(teamManager.getTurnPlayer());
				mapManager.renderBoard();
				this.getGamePlayers().forEach((p) -> p.getPlayer()
						.sendMessage(buildColorString(player, b.getName())));
			} else {
				player.sendMessage(ConfigUtil.CHAT_GAME_UNO_SELECT_COLOR.toString());
			}
			return;
		}

		UnoHand hand = playerHands.get(gamePlayer);
		UnoCard card = getSelectedCard(gamePlayer, b);

		if (!hand.canPlay(currentCard)) {
			player.sendMessage(ConfigUtil.CHAT_GAME_UNO_FORCE_DRAW.toString());
			UnoCard drawedCard = hand.draw(deck, 1).get(0);
			if(!currentCard.matches(drawedCard) && !drawedCard.getColor().equals("ALL")) {
				setCardButtons(gamePlayer);
				teamManager.nextTurn();
				toggleHandButtons();
				mapManager.renderBoard();
				return;
			}
			// Force player to play drawed card
			card = drawedCard;
		}

		if (card == null)
			return;

		if (playCard(gamePlayer, card)) {
			this.playGameSound("click");

//			player.sendMessage("played card");
			toggleHandButtons();
			setCardButtons(teamManager.getTurnPlayer());
			mapManager.renderBoard();
		} else if (!card.matches(currentCard)) {
			player.sendMessage(ConfigUtil.CHAT_GAME_UNO_INVALID_CARD.toString());
		} else {
			// Game over, render final card
			mapManager.renderBoard();
		}

		// if (playCard(gamePlayer, ))

//		UnoCard card = deck.drawCards(1).get(0);
//		Button cardButton = new Button(this, card.getGameImage(), clickLoc, 0, card.getType());
//		buttons.add(cardButton);
//		mapManager.renderBoard();
	}

	@Override
	protected void gamePlayerOutOfTime(GamePlayer turn) {
		// TODO Auto-generated method stub

	}

	@Override
	public BoardItem getBoardItem() {
		return new BoardItem(getAltName(), new ItemStack(Material.SPRUCE_TRAPDOOR, 1));
	}

	@Override
	protected void clockOutOfTime() {
		// TODO Auto-generated method stub
		
	}

	private String buildColorString(Player player, String color) {
		String formatted = ConfigUtil.CHAT_GAME_UNO_COLOR.toString();

		formatted = formatted.replace("%player%", player.getDisplayName()).replace("%color%", COLOR_MAP.get(color));

		return formatted;
	}
}
