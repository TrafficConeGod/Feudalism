package feudalism.command.realm;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import feudalism.Chat;
import feudalism.Config;
import feudalism.FeudalismException;
import feudalism.command.SubcommandBase;
import feudalism.object.GridCoord;
import feudalism.object.Realm;
import feudalism.object.User;

public class CreateSubcommand extends SubcommandBase {
    @Override
    protected String[] getAliases() {
        return new String[] { "create", "new" };
    }

    @Override
    protected int getArgLength() {
        return 1;
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
        Player player = (Player) sender;
        User user = User.get(player.getUniqueId());
        if (user.getOwnedRealms().size() >= 1) {
            throw new FeudalismException("You must not own any realms");
        }
        float createPrice = Config.getFloat("realm.create_price");
        if (!user.hasMoney(createPrice)) {
            throw new FeudalismException(String.format("You need at least %s in your account to make a realm", createPrice));
        }
        user.removeMoney(createPrice);
        String name = args[0];
        GridCoord coord = GridCoord.getFromWorldPosition((int) player.getLocation().getX(), (int) player.getLocation().getZ());
        Realm realm = new Realm(User.get(player.getUniqueId()), name, coord);
        GridCoord claim = realm.getClaims().get(0);
        Chat.sendMessage(player, String.format("Created realm with name %s at: %s, %s", realm.getName(), claim.getWorldX(), claim.getWorldZ()));
    }
}
