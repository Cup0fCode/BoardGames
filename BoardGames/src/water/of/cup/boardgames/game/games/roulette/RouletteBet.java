package water.of.cup.boardgames.game.games.roulette;

import water.of.cup.boardgames.game.Button;
import water.of.cup.boardgames.game.Game;

import java.util.ArrayList;
import java.util.Arrays;

public class RouletteBet {
	private String type;
	private int position;
	private double amount;
	private Button button;

	public RouletteBet(String type, int position, double amount, int[] loc, String color, Game game) {
		this.type = type;
		this.position = position;
		this.amount = amount;
		button = new Button(game, "ROULETTE_CHIP_" + color, new int[] {loc[0] - 3, loc[1] - 3}, 0, "chip");
	}
	
	public Button getButton() {
		return button;
	}
	
	public double getWin(int winningPos) {
		for (int n : getWinningNums())
			if (n == winningPos)
				return (getMultiplier() + 1) * amount;
		return 0;
	}

	public double getAmount() {
		return amount;
	}

	private double getMultiplier() {
		switch (type) {
		case "strait0":
		case "strait":
			return 35;
		case "split":
			return 17;
		case "street":
			return 11;
		case "six line":
			return 5;
		case "corner":
			return 8;
		case "trio":
			return 11;
		case "basket":
			return 6;

		// outside bets:
		case "red or black":
			return 1;

		case "odd or even":
			return 1;

		case "1 to 18 or 19 to 36":
			return 1;

		case "dozens":
			return 2;
		case "columns":
			return 2;

		}
		return 0;
	}

	public ArrayList<Integer> getWinningNums() {
		ArrayList<Integer> winningNums = new ArrayList<Integer>();

		switch (type) {
		case "strait0":
			winningNums.add((position - 1) * 100);
			break;
		case "strait":
			winningNums.add(position);
			break;
		case "split":
			if (position <= 24) {
				int pos = ((position + 1) / 2 - 1) * 3 + 2 - position % 2;
				winningNums.add(pos);
				winningNums.add(pos + 1);
			} else {
				int pos = position - 24;
				winningNums.add(pos);
				winningNums.add(pos + 3);
			}
			break;
		case "street":
			int street = (position - 1) * 3 + 1;
			for (int i = 0; i < 3; i++)
				winningNums.add(street + i);
			break;
		case "six line":
			int line = (position - 1) * 3 + 1;
			for (int i = 0; i < 6; i++)
				winningNums.add(line + i);
			break;
		case "corner":
			winningNums.addAll(Arrays.asList(new Integer[] { position, position + 1, position + 3, position + 4 }));
			break;

		case "trio":
			winningNums.addAll(Arrays.asList(new Integer[] { 100, 0, position, position + 1 }));
			break;

		case "basket":
			winningNums.addAll(Arrays.asList(new Integer[] { 100, 0, 1, 2, 3 }));
			break;

		// outside bets:
		case "red or black":
			Integer[] colorNums = position == 1
					? new Integer[] { 1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36 }
					: new Integer[] { 2, 4, 6, 8, 10, 11, 13, 15, 17, 20, 22, 24, 26, 28, 29, 31, 33, 35 };
			winningNums.addAll(Arrays.asList(colorNums));
			break;
		case "odd or even":
			for (int n = position; n <= 36; n += 2)
				winningNums.add(n);
			break;

		case "1 to 18 or 19 to 36":
			for (int n = (position - 1) * 18 + 1; n <= position * 18; n++)
				winningNums.add(n);
			break;

		case "dozens":
			for (int n = (position - 1) * 12 + 1; n <= position * 12; n++)
				winningNums.add(n);
			break;

		case "columns":
			for (int n = position; n <= 36; n += 3)
				winningNums.add(n);
			break;
		}

		return winningNums;
	}

}
