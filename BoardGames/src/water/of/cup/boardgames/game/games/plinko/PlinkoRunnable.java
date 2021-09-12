package water.of.cup.boardgames.game.games.plinko;

import org.bukkit.scheduler.BukkitRunnable;
import water.of.cup.boardgames.game.maps.MapManager;

public class PlinkoRunnable  extends BukkitRunnable {
	private Plinko game;
	private MapManager mapManager;
	private int runs;
	private boolean direction;

	public PlinkoRunnable(Plinko game, MapManager mapManager) {
		this.game = game;
		this.mapManager = mapManager;
		runs = 0;
	}

	@Override
	public void run() {
		if (runs >= 18) {
			game.endGame();
			cancel();
			return;
		}
		
		if (runs < 2) 
			game.moveDown(runs % 2 + 5);
		else {
			if (runs % 2 == 0) 
				direction = Math.random() < 0.5;
			game.move(direction, runs % 2 + 5);
		}
		
		mapManager.renderBoard();
		runs++;
	}
}
