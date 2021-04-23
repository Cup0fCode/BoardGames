package water.of.cup.boardgames.game.inventories;

import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import water.of.cup.boardgames.BoardGames;
import water.of.cup.boardgames.game.Game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class GameWaitPlayersInventory {

    private final GameInventory gameInventory;
    private final ArrayList<GameOption> gameOptions;
    private final Game game;

    public GameWaitPlayersInventory(GameInventory gameInventory) {
        this.gameInventory = gameInventory;
        this.gameOptions = gameInventory.getGameOptions();
        this.game = gameInventory.getGame();
    }

    public void build(Player player, WaitPlayersCallback callback) {
        String[] guiSetup = getGuiSetup();

        // TODO: Probably gonna move this to a utils func
        InventoryGui gui = new InventoryGui(BoardGames.getInstance(), player, game.getGameName(), guiSetup);

        gui.setFiller(new ItemStack(Material.BLACK_STAINED_GLASS_PANE));

        // add players head
        gui.addElement(new StaticGuiElement('s', new ItemStack(Material.SKELETON_SKULL), click -> true,
                        ChatColor.GREEN + player.getDisplayName()
                )
        );

        // loop through gameOptions and add them in
        GuiElementGroup gameOptionGroup = new GuiElementGroup('g');
        for(GameOption gameOption : this.gameOptions) {
            String label = gameOption.getLabel() == null ? "" : gameOption.getLabel();

            gameOptionGroup.addElement((new StaticGuiElement('g',
                    new ItemStack(gameOption.getMaterial()),
                    label + ChatColor.GREEN + gameInventory.getGameData(gameOption.getKey()).toString()
            )));
        }

        gui.addElement(gameOptionGroup);

        // TODO: (Start here) add players from queue

        gui.setCloseAction(close -> {
            // TODO: Reset game inventory
            return false;
        });


        gui.show(player);
    }

    private String[] getGuiSetup() {
        char[][] guiSetup = new char[6][9];

        // Fill all with spaces
        for (char[] chars : guiSetup) {
            Arrays.fill(chars, ' ');
        }

        // Define game data
        for(int y = 2; y <= 4; y++) {
            for(int x = 1; x <= 3; x++) {
                guiSetup[y][x] = 'g';
            }
        }

        // Define queue
        for(int y = 1; y <= 4; y++) {
            for(int x = 5; x <= 7; x++) {
                guiSetup[y][x] = 'q';
            }
        }

        // Define player skull
        guiSetup[1][2] = 's';

        return formatGuiSetup(guiSetup);
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

}
