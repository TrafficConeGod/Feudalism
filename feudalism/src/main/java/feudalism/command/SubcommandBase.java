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

    protected void onExecute(CommandSender sender, String[] args) throws FeudalismException {
    }

    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    public boolean hasAlias(String check) {
        return Arrays.asList(getAliases()).contains(check.toLowerCase());
    }
}
