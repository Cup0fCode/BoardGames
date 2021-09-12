package water.of.cup.boardgames.game.games.slots;

import org.bukkit.scheduler.BukkitRunnable;
import water.of.cup.boardgames.game.maps.MapManager;

public class SlotsSpinner extends BukkitRunnable {
	private SlotsGame game;
	private MapManager mapManager;
	private int runs;

	public SlotsSpinner(SlotsGame slotsGame, MapManager mapManager, int runs) {
		game = slotsGame;
		this.runs = runs;
		this.mapManager = mapManager;
		
	}

	@Override
	public void run() {
		if (runs <= 0) {
			game.finishSpin();
			cancel();
			return;
		}
		game.spin();
		game.setButtonImages();
		mapManager.renderBoard();
		
		runs--;
	}
}