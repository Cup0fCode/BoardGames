package water.of.cup.boardgames.game.games.blackjack;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import water.of.cup.boardgames.game.GamePlayer;
import water.of.cup.boardgames.config.ConfigUtil;

public class BlackjackTurnTimer extends BukkitRunnable {
	private static double CLOCKTIME = 60;
	private double lastTimeChange;
	private double timeLeft;
	private Blackjack game;
	private Player player;

	public BlackjackTurnTimer(Blackjack game) {
		lastTimeChange = System.currentTimeMillis() / 1000;
		timeLeft = CLOCKTIME;
		this.game = game;
		
	}
	
	public void start(Player player) {
		lastTimeChange = System.currentTimeMillis() / 1000;
		timeLeft = CLOCKTIME;
		this.player = player;
	}

	@Override
	public void run() {
		if (player == null)
			return;
		
		double time = System.currentTimeMillis() / 1000;
		double difference = time - lastTimeChange;
		lastTimeChange = time;
		timeLeft -= difference;

		if (timeLeft <= 0) {
			player = null;
			game.nextTurn();
			return;
		}
		sendPlayersClockTimes();

		
	}

	public void sendPlayersClockTimes() {
		String timeText = ConfigUtil.CHAT_BLACKJACK_PLAYER_TIMER.buildString(player.getDisplayName(), (int) (timeLeft / 60), (int) (timeLeft % 60));
		for (GamePlayer gamePlayer : game.getGamePlayers())
			gamePlayer.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR,
					TextComponent.fromLegacyText(ChatColor.YELLOW + timeText));

	}
}