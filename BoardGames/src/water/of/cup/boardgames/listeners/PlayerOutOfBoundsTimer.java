package water.of.cup.boardgames.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import water.of.cup.boardgames.config.ConfigUtil;
import water.of.cup.boardgames.game.Game;

public class PlayerOutOfBoundsTimer extends BukkitRunnable {

    private final long timeStart;
    private final Player player;
    private final Location boardLoc;
    private final Game game;
    private final PlayerOutOfBoundsCallback callback;

    private final int RETURN_TIME = ConfigUtil.PLAYER_DISTANCE_TIME.toInteger();
    private final int DISTANCE = ConfigUtil.PLAYER_DISTANCE_AMOUNT.toInteger();

    public PlayerOutOfBoundsTimer(Game game, Player player, Location boardLoc, PlayerOutOfBoundsCallback callback) {
        timeStart = System.currentTimeMillis();
        this.player = player;
        this.boardLoc = boardLoc;
        this.game = game;
        this.callback = callback;
    }

    @Override
    public void run() {
        if(!game.hasPlayer(player)) {
            callback.onComplete();
            cancel();
        }

        double distanceFromGame = boardLoc.distance(player.getLocation());

        if(distanceFromGame < DISTANCE) {
            callback.onComplete();
            cancel();
            return;
        }

        if(System.currentTimeMillis() - timeStart >= RETURN_TIME) {
            callback.onComplete();

            if(game.hasPlayer(player))
                game.exitPlayer(player);
            cancel();
        }
    }
}
