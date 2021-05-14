package water.of.cup.boardgames.game.games.checkers;

import water.of.cup.boardgames.game.GamePlayer;
import water.of.cup.boardgames.game.inventories.GameInventory;
import water.of.cup.boardgames.game.inventories.GameOption;

import java.util.ArrayList;
import java.util.HashMap;

public class CheckersInventory extends GameInventory {

    private final Checkers game;

    public CheckersInventory(Checkers game) {
        super(game);
        this.game = game;
    }

    @Override
    protected ArrayList<GameOption> getOptions() {
        return new ArrayList<>();
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
            player.getPlayer().sendMessage("Welcome to Checkers!");
        }

        game.startGame();
    }
}
