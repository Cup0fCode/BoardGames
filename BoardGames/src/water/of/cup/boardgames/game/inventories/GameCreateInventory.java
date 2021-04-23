package water.of.cup.boardgames.game.inventories;

import de.themoep.inventorygui.DynamicGuiElement;
import de.themoep.inventorygui.GuiStateElement;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import water.of.cup.boardgames.BoardGames;
import water.of.cup.boardgames.game.Game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class GameCreateInventory {

    private final ArrayList<GameOption> gameOptions;
    private final Game game;

    public GameCreateInventory(GameInventory gameInventory) {
        this.gameOptions = gameInventory.getGameOptions();
        this.game = gameInventory.getGame();
    }

    public void build(Player player, final int page, HashMap<String, Object> cachedGameData, CreateInventoryCallback callback) {
        HashMap<Character, GameOption> characterMap = new HashMap<>();
        ArrayList<GameOption> gameOptions = new ArrayList<>(this.gameOptions);

        // If there are more than 4 options, use page
        if(gameOptions.size() > 4) {
            gameOptions.clear();

            // Render only 3 options to make room for next
            int gameIndex = page * 3;
            for(int i = 0; i < 3; i++) {
                if(gameIndex >= this.gameOptions.size()) break;

                gameOptions.add(this.gameOptions.get(gameIndex));
                gameIndex++;
            }
        }

        HashMap<String, Object> gameData = new HashMap<>();

        if(cachedGameData == null) {
            // Load in all options
            for(GameOption gameOption : this.gameOptions) {
                if(isNumeric(gameOption.getDefaultValue())) {
                    gameData.put(gameOption.getKey(), Integer.parseInt(gameOption.getDefaultValue()));
                } else {
                    gameData.put(gameOption.getKey(), gameOption.getDefaultValue());
                }
            }
        } else {
            // Load in options from past screens
            for(String key : cachedGameData.keySet()) {
                gameData.put(key, cachedGameData.get(key));
            }
        }

        char[][] guiSetup = getGuiSetup(gameOptions, characterMap);
        String[] guiSetupString = formatGuiSetup(guiSetup);

        // Parse each icon into buttons
        InventoryGui gui = new InventoryGui(BoardGames.getInstance(), player, game.getGameName(), guiSetupString);

        gui.setFiller(new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1));

        gui.addElement(new StaticGuiElement('w', new ItemStack(Material.WHITE_STAINED_GLASS_PANE), " "));

        gui.addElement(new StaticGuiElement('x', new ItemStack(Material.SKELETON_SKULL), click -> {
                    int totalPages = (this.gameOptions.size() - 1) / 3;
                    int newPage = page;

                    if(page + 1 > totalPages) {
                        newPage = 0;
                    } else {
                        newPage++;
                    }

                    this.build(player, newPage, gameData, callback);
                    return true;
                },
                        ChatColor.GREEN + "Next Page"
                )
        );

        gui.addElement(new StaticGuiElement('y', new ItemStack(Material.LIME_STAINED_GLASS_PANE), click -> {
                    gui.close(true);
                    callback.onCreateGame(gameData);
                    return true;
                },
                        ChatColor.GREEN + "Create Game"
                )
        );

        // Loop through the middle
        for(int x = 1; x <= 7; x++) {
            char curr = guiSetup[2][x];
            if(characterMap.containsKey(curr)) {
                GameOption gameOption = characterMap.get(curr);
                if(gameOption.getOptionType() != GameOptionType.CUSTOM) {
                    // Add icon
                    String label = gameOption.getLabel() == null ? "" : gameOption.getLabel();

                    gui.addElement(new DynamicGuiElement(curr, () ->
                            new StaticGuiElement(curr, new ItemStack(gameOption.getMaterial()), click -> {
                                click.getEvent().getWhoClicked().sendMessage("Clicking " + gameOption.getKey());
                                return true;
                            },
                                    label + ChatColor.GREEN + gameData.get(gameOption.getKey()).toString() // gameData.get(gameOption.getKey())
                            )));

                    // Add toggle buttons
                    switch (gameOption.getOptionType()) {
                        case TOGGLE: {
                            char belowChar = guiSetup[2 + 1][x];

                            assert characterMap.get(belowChar) != null;
                            assert gameOption.getCustomValues() != null;
                            GuiStateElement toggleElement = new GuiStateElement(belowChar,
                                    new GuiStateElement.State(
                                            change -> {
                                                gameData.put(gameOption.getKey(), gameOption.getCustomValues().get(0));
                                                change.getGui().draw();
                                            },
                                            gameOption.getCustomValues().get(0), // a key to identify this state by
                                            new ItemStack(Material.GREEN_STAINED_GLASS_PANE), // the item to display as an icon
                                            ChatColor.GREEN + gameOption.getCustomValues().get(0) // explanation text what this element does
                                    ),
                                    new GuiStateElement.State(
                                            change -> {
                                                gameData.put(gameOption.getKey(), gameOption.getCustomValues().get(1));
                                                change.getGui().draw();
                                            },
                                            gameOption.getCustomValues().get(1),
                                            new ItemStack(Material.RED_STAINED_GLASS_PANE),
                                            ChatColor.RED + gameOption.getCustomValues().get(1)
                                    )
                            );

                            toggleElement.setState((String) gameData.get(gameOption.getKey()));

                            gui.addElement(toggleElement);
                            break;
                        }
                        case COUNT: {
                            char aboveChar = guiSetup[2 - 1][x];
                            char belowChar = guiSetup[2 + 1][x];

                            assert characterMap.get(aboveChar) != null;
                            assert characterMap.get(belowChar) != null;

                            // if no custom values, use num
                            // up button
                            gui.addElement(new StaticGuiElement(aboveChar, new ItemStack(Material.SKELETON_SKULL), click -> {
                                        if(gameOption.getCustomValues() == null) {
                                            // TODO: add shift/right click to add more/less
                                            gameData.put(gameOption.getKey(), (int) gameData.get(gameOption.getKey()) + 1);
                                        } else {
                                            String currItem = gameData.get(gameOption.getKey()) + "";
                                            int currIndex = gameOption.getCustomValues().indexOf(currItem);
                                            if(currIndex == gameOption.getCustomValues().size() - 1) return true;

                                            String nextElm = gameOption.getCustomValues().get(currIndex + 1);

                                            if(isNumeric(nextElm)) {
                                                gameData.put(gameOption.getKey(), Integer.parseInt(nextElm));
                                            } else {
                                                gameData.put(gameOption.getKey(), nextElm);
                                            }
                                        }

                                        click.getGui().draw();
                                        return true;
                                    },
                                            ChatColor.GREEN + "/\\"
                                    )
                            );

                            // down button
                            gui.addElement(new StaticGuiElement(belowChar, new ItemStack(Material.SKELETON_SKULL), click -> {
                                        if(gameOption.getCustomValues() == null) {
                                            if((int) gameData.get(gameOption.getKey()) == 0) return true;

                                            gameData.put(gameOption.getKey(), (int) gameData.get(gameOption.getKey()) - 1);
                                        } else {
                                            String currItem = gameData.get(gameOption.getKey()) + "";
                                            int currIndex = gameOption.getCustomValues().indexOf(currItem);
                                            if(currIndex == 0) return true;

                                            String nextElm = gameOption.getCustomValues().get(currIndex - 1);

                                            if(isNumeric(nextElm)) {
                                                gameData.put(gameOption.getKey(), Integer.parseInt(nextElm));
                                            } else {
                                                gameData.put(gameOption.getKey(), nextElm);
                                            }

                                        }

                                        click.getGui().draw();
                                        return true;
                                    },
                                            ChatColor.GREEN + "\\/"
                                    )
                            );
                            break;
                        }
                    }
                }

            }
        }

        // TODO: figure out when they close vs auto closed, add create button
        gui.setCloseAction(close -> {
            // They left the inventory, do not create game
            callback.onCreateGame(null);
            return false;
        });

        gui.show(player);
    }

    public void build(Player player, CreateInventoryCallback callback) {
        build(player, 0, null, callback);
    }

    private char[][] getGuiSetup(ArrayList<GameOption> gameOptions, HashMap<Character, GameOption> characterMap) {
        char[][] guiSetup = new char[6][9];

        // Fill all with spaces
        for (char[] chars : guiSetup) {
            Arrays.fill(chars, ' ');
        }

        // Define option background
        for(int y = 1; y <= 3; y++) {
            for(int x = 1; x <= 7; x++) {
                guiSetup[y][x] = 'w';
            }
        }

        // Define create
        guiSetup[5][4] = 'y';

        // Define a "Next" button
        if(this.gameOptions.size() > 4)
            guiSetup[2][7] = 'x';

        int[] startPos = new int[] {1, 2};

        char startChar = 'a';

        // Define char layout
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

        return guiSetup;
    }

    private String[] formatGuiSetup(char[][] guiSetup) {
        String[] guiSetupString = new String[guiSetup.length];
        for(int y = 0; y < guiSetup.length; y++) {
            StringBuilder row = new StringBuilder();
            for(int x = 0; x < guiSetup[y].length; x++) {
                row.append(guiSetup[y][x]);
            }
            guiSetupString[y] = row.toString();
        }
        return guiSetupString;
    }

    private boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

}
