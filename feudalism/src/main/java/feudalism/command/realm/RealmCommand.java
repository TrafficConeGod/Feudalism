package feudalism.command.realm;

import feudalism.command.CommandBase;
import feudalism.command.SubcommandBase;


public class RealmCommand extends CommandBase {
    @Override
    protected SubcommandBase[] getSubcommands() {
        return new SubcommandBase[]{new CreateSubcommand()};
    }
}