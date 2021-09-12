package water.of.cup.boardgames.game.games.roulette;

import org.bukkit.scheduler.BukkitRunnable;

public class RouletteSpinnerRunnable extends BukkitRunnable {
	private Roulette game;
	private int totalSpins;
	private int spinsLeft;
	private RouletteSpinner spinner;
	private static int WHEELSPINS = 30;

	public RouletteSpinnerRunnable(Roulette game, RouletteSpinner spinner) {
		spinsLeft = (int) (WHEELSPINS + Math.random() * 38);
		totalSpins = spinsLeft;
		this.game = game;
		this.spinner = spinner;

	}

	@Override
	public void run() {

		if (spinsLeft == 0) {
			game.endSpin();
			cancel();
			return;
		}
		if (totalSpins - spinsLeft < WHEELSPINS)
			spinner.spin();
		else 
			spinner.moveBall(1);
		game.updateSpinner();
		spinsLeft--;

	}
}
