package feudalism.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import feudalism.Chat;

public class CommandBase implements CommandExecutor {
    protected SubcommandBase[] getSubcommands() {
        return new SubcommandBase[0];
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            Chat.sendErrorMessage(sender, "Not enough arguments for command");
            return false;
        }
        String subCommandAlias = args[0];
        for (SubcommandBase subcommand : getSubcommands()) {
            if (subcommand.hasAlias(subCommandAlias)) {
                subcommand.onExecute(sender, args);
                break;
            }
        }
        return true;
    }
}