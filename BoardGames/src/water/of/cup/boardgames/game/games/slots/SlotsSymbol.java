package water.of.cup.boardgames.game.games.slots;

import water.of.cup.boardgames.game.games.gameutils.MathUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class SlotsSymbol {
	private String imageName;
	private SymbolType type;

	private HashMap<Integer, Double> payouts;
	private HashMap<HashMap<SlotsSymbol, Integer>, Double> specialPayouts;

	public SlotsSymbol(String imageName, SymbolType type) {
		this.imageName = imageName;
		this.type = type;
		payouts = new HashMap<Integer, Double>();
		specialPayouts = new HashMap<HashMap<SlotsSymbol, Integer>, Double>();
	}

	public String getGameImage() {
		return imageName;
	}

	public void setGameImage(String gameImage) {
		this.imageName = gameImage;
	}

	public SymbolType getType() {
		return type;
	}

	public void setType(SymbolType type) {
		this.type = type;
	}

	public void addPayout(int quantity, double payout) {
		payouts.put(quantity, payout);
	}

	private double getPayout(int quantity) {
		if (!payouts.containsKey(quantity))
			return 0;
		return payouts.get(quantity);
	}

	private boolean hasSpecialPayouts() {
		return specialPayouts.size() > 0;
	}

	public void addSpecialPayout(int quantity, HashMap<SlotsSymbol, Integer> symbolQuantities, double payout) {
		symbolQuantities.put(this, quantity);
		specialPayouts.put(symbolQuantities, payout);
	}

	private double getSpecialPayout(HashMap<SlotsSymbol, Integer> symbols) {
		if (!hasSpecialPayouts())
			return 0;
		double payout = 0;

		specialPayouts: for (Entry<HashMap<SlotsSymbol, Integer>, Double> specialPayout : specialPayouts.entrySet()) {
			for (Entry<SlotsSymbol, Integer> symbolQuantity : specialPayout.getKey().entrySet()) {
				SlotsSymbol symbol = symbolQuantity.getKey();
				// check that symbols has symbol
				if (!symbols.containsKey(symbol))
					continue specialPayouts;
				// check that quantities are the same
				if (symbolQuantity.getValue() != symbols.get(symbol))
					continue specialPayouts;
			}
			if (specialPayout.getValue() > payout)
				payout = specialPayout.getValue();
		}

		return payout;
	}

	public double getPayout(HashMap<SlotsSymbol, Integer> symbols) {
		int quantity = symbols.get(this);
		double payout = getPayout(quantity);
		double specialPayout = getSpecialPayout(symbols);
		if (specialPayout > payout)
			return specialPayout;
		return payout;
	}

	protected double[] getAveragePayoutAndQuantity(int uniqueSymbolQuantity, int amountOfSymbols) {
		double totalPayout = 0;
		int quantity = 0;
		
		for (Entry<Integer, Double> payout : payouts.entrySet()) {
			ArrayList<Integer> uniqueSymbols = new ArrayList<Integer>();
			uniqueSymbols.add(payout.getKey());
			
			int combinations = getTotalPayoutCombinations(uniqueSymbols, uniqueSymbolQuantity, amountOfSymbols);
			quantity += combinations;
			totalPayout += payout.getValue() * combinations;
		}
		
		for (Entry<HashMap<SlotsSymbol, Integer>, Double> payout : specialPayouts.entrySet()) {
			ArrayList<Integer> uniqueSymbols = new ArrayList<Integer>();
			for (Entry<SlotsSymbol, Integer> entry : payout.getKey().entrySet()) {
				uniqueSymbols.add(entry.getValue());
			}
			
			int combinations = getTotalPayoutCombinations(uniqueSymbols, uniqueSymbolQuantity, amountOfSymbols);
			quantity += combinations;
			totalPayout += payout.getValue() * combinations;
		}
		if (quantity == 0)
			return new double[] {0 ,0};
		
		return new double[] {totalPayout / quantity, quantity};
	}
	
	private int getTotalPayoutCombinations(ArrayList<Integer> uniqueSymbols, int uniqueSymbolQuantity, int amountOfSymbols) {
		int uniqueSymbolsTotal = 0;
		for (Integer n : uniqueSymbols)
			uniqueSymbolsTotal += n;
		
		long totalPayoutCombinations = MathUtils.factorial(amountOfSymbols) / MathUtils.factorial(amountOfSymbols - uniqueSymbolsTotal);
		
		if (amountOfSymbols - uniqueSymbolsTotal > 0)
			totalPayoutCombinations *= Math.pow(uniqueSymbolQuantity - uniqueSymbols.size(), amountOfSymbols - uniqueSymbolsTotal);
		
		for (Integer n : uniqueSymbols)
			totalPayoutCombinations /= MathUtils.factorial(n);
		//System.out.println(imageName + ": " + totalPayoutCombinations);
		return (int) totalPayoutCombinations;
	} 
}
