package water.of.cup.boardgames.game.games.uno;

import java.util.ArrayList;
import java.util.HashMap;

import water.of.cup.boardgames.config.ConfigUtil;
import water.of.cup.boardgames.game.GamePlayer;
import water.of.cup.boardgames.game.inventories.GameInventory;
import water.of.cup.boardgames.game.inventories.GameOption;

public class UnoInventory extends GameInventory {

    private final Uno game;

    public UnoInventory(Uno game) {
        super(game);
        this.game = game;
    }

    @Override
    protected ArrayList<GameOption> getOptions() {
        ArrayList<GameOption> options = new ArrayList<>();

//        List<String> rankedValues =  Arrays.asList("ranked", "unranked");
//        GameOption ranked = new GameOption("ranked", Material.EXPERIENCE_BOTTLE, GameOptionType.TOGGLE, null, rankedValues.get(0), rankedValues);
//        options.add(ranked);

        return options;
    }

    @Override
    protected int getMaxQueue() {
        return 3;
    }

    @Override
    protected int getMaxGame() {
        return 8;
    }

    // Whether or not game players are allowed to place bets
    @Override
    protected boolean hasGameWagers() {
        return false;
    }

    // Whether or not outside players can place bets, hasGameWagers must be true
    @Override
    public boolean hasWagerScreen() {
        return false;
    }

    @Override
    protected void onGameCreate(HashMap<String, Object> gameData, ArrayList<GamePlayer> players) {
        for(GamePlayer player : players) {
            player.getPlayer().sendMessage(ConfigUtil.CHAT_WELCOME_GAME.buildString(game.getAltName()));
        }

        game.startGame();
    }

	@Override
	protected int getMinGame() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	protected boolean hasTeamSelect() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean hasForfeitScreen() {
		// TODO Auto-generated method stub
		return true;
	}


}