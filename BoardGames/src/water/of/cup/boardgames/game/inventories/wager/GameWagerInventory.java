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
import water.of.cup.boardgames.config.ConfigUtil;
import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.GamePlayer;
import water.of.cup.boardgames.game.inventories.GameInventory;
import water.of.cup.boardgames.game.inventories.InventoryScreen;
import water.of.cup.boardgames.game.inventories.InventoryUtils;
import water.of.cup.boardgames.game.wagers.RequestWager;
import water.of.cup.boardgames.game.wagers.WagerManager;

import java.util.ArrayList;
import java.util.Arrays;

public class GameWagerInventory extends InventoryScreen {

    private final Game game;
    private final GameInventory gameInventory;
    private final WagerManager wagerManager;

    public GameWagerInventory(GameInventory gameInventory) {
        super(gameInventory);
        this.gameInventory = gameInventory;
        this.game = gameInventory.getGame();
        this.wagerManager = gameInventory.getWagerManager();
    }

    public void build(Player player, GameWagerCallback callback) {
        WagerOption wagerOption = gameInventory.getWagerOption(player);

        if(!wagerOption.isOpened()) {
            renderJoinWager(player, callback);
            return;
        }

        if(wagerOption.getSelectedWager() == null) {
            renderCreateWager(player, callback);
            return;
        }

        // If the wager they have selected is no longer avail (someone else accepted first/cancelled)
        if(!wagerManager.getRequestWagers().contains(wagerOption.getSelectedWager())) {
            wagerOption.setSelectedWager(null);
            renderCreateWager(player, callback);
            return;
        }

        renderAcceptWager(player, callback);
    }


    private void renderCreateWager(Player player, GameWagerCallback callback) {
        String[] guiSetup = getCreateWagerViewSetup();

        InventoryGui gui = new InventoryGui(BoardGames.getInstance(), player, game.getGameName(), guiSetup);

        gui.setFiller(new ItemStack(Material.BLACK_STAINED_GLASS_PANE));

        gui.addElement(new StaticGuiElement('w', new ItemStack(Material.WHITE_STAINED_GLASS_PANE), " "));

        // Render in wagers
        renderRequestWagers(gui, player, callback);

        // Render create wager buttons
        gui.addElement(new StaticGuiElement('a', InventoryUtils.getPlayerHead(player), player.getDisplayName()));

        boolean hasWager = wagerManager.hasRequestWager(player);
        WagerOption wagerOption = gameInventory.getWagerOption(player);
        ItemStack rightArrow = InventoryUtils.getCustomTextureHead(InventoryUtils.RIGHT_ARROW);
        ItemStack leftArrow = InventoryUtils.getCustomTextureHead(InventoryUtils.LEFT_ARROW);

        gui.addElement(new DynamicGuiElement('b', (viewer) ->
                new StaticGuiElement('b', InventoryUtils.getPlayerHead(wagerOption.getGamePlayer().getPlayer()),
                click -> true, ChatColor.GREEN + wagerOption.getGamePlayer().getPlayer().getDisplayName())));


        renderBetOptionButton(player, gui, 'd', rightArrow, ConfigUtil.GUI_WAGER_NEXT.toString());
        renderBetOptionButton(player, gui, 'c', leftArrow, ConfigUtil.GUI_WAGER_BACK.toString());

        gui.addElement(new DynamicGuiElement('e', (viewer) ->
                new StaticGuiElement('e', new ItemStack(Material.GOLD_INGOT),
                        click -> true, ChatColor.GREEN + "" + wagerOption.getWagerAmount())));

        renderWagerButton(player, gui, 'h', rightArrow, true);
        renderWagerButton(player, gui, 'f', leftArrow, false);

        if(hasWager) {
            gui.addElement(new StaticGuiElement('i', new ItemStack(Material.RED_STAINED_GLASS_PANE), click -> {
                        RequestWager requestWager = wagerManager.getRequestWager(player);
                        callback.onCancel(requestWager);
                        return true;
                    },
                            ConfigUtil.GUI_WAGER_CANCEL.toString()
                    )
            );
        } else {
            gui.addElement(new StaticGuiElement('i', new ItemStack(Material.LIME_STAINED_GLASS_PANE), click -> {
                        RequestWager requestWager = new RequestWager(player, wagerOption.getGamePlayer(), wagerOption.getWagerAmount());
                        if(requestWager.canCreate()) {
                            callback.onCreate(requestWager);
                        } else {
                            player.sendMessage(ConfigUtil.GUI_WAGER_NO_MONEY_CREATE.toString());
                        }

                        return true;
                    },
                            ConfigUtil.GUI_WAGER_CREATE.toString()
                    )
            );
        }

        gui.setCloseAction(close -> {
            callback.onLeave(player);
            return false;
        });

        gui.show(player);
    }

    private void renderAcceptWager(Player player, GameWagerCallback callback) {
        WagerOption wagerOption = gameInventory.getWagerOption(player);
        RequestWager selectedWager = wagerOption.getSelectedWager();

        String[] guiSetup = getAcceptWagerViewSetup();

        InventoryGui gui = new InventoryGui(BoardGames.getInstance(), player, game.getGameName(), guiSetup);

        gui.setFiller(new ItemStack(Material.BLACK_STAINED_GLASS_PANE));

        gui.addElement(new StaticGuiElement('w', new ItemStack(Material.WHITE_STAINED_GLASS_PANE), " "));

        // Render in wagers
        renderRequestWagers(gui, player, callback);

        // Player skulls
        gui.addElement(new StaticGuiElement('a', InventoryUtils.getPlayerHead(selectedWager.getOwner()), selectedWager.getOwner().getDisplayName()));
        gui.addElement(new StaticGuiElement('c', InventoryUtils.getPlayerHead(player), player.getDisplayName()));

        // Wager amount
        gui.addElement(new StaticGuiElement('b', new ItemStack(Material.GOLD_INGOT), ChatColor.GREEN + "" + selectedWager.getAmount()));

        // Bet skulls
        Player ownerBetPlayer = selectedWager.getOwnerBet().getPlayer();
        Player accepterBetPlayer = game.getGamePlayers().get(0).getPlayer() == ownerBetPlayer
                ? game.getGamePlayers().get(1).getPlayer()
                : game.getGamePlayers().get(0).getPlayer();

        gui.addElement(new StaticGuiElement('d', InventoryUtils.getPlayerHead(ownerBetPlayer), ownerBetPlayer.getDisplayName()));
        gui.addElement(new StaticGuiElement('e', InventoryUtils.getPlayerHead(accepterBetPlayer), accepterBetPlayer.getDisplayName()));

        // Accept wager
        gui.addElement(new StaticGuiElement('f', new ItemStack(Material.LIME_STAINED_GLASS_PANE), click -> {
                    if(selectedWager.canAccept(player)) {
                        // Deselect once accepted
                        wagerOption.setSelectedWager(null);

                        callback.onAccept(player, selectedWager);
                    } else {
                        player.sendMessage(ConfigUtil.GUI_WAGER_NO_MONEY_ACCEPT.toString());
                    }
                    return true;
                },
                        ConfigUtil.GUI_WAGER_ACCEPT.toString()
                )
        );

        // Decline wager
        gui.addElement(new StaticGuiElement('h', new ItemStack(Material.RED_STAINED_GLASS_PANE), click -> {
                    // Deselect
                    wagerOption.setSelectedWager(null);
                    this.build(player, callback);
                    return true;
                },
                        ConfigUtil.GUI_WAGER_DECLINE.toString()
                )
        );

        gui.setCloseAction(close -> {
            callback.onLeave(player);
            return false;
        });

        gui.show(player);
    }

    private void renderBetOptionButton(Player player, InventoryGui gui, char slot, ItemStack itemStack, String text) {
        WagerOption wagerOption = gameInventory.getWagerOption(player);
        boolean hasWager = wagerManager.hasRequestWager(player);
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
        boolean hasWager = wagerManager.hasRequestWager(player);
        String text = increase ? ConfigUtil.GUI_WAGER_INCREASE.toString() : ConfigUtil.GUI_WAGER_DECREASE.toString();
        gui.addElement(new StaticGuiElement(slot, itemStack, click -> {
                    // If they have an open wager, don't let change wager options
                    if(hasWager) return true;

                    double currAmount = wagerOption.getWagerAmount();

                    if(increase) {
                        currAmount++;
                    } else {
                        currAmount--;
                        if(currAmount < 1) currAmount = 1;
                    }

                    wagerOption.setWagerAmount(currAmount);

                    click.getGui().draw();
                    return true;
                },
                        text
                )
        );
    }

    private void renderRequestWagers(InventoryGui gui, Player player, GameWagerCallback callback) {
        WagerOption wagerOption = gameInventory.getWagerOption(player);
        RequestWager selectedWager = wagerOption.getSelectedWager();

        ArrayList<RequestWager> requestWagers = wagerManager.getRequestWagers();
        GuiElementGroup requestWagersGroup = new GuiElementGroup('g');

        for(RequestWager requestWager : requestWagers) {
            ItemStack playerHead = InventoryUtils.getPlayerHead(requestWager.getOwner());
            requestWagersGroup.addElement((new StaticGuiElement('g',
                    playerHead,
                    click -> {
                        // If they have an open wager, don't let them join others.
                        if(wagerManager.hasRequestWager(player)) {
                            return true;
                        }
                        // Deselect if clicked again
                        if(selectedWager != null && selectedWager == requestWager) {
                            wagerOption.setSelectedWager(null);
                        } else {
                            wagerOption.setSelectedWager(requestWager);
                        }

                        this.build(player, callback);
                        return true;
                    },
                    ChatColor.GREEN + requestWager.getOwner().getDisplayName(),
                    ConfigUtil.GUI_WAGER_BETTINGON.buildString(requestWager.getOwnerBet().getPlayer().getDisplayName())
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
                    gameInventory.getWagerOption(player).setOpened(true);
                    this.build(player, callback);
                    return true;
                },
                        ConfigUtil.GUI_WAGER_TEXT.toString()
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

        // Define accept wager
        for(int y = 1; y <= 4; y++) {
            for(int x = 5; x <= 7; x++) {
                guiSetup[y][x] = 'w';
            }
        }

        // Define wager skulls
        guiSetup[1][5] = 'a'; // player1 skull
        guiSetup[1][6] = 'b'; // wager amount
        guiSetup[1][7] = 'c'; // player2 skull

        guiSetup[2][5] = 'd'; // player1 bet
        guiSetup[2][7] = 'e'; // player2 bet

        guiSetup[3][6] = 'f'; // Accept wager
        guiSetup[4][6] = 'h'; // Decline wager

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
