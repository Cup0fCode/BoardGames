package water.of.cup.boardgames.game.games.tictactoe;

import org.bukkit.Material;
import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.inventories.GameInventory;
import water.of.cup.boardgames.game.inventories.GameOption;
import water.of.cup.boardgames.game.inventories.GameOptionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class TicTacToeInventory extends GameInventory {

    public TicTacToeInventory(Game game) {
        super(game);
    }

    @Override
    protected ArrayList<GameOption> getOptions() {
        ArrayList<GameOption> options = new ArrayList<>();

        List<String> rankedValues =  Arrays.asList("unranked", "ranked");
        GameOption ranked = new GameOption("ranked", Material.EXPERIENCE_BOTTLE, GameOptionType.TOGGLE, rankedValues.get(0), rankedValues);
        options.add(ranked);

        GameOption wager = new GameOption("wager", Material.GOLD_INGOT, GameOptionType.COUNT, "0");
        options.add(wager);

        return options;
    }

    @Override
    protected int getMaxQueue() {
        return 3;
    }

    @Override
    protected int getMaxGame() {
        return 2;
    }
}
