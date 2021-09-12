package water.of.cup.boardgames.game.games.mines;

import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import water.of.cup.boardgames.BoardGames;
import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.inventories.GameInventory;
import water.of.cup.boardgames.game.inventories.InventoryScreen;
import water.of.cup.boardgames.game.inventories.InventoryUtils;
import water.of.cup.boardgames.config.ConfigUtil;

import java.util.Arrays;

public class MinesInGameInventory extends InventoryScreen {

    private final Game game;

    public MinesInGameInventory(GameInventory gameInventory) {
        super(gameInventory);
        this.game = gameInventory.getGame();
    }

    public void build(Player player, MinesInventoryCallback callback) {
        String[] guiSetup = getGuiSetup();

        InventoryGui gui = new InventoryGui(BoardGames.getInstance(), player, game.getAltName(), guiSetup);

        gui.setFiller(new ItemStack(Material.BLACK_STAINED_GLASS_PANE));

        gui.addElement(new StaticGuiElement('w', new ItemStack(Material.WHITE_STAINED_GLASS_PANE), " "));

        ItemStack playerHead = InventoryUtils.getPlayerHead(player);
        gui.addElement(new StaticGuiElement('s', playerHead, click -> true,
                        ChatColor.GREEN + player.getDisplayName()
                )
        );

        gui.addElement(new StaticGuiElement('a', new ItemStack(Material.LIME_STAINED_GLASS_PANE), click -> {
                    gui.close(true);
                    callback.onCashOut();
                    return true;
                },
                        ConfigUtil.GUI_MINES_CASH_OUT.toString()
                )
        );

        gui.setCloseAction(close -> false);

        gui.show(player);
    }

    private String[] getGuiSetup() {
        char[][] guiSetup = new char[6][9];

        // Fill all with spaces
        for (char[] chars : guiSetup) {
            Arrays.fill(chars, ' ');
        }

        // Define background
        for(int y = 1; y <= 4; y++) {
            for(int x = 1; x <= 7; x++) {
                guiSetup[y][x] = 'w';
            }
        }

        guiSetup[2][4] = 's'; // Player skull
        guiSetup[3][4] = 'a'; // End game button

        return formatGuiSetup(guiSetup);
    }
}
