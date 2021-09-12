package water.of.cup.boardgames.game.games.hilo;

import org.bukkit.Material;
import water.of.cup.boardgames.game.GamePlayer;
import water.of.cup.boardgames.game.inventories.GameInventory;
import water.of.cup.boardgames.game.inventories.GameOption;
import water.of.cup.boardgames.game.inventories.GameOptionType;
import water.of.cup.boardgames.config.ConfigUtil;

import java.util.ArrayList;
import java.util.HashMap;

public class HiLoInventory extends GameInventory {
    private final HiLo game;
    public HiLoInventory(HiLo game) {
        super(game);
        this.game = game;
    }

    @Override
    protected ArrayList<GameOption> getOptions() {
        ArrayList<GameOption> options = new ArrayList<GameOption>();
        GameOption bet = new GameOption("betAmount", Material.GOLD_NUGGET, GameOptionType.COUNT, ConfigUtil.GUI_BET_AMOUNT_LABEL.toString(), "1", true, 1, Integer.MAX_VALUE);
        options.add(bet);

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
    protected void onGameCreate(HashMap<String, Object> hashMap, ArrayList<GamePlayer> arrayList) {
        game.startGame();
    }

    @Override
    public boolean hasCustomInGameInventory() {
        return true;
    }
}
