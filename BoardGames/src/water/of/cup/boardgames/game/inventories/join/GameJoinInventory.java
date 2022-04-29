package water.of.cup.boardgames.game.inventories.join;

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

public class GameJoinInventory extends InventoryScreen {

    private final GameInventory gameInventory;
    private final ArrayList<GameOption> gameOptions;
    private final Game game;

    public GameJoinInventory(GameInventory gameInventory) {
        super(gameInventory);
        this.gameInventory = gameInventory;
        this.gameOptions = gameInventory.getGameOptions();
        this.game = gameInventory.getGame();
    }

    public void build(Player player, JoinGameCallback callback) {
        String[] guiSetup = getGuiSetup();

        InventoryGui gui = new InventoryGui(BoardGames.getInstance(), player, ConfigUtil.GUI_GAME_JOIN_TITLE.buildString(game.getAltName()), guiSetup);

        gui.setFiller(new ItemStack(Material.BLACK_STAINED_GLASS_PANE));

        gui.addElement(new StaticGuiElement('w', new ItemStack(Material.WHITE_STAINED_GLASS_PANE), " "));

        renderGameOptions(gui, 's', 'g');

        // Depending on if they've been accepted/waiting render different
        if(gameInventory.getJoinPlayerQueue().contains(player)) {
            renderJoinStatus(gui, Material.CLOCK, ConfigUtil.GUI_WAIT_CREATOR.toString());
        } else if(gameInventory.getAcceptedPlayers().contains(player)) {
            renderJoinStatus(gui, Material.LIME_WOOL, ConfigUtil.GUI_WAIT_PLAYERS.toString());
        } else {
            gui.addElement(new StaticGuiElement('z', InventoryUtils.getPlayerHead(player), ConfigUtil.GUI_CREATE_GAME_DATA_COLOR.toString() + player.getDisplayName()));

            gui.addElement(new StaticGuiElement('a', new ItemStack(Material.LIME_STAINED_GLASS_PANE), click -> {
                        callback.onJoin(player);
                        return true;
                    },
                            ConfigUtil.GUI_JOIN_GAME.toString()
                    )
            );

            gui.addElement(new StaticGuiElement('d', new ItemStack(Material.RED_STAINED_GLASS_PANE), click -> {
                        gui.close(true);
                        callback.onLeave(player);
                        return true;
                    },
                            ConfigUtil.GUI_LEAVE_GAME.toString()
                    )
            );
        }

        gui.setCloseAction(close -> {
//            player.sendMessage("Closing join game!");
            callback.onLeave(player);
            return false;
        });

        gui.show(player);
    }

    private void renderJoinStatus(InventoryGui gui, Material material, String status) {
        gui.addElement(new StaticGuiElement('z', new ItemStack(material), ConfigUtil.GUI_CREATE_GAME_DATA_COLOR.toString() + status));
        gui.addElement(new StaticGuiElement('a', new ItemStack(Material.WHITE_STAINED_GLASS_PANE), " "));
        gui.addElement(new StaticGuiElement('d', new ItemStack(Material.WHITE_STAINED_GLASS_PANE),  " "));
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

        // Define background
        for(int y = 1; y <= 4; y++) {
            for(int x = 5; x <= 7; x++) {
                guiSetup[y][x] = 'w';
            }
        }

        // Define join/deny game buttons
        guiSetup[1][6] = 'z'; // Player join skull
        guiSetup[3][6] = 'a'; // Accept button
        guiSetup[4][6] = 'd'; // Decline button

        // Define game creator skull
        guiSetup[1][2] = 's';

        return formatGuiSetup(guiSetup);
    }
}
