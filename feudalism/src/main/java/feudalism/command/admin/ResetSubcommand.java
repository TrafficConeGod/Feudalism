package feudalism.command.admin;

import feudalism.command.SubcommandBase;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ca.uqac.lif.azrael.json.JsonFileFridge;
import feudalism.Chat;
import feudalism.FeudalismException;
import feudalism.Registry;
import feudalism.object.GridCoord;
import feudalism.object.Realm;

public class ResetSubcommand extends SubcommandBase {
    @Override
    protected String[] getAliases() {
        return new String[]{ "reset", "clear" };
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) throws FeudalismException {
        Registry.resetInstance();
    }
}
