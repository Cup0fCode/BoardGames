package water.of.cup.boardgames.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import water.of.cup.boardgames.BoardGames;
import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.GameManager;

public class PlayerQuit implements Listener {
	private final BoardGames instance = BoardGames.getInstance();
    private final GameManager gameManager = instance.getGameManager();

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
    	Player player = event.getPlayer();
    	Game game = gameManager.getGameByPlayer(player);
    	if (game != null) {
    		game.exitPlayer(player);
    	}
    }
}
