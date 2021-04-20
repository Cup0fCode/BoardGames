package water.of.cup.boardgames.game;

import org.bukkit.entity.Player;

public class GamePlayer {
	private Player player;

	public GamePlayer(Player player) {
		this.player = player;
	}
	
	public Player getPlayer() {
		return player;
	}
}
