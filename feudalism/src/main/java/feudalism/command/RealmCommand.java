package feudalism.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import feudalism.Chat;
import feudalism.Config;
import feudalism.FeudalismException;
import feudalism.Util;
import feudalism.object.GridCoord;
import feudalism.object.Realm;
import feudalism.object.User;

public class RealmCommand implements CommandElement, CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        execute(sender, label, args, new ArrayList<>());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return getTabComplete(sender, alias, args, new ArrayList<>());
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public CommandElement[] getSubelements() {
        return new CommandElement[] {
            new Create()
        };
    }
    
    @Override
    public int getRequiredArgs() {
        return 1;
    }

    @Override
    public void onExecute(CommandSender sender, String alias, String[] args, List<Object> data) throws FeudalismException {
        Util.checkPlayer(sender);
        CommandElement element = getSubelementWithAlias(args[0]);
        element.execute(sender, alias, Util.trimArgs(args, 1), data);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String alias, String[] args, List<Object> data) throws FeudalismException {
        if (args.length <= 1) {
            return getSubaliases();
        }
        CommandElement element = getSubelementWithAlias(args[0]);
        return element.getTabComplete(sender, alias, Util.trimArgs(args, 1), data);
    }

    private class Create implements CommandElement {
        @Override
        public String[] getAliases() {
            return new String[] { "create", "new" };
        }

        @Override
        public CommandElement[] getSubelements() {
            return new CommandElement[0];
        }

        @Override
        public int getRequiredArgs() {
            return 1;
        }

        @Override
        public void onExecute(CommandSender sender, String alias, String[] args, List<Object> data) throws FeudalismException {
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
            Realm realm = new Realm(User.get(player), name, coord);
            GridCoord claim = realm.getClaims().get(0);
            Chat.sendMessage(player, String.format("Created realm with name %s at: %s, %s", realm.getName(), claim.getWorldX(), claim.getWorldZ()));
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, String alias, String[] args, List<Object> data) throws FeudalismException {
            return new ArrayList<>();
        }
    }

}
