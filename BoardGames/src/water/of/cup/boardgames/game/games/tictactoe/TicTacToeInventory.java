package water.of.cup.boardgames.game.games.tictactoe;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.GamePlayer;
import water.of.cup.boardgames.game.inventories.GameInventory;
import water.of.cup.boardgames.game.inventories.GameOption;
import water.of.cup.boardgames.game.inventories.GameOptionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class TicTacToeInventory extends GameInventory {

    private final Game game;

    public TicTacToeInventory(Game game) {
        super(game);
        this.game = game;
    }

    @Override
    protected ArrayList<GameOption> getOptions() {
        ArrayList<GameOption> options = new ArrayList<>();

        List<String> rankedValues =  Arrays.asList("ranked", "unranked");
        GameOption ranked = new GameOption("ranked", Material.EXPERIENCE_BOTTLE, GameOptionType.TOGGLE, null, rankedValues.get(0), rankedValues);
        options.add(ranked);

        List<String> derpedValues =  Arrays.asList("derped", "underped");
        GameOption derped = new GameOption("derped", Material.QUARTZ, GameOptionType.TOGGLE, null, derpedValues.get(0), derpedValues);
        options.add(derped);

        List<String> listValues =  Arrays.asList("hello", "jeff", "test");
        GameOption listTest = new GameOption("list", Material.LEATHER, GameOptionType.COUNT, null, listValues.get(0), listValues);
        options.add(listTest);

        List<String> cheekyValues =  Arrays.asList("cheeky", "not cheeky");
        GameOption cheeky = new GameOption("cheeky", Material.QUARTZ, GameOptionType.TOGGLE, null, cheekyValues.get(0), cheekyValues);
        options.add(cheeky);

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

    // Whether or not game players are allowed to place bets
    @Override
    protected boolean hasGameWagers() {
        return true;
    }

    // Whether or not outside players can place bets, hasGameWagers must be true
    @Override
    public boolean hasWagerScreen() {
        return true;
    }

    @Override
    protected void onGameCreate(HashMap<String, Object> gameData, ArrayList<GamePlayer> players) {
        for(GamePlayer player : players) {
            player.getPlayer().sendMessage("Welcome to TIC TAC TOE!");
        }
        // TODO: start game set vars
    }


}
