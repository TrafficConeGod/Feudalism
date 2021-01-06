package feudalism.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;

public class SubcommandBase {
    protected String[] getAliases() {
        return new String[0];
    }

    protected boolean onExecute(CommandSender sender, String[] args) {
        return false;
    }

    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    public boolean hasAlias(String check) {
        return Arrays.asList(getAliases()).contains(check);
    }
}
