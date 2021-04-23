package water.of.cup.boardgames.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import water.of.cup.boardgames.game.games.tictactoe.TicTacToe;
import water.of.cup.boardgames.game.games.tictactoe.TicTacToeInventory;

public class DebugCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(!(commandSender instanceof Player)) return false;

        if(command.getName().equalsIgnoreCase("debug")) {
            Player player = (Player) commandSender;
            if(!player.isOp()) return false;

            if(args.length > 0) {
                switch (args[0]) {
                    case "inventory": {
                        player.sendMessage("Opening inv");
                        new TicTacToeInventory(new TicTacToe(0)).build(player, null);
                        break;
                    }
                }
            }
            return true;
        }

        return false;
    }
}
