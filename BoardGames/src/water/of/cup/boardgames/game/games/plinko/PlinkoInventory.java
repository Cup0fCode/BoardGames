package water.of.cup.boardgames.game.games.plinko;

import org.bukkit.Material;
import water.of.cup.boardgames.game.GamePlayer;
import water.of.cup.boardgames.game.inventories.GameInventory;
import water.of.cup.boardgames.game.inventories.GameOption;
import water.of.cup.boardgames.game.inventories.GameOptionType;
import water.of.cup.boardgames.config.ConfigUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class PlinkoInventory extends GameInventory {

	private final Plinko game;

	public PlinkoInventory(Plinko game) {
		super(game);
		this.game = game;
	}

	@Override
	protected ArrayList<GameOption> getOptions() {
		ArrayList<GameOption> options = new ArrayList<GameOption>();
		GameOption bet = new GameOption("betAmount", Material.GOLD_NUGGET, GameOptionType.COUNT, null, "0", true);
		options.add(bet);

		List<String> riskValues = Arrays.asList(ConfigUtil.GUI_PLINKO_LOW_RISK.toString(), ConfigUtil.GUI_PLINKO_NORMAL_RISK.toString(), ConfigUtil.GUI_PLINKO_HIGH_RISK.toString());
		GameOption risk = new GameOption("risk", Material.EXPERIENCE_BOTTLE, GameOptionType.COUNT, null, riskValues.get(1), riskValues);
		options.add(risk);
		return options;
	}

	@Override
	protected int getMaxQueue() {
		return 0;
	}

	@Override
	protected int getMaxGame() {
		return 1;
	}

	@Override
	protected int getMinGame() {
		return 1;
	}

	@Override
	protected boolean hasTeamSelect() {
		return false;
	}

	@Override
	protected boolean hasGameWagers() {
		return false;
	}

	@Override
	protected boolean hasWagerScreen() {
		return false;
	}

	@Override
	protected boolean hasForfeitScreen() {
		return false;
	}

	@Override
	protected void onGameCreate(HashMap<String, Object> gameData, ArrayList<GamePlayer> players) {
//        for(GamePlayer player : players) {
//            player.getPlayer().sendMessage(ConfigUtil.CHAT_WELCOME_GAME.buildString("Slots"));
//        }

		game.startGame();
	}
}
