package water.of.cup.boardgames.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import water.of.cup.boardgames.game.games.tictactoe.TicTacToe;
import water.of.cup.boardgames.game.games.tictactoe.TicTacToeInventory;

public class DebugCommand implements CommandExecutor {

    private static TicTacToeInventory ticTacToeInventory;

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
                        if(ticTacToeInventory == null) {
                            ticTacToeInventory = new TicTacToeInventory(new TicTacToe(0));
                        }

                        ticTacToeInventory.build(player);
                        break;
                    }
                    case "reset": {
                        player.sendMessage("reset");
                        ticTacToeInventory = null;
                        break;
                    }
                }
            }
            return true;
        }

        return false;
    }
}
