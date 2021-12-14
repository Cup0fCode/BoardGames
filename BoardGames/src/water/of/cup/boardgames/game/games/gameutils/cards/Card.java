package water.of.cup.boardgames.game.games.gameutils.cards;

import water.of.cup.boardgames.BoardGames;
import water.of.cup.boardgames.config.ConfigUtil;
import water.of.cup.boardgames.game.GameImage;

public class Card {
	int num; // ace is 0
	CardSuit suit;

	public Card(CardSuit suit, int num) {
		this.suit = suit;
		assert num >= 0 && num < 13;
		this.num = num;
	}

	public int getValue() {
		return getValue(true);
	}

	public int getValue(boolean aceHigh) {
		if (aceHigh && num == 0)
			return 14;
		return num + 1;
	}

	public int getBlackJackValue(boolean aceHigh) {
		if (aceHigh && num == 0)
			return 11;
		if (num + 1 > 10)
			return 10;
		return num + 1;
	}

	public int getPoints() {
		// num * 10 + suit
		return getValue() * 10 + suit.getPoints();
	}

	public CardSuit getSuit() {
		return suit;
	}

	public GameImage getGameImage() {
		GameImage image = new GameImage(BoardGames.getImageManager().getImage("PLAYINGCARDS_" + suit.toString() + "_" + getValue()), 0);
		return image;
	}

	public String getName() {
		return getShortName() + ConfigUtil.CARD_FORMAT.toString() + suit.getName();
	}

	public String getShortName() {
		switch (getValue(false)) {
		case 1:
			return ConfigUtil.CARD_ACE.toString();
		case 2:
			return ConfigUtil.CARD_TWO.toString();
		case 3:
			return ConfigUtil.CARD_THREE.toString();
		case 4:
			return ConfigUtil.CARD_FOUR.toString();
		case 5:
			return ConfigUtil.CARD_FIVE.toString();
		case 6:
			return ConfigUtil.CARD_SIX.toString();
		case 7:
			return ConfigUtil.CARD_SEVEN.toString();
		case 8:
			return ConfigUtil.CARD_EIGHT.toString();
		case 9:
			return ConfigUtil.CARD_NINE.toString();
		case 10:
			return ConfigUtil.CARD_TEN.toString();
		case 11:
			return ConfigUtil.CARD_JACK.toString();
		case 12:
			return ConfigUtil.CARD_QUEEN.toString();
		case 13:
			return ConfigUtil.CARD_KING.toString();
		}

		return "";
	}
}
