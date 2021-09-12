package water.of.cup.boardgames.game.games.gameutils.cards;

import water.of.cup.boardgames.config.ConfigUtil;

public enum CardSuit {
	CLUBS, DIAMONDS, HEARTS, SPADES;
	
	public int getPoints() {
		switch (this) {
		case CLUBS:
			return 0;
		case DIAMONDS:
			return 1;
		case HEARTS:
			return 2;
		case SPADES:
			return 3;
		default:
			return 0;
		
		}
	}
	
	public String getName() {
		switch (this) {
		case CLUBS:
			return ConfigUtil.CARD_SUIT_CLUBS.toString();
		case DIAMONDS:
			return ConfigUtil.CARD_SUIT_DIAMONDS.toString();
		case HEARTS:
			return ConfigUtil.CARD_SUIT_HEARTS.toString();
		case SPADES:
			return ConfigUtil.CARD_SUIT_SPADES.toString();
		default:
			return ConfigUtil.CARD_SUIT_JOKER.toString();
		
		}
	}
}
