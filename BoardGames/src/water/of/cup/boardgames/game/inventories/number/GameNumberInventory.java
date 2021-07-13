package water.of.cup.boardgames.game.inventories.number;

import de.themoep.inventorygui.DynamicGuiElement;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import water.of.cup.boardgames.BoardGames;
import water.of.cup.boardgames.config.ConfigUtil;
import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.MathUtils;
import water.of.cup.boardgames.game.inventories.GameInventory;
import water.of.cup.boardgames.game.inventories.GameOption;
import water.of.cup.boardgames.game.inventories.InventoryScreen;
import water.of.cup.boardgames.game.inventories.InventoryUtils;
import water.of.cup.boardgames.game.inventories.ready.GameReadyCallback;

import java.util.Arrays;

public class GameNumberInventory extends InventoryScreen {

    private final BoardGames instance = BoardGames.getInstance();
    private final GameInventory gameInventory;
    private final Game game;

    public GameNumberInventory(GameInventory gameInventory) {
        super(gameInventory);
        this.gameInventory = gameInventory;
        this.game = gameInventory.getGame();
    }

    public void build(Player player, GameNumberInventoryCallback callback, GameOption gameOption, int currVal) {
        char[][] guiSetup = getGuiSetup();
        String[] guiSetupString = formatGuiSetup(guiSetup);

        String invName = gameOption.getLabel() != null ? gameOption.getLabel() : game.getAltName();
        InventoryGui gui = new InventoryGui(BoardGames.getInstance(), player, invName, guiSetupString);

        gui.setFiller(new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1));

        gui.addElement(new StaticGuiElement('w', new ItemStack(Material.WHITE_STAINED_GLASS_PANE), " "));

        ItemStack downButton = InventoryUtils.getCustomTextureHead(InventoryUtils.DOWN_ARROW);
        ItemStack upButton = InventoryUtils.getCustomTextureHead(InventoryUtils.UP_ARROW);

        int[] numAmounts = new int[7];
        setNumAmounts(numAmounts, currVal);

        for(int x = 1; x <= 7; x++) {
            char curr = guiSetup[2][x];
            char aboveChar = guiSetup[2 - 1][x];
            char belowChar = guiSetup[2 + 1][x];
            final int numIndex = x - 1;

            gui.addElement(new DynamicGuiElement(curr, () ->
                    new StaticGuiElement(curr, getItemStack(numAmounts[numIndex]), numAmounts[numIndex], click -> true,
                           ChatColor.GREEN + getNumString(numAmounts)
                    )));

            gui.addElement(new StaticGuiElement(aboveChar, upButton, click -> {
                incrementNumAmounts(numAmounts, numIndex, 1, gameOption);

                click.getGui().draw();
                return true;
            },
                ConfigUtil.GUI_UP_ARROW.toString()));

            gui.addElement(new StaticGuiElement(belowChar, downButton, click -> {
                incrementNumAmounts(numAmounts, numIndex, -1, gameOption);

                click.getGui().draw();
                return true;
            },
                    ConfigUtil.GUI_DOWN_ARROW.toString()));
        }

        gui.addElement(new StaticGuiElement('z', new ItemStack(Material.RED_STAINED_GLASS_PANE), click -> {
                    setNumAmounts(numAmounts, gameOption.getMinIntValue());
                    click.getGui().draw();
                    return true;
                },
                        ConfigUtil.GUI_RESET_NUMBERS.toString()
                )
        );

        gui.addElement(new StaticGuiElement('y', new ItemStack(Material.LIME_STAINED_GLASS_PANE), click -> {
                    gui.close(false);
                    callback.onSubmit(gameOption.getKey(), getFinalNum(numAmounts));
                    return true;
                },
                        ConfigUtil.GUI_DONE_TEXT.toString()
                )
        );

        // Half amount
        gui.addElement(new StaticGuiElement('a', new ItemStack(Material.BLUE_STAINED_GLASS_PANE), click -> {
                    int currAmount = getFinalNum(numAmounts);
                    setNumAmounts(numAmounts, currAmount / 2);
                    normalizeNumAmounts(numAmounts, gameOption);
                    click.getGui().draw();
                    return true;
                },
                        ConfigUtil.GUI_NUMBERS_HALF.toString()
                )
        );

        // Double amount
        gui.addElement(new StaticGuiElement('b', new ItemStack(Material.BLUE_STAINED_GLASS_PANE), click -> {
                    int currAmount = getFinalNum(numAmounts);
                    setNumAmounts(numAmounts, currAmount * 2);
                    normalizeNumAmounts(numAmounts, gameOption);
                    click.getGui().draw();
                    return true;
                },
                        ConfigUtil.GUI_NUMBERS_DOUBLE.toString()
                )
        );

        // Max amount
        gui.addElement(new StaticGuiElement('c', new ItemStack(Material.BLUE_STAINED_GLASS_PANE), click -> {
                    int maxAmount = gameOption.requiresEconomy() ? (int) instance.getEconomy().getBalance(player) : gameOption.getMaxIntValue();
                    setNumAmounts(numAmounts, maxAmount);
                    normalizeNumAmounts(numAmounts, gameOption);
                    click.getGui().draw();
                    return true;
                },
                        ConfigUtil.GUI_NUMBERS_MAX.toString()
                )
        );

        gui.setCloseAction(close -> {
            callback.onSubmit(gameOption.getKey(), currVal);
            return true;
        });

        gui.show(player);
    }

    private void incrementNumAmounts(int[] numAmounts, int numIndex, int amount, GameOption gameOption) {
        numAmounts[numIndex] = numAmounts[numIndex] + amount;
        if(numAmounts[numIndex] < 0) {
            numAmounts[numIndex] = 0;
        } else if(numAmounts[numIndex] > 9) {
            numAmounts[numIndex] = 9;
        }

        normalizeNumAmounts(numAmounts, gameOption);
    }

    private void setNumAmounts(int[] numAmounts, int newNum) {
        Arrays.fill(numAmounts, 0);

        if(newNum > 9999999)
            newNum = 9999999;

        int num = newNum;
        for(int i = numAmounts.length - 1; i >= 0; i--) {
            numAmounts[i] = num % 10;
            num /= 10;
        }
    }

    private void normalizeNumAmounts(int[] numAmounts, GameOption gameOption) {
        int currAmount = getFinalNum(numAmounts);
        if(currAmount > gameOption.getMaxIntValue()) {
            setNumAmounts(numAmounts, gameOption.getMaxIntValue());
        } else if(currAmount < gameOption.getMinIntValue()) {
            setNumAmounts(numAmounts, gameOption.getMinIntValue());
        }
    }

    private char[][] getGuiSetup() {
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

        // Define clear
        guiSetup[2][0] = 'z';

        // Define 1/2
        guiSetup[4][3] = 'a';
        // Define 2x
        guiSetup[4][4] = 'b';
        // Define max
        guiSetup[4][5] = 'c';

        int[] startPos = new int[] {1, 2};

        char startChar = 'd';

        for(int i = 0; i < 7; i++) {
            guiSetup[startPos[1]][startPos[0]] = startChar;
            guiSetup[startPos[1] + 1][startPos[0]] = (char) (startChar + 1);
            guiSetup[startPos[1] - 1][startPos[0]] = (char) (startChar + 2);

            startChar += 3;

            startPos[0] += 1;
        }

        return guiSetup;
    }

    private String getNumString(int[] nums) {
        StringBuilder numString = new StringBuilder();
        for(int num : nums) {
            numString.append(num);
        }
        return numString.toString();
    }

    private int getFinalNum(int[] nums) {
        String numString = getNumString(nums);
        if(!MathUtils.isNumeric(numString)) return 0;
        return Integer.parseInt(numString);
    }

    private ItemStack getItemStack(int num) {
        if(num == 0) return new ItemStack(Material.GOLD_NUGGET);
        return new ItemStack(Material.GOLD_INGOT);
    }
}
