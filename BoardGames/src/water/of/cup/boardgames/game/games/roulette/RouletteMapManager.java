package water.of.cup.boardgames.game.games.roulette;

import org.bukkit.Bukkit;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import water.of.cup.boardgames.BoardGames;
import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.maps.GameMap;
import water.of.cup.boardgames.game.maps.GameRenderer;
import water.of.cup.boardgames.game.maps.MapManager;

public class RouletteMapManager extends MapManager {
	private Game game;

	public RouletteMapManager(int[][] mapStructure, int rotation, Game game) {
		super(mapStructure, rotation, game);
		assert (game instanceof Roulette);
		
		this.game = game;
	}
	
	public void renderSpinner() {
		for (int mapVal : new int[]{1,2,3,4}) {
			GameMap map = game.getGameMapByMapVal(mapVal);
			MapMeta mapMeta = map.getMapMeta();
			MapView view = mapMeta.getMapView();

			for (MapRenderer renderer : view.getRenderers())
				if (renderer instanceof GameRenderer)
					((GameRenderer) renderer).rerender();
			Bukkit.getServer().getScheduler().runTaskAsynchronously(BoardGames.getInstance(), new Runnable() {
				@Override
				public void run() {
					view.getWorld().getPlayers().forEach(player -> player.sendMap(view));
				}
			});
		}
	}

}
