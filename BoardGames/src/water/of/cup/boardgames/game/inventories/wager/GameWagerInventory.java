package water.of.cup.boardgames.game.inventories.wager;

import de.themoep.inventorygui.DynamicGuiElement;
import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import water.of.cup.boardgames.BoardGames;
import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.GamePlayer;
import water.of.cup.boardgames.game.inventories.GameInventory;
import water.of.cup.boardgames.game.inventories.InventoryScreen;
import water.of.cup.boardgames.game.inventories.InventoryUtils;
import water.of.cup.boardgames.game.wagers.RequestWager;

import java.util.ArrayList;
import java.util.Arrays;

public class GameWagerInventory extends InventoryScreen {

    private final Game game;
    private final GameInventory gameInventory;

    public GameWagerInventory(GameInventory gameInventory) {
        super(gameInventory);
        this.gameInventory = gameInventory;
        this.game = gameInventory.getGame();
    }

    public void build(Player player, boolean opened, RequestWager selectedWager, GameWagerCallback callback) {
        if(!opened) {
            renderJoinWager(player, callback);
            return;
        }

        if(selectedWager == null) {
            renderCreateWager(player, callback);
            return;
        }

        renderAcceptWager();
    }

    public void build(Player player, GameWagerCallback callback) {
        build(player, false, null, callback);
    }

    private void renderCreateWager(Player player, GameWagerCallback callback) {
        String[] guiSetup = getCreateWagerViewSetup();

        InventoryGui gui = new InventoryGui(BoardGames.getInstance(), player, game.getGameName(), guiSetup);

        gui.setFiller(new ItemStack(Material.BLACK_STAINED_GLASS_PANE));

        gui.addElement(new StaticGuiElement('w', new ItemStack(Material.WHITE_STAINED_GLASS_PANE), " "));

        // Render in wagers
        renderRequestWagers(gui, player, null, callback);

        // Render create wager buttons
        gui.addElement(new StaticGuiElement('a', InventoryUtils.getPlayerHead(player), player.getDisplayName()));

        boolean hasWager = gameInventory.hasRequestWager(player);
        WagerOption wagerOption = gameInventory.getWagerOption(player);
        ItemStack rightArrow = InventoryUtils.getCustomTextureHead(InventoryUtils.RIGHT_ARROW);
        ItemStack leftArrow = InventoryUtils.getCustomTextureHead(InventoryUtils.LEFT_ARROW);

        gui.addElement(new DynamicGuiElement('b', (viewer) ->
                new StaticGuiElement('b', InventoryUtils.getPlayerHead(wagerOption.getGamePlayer().getPlayer()),
                click -> true, ChatColor.GREEN + wagerOption.getGamePlayer().getPlayer().getDisplayName())));


        renderBetOptionButton(player, gui, 'd', rightArrow, "Next");
        renderBetOptionButton(player, gui, 'c', leftArrow, "Back");

        gui.addElement(new DynamicGuiElement('e', (viewer) ->
                new StaticGuiElement('e', new ItemStack(Material.GOLD_INGOT),
                        click -> true, ChatColor.GREEN + "" + wagerOption.getWagerAmount())));

        renderWagerButton(player, gui, 'h', rightArrow, true);
        renderWagerButton(player, gui, 'f', leftArrow, false);

        if(hasWager) {
            gui.addElement(new StaticGuiElement('i', new ItemStack(Material.RED_STAINED_GLASS_PANE), click -> {
                        RequestWager requestWager = gameInventory.getRequestWager(player);
                        callback.onCancel(requestWager);
                        return true;
                    },
                            ChatColor.RED + "Cancel Wager"
                    )
            );
        } else {
            gui.addElement(new StaticGuiElement('i', new ItemStack(Material.LIME_STAINED_GLASS_PANE), click -> {
                        RequestWager requestWager = new RequestWager(player, wagerOption.getGamePlayer(), wagerOption.getWagerAmount());
                        callback.onCreate(requestWager);
                        return true;
                    },
                            ChatColor.GREEN + "Create Wager"
                    )
            );
        }

        gui.setCloseAction(close -> {
            callback.onLeave(player);
            return false;
        });

        gui.show(player);
    }

    private void renderAcceptWager() {

    }

    private void renderBetOptionButton(Player player, InventoryGui gui, char slot, ItemStack itemStack, String text) {
        WagerOption wagerOption = gameInventory.getWagerOption(player);
        boolean hasWager = gameInventory.hasRequestWager(player);
        gui.addElement(new StaticGuiElement(slot, itemStack, click -> {
                    // If they have an open wager, don't let change wager options
                    if(hasWager) return true;

                    GamePlayer nextPlayer = game.getGamePlayers().get(0) == wagerOption.getGamePlayer()
                            ? game.getGamePlayers().get(1)
                            : game.getGamePlayers().get(0);

                    wagerOption.setGamePlayer(nextPlayer);

                    click.getGui().draw();
                    return true;
                },
                        ChatColor.GREEN + text
                )
        );
    }

    private void renderWagerButton(Player player, InventoryGui gui, char slot, ItemStack itemStack, boolean increase) {
        WagerOption wagerOption = gameInventory.getWagerOption(player);
        boolean hasWager = gameInventory.hasRequestWager(player);
        String text = increase ? "Increase Wager" : "Decrease Wager";
        gui.addElement(new StaticGuiElement(slot, itemStack, click -> {
                    // If they have an open wager, don't let change wager options
                    if(hasWager) return true;

                    double currAmount = wagerOption.getWagerAmount();

                    if(increase) {
                        currAmount++;
                    } else {
                        currAmount--;
                        if(currAmount <= 0) currAmount = 0;
                    }

                    wagerOption.setWagerAmount(currAmount);

                    click.getGui().draw();
                    return true;
                },
                        ChatColor.GREEN + text
                )
        );
    }

    private void renderRequestWagers(InventoryGui gui, Player player, RequestWager selectedWager, GameWagerCallback callback) {
        ArrayList<RequestWager> requestWagers = gameInventory.getRequestWagers();
        GuiElementGroup requestWagersGroup = new GuiElementGroup('g');
        for(RequestWager requestWager : requestWagers) {
            ItemStack playerHead = InventoryUtils.getPlayerHead(requestWager.getOwner());
            requestWagersGroup.addElement((new StaticGuiElement('g',
                    playerHead,
                    click -> {
                        // If they have an open wager, don't let them join others.
                        if(gameInventory.hasRequestWager(player)) {
                            return true;
                        }

                        // Deselect if clicked again
                        if(selectedWager != null && selectedWager == requestWager) {
                            this.build(player, true, null, callback);
                        } else {
                            this.build(player, true, requestWager, callback);
                        }

                        return true;
                    },
                    ChatColor.GREEN + requestWager.getOwner().getDisplayName(),
                    "Betting on " + requestWager.getOwnerBet().getPlayer().getDisplayName()
            )));
        }

        gui.addElement(requestWagersGroup);
    }

    private void renderJoinWager(Player player, GameWagerCallback callback) {
        String[] guiSetup = getJoinGuiSetup();

        InventoryGui gui = new InventoryGui(BoardGames.getInstance(), player, game.getGameName(), guiSetup);

        gui.setFiller(new ItemStack(Material.BLACK_STAINED_GLASS_PANE));

        gui.addElement(new StaticGuiElement('w', new ItemStack(Material.WHITE_STAINED_GLASS_PANE), " "));

        renderGameOptions(gui, 's', 'g');

        gui.addElement(new StaticGuiElement('a', new ItemStack(Material.BOOK), click -> {
                    this.build(player, true, null, callback);
                    return true;
                },
                        ChatColor.GREEN + "Wagers"
                )
        );

        gui.setCloseAction(close -> {
            callback.onLeave(player);
            return false;
        });

        gui.show(player);
    }

    private String[] getJoinGuiSetup() {
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

        // Define join wager button
        guiSetup[3][6] = 'a';

        // Define game creator skull
        guiSetup[1][2] = 's';

        return formatGuiSetup(guiSetup);
    }

    private String[] getCreateWagerViewSetup() {
        char[][] guiSetup = new char[6][9];

        // Fill all with spaces
        for (char[] chars : guiSetup) {
            Arrays.fill(chars, ' ');
        }

        // Define wager area
        addWagerArea(guiSetup);

        // Define create wager
        for(int y = 1; y <= 4; y++) {
            for(int x = 5; x <= 7; x++) {
                guiSetup[y][x] = 'w';
            }
        }

        // Define player skull
        guiSetup[1][6] = 'a';

        // Define player select
        guiSetup[2][6] = 'b';
        guiSetup[2][5] = 'c'; // next player
        guiSetup[2][7] = 'd'; // last player

        // Define wager select
        guiSetup[3][6] = 'e';
        guiSetup[3][5] = 'f'; // increase wager
        guiSetup[3][7] = 'h'; // decrease wager

        guiSetup[4][6] = 'i'; // create wager

        return formatGuiSetup(guiSetup);
    }

    private String[] getAcceptWagerViewSetup() {
        char[][] guiSetup = new char[6][9];

        // Fill all with spaces
        for (char[] chars : guiSetup) {
            Arrays.fill(chars, ' ');
        }

        // Define wager area
        addWagerArea(guiSetup);

        // Define create wager
        for(int y = 1; y <= 4; y++) {
            for(int x = 5; x <= 7; x++) {
                guiSetup[y][x] = 'w';
            }
        }

        // Define player skulls
        guiSetup[1][5] = 'a'; // wager creator skull
        guiSetup[1][7] = 'b'; // wager acceptor skull
        guiSetup[1][6] = 'c'; // wager amount

        guiSetup[2][5] = 'd'; // wager creator bet
        guiSetup[2][7] = 'e'; // other game player

        guiSetup[3][6] = 'f'; // Accept wager

        return formatGuiSetup(guiSetup);
    }

    private void addWagerArea(char[][] guiSetup) {
        // Define wager area
        for(int y = 1; y <= 4; y++) {
            for(int x = 1; x <= 3; x++) {
                guiSetup[y][x] = 'g';
            }
        }
    }

}
