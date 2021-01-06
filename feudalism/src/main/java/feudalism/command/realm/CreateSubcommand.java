package feudalism.command.realm;

import org.bukkit.command.CommandSender;
import feudalism.command.SubcommandBase;

public class CreateSubcommand extends SubcommandBase {
    @Override
    protected String[] getAliases() {
        /* TODO: add aliases to config */
        return new String[]{ "create", "new" };
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        System.out.println(args[0]);
    }
}
