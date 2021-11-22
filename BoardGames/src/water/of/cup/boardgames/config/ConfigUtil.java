package water.of.cup.boardgames.config;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import water.of.cup.boardgames.BoardGames;
import water.of.cup.boardgames.game.MathUtils;
import water.of.cup.boardgames.game.storage.StorageType;

import java.text.DecimalFormat;
import java.util.HashMap;

public enum ConfigUtil implements ConfigInterface {

    // PLUGIN SETTINGS
    PERMISSIONS_ENABLED("settings.permissions", "true"),
    WAGERS_ENABLED("settings.wagers", "true"),
    ITEM_WAGERS_ENABLED("settings.itemwagers", "true"),
    RECIPE_ENABLED("settings.recipe.enabled", "true"),
    RECIPE_AUTO_DISCOVER_ENABLED("settings.recipe.autodiscover", "true"),
    DB_HOST("settings.database.host", "localhost"),
    DB_PORT("settings.database.port", "3306"),
    DB_NAME("settings.database.database", "boardgames"),
    DB_USERNAME("settings.database.username", "root"),
    DB_PASS("settings.database.password", " "),
    DB_ENABLED("settings.database.enabled", "false"),
    DB_TRANSFERRED("settings.database.chesstransfer", "false"),
    BOARD_CLICK_DELAY("settings.clickdelay", "0"),
    LOAD_SKULLS("settings.loadskulls", "true"),
    PLAYER_DISTANCE_AMOUNT("settings.distance.amount", "5"),
    PLAYER_DISTANCE_TIME("settings.distance.time", "5000"),

    // GUI MESSAGES
    GUI_NEXT_PAGE("settings.messages.gui.nextpage", "&aNext Page"),
    GUI_CREATE_GAME("settings.messages.gui.creategame", "&aCreate Game"),
    GUI_ACCEPT_PLAYER("settings.messages.gui.acceptplayer", "&2LEFT CLICK - ACCEPT"),
    GUI_DECLINE_PLAYER("settings.messages.gui.declineplayer", "&4RIGHT CLICK - DECLINE"),
    GUI_START_GAME_WITH("settings.messages.gui.startgamewith", "&aStart game with %num% players"),
    GUI_FORFEIT_GAME("settings.messages.gui.forfeitgame", "&cForfeit Game"),
    GUI_WAIT_CREATOR("settings.messages.gui.waitcreator", "Waiting for game creator"),
    GUI_WAIT_PLAYERS("settings.messages.gui.waitplayers", "Waiting for more players"),
    GUI_JOIN_GAME("settings.messages.gui.joingame", "&aJoin Game"),
    GUI_LEAVE_GAME("settings.messages.gui.leavegame", "&cLeave Game"),
    GUI_READY_TEXT("settings.messages.gui.readytext", "&a&lREADY"),
    GUI_UNREADY_TEXT("settings.messages.gui.unreadytext", "&c&lCLICK TO READY"),
    GUI_NOT_READY_TEXT("settings.messages.gui.notreadytext", "&c&lNOT READY"),
    GUI_WAGER_NEXT("settings.messages.gui.wagernext", "Next"),
    GUI_WAGER_BACK("settings.messages.gui.wagerback", "Back"),
    GUI_WAGER_CANCEL("settings.messages.gui.wagercancel", "&cCancel Wager"),
    GUI_WAGER_CREATE("settings.messages.gui.wagercreate", "&aCreate Wager"),
    GUI_WAGER_ACCEPT("settings.messages.gui.wageraccept", "&aAccept Wager"),
    GUI_WAGER_DECLINE("settings.messages.gui.wagerdecline", "&cDecline Wager"),
    GUI_WAGER_NO_MONEY_CREATE("settings.messages.gui.wagernomoneycreate", "&cNot enough money to create wager"),
    GUI_WAGER_NO_MONEY_ACCEPT("settings.messages.gui.wagernomoneyaccept", "&cNot enough money to accept wager"),
    GUI_WAGER_INCREASE("settings.messages.gui.wagerincrease", "&aIncrease Wager"),
    GUI_WAGER_DECREASE("settings.messages.gui.wagerdecrease", "&aDecrease Wager"),
    GUI_WAGER_BETTINGON("settings.messages.gui.bettingon", "&aBetting on %player%"),
    GUI_WAGER_TEXT("settings.messages.gui.wagertext", "&aWagers"),
    GUI_UP_ARROW("settings.messages.gui.uparrow", "&a/\\"),
    GUI_DOWN_ARROW("settings.messages.gui.downarrow", "&a\\/"),
    GUI_RESET_NUMBERS("settings.messages.gui.resetnumbers", "&cRESET"),
    GUI_DONE_TEXT("settings.messages.gui.donetext", "&aSAVE"),
    GUI_NUMBERS_HALF("settings.messages.gui.numhalf", "&a1/2"),
    GUI_NUMBERS_DOUBLE("settings.messages.gui.numdouble", "&a2x"),
    GUI_NUMBERS_MAX("settings.messages.gui.nummax", "&aMax"),
    GUI_GAME_CREATE_TITLE("settings.messages.gui.gamecreatetitle", "%game% | Create Game"),
    GUI_GAME_FORFEIT_TITLE("settings.messages.gui.gameforfeittitle", "%game% | Forfeit Game"),
    GUI_GAME_JOIN_TITLE("settings.messages.gui.gamejointitle", "%game% | Join Game"),
    GUI_GAME_READY_TITLE("settings.messages.gui.gamereadytitle", "%game% | Ready Game"),
    GUI_GAME_WAGER_TITLE("settings.messages.gui.gamewagertitle", "%game% | Wagers"),
    GUI_GAME_TRADE_TITLE("settings.messages.gui.gametradetitle", "%game% | Bet Items"),
    GUI_CREATE_GAME_DATA_COLOR("settings.messages.gui.creategamedatacolor", "&a"),

    // GUI GAME OPTIONS
    GUI_WAGER_LABEL("settings.messages.gui.wagerlabel", "&2Wager: "),
    GUI_TEAM_LABEL("settings.messages.gui.teamlabel", "&2Team: "),
    GUI_RANKED_OPTION_TEXT("settings.messages.gui.rankedoption", "ranked"),
    GUI_UNRANKED_OPTION_TEXT("settings.messages.gui.unrankedoption", "unranked"),
    GUI_WAGERITEMS_LABEL("settings.messages.gui.wageritemslabel", "&2Wager Items: "),
    GUI_WAGERITEMS_DISABLED_LABEL("settings.messages.gui.wageritemsdisabledlabel", "NO"),
    GUI_WAGERITEMS_ENABLED_LABEL("settings.messages.gui.wageritemsenabledlabel", "YES"),

    GUI_TEAM_RED_TEXT("settings.messages.gui.teamredtext", "RED"),
    GUI_TEAM_BLACK_TEXT("settings.messages.gui.teamblacktext", "BLACK"),
    GUI_TEAM_WHITE_TEXT("settings.messages.gui.teamwhitetext", "WHITE"),
    GUI_TEAM_BLUE_TEXT("settings.messages.gui.teambluetext", "BLUE"),

    // GUI CHAT MESSAGES
    CHAT_GUI_GAME_ALREADY_CREATED("settings.messages.gui.gamealreadycreated", "Game has already been created."),
    CHAT_GUI_GAME_NO_MONEY_CREATE("settings.messages.gui.gamenomoneycreate", "&cNot enough money to create game."),
    CHAT_GUI_GAME_NO_MONEY_ACCEPT("settings.messages.gui.gamenomoneyaccept", "&cPlayer no longer has enough money.."),
    CHAT_GUI_GAME_NO_MONEY_JOIN("settings.messages.gui.gamenomoneyjoin", "&cYou do not have enough money!"),
    CHAT_GUI_GAME_ACCEPT("settings.messages.gui.gameacceptchat", "Accepting %player%"),
    CHAT_GUI_GAME_DECLINE("settings.messages.gui.gamedeclinechat", "Declining %player%"),
    CHAT_GUI_GAME_OWNER_LEFT("settings.messages.gui.gameownerleft", "&cGame owner has left. Game cancelled."),
    CHAT_GUI_GAME_PLAYER_LEFT("settings.messages.gui.gameplayerleft", "Player left ready screen. Game cancelled."),
    CHAT_GUI_GAME_NO_AVAIL_GAME("settings.messages.gui.noavailgame", "No available game to join."),
    CHAT_GUI_GAME_FULL_QUEUE("settings.messages.gui.fullqueue", "Too many players are queuing!"),
    CHAT_GUI_WAGER_ACCEPT("settings.messages.gui.chatwageraccept", "%player% has accepted your wager!"),
    CHAT_GUI_WAGER_ACCEPTED("settings.messages.gui.chatwageraccept", "You have accepted %player%'s wager!"),

    // GAME CHAT MESSAGES
    CHAT_GAME_PLAYER_WIN("settings.messages.chat.playerwin", "%player% has won the %game% game!"),
    CHAT_GAME_PLAYER_LOSE("settings.messages.chat.playerlose", "&aYou lost the %game% game!"),
    CHAT_GAME_TIE("settings.messages.chat.gametie", "&aTie %game% game!"),
    CHAT_GAME_FORCE_JUMP("settings.messages.chat.forcejump", "You must select a piece that can jump if a jump is possible."),
    CHAT_GAME_UNO_FORCE_2("settings.messages.chat.unoforce2", "You were forced to draw 2 cards."),
    CHAT_GAME_UNO_FORCE_4("settings.messages.chat.unoforce4", "You were forced to draw 4 cards."),
    CHAT_GAME_UNO_SKIPPED("settings.messages.chat.unoskipped", "You were skipped."),
    CHAT_GAME_UNO_NOT_YOUR_TURN("settings.messages.chat.unonotyourturn", "It is not your turn."),
    CHAT_GAME_UNO_SELECT_COLOR("settings.messages.chat.unoselectcolor", "Select a color."),
    CHAT_GAME_UNO_FORCE_DRAW("settings.messages.chat.unoforcedraw", "You have no playable cards and were forced to draw a card."),
    CHAT_GAME_UNO_INVALID_CARD("settings.messages.chat.unoinvalidcard", "You can not play that card."),
    CHAT_GAME_PLAYER_LEAVE("settings.messages.chat.gameplayerleave", "%player% has left %game%."),
    CHAT_GAME_UNO_LAST_CARD("settings.messages.chat.unolastcard", "%player%: Uno!"),
    CHAT_GAME_UNO_COLOR_RED("settings.messages.chat.unored", "RED"),
    CHAT_GAME_UNO_COLOR_BLUE("settings.messages.chat.unoblue", "BLUE"),
    CHAT_GAME_UNO_COLOR_YELLOW("settings.messages.chat.unoyellow", "YELLOW"),
    CHAT_GAME_UNO_COLOR_GREEN("settings.messages.chat.unogreen", "GREEN"),
    CHAT_GAME_UNO_COLOR("settings.messages.chat.unocolor", "%player%: %color%!"),

    // CHAT MESSAGES
    CHAT_NO_DB("settings.messages.chat.nodb", "&cDatabase must be enabled to view stats."),
    CHAT_NO_GAME("settings.messages.chat.nogame", "&cNo game found with that name."),
    CHAT_NO_PLAYER("settings.messages.chat.noplayer", "&cNo player found with that name."),
    CHAT_DB_ERROR("settings.messages.chat.dberror", "&cError calling to database."),
    CHAT_RELOAD("settings.messages.chat.reload", "&aReloaded board games config."),
    CHAT_GAME_NAMES("settings.messages.chat.gamenames", "&r&lGame Names: &r"),
    CHAT_STATS_HEADER("settings.messages.chat.statsheader", "&r&l%game% &r&7%player%&r's stats"),
    CHAT_STATS_FORMAT("settings.messages.chat.statsformat", "&7%statName% - &r%statVal%"),
    CHAT_LEADERBOARD_HEADER("settings.messages.chat.leaderboardheader", "&r&l%game% &rLeaderboard &7(%sort%)&r"),
    CHAT_LEADERBOARD_FORMAT("settings.messages.chat.leaderboardformat", "&7#%num%.&r %player% - %statVal%"),
    CHAT_AVAIL_COMMANDS("settings.messages.chat.availcommands", "&f&lBoard&9&lGames &rAvailable commands\n&r/bg games &7- lists games\n&r/bg board [game name] &7- gives you the game's item\n/&rbg stats [game name] [player name]\n/bg leaderboard [game name] [order by]\n/bg reload &7- reloads config"),
    CHAT_PLAYER_INGAME("settings.messages.chat.playeringame", "&cYou must finish your game before joining another."),
    CHAT_PLACED_BOARD("settings.messages.chat.placedboard", "&aPlaced board."),
    CHAT_NO_BOARD_ROOM("settings.messages.chat.noboardroom", "&cNo room to place board."),
    CHAT_WELCOME_GAME("settings.messages.chat.welcomegame", "&aWelcome to %game%!"),


    // CASINO GAMES

    // CHAT

    // POKER
    CHAT_POKER_ALL_IN("settings.messages.chat.pokerallin", "%player% has gone all in!"),
    CHAT_POKER_BET_SMALL_BLIND("settings.messages.chat.pokersmallblind", "%player% bet small blind %num%"),
    CHAT_POKER_BET_BIG_BLIND("settings.messages.chat.pokerbigblind", "%player% bet big blind %num%"),
    CHAT_POKER_JOIN("settings.messages.chat.pokerjoin", "You have joined the Poker game."),
    CHAT_POKER_GAME_STARTING("settings.messages.chat.pokergamestarting", "Game starting!"),
    CHAT_POKER_PLAYER_CALL("settings.messages.chat.pokerplayercall", "%player% has called %num%"),
    CHAT_POKER_PLAYER_RAISE("settings.messages.chat.pokerplayerraise", "%player% has raised the bet to %num%"),
    CHAT_POKER_PLAYER_CHECK("settings.messages.chat.pokerplayercheck", "%player% has checked"),
    CHAT_POKER_PLAYER_FOLD("settings.messages.chat.pokerplayerfold", "%player% has folded"),
    CHAT_POKER_MONEY_BACK("settings.messages.chat.pokermoneyback", "You get %num% back!"),
    CHAT_POKER_NEXT_ROUND("settings.messages.chat.pokernextround", "Next round starting! Pot: %num%"),
    CHAT_POKER_SIDE_POT("settings.messages.chat.pokersidepot", "Side pot: %num% Players: %num2%"),
    CHAT_POKER_FLOP("settings.messages.chat.pokerflop", "Flop:"),
    CHAT_POKER_GAME_OVER("settings.messages.chat.pokergameover", "Game over!"),
    CHAT_POKER_WIN_POT("settings.messages.chat.pokerwinpot", "%player% won the pot worth %num%"),
    CHAT_POKER_WIN_SIDE_POT("settings.messages.chat.pokerwinsidepot", "%player% won the side pot worth %num%"),
    CHAT_POKER_NEXT_GAME("settings.messages.chat.pokernextgame", "Starting next game..."),
    CHAT_POKER_PLAYER_REMOVE("settings.messages.chat.pokerplayerremove", "%player% has been removed for not having enough money. "),
    CHAT_POKER_NOT_ENOUGH_PLAYERS("settings.messages.chat.pokernotenoughplayers", "Not enough players to start next game."),
    CHAT_POKER_GAME_START_TIMER("settings.messages.chat.pokergamestarttimer", "Time to next game: %num%:%num2%"),
    CHAT_POKER_GAME_PLAYER_TIMER("settings.messages.chat.pokergameplayertimer", "%player%'s time left: %num%:%num2%"),

    // MINES
    CHAT_MINES_CURRENT_MULT("settings.messages.chat.minescurrentmult", "Current multiplier: %num%x Cash out at: %num2%%"),
    CHAT_MINES_LOSE("settings.messages.chat.minelose", "You lost at %num%x!"),
    CHAT_MINES_WIN("settings.messages.chat.minewin", "You cashed out at %num%x! Payout: %num2%"),

    // PLINKO
    CHAT_PLINKO_WIN("settings.messages.chat.plinkowin", "Plinko: You won: %num%"),

    // HI LO
    CHAT_HILO_CURRENT_BET("settings.messages.chat.hilocurrentbet", "HI-LO: Current Bet: %num%"),
    CHAT_HILO_LOSE("settings.messages.chat.hilolose", "HI-LO: You Lost: %num%"),
    CHAT_HILO_WIN("settings.messages.chat.hilowin", "HI-LO: You cashed out with: %num%"),

    // SLOTS
    CHAT_SLOTS_WIN("settings.messages.chat.slotswin", "You won: %num%"),
    CHAT_SLOTS_WIN_NEARBY("settings.messages.chat.slotswinnearby", "%player% won %num% playing %game%"),

    // BLACKJACK
    CHAT_BLACKJACK_DEALER("settings.messages.chat.bjdealer", "Dealer: "),
    CHAT_BLACKJACK_PLACEBET("settings.messages.chat.bjplacebet", "Click the table to place your next bet."),
    CHAT_BLACKJACK_NOMONEY("settings.messages.chat.bjnomoney", "You do not have enough money for that bet."),
    CHAT_BLACKJACK_BETAMOUNT("settings.messages.chat.bjbetamount", "You bet $%num%"),
    CHAT_BLACKJACK_TURN("settings.messages.chat.bjturn", "It is now your turn."),
    CHAT_BLACKJACK_BUST("settings.messages.chat.bjbust", "Your hand busted, with a total of %num%"),
    CHAT_BLACKJACK_DEALERWIN("settings.messages.chat.bjdealerwin", "I beat your hand with a natural Blackjack."),
    CHAT_BLACKJACK_PLAYERWIN("settings.messages.chat.bjplayerwin", "Your hand won, beating mine %num%:%num2%"),
    CHAT_BLACKJACK_DEALERBUST("settings.messages.chat.bjdealerbust", "My hand busted, your hand won"),
    CHAT_BLACKJACK_TIE("settings.messages.chat.bjtie", "Our hands tied, Push. %num%:%num2%"),
    CHAT_BLACKJACK_PLAYERLOSE("settings.messages.chat.bjplayerlose", "Your hand lost to mine. %num%:%num2%"),
    CHAT_BLACKJACK_PLAYERWINBET("settings.messages.chat.bjwinbet", "You won: $%num%"),
    CHAT_BLACKJACK_PLAYERHIT("settings.messages.chat.bjplayerhit", "You hit, your total on this hand is: %num%"),
    CHAT_BLACKJACK_BLACKJACK("settings.messages.chat.bjblackjack", "Blackjack!"),
    CHAT_BLACKJACK_PLAYERBUST("settings.messages.chat.bjplayerbust", "Bust! Hitting put you at a total of: %num%"),
    CHAT_BLACKJACK_NOMONEYDOUBLE("settings.messages.chat.bjnomoneydouble", "You can't afford to double down."),
    CHAT_BLACKJACK_DOUBLEDOWN("settings.messages.chat.bjdoubledown", "You doubled down, your current bet on this hand is: %num%"),
    CHAT_BLACKJACK_NOMONEYSPLIT("settings.messages.chat.bjnomoneysplit", "You can't afford to split."),
    CHAT_BLACKJACK_SPLITACE("settings.messages.chat.bjsplitace", "You split aces and have no more moves."),
    CHAT_BLACKJACK_NOMONEYINSURACE("settings.messages.chat.bjnomoneyinsurance", "You can't afford insurance."),
    CHAT_BLACKJACK_BETRETURN("settings.messages.chat.bjbetreturn", "Your bet of $%num% was returned to you."),
    CHAT_BLACKJACK_DRAWCARD("settings.messages.chat.bjdrawcard", "I drew a %string%"),
    CHAT_BLACKJACK_FLIPCARD("settings.messages.chat.bjflipcard", "I flipped a %string%"),
    CHAT_BLACKJACK_START_TIMER("settings.messages.chat.bjgamestarttimer", "Time to next game: %num%:%num2%"),
    CHAT_BLACKJACK_PLAYER_TIMER("settings.messages.chat.bjgameplayertimer", "%player%'s time left: %num%:%num2%"),

    // ROULETTE

    //    CHAT_ROULETTE_("settings.messages.chat.roulette", ""),
    CHAT_ROULETTE_WELCOME("settings.messages.chat.roulettewelcome", "Welcome to Roulette.  Your chips are %string%"),
    CHAT_ROULETTE_DEALER("settings.messages.chat.roulettedealer", "Dealer: "),
    CHAT_ROULETTE_NUMBERS("settings.messages.chat.roulettenumbers", "Your numbers for %string% are %string2%"),
    CHAT_ROULETTE_WIN("settings.messages.chat.roulettewin", "You won: $%num%"),
    CHAT_ROULETTE_PLACEBETS("settings.messages.chat.rouletteplacebets", "Place your bets!"),
    CHAT_ROULETTE_START_TIMER("settings.messages.chat.roulettetimer", "Bets close in: %num%:%num2%"),
    CHAT_ROULETTE_SPINNER("settings.messages.chat.roulettespinner", "Landed on %num%"),

    // CARDS
    CARD_SUIT_CLUBS("settings.messages.cards.clubs", "Clubs"),
    CARD_SUIT_DIAMONDS("settings.messages.cards.diamonds", "Diamonds"),
    CARD_SUIT_HEARTS("settings.messages.cards.hearts", "Hearts"),
    CARD_SUIT_SPADES("settings.messages.cards.spades", "Spades"),
    CARD_SUIT_JOKER("settings.messages.cards.joker", "Joker"),
    CARD_ACE("settings.messages.cards.ace", "Ace"),
    CARD_TWO("settings.messages.cards.two", "Two"),
    CARD_THREE("settings.messages.cards.three", "Three"),
    CARD_FOUR("settings.messages.cards.four", "Four"),
    CARD_FIVE("settings.messages.cards.five", "Five"),
    CARD_SIX("settings.messages.cards.six", "Six"),
    CARD_SEVEN("settings.messages.cards.seven", "Seven"),
    CARD_EIGHT("settings.messages.cards.eight", "Eight"),
    CARD_NINE("settings.messages.cards.nine", "Nine"),
    CARD_TEN("settings.messages.cards.ten", "Ten"),
    CARD_JACK("settings.messages.cards.jack", "Jack"),
    CARD_QUEEN("settings.messages.cards.queen", "Queen"),
    CARD_KING("settings.messages.cards.king", "King"),
    CARD_FORMAT("settings.messages.cards.format", " of "),

    // NPC
    NPC_POKER_NAME("settings.messages.npc.pokernpcname", "Dealer"),
    NPC_POKER_SKIN_SIG("settings.messages.npc.pokerskinsig", "T5QGS3fQ9wWvsjmD6l9b/nZMkfOfYW1X3c1xvDdZQ5WHvPmew//3Q86+yfgQqIjPvEcXiDilr71p3WDrz/itsLb5mf9wLU5P4X18x5c6bmmv49TDLUCH5mEIUXu1jiQ8Kog/vzZNGZAAxadTGQPJ7BdII/+OpHDLS+WiCPRMnjCs/1h5RTE7I1OOPQnsh+yk+gOpaxCxgVFMLnMqNnL3mJP05qajHI6OKKXnyyXPwV0xxA3XT2WPbtCPsux3CjNCPP7fA1mYL4dPtdTaju9kP+6jeuf0IkS0jZ31bHKx324cM/W4xiSbR/2OSyYepHdS7TxWPZIYpkMPbaHMLXao7Ok209LD7p3GWZ5RDNvnZTcvGlF10wKoHJ9xy7lHoSfy4NfRAD3doATK5meRo7/JQCCo8M8Mw6dnBvYC9bcb3zCrvTkwQz2dfjkHvmH/QcWkJS5iqYCS6Uk67PJsFtYxa5a9ZBiZGUVxhprrB0hoZem0vfsnzGgzbwjpw0VxDSN1ndXSIJZ4yXB2KI58NE0HMjkVL9OcmOItoS4fqLqdo7CqrntdHsRcDZ7lSaCFVphBMsJI3AbrWAyIM54N9SSMJgpQkrbJ1tWhO1jp8mTXGqW1YlbmCEFS+LRR6sk/F3YK6FtSucJlhlrOdeKGHVaESWLVFzTMBgfVS3TfKSxSRI8="),
    NPC_POKER_SKIN_DATA("settings.messages.npc.pokerskindata", "ewogICJ0aW1lc3RhbXAiIDogMTYxODIxNTU4MTc1OCwKICAicHJvZmlsZUlkIiA6ICJlZDUzZGQ4MTRmOWQ0YTNjYjRlYjY1MWRjYmE3N2U2NiIsCiAgInByb2ZpbGVOYW1lIiA6ICI0MTQxNDE0MWgiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTA0NGNiODMyZDg3Y2RlNmFmNDJhMGRlNDdiYzg1YTY3YzdkNGU1OWEyZDc0NjY2MTc2ZDFjYTQxYWJkMGEyZCIKICAgIH0KICB9Cn0="),

    NPC_BLACKJACK_NAME("settings.messages.npc.bjnpcname", "Dealer"),
    NPC_BLACKJACK_SKIN_SIG("settings.messages.npc.bjskinsig", "T5QGS3fQ9wWvsjmD6l9b/nZMkfOfYW1X3c1xvDdZQ5WHvPmew//3Q86+yfgQqIjPvEcXiDilr71p3WDrz/itsLb5mf9wLU5P4X18x5c6bmmv49TDLUCH5mEIUXu1jiQ8Kog/vzZNGZAAxadTGQPJ7BdII/+OpHDLS+WiCPRMnjCs/1h5RTE7I1OOPQnsh+yk+gOpaxCxgVFMLnMqNnL3mJP05qajHI6OKKXnyyXPwV0xxA3XT2WPbtCPsux3CjNCPP7fA1mYL4dPtdTaju9kP+6jeuf0IkS0jZ31bHKx324cM/W4xiSbR/2OSyYepHdS7TxWPZIYpkMPbaHMLXao7Ok209LD7p3GWZ5RDNvnZTcvGlF10wKoHJ9xy7lHoSfy4NfRAD3doATK5meRo7/JQCCo8M8Mw6dnBvYC9bcb3zCrvTkwQz2dfjkHvmH/QcWkJS5iqYCS6Uk67PJsFtYxa5a9ZBiZGUVxhprrB0hoZem0vfsnzGgzbwjpw0VxDSN1ndXSIJZ4yXB2KI58NE0HMjkVL9OcmOItoS4fqLqdo7CqrntdHsRcDZ7lSaCFVphBMsJI3AbrWAyIM54N9SSMJgpQkrbJ1tWhO1jp8mTXGqW1YlbmCEFS+LRR6sk/F3YK6FtSucJlhlrOdeKGHVaESWLVFzTMBgfVS3TfKSxSRI8="),
    NPC_BLACKJACK_SKIN_DATA("settings.messages.npc.bjskindata", "ewogICJ0aW1lc3RhbXAiIDogMTYxODIxNTU4MTc1OCwKICAicHJvZmlsZUlkIiA6ICJlZDUzZGQ4MTRmOWQ0YTNjYjRlYjY1MWRjYmE3N2U2NiIsCiAgInByb2ZpbGVOYW1lIiA6ICI0MTQxNDE0MWgiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTA0NGNiODMyZDg3Y2RlNmFmNDJhMGRlNDdiYzg1YTY3YzdkNGU1OWEyZDc0NjY2MTc2ZDFjYTQxYWJkMGEyZCIKICAgIH0KICB9Cn0="),

    NPC_ROULETTE_NAME("settings.messages.npc.roulettenpcname", "Dealer"),
    NPC_ROULETTE_SKIN_SIG("settings.messages.npc.rouletteskinsig", "T5QGS3fQ9wWvsjmD6l9b/nZMkfOfYW1X3c1xvDdZQ5WHvPmew//3Q86+yfgQqIjPvEcXiDilr71p3WDrz/itsLb5mf9wLU5P4X18x5c6bmmv49TDLUCH5mEIUXu1jiQ8Kog/vzZNGZAAxadTGQPJ7BdII/+OpHDLS+WiCPRMnjCs/1h5RTE7I1OOPQnsh+yk+gOpaxCxgVFMLnMqNnL3mJP05qajHI6OKKXnyyXPwV0xxA3XT2WPbtCPsux3CjNCPP7fA1mYL4dPtdTaju9kP+6jeuf0IkS0jZ31bHKx324cM/W4xiSbR/2OSyYepHdS7TxWPZIYpkMPbaHMLXao7Ok209LD7p3GWZ5RDNvnZTcvGlF10wKoHJ9xy7lHoSfy4NfRAD3doATK5meRo7/JQCCo8M8Mw6dnBvYC9bcb3zCrvTkwQz2dfjkHvmH/QcWkJS5iqYCS6Uk67PJsFtYxa5a9ZBiZGUVxhprrB0hoZem0vfsnzGgzbwjpw0VxDSN1ndXSIJZ4yXB2KI58NE0HMjkVL9OcmOItoS4fqLqdo7CqrntdHsRcDZ7lSaCFVphBMsJI3AbrWAyIM54N9SSMJgpQkrbJ1tWhO1jp8mTXGqW1YlbmCEFS+LRR6sk/F3YK6FtSucJlhlrOdeKGHVaESWLVFzTMBgfVS3TfKSxSRI8="),
    NPC_ROULETTE_SKIN_DATA("settings.messages.npc.rouletteskindata", "ewogICJ0aW1lc3RhbXAiIDogMTYxODIxNTU4MTc1OCwKICAicHJvZmlsZUlkIiA6ICJlZDUzZGQ4MTRmOWQ0YTNjYjRlYjY1MWRjYmE3N2U2NiIsCiAgInByb2ZpbGVOYW1lIiA6ICI0MTQxNDE0MWgiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTA0NGNiODMyZDg3Y2RlNmFmNDJhMGRlNDdiYzg1YTY3YzdkNGU1OWEyZDc0NjY2MTc2ZDFjYTQxYWJkMGEyZCIKICAgIH0KICB9Cn0="),

    // GUI
    GUI_BOMB_AMOUNT_LABEL("settings.messages.gui.bombamountlabel", "&2Bomb Amount: "),
    GUI_BET_AMOUNT_LABEL("settings.messages.gui.betamountlabel", "&2Bet Amount: "),
    GUI_MINES_CASH_OUT("settings.messages.gui.minescashout", "&aCash-Out"),
    GUI_POKER_MIN_ENTRY_LABEL("settings.messages.gui.pokerminentrylabel", "&aMinimum Entry: "),
    GUI_POKER_RAISE_LIMIT_LABEL("settings.messages.gui.pokerraiselimitlabel", "&aRaise Limit: "),
    GUI_PLINKO_LOW_RISK("settings.messages.gui.plinkolowrisk", "Low Risk"),
    GUI_PLINKO_NORMAL_RISK("settings.messages.gui.plinkonormalrisk", "Normal Risk"),
    GUI_PLINKO_HIGH_RISK("settings.messages.gui.plinkohighrisk", "High Risk"),
    CHAT_RETURN_TO_GAME("settings.messages.chat.returntogame", "&cReturn to your %game% game!");

    private final String path;
    private final String defaultValue;
    private static final BoardGames instance = BoardGames.getInstance();
    private static final HashMap<String, ConfigUtil> teamNameMap = new HashMap<>();

    static {
        teamNameMap.put("RED", ConfigUtil.GUI_TEAM_RED_TEXT);
        teamNameMap.put("BLACK", ConfigUtil.GUI_TEAM_BLACK_TEXT);
        teamNameMap.put("WHITE", ConfigUtil.GUI_TEAM_WHITE_TEXT);
        teamNameMap.put("BLUE", ConfigUtil.GUI_TEAM_BLUE_TEXT);
    }

    ConfigUtil(String path, String defaultValue) {
        this.path = path;
        this.defaultValue = defaultValue;
    }

    @Override
    public String toString() {
        String configString = instance.getConfig().getString(this.path);

        if(configString == null) return "";

        return ChatColor.translateAlternateColorCodes('&', configString);
    }

    public String toRawString() {
        return ChatColor.stripColor(this.toString());
    }

    public boolean toBoolean() {
        return this.toString().equals("true");
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public String getDefaultValue() {
        return this.defaultValue;
    }

    public static boolean getBoolean(String path) {
        String configString = instance.getConfig().getString(path);

        if(configString == null) return false;

        return configString.equals("true");
    }

    public int toInteger() {
        if(MathUtils.isNumeric(this.toString())) {
           return Integer.parseInt(this.toString());
        }
        return 0;
    }

    public static String translateTeamName(String teamName) {
        if(teamNameMap.containsKey(teamName)) {
            return teamNameMap.get(teamName).toString();
        }

        return teamName;
    }

    public String buildString(String replaceWith) {
        String formatted = this.toString();

        formatted = formatted.replace("%player%", replaceWith)
                .replace("%string%", replaceWith)
                .replace("%game%", replaceWith)
                .replace("%num%", replaceWith);
        return formatted;
    }

    public String buildString(String replaceWith, String replaceWith2) {
        String formatted = this.toString();

        formatted = formatted.replace("%string%", replaceWith)
                .replace("%string2%", replaceWith2);

        return formatted;
    }

    public String buildString(String player, String game, Number num) {
        String formatted = this.toString();

        formatted = formatted.replace("%player%", player)
                .replace("%game%", game)
                .replace("%num%", getFormattedNum(num));
        return formatted;
    }

    public String buildString(String replaceWith, int num) {
        String formatted = this.toString();

        formatted = formatted
                .replace("%player%", replaceWith)
                .replace("%game%", replaceWith)
                .replace("%num%", num + "");

        return formatted;
    }

    public String buildString(String replaceWith, int num, int num2) {
        String formatted = this.toString();

        formatted = formatted
                .replace("%player%", replaceWith)
                .replace("%game%", replaceWith)
                .replace("%num%", num + "")
                .replace("%num2%", num2 + "");

        return formatted;
    }

    public String buildString(Number num, Number num2) {
        String formatted = this.toString();

        formatted = formatted
                .replace("%num%", getFormattedNum(num))
                .replace("%num2%", getFormattedNum(num2));

        return formatted;
    }

    public String buildStringPlayerGame(String playerName, String gameName) {
        String formatted = this.toString();

        formatted = formatted.replace("%player%", playerName).replace("%game%", gameName);

        return formatted;
    }

    public String buildStringLeaderboard(String game, String sort) {
        String formatted = this.toString();

        formatted = formatted.replace("%game%", game).replace("%sort%", sort);

        return formatted;
    }

    public String buildString(int num) {
        String formatted = this.toString();

        formatted = formatted.replace("%num%", num + "");

        return formatted;
    }

    public String buildStatsFormat(String statName, String statVal) {
        String formatted = this.toString();

        formatted = formatted.replace("%statName%", statName).replace("%statVal%", statVal);

        return formatted;
    }

    public String buildLeaderBoardFormat(int num, String playerName, String statVal) {
        String formatted = this.toString();

        formatted = formatted.replace("%num%", num + "").replace("%player%", playerName).replace("%statVal%", statVal);

        return formatted;
    }

    public void setValue(String value) {
        instance.getConfig().set(this.path, value);
        instance.saveConfig();
    }

    private String getFormattedNum(Number number) {
        return new DecimalFormat("#.##").format(number);
    }

}
