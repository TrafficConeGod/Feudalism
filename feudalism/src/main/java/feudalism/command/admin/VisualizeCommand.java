package feudalism.command.admin;

import feudalism.command.SubcommandBase;
import feudalism.object.GridCoord;

import org.bukkit.command.CommandSender;
import feudalism.Chat;
import feudalism.FeudalismException;
import feudalism.Registry;

public class VisualizeCommand extends SubcommandBase {
    @Override
    protected String[] getAliases() {
        return new String[]{ "visualize", "view" };
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) throws FeudalismException {
        if (args.length < 3) {
            throw new FeudalismException("Too few arguments");
        }
        int x = Integer.parseInt(args[0]);
        int z = Integer.parseInt(args[1]);
        int size = Integer.parseInt(args[2]);
        Chat.sendMessage(sender, Registry.getInstance().getChunkVisualization(GridCoord.getFromGridPosition(x, z), size));
    }
}
