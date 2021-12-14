package water.of.cup.boardgames.game.games.poker;

import org.bukkit.Material;
import water.of.cup.boardgames.config.ConfigUtil;
import water.of.cup.boardgames.game.GamePlayer;
import water.of.cup.boardgames.game.inventories.GameInventory;
import water.of.cup.boardgames.game.inventories.GameOption;
import water.of.cup.boardgames.game.inventories.GameOptionType;

import java.util.ArrayList;
import java.util.HashMap;

public class PokerInventory extends GameInventory {

    private final Poker game;

    public PokerInventory(Poker game) {
        super(game);
        this.game = game;
    }

    @Override
    protected ArrayList<GameOption> getOptions() {
        ArrayList<GameOption> options = new ArrayList<GameOption>();
        GameOption minEntry = new GameOption("minEntry", Material.GOLD_NUGGET, GameOptionType.COUNT, ConfigUtil.GUI_POKER_MIN_ENTRY_LABEL.toString(), "1", true, 1, Integer.MAX_VALUE);
        options.add(minEntry);

        GameOption raiseLimit = new GameOption("raiseLimit", Material.GOLD_NUGGET, GameOptionType.COUNT, ConfigUtil.GUI_POKER_RAISE_LIMIT_LABEL.toString(), "1", false, 1, Integer.MAX_VALUE);
        options.add(raiseLimit);
        return options;
    }

    @Override
    protected int getMaxQueue() {
        return 7;
    }

    // Max game to 1 to auto start the game on creation
    @Override
    protected int getMaxGame() {
        return 1;
    }

    @Override
    protected int getMinGame() {
        return 2;
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
        return true;
    }

    @Override
    protected void onGameCreate(HashMap<String, Object> hashMap, ArrayList<GamePlayer> arrayList) {
        game.initGame();
    }
}
