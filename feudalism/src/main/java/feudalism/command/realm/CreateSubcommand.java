package feudalism.command.realm;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import feudalism.Chat;
import feudalism.FeudalismException;
import feudalism.command.SubcommandBase;
import feudalism.object.GridCoord;
import feudalism.object.Realm;

public class CreateSubcommand extends SubcommandBase {
    @Override
    protected String[] getAliases() {
        return new String[] { "create", "new" };
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) throws FeudalismException {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.isOnline()) {
                throw new FeudalismException("Sender must be an online player");
            }
        } else {
            throw new FeudalismException("Sender must be an online player");
        }
        if (args.length < 1) {
            throw new FeudalismException("Not enough arguments");
        }
        Player player = (Player) sender;
        String name = args[0];
        Realm realm = new Realm(player.getUniqueId(), name);
        realm.addClaimFromWorldPosition((int) player.getLocation().getX(), (int) player.getLocation().getZ());
        GridCoord claim = realm.getClaims().get(0);
        Chat.sendMessage(player, String.format("Created realm with name %s at: %s, %s", realm.getName(), claim.getGridX(), claim.getGridZ()));
    }
}
