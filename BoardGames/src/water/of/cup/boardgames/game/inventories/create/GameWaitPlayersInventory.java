package water.of.cup.boardgames.game.inventories.create;

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
import water.of.cup.boardgames.game.inventories.GameOption;
import water.of.cup.boardgames.game.inventories.InventoryScreen;
import water.of.cup.boardgames.game.inventories.InventoryUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class GameWaitPlayersInventory extends InventoryScreen {

    private final GameInventory gameInventory;
    private final Game game;

    public GameWaitPlayersInventory(GameInventory gameInventory) {
        super(gameInventory);
        this.gameInventory = gameInventory;
        this.game = gameInventory.getGame();
    }

    public void build(Player player, WaitPlayersCallback callback) {
        String[] guiSetup = getGuiSetup();

        String inventoryName = game.getAltName() + " (" + gameInventory.getAcceptedPlayers().size() + "/" + (gameInventory.getMaxPlayers() - 1) + ")";

        InventoryGui gui = new InventoryGui(BoardGames.getInstance(), player, inventoryName, guiSetup);

        gui.setFiller(new ItemStack(Material.BLACK_STAINED_GLASS_PANE));

        renderGameOptions(gui, 's', 'g');

        // Add players from queue
        ArrayList<Player> playerQueue = gameInventory.getJoinPlayerQueue();
        GuiElementGroup playerQueueGroup = new GuiElementGroup('q');
        for(Player queuePlayer : playerQueue) {
            playerQueueGroup.addElement((new StaticGuiElement('q',
                    InventoryUtils.getPlayerHead(queuePlayer),
                    click -> {
                        if(click.getEvent().isLeftClick()) {
                            callback.onAccept(queuePlayer);
                        } else if(click.getEvent().isRightClick()) {
                            callback.onDecline(queuePlayer);
                        }

                        return true;
                    },
                    ChatColor.GREEN + queuePlayer.getDisplayName(),
                    ConfigUtil.GUI_ACCEPT_PLAYER.toString(),
                    ConfigUtil.GUI_DECLINE_PLAYER.toString()
            )));
        }

        // Fill in empty spaces with white glass
        for(int i = playerQueue.size(); i < 12; i++) {
            playerQueueGroup.addElement((new StaticGuiElement('q',
                    new ItemStack(Material.WHITE_STAINED_GLASS_PANE),
                    " "
            )));
        }

        gui.addElement(playerQueueGroup);

        // If they have enough players to start, render in start game button
        int numAccepted = gameInventory.getAcceptedPlayers().size();
        if(numAccepted >= gameInventory.getMinPlayers() - 1) {
            ItemStack startButton = InventoryUtils.getCustomTextureHead(InventoryUtils.RIGHT_ARROW);
            gui.addElement(new StaticGuiElement('b', startButton, click -> {
                        callback.onStart();
                        return true;
                    },
                            ConfigUtil.GUI_START_GAME_WITH.buildString(numAccepted)
                    )
            );
        }

        gui.setCloseAction(close -> {
            callback.onLeave();
            return false;
        });


        gui.show(player);
    }

    // Note: For refresh, look into InventoryGui.get(InventoryHolder holder); / look into dynamic

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

        // Define game creator skull
        guiSetup[1][2] = 's';

        // Define start game button
        guiSetup[1][4] = 'b';

        return formatGuiSetup(guiSetup);
    }

}
