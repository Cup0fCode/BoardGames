package water.of.cup.boardgames.game.inventories;

import de.themoep.inventorygui.InventoryGui;
import org.bukkit.Bukkit;
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
        // TODO: if idle, build create game

        char[][] guiSetup = new char[6][9];
        ArrayList<GameOption> gameOptions = getOptions();

//
        for (char[] chars : guiSetup) {
            Arrays.fill(chars, ' ');
        }

        for(int y = 1; y <= 3; y++) {
            for(int x = 1; x <= 7; x++) {
                guiSetup[y][x] = 'w';
            }
        }

        // TODO: figure out what to do with this when game is created
        HashMap<Character, GameOption> characterMap = new HashMap<>();

        // TODO: Maybe set to class variable after created
        HashMap<String, Object> gameData = new HashMap<>();

        int[] startPos = new int[] {1, 2};

        char startChar = 'a';

        for(GameOption gameOption : gameOptions) {
            switch (gameOption.getOptionType()) {
                case TOGGLE: {
                    guiSetup[startPos[1]][startPos[0]] = startChar;
                    guiSetup[startPos[1] + 1][startPos[0]] = (char) (startChar + 1);

                    characterMap.put(startChar, gameOption);
                    characterMap.put((char) (startChar + 1), gameOption);

                    startChar += 2;
                    break;
                }
                case COUNT: {
                    guiSetup[startPos[1] - 1][startPos[0]] = startChar;
                    guiSetup[startPos[1]][startPos[0]] = (char) (startChar + 1);
                    guiSetup[startPos[1] + 1][startPos[0]] = (char) (startChar + 2);

                    characterMap.put(startChar, gameOption);
                    characterMap.put((char) (startChar + 1), gameOption);
                    characterMap.put((char) (startChar + 2), gameOption);

                    startChar += 3;
                    break;
                }
            }

            // increment startPos
            startPos[0] += 2;
        }

        String[] guiSetupString = new String[6];
        for(int y = 0; y < guiSetup.length; y++) {
            StringBuilder row = new StringBuilder();
            for(int x = 0; x < guiSetup[y].length; x++) {
                row.append(guiSetup[y][x]);
            }
            guiSetupString[y] = row.toString();
        }

        Bukkit.getLogger().info("[BoardGamesDebug] Gui Setup: ");
        for(String line : guiSetupString) {
            Bukkit.getLogger().info("'" + line + "'");
        }

        // TODO: Parse each icon into buttons

//        InventoryGui gui = new InventoryGui(BoardGames.getInstance(), player, game.getGameName(), guiSetupString);


//        getOptions();
//        callback.onGameCreated();
    }

    public Game getGame() {
        return this.game;
    }

}
