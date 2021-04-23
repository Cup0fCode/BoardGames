package water.of.cup.boardgames.game.inventories;

import de.themoep.inventorygui.InventoryGui;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import water.of.cup.boardgames.BoardGames;
import water.of.cup.boardgames.game.Game;

import java.util.ArrayList;

public class GameWaitPlayersInventory {

    private final ArrayList<GameOption> gameOptions;
    private final Game game;

    public GameWaitPlayersInventory(Game game, ArrayList<GameOption> gameOptions) {
        this.gameOptions = gameOptions;
        this.game = game;
    }

    public void build(Player player) {
        String[] temp = new String[] {
                "         ",
                "         ",
                "         ",
                "         ",
                "         ",
                "         ",
        };

        InventoryGui gui = new InventoryGui(BoardGames.getInstance(), player, game.getGameName(), temp);

        gui.setFiller(new ItemStack(Material.BLACK_STAINED_GLASS_PANE));

        gui.show(player);
    }

}
