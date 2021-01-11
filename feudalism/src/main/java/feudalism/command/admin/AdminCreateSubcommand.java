package feudalism.command.admin;

import java.util.UUID;

import org.bukkit.command.CommandSender;

import feudalism.FeudalismException;
import feudalism.Util;
import feudalism.command.SubcommandBase;
import feudalism.object.GridCoord;
import feudalism.object.Realm;
import feudalism.object.User;

public class AdminCreateSubcommand extends SubcommandBase {
    @Override
    protected String[] getAliases() {
        return new String[]{ "create", "new" };
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) throws FeudalismException {
        if (args.length < 2) {
            throw new FeudalismException("Too few arguments");
        }
        String name = args[0];
        String ownerName = args[1];
        User owner = User.get(Util.getPlayerUuidByName(ownerName));
        Realm realm = new Realm(owner, name);
        realm.addClaim(GridCoord.getFromGridPosition(0, 0));
        System.out.println(realm);
    }
}
