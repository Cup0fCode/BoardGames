package water.of.cup.boardgames.game.games.roulette;

import water.of.cup.boardgames.game.GamePlayer;
import water.of.cup.boardgames.game.inventories.GameInventory;
import water.of.cup.boardgames.game.inventories.GameOption;

import java.util.ArrayList;
import java.util.HashMap;

public class RouletteInventory extends GameInventory {

    private final Roulette game;

    public RouletteInventory(Roulette game) {
        super(game);
        this.game = game;
    }

    @Override
    protected ArrayList<GameOption> getOptions() {
        return null;
    }

    @Override
    protected int getMaxQueue() {
        return 1;
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
        return true;
    }

    @Override
    protected void onGameCreate(HashMap<String, Object> hashMap, ArrayList<GamePlayer> arrayList) {
        game.startGame();
    }
}
