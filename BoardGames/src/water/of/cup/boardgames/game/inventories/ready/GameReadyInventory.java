package water.of.cup.boardgames.game.inventories.ready;

import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import water.of.cup.boardgames.BoardGames;
import water.of.cup.boardgames.config.ConfigUtil;
import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.inventories.GameInventory;
import water.of.cup.boardgames.game.inventories.InventoryScreen;
import water.of.cup.boardgames.game.inventories.InventoryUtils;

import java.util.Arrays;
import java.util.Set;

public class GameReadyInventory extends InventoryScreen {

    private final GameInventory gameInventory;
    private final Game game;

    public GameReadyInventory(GameInventory gameInventory) {
        super(gameInventory);
        this.gameInventory = gameInventory;
        this.game = gameInventory.getGame();
    }

    public void build(Player player, GameReadyCallback callback) {
        String[] guiSetup = getGuiSetup();

        String inventoryName = game.getGameName() + " (" + gameInventory.getNumReady() + "/" + (gameInventory.getReadyPlayers().size()) + ")";

        InventoryGui gui = new InventoryGui(BoardGames.getInstance(), player, inventoryName, guiSetup);

        gui.setFiller(new ItemStack(Material.BLACK_STAINED_GLASS_PANE));

        gui.addElement(new StaticGuiElement('w', new ItemStack(Material.WHITE_STAINED_GLASS_PANE), " "));

        Set<Player> readyPlayers = gameInventory.getReadyPlayers();
        GuiElementGroup playerReadyGroup = new GuiElementGroup('g');
        for(Player readyPlayer : readyPlayers) {
            boolean isReady = gameInventory.getReadyStatus(readyPlayer);
            ItemStack playerHead = InventoryUtils.getPlayerHead(readyPlayer, isReady);
            String readyText = isReady
                    ? ConfigUtil.GUI_READY_TEXT.toString()
                    : ConfigUtil.GUI_UNREADY_TEXT.toString();

            playerReadyGroup.addElement((new StaticGuiElement('g',
                    playerHead,
                    click -> true,
                    ChatColor.GREEN + readyPlayer.getDisplayName(),
                    readyText
            )));
        }

        // Fill in empty spaces with white glass
        for(int i = readyPlayers.size(); i < 12; i++) {
            playerReadyGroup.addElement((new StaticGuiElement('q',
                    new ItemStack(Material.WHITE_STAINED_GLASS_PANE),
                    " "
            )));
        }

        gui.addElement(playerReadyGroup);

        boolean isReady = gameInventory.getReadyStatus(player);
        Material readyMat = isReady ? Material.LIME_STAINED_GLASS_PANE : Material.YELLOW_STAINED_GLASS_PANE;
        String readyText = isReady
                ? ConfigUtil.GUI_READY_TEXT.toString()
                : ConfigUtil.GUI_UNREADY_TEXT.toString();
        gui.addElement(new StaticGuiElement('r', new ItemStack(readyMat), click -> {
                    if(!isReady) {
                        callback.onReady(player);
                    }

                    return true;
                },
                        readyText
                )
        );

        gui.addElement(new StaticGuiElement('l', new ItemStack(Material.RED_STAINED_GLASS_PANE), click -> {
                    gui.close(true);
                    callback.onLeave(player);
                    return true;
                },
                ConfigUtil.GUI_LEAVE_GAME.toString()
                )
        );

        gui.setCloseAction(close -> {
//            player.sendMessage("Closing ready game!");
            callback.onLeave(player);
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

        // Define ready status
        for(int y = 1; y <= 4; y++) {
            for(int x = 1; x <= 3; x++) {
                guiSetup[y][x] = 'g';
            }
        }

        // Define ready buttons background
        for(int y = 1; y <= 4; y++) {
            for(int x = 5; x <= 7; x++) {
                guiSetup[y][x] = 'w';
            }
        }

        // Define ready / leave buttons
        guiSetup[2][6] = 'r'; // Ready button
        guiSetup[3][6] = 'l'; // Leave button

        return formatGuiSetup(guiSetup);
    }
}
