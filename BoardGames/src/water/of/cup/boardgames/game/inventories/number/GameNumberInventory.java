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
import water.of.cup.boardgames.game.inventories.InventoryScreen;
import water.of.cup.boardgames.game.inventories.InventoryUtils;
import water.of.cup.boardgames.game.inventories.ready.GameReadyCallback;

import java.util.Arrays;

public class GameNumberInventory extends InventoryScreen {

    private final GameInventory gameInventory;
    private final Game game;

    public GameNumberInventory(GameInventory gameInventory) {
        super(gameInventory);
        this.gameInventory = gameInventory;
        this.game = gameInventory.getGame();
    }

    public void build(Player player, GameNumberInventoryCallback callback, String dataKey) {
        char[][] guiSetup = getGuiSetup();
        String[] guiSetupString = formatGuiSetup(guiSetup);

        InventoryGui gui = new InventoryGui(BoardGames.getInstance(), player, game.getGameName(), guiSetupString);

        gui.setFiller(new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1));

        gui.addElement(new StaticGuiElement('w', new ItemStack(Material.WHITE_STAINED_GLASS_PANE), " "));

        ItemStack downButton = InventoryUtils.getCustomTextureHead(InventoryUtils.DOWN_ARROW);
        ItemStack upButton = InventoryUtils.getCustomTextureHead(InventoryUtils.UP_ARROW);

        int[] numAmounts = new int[7];

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
                numAmounts[numIndex] = numAmounts[numIndex] + 1;
                if(numAmounts[numIndex] > 9)
                    numAmounts[numIndex] = 9;

                click.getGui().draw();
                return true;
            },
                ConfigUtil.GUI_UP_ARROW.toString()));

            gui.addElement(new StaticGuiElement(belowChar, downButton, click -> {
                numAmounts[numIndex] = numAmounts[numIndex] - 1;
                if(numAmounts[numIndex] < 0)
                    numAmounts[numIndex] = 0;

                click.getGui().draw();
                return true;
            },
                    ConfigUtil.GUI_DOWN_ARROW.toString()));
        }

        gui.addElement(new StaticGuiElement('x', new ItemStack(Material.RED_STAINED_GLASS_PANE), click -> {
                    Arrays.fill(numAmounts, 0);
                    click.getGui().draw();
                    return true;
                },
                        ConfigUtil.GUI_RESET_NUMBERS.toString()
                )
        );

        gui.addElement(new StaticGuiElement('y', new ItemStack(Material.LIME_STAINED_GLASS_PANE), click -> {
                    gui.close(false);
                    callback.onSubmit(dataKey, getFinalNum(numAmounts));
                    return true;
                },
                        ConfigUtil.GUI_DONE_TEXT.toString()
                )
        );

        gui.setCloseAction(close -> {
            callback.onSubmit(dataKey, getFinalNum(numAmounts));
            return true;
        });

        gui.show(player);
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
        guiSetup[2][0] = 'x';

        int[] startPos = new int[] {1, 2};

        char startChar = 'a';

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
