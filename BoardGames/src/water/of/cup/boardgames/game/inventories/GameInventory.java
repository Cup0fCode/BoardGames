package water.of.cup.boardgames.game.inventories;

import de.themoep.inventorygui.InventoryGui;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import water.of.cup.boardgames.BoardGames;
import water.of.cup.boardgames.game.Game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public abstract class GameInventory {

    private final Game game;
    protected abstract ArrayList<GameOption> getOptions();
    protected abstract int getMaxQueue();
    protected abstract int getMaxGame();

    /*
        - Create game screen
            - define options
            - open if game is idle
        - Waiting players screen
        - Join players screen
        - Confirm players screen
        - Spectator screen
     */

    public GameInventory(Game game) {
        this.game = game;
    }

    public void build(Player player, GameInventoryCallback callback) {
        // if idle, build create game

        // save game settings

//        String[] guiSetup = {
//                "  s i z  ",
//                "  ggggg  ",
//                "  fpdnl  "
//        };

        char[][] guiSetup = new char[6][9];
        ArrayList<GameOption> gameOptions = getOptions();

//
        for (char[] chars : guiSetup) {
            Arrays.fill(chars, ' ');
        }

        for(int y = 1; y <= 3; y++) {
            for(int x = 1; x <= 8; x++) {
                guiSetup[y][x] = 'w';
            }
        }

        HashMap<Character, GameOption> characterMap = new HashMap<>();
        HashMap<String, Object> gameData = new HashMap<>();

        int[] startPos = new int[] {1, 2};

        char startChar = 'a';

        for(GameOption gameOption : gameOptions) {
            switch (gameOption.getOptionType()) {
                case TOGGLE: {
                    guiSetup[startPos[1]][startPos[0]] = startChar;
                    guiSetup[startPos[1] - 1][startPos[0]] = (char) (startChar + 1);

                    characterMap.put(startChar, gameOption);
                    characterMap.put((char) (startChar + 1), gameOption);

                    startChar += 2;
                    break;
                }
                case COUNT: {
                    guiSetup[startPos[1] + 1][startPos[0]] = startChar;
                    guiSetup[startPos[1]][startPos[0]] = (char) (startChar + 1);
                    guiSetup[startPos[1] - 1][startPos[0]] = (char) (startChar + 2);

                    characterMap.put(startChar, gameOption);
                    characterMap.put((char) (startChar + 1), gameOption);
                    characterMap.put((char) (startChar + 2), gameOption);

                    startChar += 3;
                    break;
                }
            }
            // increment startPos

        }

//        InventoryGui gui = new InventoryGui(BoardGames.getInstance(), player, game.getGameName(), guiSetup);


//        getOptions();
//        callback.onGameCreated();
    }

    public Game getGame() {
        return this.game;
    }

}
