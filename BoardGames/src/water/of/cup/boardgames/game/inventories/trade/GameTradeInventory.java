package water.of.cup.boardgames.game.inventories.trade;

import de.themoep.inventorygui.GuiStorageElement;
import de.themoep.inventorygui.InventoryGui;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import water.of.cup.boardgames.BoardGames;
import water.of.cup.boardgames.config.ConfigUtil;
import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.inventories.GameInventory;
import water.of.cup.boardgames.game.inventories.GameOption;
import water.of.cup.boardgames.game.inventories.InventoryScreen;

import java.util.ArrayList;

public class GameTradeInventory extends InventoryScreen {

    private final GameInventory gameInventory;
    private final ArrayList<GameOption> gameOptions;
    private final Game game;

    public GameTradeInventory(GameInventory gameInventory) {
        super(gameInventory);
        this.gameInventory = gameInventory;
        this.gameOptions = gameInventory.getGameOptions();
        this.game = gameInventory.getGame();
    }

    public void build(Player player) {
        String[] guiSetup = getGuiSetup();
        InventoryGui gui = new InventoryGui(BoardGames.getInstance(), player, ConfigUtil.GUI_GAME_TRADE_TITLE.toString(), guiSetup);

        gui.setFiller(new ItemStack(Material.BLACK_STAINED_GLASS_PANE));

        Inventory inv = Bukkit.createInventory(null, InventoryType.CHEST);
        gui.addElement(new GuiStorageElement('i', inv));

    }

    private String[] getGuiSetup() {
        return new String[] {
                "         ",
                " iii jjj ",
                " iii jjj ",
                " iii jjj ",
                " iii jjj ",
                "         ",
        };
    }
}
