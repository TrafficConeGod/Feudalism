package feudalism.command.realm;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import feudalism.Chat;
import feudalism.Registry;
import feudalism.command.SubcommandBase;
import feudalism.object.Realm;

public class CreateSubcommand extends SubcommandBase {
    @Override
    protected String[] getAliases() {
        return new String[]{ "create", "new" };
    }

    @Override
    public boolean onExecute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.isOnline()) {
                Chat.sendErrorMessage(sender, "Sender must be an online player");
                return false;
            }
        } else {
            Chat.sendErrorMessage(sender, "Sender must be an online player");
            return false;
        }
        if (args.length < 1) {
            Chat.sendErrorMessage(sender, "Not enough arguments");
            return false;
        }
        Player player = (Player) sender;
        String name = args[0];
        Realm realm = new Realm();
        realm.setOwner(player.getUniqueId());
        realm.setName(name);
        Registry.getInstance().save();
        Chat.sendMessage(player, String.format("Created realm with name %s", realm.getName()));
        return true;
    }
}
