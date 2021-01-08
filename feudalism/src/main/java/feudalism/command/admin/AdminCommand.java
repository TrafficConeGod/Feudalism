package feudalism.command.admin;

import feudalism.command.CommandBase;
import feudalism.command.SubcommandBase;

public class AdminCommand extends CommandBase {
    @Override
    protected SubcommandBase[] getSubcommands() {
        return new SubcommandBase[]{new ResetSubcommand()};
    }
}