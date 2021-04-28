package water.of.cup.boardgames.game.maps;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import water.of.cup.boardgames.game.Button;
import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.GameImage;
import water.of.cup.boardgames.game.GamePlayer;

public class GameRenderer extends MapRenderer {
	private Game game;
	private MapManager mapManager;
	private int[] loc;
//
//	public GameRenderer(Game game) {
//		this.game = game;
//	}
	
	public GameRenderer(Game game, int[] mapValsLocationOnBoard) {
		this.game = game;
		loc = mapValsLocationOnBoard;
	}

	@Override
	public void render(MapView map, MapCanvas canvas, Player player) {
		// used to prevent map from continuously rendering
		if (map.isLocked())
			return;
		
		GamePlayer gamePlayer = game.getGamePlayer(player);
		boolean ingamePlayer = gamePlayer != null;
		
		GameImage gameImage = game.getGameImage().clone();
		gameImage.cropMap(loc);
		for (Button button : game.getButtons()) {
			if (button.isVisibleForAll() || ingamePlayer && button.visibleForPlayer(gamePlayer)) {
				gameImage.addGameImage(button.getImage(), button.getLocation());
			}
		}
		
		canvas.drawImage(0, 0, gameImage.getImage(game.getRotation()));
		map.setLocked(true);
	}
}
