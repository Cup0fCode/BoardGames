package water.of.cup.boardgames.game.inventories.trade;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class GameTradeTimer extends BukkitRunnable {

    private static final double TIME_UNTIL_START = 5;
    private double lastTimeChange;
    private double timeLeft;

    private final GameTrade gameTrade;

    public GameTradeTimer(GameTrade gameTrade) {
        this.gameTrade = gameTrade;
        this.lastTimeChange = System.currentTimeMillis() / 1000;
        this.timeLeft = TIME_UNTIL_START;
        gameTrade.updateInventory((int) timeLeft);
    }

    @Override
    public void run() {
        double time = System.currentTimeMillis() / 1000;
        double difference = time - lastTimeChange;
        lastTimeChange = time;

        timeLeft -= difference;

        if(timeLeft <= 0) {
            gameTrade.onAccept();
            cancel();
            return;
        }

        if(difference >= 1)
            gameTrade.updateInventory((int) timeLeft);
    }
}
