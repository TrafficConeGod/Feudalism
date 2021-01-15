package feudalism.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import feudalism.Chat;
import feudalism.FeudalismException;

public class CommandBase implements CommandExecutor, TabCompleter {
    private List<String> aliases = new ArrayList<>();

    public CommandBase() {
        for (SubcommandBase subcommand : getSubcommands()) {
            aliases.addAll(Arrays.asList(subcommand.getAliases()));
        }
    }

    protected SubcommandBase[] getSubcommands() {
        return new SubcommandBase[0];
    }

    private SubcommandBase getSubcommandByAlias(String alias) throws FeudalismException {
        for (SubcommandBase subcommand : getSubcommands()) {
            if (subcommand.hasAlias(alias)) {
                return subcommand;
            }
        }
        throw new FeudalismException(String.format("No subcommand by alias: %s", alias));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            Chat.sendErrorMessage(sender, "Not enough arguments for this command");
            return true;
        }
        String alias = args[0];
        try {
            SubcommandBase subcommand = getSubcommandByAlias(alias);
            subcommand.execute(sender, args);
            return true;
        } catch (FeudalismException e) {
            Chat.sendErrorMessage(sender, e.getMessage());
            return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return aliases;
        } else {
            String alias = args[0];
            try {
                SubcommandBase subcommand = getSubcommandByAlias(alias);
                return subcommand.getTabComplete(sender, args);
            } catch (Exception e) {
                return new ArrayList<>();
            }
        }
    }
}