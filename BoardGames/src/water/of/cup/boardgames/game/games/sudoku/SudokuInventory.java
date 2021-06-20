package water.of.cup.boardgames.game.games.sudoku;

import java.util.ArrayList;
import java.util.HashMap;

import water.of.cup.boardgames.game.GamePlayer;
import water.of.cup.boardgames.game.inventories.GameInventory;
import water.of.cup.boardgames.game.inventories.GameOption;

public class SudokuInventory extends GameInventory {

    private final Sudoku game;

    public SudokuInventory(Sudoku game) {
        super(game);
        this.game = game;
    }

    @Override
    protected ArrayList<GameOption> getOptions() {
        return new ArrayList<>();
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
        game.startGame();
    }
}
