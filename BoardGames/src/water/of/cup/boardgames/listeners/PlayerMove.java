package water.of.cup.boardgames.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import water.of.cup.boardgames.BoardGames;
import water.of.cup.boardgames.config.ConfigUtil;
import water.of.cup.boardgames.game.Game;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;

public class PlayerMove implements Listener {

    private final HashMap<Player, Long> moveTimer = new HashMap<>();
    private final ArrayList<Player> returnPlayers = new ArrayList<>();
    public static final int DISTANCE = ConfigUtil.PLAYER_DISTANCE_AMOUNT.toInteger();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if(!moveTimer.containsKey(player)) {
            moveTimer.put(player, System.currentTimeMillis());

            Game game = BoardGames.getInstance().getGameManager().getGameByPlayer(player);
            if(game != null && game.getPlacedMapLoc() != null) {
                if(!playerIsInRange(game.getPlacedMapLoc(), player.getLocation()) && !returnPlayers.contains(player)) {
                    returnPlayers.add(player);

                    player.sendMessage(ConfigUtil.CHAT_RETURN_TO_GAME.buildString(game.getAltName()));

                    new PlayerOutOfBoundsTimer(game, player, game.getPlacedMapLoc(), new PlayerOutOfBoundsCallback() {
                        @Override
                        public void onComplete() {
                            returnPlayers.remove(player);
                        }
                    }).runTaskTimer(BoardGames.getInstance(), 5, 5);
                }
            }

        } else if(System.currentTimeMillis() - moveTimer.get(player) >= 500) {
            moveTimer.remove(player);
        }
    }

    public static boolean playerIsInRange(Location boardLoc, Location playerLoc) {
        if(boardLoc.getWorld() == null || playerLoc.getWorld() == null) {
            return false;
        }

        if(boardLoc.getWorld() != playerLoc.getWorld()) {
            return false;
        }

        if(boardLoc.distance(playerLoc) >= ConfigUtil.PLAYER_DISTANCE_AMOUNT.toInteger()) {
            return false;
        }

        return true;
    }
}
