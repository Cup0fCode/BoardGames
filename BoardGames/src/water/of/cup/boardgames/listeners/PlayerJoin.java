package water.of.cup.boardgames.listeners;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import water.of.cup.boardgames.BoardGames;
import water.of.cup.boardgames.config.ConfigUtil;
import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.GameManager;

public class PlayerJoin implements Listener {

    private final BoardGames instance = BoardGames.getInstance();
    private final GameManager gameManager = instance.getGameManager();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        gameManager.rerender(player);
        
        if(ConfigUtil.RECIPE_ENABLED.toBoolean()) {
            for (String name : gameManager.getGameNames()) {
                Game temp = gameManager.newGame(name, 0);

                if (temp != null && temp.getGameRecipe() != null) {
                    player.discoverRecipe(new NamespacedKey(instance, temp.getName()));
                }
            }
        }
    }

}
