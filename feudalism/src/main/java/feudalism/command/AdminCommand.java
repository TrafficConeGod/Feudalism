package feudalism.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import feudalism.Chat;
import feudalism.FeudalismException;
import feudalism.Registry;
import feudalism.Util;
import feudalism.object.Confirmation;
import feudalism.object.GridCoord;
import feudalism.object.Realm;
import feudalism.object.User;

public class AdminCommand implements CommandElement, CommandExecutor, TabCompleter {
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
    public int getRequiredArgs() {
        return 1;
    }

    @Override
    public CommandElement[] getSubelements() {
        return new CommandElement[] {
            new Create(),
            new Reset(),
            new Visualize(),
            new Teleport()
        };
    }

    @Override
    public void onExecute(CommandSender sender, String alias, String[] args, List<Object> data) throws FeudalismException {
        Chat.sendMessage(sender, "You are about to perform an admin action. Do you wish to continue?");
        new Confirmation(sender, () -> {
            CommandElement element = getSubelementWithAlias(args[0]);
            element.execute(sender, alias, Util.trimArgs(args, 1), data);
        });
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
        public int getRequiredArgs() {
            return 2;
        }
    
        @Override
        public CommandElement[] getSubelements() {
            return new CommandElement[0];
        }
    
        @Override
        public void onExecute(CommandSender sender, String alias, String[] args, List<Object> data) throws FeudalismException {
            String name = args[0];
            String ownerName = args[1];
            User owner = User.get(Util.getPlayerUuidByName(ownerName));
            GridCoord coord;
            if (args.length == 4) {
                int x = Util.intFromString(args[2]);
                int z = Util.intFromString(args[3]);
                coord = GridCoord.getFromGridPosition(x, z);
            } else {
                coord = GridCoord.getFromGridPosition(0, 0);
            }
            Realm realm = new Realm(owner, name, coord);
            Chat.sendMessage(sender, realm.getInfo());
        }
    
        @Override
        public List<String> onTabComplete(CommandSender sender, String alias, String[] args, List<Object> data) throws FeudalismException {
            return new ArrayList<>();
        }
        
    }
    
    private class Reset implements CommandElement {

        @Override
        public String[] getAliases() {
            return new String[] { "reset", "clear" };
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
            Registry.resetInstance();
            Chat.sendMessage(sender, "Cleared Registry");
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, String alias, String[] args, List<Object> data) throws FeudalismException {
            return new ArrayList<>();
        }
        
    }
    private class Visualize implements CommandElement {

        @Override
        public String[] getAliases() {
            return new String[] { "visualize", "view" };
        }
    
        @Override
        public int getRequiredArgs() {
            return 3;
        }
    
        @Override
        public CommandElement[] getSubelements() {
            return new CommandElement[0];
        }
    
        @Override
        public void onExecute(CommandSender sender, String alias, String[] args, List<Object> data) throws FeudalismException {
            int x = Integer.parseInt(args[0]);
            int z = Integer.parseInt(args[1]);
            int size = Integer.parseInt(args[2]);
            sender.sendMessage(Registry.getInstance().getChunkVisualization(GridCoord.getFromGridPosition(x, z), size));
        }
    
        @Override
        public List<String> onTabComplete(CommandSender sender, String alias, String[] args, List<Object> data) throws FeudalismException {
            return new ArrayList<>();
        }
        
    }

    private class Teleport implements CommandElement {

        @Override
        public String[] getAliases() {
            return new String[] { "teleport", "tp" };
        }
    
        @Override
        public int getRequiredArgs() {
            return 1;
        }
    
        @Override
        public CommandElement[] getSubelements() {
            return new CommandElement[0];
        }
    
        @Override
        public void onExecute(CommandSender sender, String alias, String[] args, List<Object> data) throws FeudalismException {
            Util.checkPlayer(sender);
            Player player = (Player) sender;
            Realm realm = Registry.getInstance().getRealmByName(args[0]);
            for (GridCoord coord : realm.getClaims()) {
                player.teleport(coord.getLocation());
            }
        }
    
        @Override
        public List<String> onTabComplete(CommandSender sender, String alias, String[] args, List<Object> data) throws FeudalismException {
            if (args.length == 1) {
                List<String> names = new ArrayList<>();
                for (Realm realm : Registry.getInstance().getRealms()) {
                    names.add(realm.getName());
                }
                return names;
            }
            return new ArrayList<>();
        }
        
    }
    
}
