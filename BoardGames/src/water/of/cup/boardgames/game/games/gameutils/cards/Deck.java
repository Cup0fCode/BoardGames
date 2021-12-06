package water.of.cup.boardgames.game.games.gameutils.cards;

import java.util.ArrayList;
import java.util.Collections;

public class Deck {
	ArrayList<Card> cards;
	int amountOfDecks;

	public Deck(int amountOfDecks) {
		this.amountOfDecks = amountOfDecks;
		newCards();
	}

	public void newCards() {
		cards = new ArrayList<Card>();
		for (int a = 0; a < amountOfDecks; a++)
			for (CardSuit suit : CardSuit.values())
				for (int n = 0; n < 13; n++) {
					Card card = new Card(suit, n);
					cards.add(card);

				}
		Collections.shuffle(cards);
	}

	public int amountOfCards() {
		return cards.size();
	}

	public Card draw() {
		if (cards.size() == 0)
			newCards();
		Card card = cards.get(0);
		cards.remove(0);
		return card;
	}

	public ArrayList<Card> draw(int amt) {
		ArrayList<Card> drawn = new ArrayList<Card>();
		for (int i = 0; i < amt; i++)
			drawn.add(draw());
		return drawn;
	}

}