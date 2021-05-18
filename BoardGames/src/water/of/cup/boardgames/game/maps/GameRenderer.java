package water.of.cup.boardgames.game.maps;

import java.util.HashSet;
import java.util.UUID;

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
	private Screen screen;
	private int[] loc;
	private HashSet<UUID> rendered;
	
	public GameRenderer(Game game, int[] mapValsLocationOnBoard) {
		super(true);
		this.game = game;
		loc = mapValsLocationOnBoard;
		rendered = new HashSet<UUID>();
	}

	public GameRenderer(Game game, int[] mapValsLocationOnScreen, Screen screen) {
		super(true);
		this.game = game;
		loc = mapValsLocationOnScreen;
		this.screen = screen;
		rendered = new HashSet<UUID>();
	}

	@Override
	public void render(MapView map, MapCanvas canvas, Player player) {
		if (rendered == null)
			rendered = new HashSet<UUID>();
		if (rendered.contains(player.getUniqueId())) { 
			return;
		}
		
		rendered.add(player.getUniqueId());
		
		GamePlayer gamePlayer = game.getGamePlayer(player);
		boolean ingamePlayer = gamePlayer != null;
		
		GameImage gameImage;
		if (screen != null) {
			gameImage = screen.getGameImage().clone();
		} else {
			gameImage = game.getGameImage().clone();
		}
		
		for (Button button : game.getButtons()) {
			// check that button is visible for player &
			// check that button belongs to screen/board (button.getScreen() returns null if button belongs to game)
			if (button.getScreen() == screen && (button.isVisibleForAll() || (ingamePlayer && button.visibleForPlayer(gamePlayer)))) {
				gameImage.addGameImage(button.getImage(), button.getLocation());
			}
		}
		
		gameImage.cropMap(loc);
		canvas.drawImage(0, 0, gameImage.getImage(screen == null ? game.getRotation() : 0));
	}
}
