package water.of.cup.boardgames.game.games.blackjack;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import water.of.cup.boardgames.game.GamePlayer;
import water.of.cup.boardgames.config.ConfigUtil;

public class BlackjackNextGameTimer extends BukkitRunnable {
	private static double TIME_UNTIL_START = 20;
	private double lastTimeChange;
	private double timeLeft;
	private Blackjack game;

	public BlackjackNextGameTimer(Blackjack game) {
		lastTimeChange = System.currentTimeMillis() / 1000;
		timeLeft = TIME_UNTIL_START;
		this.game = game;
		
	}

	@Override
	public void run() {
		double time = System.currentTimeMillis() / 1000;
		double difference = time - lastTimeChange;
		lastTimeChange = time;
		
		if (game.getAmountOfBets() == 0)
			return;
		
		timeLeft -= difference;

		if (timeLeft <= 0) {
			// game.star;
			game.startRound();
			cancel();
			return;
		}
		sendPlayersClockTimes();

		
	}

	public void sendPlayersClockTimes() {
		String timeText = ConfigUtil.CHAT_BLACKJACK_START_TIMER.buildString((int) (timeLeft / 60), (int) (timeLeft % 60));
		for (GamePlayer gamePlayer : game.getGamePlayers())
			gamePlayer.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR,
					TextComponent.fromLegacyText(ChatColor.YELLOW + timeText));

	}
}
