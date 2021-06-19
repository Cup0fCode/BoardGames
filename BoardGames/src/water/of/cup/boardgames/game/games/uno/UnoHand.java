package water.of.cup.boardgames.game.games.uno;

import java.util.ArrayList;

public class UnoHand {
	ArrayList<UnoCard> cards;

	public UnoHand() {
		cards = new ArrayList<UnoCard>();
	}
	
	public void draw(UnoDeck deck, int n) {
		cards.addAll(deck.drawCards(n));
	}
	
	public ArrayList<UnoCard> getCards() {
		return new ArrayList<UnoCard>(cards);
	}
	
	public ArrayList<UnoCard> getCards(UnoCard matcher) { //returns with playable cards first
		if (matcher == null)
			return getCards();
		
		ArrayList<UnoCard> hand = new ArrayList<UnoCard>();
		for (UnoCard card : cards) {
			if (card.matches(matcher)) {
				hand.add(0, card);
			} else {
				hand.add(card);
			}
		}
		return hand;
	}
	
	public void removeCard(UnoCard card) {
		cards.remove(card);
	}
	
	public int cardsLeft() {
		return cards.size();
	}
	
	public boolean canPlay(UnoCard otherCard) {
		for (UnoCard card : cards) {
			if (card.matches(otherCard))
				return true;
		}
		return false;
	}
}
