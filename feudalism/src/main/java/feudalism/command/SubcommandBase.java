package feudalism.command;

import java.util.Arrays;

import org.bukkit.command.CommandSender;

public class SubcommandBase {
    protected String[] getAliases() {
        return new String[0];
    }

    protected void onExecute(CommandSender sender, String[] args) {
        
    }

    public boolean hasAlias(String check) {
        return Arrays.asList(getAliases()).contains(check);
    }
}
