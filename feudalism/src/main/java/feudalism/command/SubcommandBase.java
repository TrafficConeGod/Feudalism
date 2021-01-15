package feudalism.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;

import feudalism.FeudalismException;

public class SubcommandBase {
    protected String[] getAliases() {
        return new String[0];
    }

    protected int getArgLength() {
        return 0;
    }

    private String[] getSubcommandArgs(String[] args) {
        String[] subcommandArgs = new String[args.length - 1];
        for (int i = 1; i < args.length; i++) {
            subcommandArgs[i - 1] = args[i];
        }
        return subcommandArgs;
    }

    public void execute(CommandSender sender, String[] args) throws FeudalismException {
        String[] subcommandArgs = getSubcommandArgs(args);
        if (subcommandArgs.length < getArgLength()) {
            throw new FeudalismException("Not enough arguments for this command");
        }
        onExecute(sender, subcommandArgs);
    }

    protected void onExecute(CommandSender sender, String[] args) throws FeudalismException {
    }

    public List<String> getTabComplete(CommandSender sender, String[] args) {
        return onTabComplete(sender, getSubcommandArgs(args));
    }

    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    public boolean hasAlias(String check) {
        return Arrays.asList(getAliases()).contains(check.toLowerCase());
    }
}
