package water.of.cup.boardgames.game.games.chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;

import water.of.cup.boardgames.config.ConfigUtil;
import water.of.cup.boardgames.game.GamePlayer;
import water.of.cup.boardgames.game.games.checkers.Checkers;
import water.of.cup.boardgames.game.inventories.GameInventory;
import water.of.cup.boardgames.game.inventories.GameOption;
import water.of.cup.boardgames.game.inventories.GameOptionType;
import water.of.cup.boardgames.game.storage.BoardGamesStorageType;

public class ChessInventory extends GameInventory {

    private final Chess game;

    public ChessInventory(Chess game) {
        super(game);
        this.game = game;
    }

    @Override
    protected ArrayList<GameOption> getOptions() {
        ArrayList<GameOption> options = new ArrayList<>();

        List<String> rankedValues =  Arrays.asList(ConfigUtil.GUI_RANKED_OPTION_TEXT.toString(), ConfigUtil.GUI_UNRANKED_OPTION_TEXT.toString());
        GameOption ranked = new GameOption("ranked", Material.EXPERIENCE_BOTTLE, GameOptionType.TOGGLE, null, rankedValues.get(0), rankedValues);

        if(ConfigUtil.getBoolean("settings.games.Chess.database.rating"))
            options.add(ranked);

        List<String> timeValues =  Arrays.asList( "10 min", "20 min", "30 min", "40 min", "50 min", "60 min");
        GameOption times = new GameOption("time", Material.LEATHER, GameOptionType.COUNT, null, timeValues.get(0), timeValues);
        options.add(times);

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

    @Override
    protected int getMinGame() {
        return 2;
    }

    @Override
    protected boolean hasTeamSelect() {
        return true;
    }

    @Override
    protected boolean hasGameWagers() {
        return true;
    }

    @Override
    protected boolean hasWagerScreen() {
        return true;
    }

    @Override
    protected boolean hasForfeitScreen() {
        return true;
    }

    @Override
    protected void onGameCreate(HashMap<String, Object> gameData, ArrayList<GamePlayer> players) {
        for(GamePlayer player : players) {
            player.getPlayer().sendMessage(ConfigUtil.CHAT_WELCOME_GAME.buildString(game.getAltName()));
        }

        game.startGame();
    }
}