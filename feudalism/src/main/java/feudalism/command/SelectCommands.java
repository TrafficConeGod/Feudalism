package feudalism.command;

import java.util.List;
import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import feudalism.Chat;
import feudalism.Config;
import feudalism.FeudalismException;
import feudalism.object.Confirmation;
import feudalism.object.GridCoord;
import feudalism.object.Realm;
import feudalism.object.User;

public class SelectCommands {
    public static final CommandElement[] elements = new CommandElement[] {
        new Claim(),
        new Abandon()
    };
    
    public static class Abandon implements CommandElement {

        @Override
        public String[] getAliases() {
            return new String[] { "abandon" };
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
            Chat.sendMessage(sender, "Are you sure you want to abandon this realm?");
            new Confirmation(sender, () -> {
                Realm realm = (Realm) data.get(0);
                realm.removeOwner();
                Chat.sendMessage(sender, String.format("Successfully abandoned realm %s", realm.getName()));
            });
        }
    
        @Override
        public List<String> onTabComplete(CommandSender sender, String alias, String[] args, List<Object> data) throws FeudalismException {
            return new ArrayList<>();
        }
        
    }

    private static class Claim implements CommandElement {

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
            Realm realm = (Realm) data.get(0);
            Player player = (Player) sender;
            User user = User.get(player);
            GridCoord coord = GridCoord.getFromLocation(player.getLocation());
            if (coord.hasOwner()) {
                throw new FeudalismException("You can not claim owned land");
            }
            if (!realm.hasDirectBorder(coord)) {
                throw new FeudalismException("You can not claim land that you do not border");
            }
            float claimPrice = Config.getFloat("realm.claim_price");
            if (!user.hasMoney(claimPrice)) {
                throw new FeudalismException(String.format("You need at least %s in your account to claim", claimPrice));
            }
            user.removeMoney(claimPrice);
            realm.addClaim(coord);
            Chat.sendMessage(sender, String.format("Successfully claimed %s, %s for %s", coord.getGridX(), coord.getGridZ(), claimPrice));
            coord.clean();
        }
    
        @Override
        public List<String> onTabComplete(CommandSender sender, String alias, String[] args, List<Object> data) throws FeudalismException {
            return new ArrayList<>();
        }
        
    }
}
