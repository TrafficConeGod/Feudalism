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
    public boolean onExecute(CommandSender sender, String[] args) {
        try {
            Registry.resetInstance();
            return true;
        } catch (FeudalismException e) {
            e.printStackTrace();
            return false;
        }
    }
}
