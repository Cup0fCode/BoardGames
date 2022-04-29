package water.of.cup.boardgames.game.games.uno;

import java.util.ArrayList;
import java.util.Collections;

public class UnoDeck {
	ArrayList<UnoCard> cards;

	public UnoDeck() {
		cards = new ArrayList<UnoCard>();
		fillDeck();
	}

	private void fillDeck() {
		// add nums
		for (int i = 0; i < 10; i++) {
			for (String color : new String[] { "RED", "YELLOW", "BLUE", "GREEN" }) {
				cards.add(new UnoCard("" + i, color));
				if (i > 0) { // double for non-0
					cards.add(new UnoCard("" + i, color));
				}
			}
		}

		// add colored action cards
		for (String type : new String[] { "DRAW2", "REVERSE", "SKIP" }) {
			for (String color : new String[] { "RED", "YELLOW", "BLUE", "GREEN" }) {
				cards.add(new UnoCard(type, color));
				cards.add(new UnoCard(type, color));
			}
		}

		// add wild cards
		for (int i = 0; i < 4; i++) {
			cards.add(new UnoCard("WILD", "ALL"));
			cards.add(new UnoCard("WILDDRAW4", "ALL"));
		}

		Collections.shuffle(cards);
	}

	public ArrayList<UnoCard> drawCards(int n) {
		ArrayList<UnoCard> dcards = new ArrayList<UnoCard>();
		for (int i = 0; i < n; i++) {
			if (cards.size() == 0)
				fillDeck();
				
			UnoCard card = cards.get(0);
			cards.remove(0);
			dcards.add(card);
		}
		
		return dcards;
	}
}
