package water.of.cup.boardgames.game.inventories;

import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class InventoryScreen {

    private final GameInventory gameInventory;

    public InventoryScreen(GameInventory gameInventory) {
        this.gameInventory = gameInventory;
    }

    public void renderGameOptions(InventoryGui gui, char playerPos, char groupPos) {
        gui.addElement(new StaticGuiElement(playerPos, new ItemStack(Material.SKELETON_SKULL), click -> true,
                        ChatColor.GREEN + gameInventory.getGameCreator().getDisplayName()
                )
        );

        // loop through gameOptions and add them in
        GuiElementGroup gameOptionGroup = new GuiElementGroup(groupPos);
        for(GameOption gameOption : gameInventory.getGameOptions()) {
            String label = gameOption.getLabel() == null ? "" : gameOption.getLabel();

            gameOptionGroup.addElement((new StaticGuiElement(groupPos,
                    new ItemStack(gameOption.getMaterial()),
                    label + ChatColor.GREEN + gameInventory.getGameData(gameOption.getKey()).toString()
            )));
        }

        // Fill in empty spaces with white glass
        for(int i = gameInventory.getGameOptions().size(); i < 9; i++) {
            gameOptionGroup.addElement((new StaticGuiElement(groupPos,
                    new ItemStack(Material.WHITE_STAINED_GLASS_PANE),
                    " "
            )));
        }

        gui.addElement(gameOptionGroup);
    }

    public String[] formatGuiSetup(char[][] guiSetup) {
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
