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
import feudalism.Registry;
import feudalism.Util;
import feudalism.object.Confirmation;
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
            new This(),
            new As(),
            new Create(),
            new Claim()
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
        if (args.length == 1) {
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
            GridCoord coord = GridCoord.getFromWorldPosition((int) player.getLocation().getX(), (int) player.getLocation().getZ());
            if (coord.hasOwner()) {
                throw new FeudalismException(String.format("%s, %s is already owned by another realm", coord.getWorldX(), coord.getWorldZ()));
            }
            
            Chat.sendMessage(sender, String.format("Are you sure you want to create a realm called %s? This will cost %s.", args[0], createPrice));
            new Confirmation(sender, () -> {
                String name = args[0];
                Realm realm = new Realm(User.get(player), name, coord);
                user.removeMoney(createPrice);
                GridCoord claim = realm.getClaims().get(0);
                Chat.sendMessage(player, String.format("Created realm with name %s at: %s, %s", realm.getName(), claim.getWorldX(), claim.getWorldZ()));
            });
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, String alias, String[] args, List<Object> data) throws FeudalismException {
            return new ArrayList<>();
        }
    }

    private class Claim implements CommandElement {

        @Override
        public String[] getAliases() {
            return new String[] { "claim" };
        }
    
        @Override
        public int getRequiredArgs() {
            return 0;
        }
    
        @Override
        public CommandElement[] getSubelements() {
            return new CommandElement[0];
        }
    
        @Override
        public void onExecute(CommandSender sender, String alias, String[] args, List<Object> data) throws FeudalismException {
            Player player = (Player) sender;
            User user = User.get(player);
            Realm realm = Registry.getInstance().getRealmByName(args[0]);
            if (realm.hasOwner()) {
                throw new FeudalismException("Realm already has owner");
            }
            realm.setOwner(user);
            Chat.sendMessage(sender, String.format("Claimed realm %s", realm.getName()));
        }
    
        @Override
        public List<String> onTabComplete(CommandSender sender, String alias, String[] args, List<Object> data) throws FeudalismException {
            if (args.length != 1) {
                return new ArrayList<>();
            }
            List<String> names = new ArrayList<>();
            for (Realm realm : Registry.getInstance().getRealms()) {
                if (!realm.hasOwner()) {
                    names.add(realm.getName());
                }
            }
            return names;
        }
        
    }

    private class As implements CommandElement {

        @Override
        public String[] getAliases() {
            return new String[] { "as" };
        }
    
        @Override
        public int getRequiredArgs() {
            return 2;
        }
    
        @Override
        public CommandElement[] getSubelements() {
            return new CommandElement[] {
                new SelectCommands.Abandon()
            };
        }
    
        @Override
        public void onExecute(CommandSender sender, String alias, String[] args, List<Object> data) throws FeudalismException {
            String realmName = args[0];
            Player player = (Player) sender;
            User user = User.get(player);
            for (Realm realm : user.getOwnedRealms()) {
                if (realm.getName().equals(realmName) && realm.hasOwner() && realm.getOwner() == user) {
                    data.add(realm);
                    CommandElement element = getSubelementWithAlias(args[1]);
                    element.execute(sender, alias, Util.trimArgs(args, 2), data);
                    return;
                }
            }
            throw new FeudalismException(String.format("You do not own a realm with name %s", realmName));
        }
    
        @Override
        public List<String> onTabComplete(CommandSender sender, String alias, String[] args, List<Object> data) throws FeudalismException {
            if (args.length == 1) {
                List<String> names = new ArrayList<>();
                Player player = (Player) sender;
                User user = User.get(player);
                for (Realm realm : user.getOwnedRealms()) {
                    names.add(realm.getName());
                }
                return names;
            } else if (args.length == 2) {
                return getSubaliases();
            }
            CommandElement element = getSubelementWithAlias(args[0]);
            return element.getTabComplete(sender, alias, Util.trimArgs(args, 1), data);
        }
        
    }

    private class This implements CommandElement {

        @Override
        public String[] getAliases() {
            return new String[] { "this" };
        }
    
        @Override
        public int getRequiredArgs() {
            return 1;
        }
    
        @Override
        public CommandElement[] getSubelements() {
            return new CommandElement[] {
                new SelectCommands.Abandon()
            };
        }
    
        @Override
        public void onExecute(CommandSender sender, String alias, String[] args, List<Object> data) throws FeudalismException {
            Player player = (Player) sender;
            User user = User.get(player);
            GridCoord coord = GridCoord.getFromLocation(player.getLocation());
            if (!coord.hasOwner()) {
                throw new FeudalismException("No realm owns the coord you are standing in");
            }
            Realm realm = coord.getOwner();
            if (!realm.hasOwner() || realm.getOwner() != user) {
                throw new FeudalismException("You do not own this realm");
            }
            data.add(realm);
            CommandElement element = getSubelementWithAlias(args[0]);
            element.execute(sender, alias, Util.trimArgs(args, 1), data);
        }
    
        @Override
        public List<String> onTabComplete(CommandSender sender, String alias, String[] args, List<Object> data) throws FeudalismException {
            if (args.length == 1) {
                return getSubaliases();
            }
            CommandElement element = getSubelementWithAlias(args[0]);
            return element.getTabComplete(sender, alias, Util.trimArgs(args, 1), data);
        }
        
    }

}
