package feudalism.command;

import java.util.List;
import java.util.ArrayList;

import org.bukkit.command.CommandSender;

import feudalism.Chat;
import feudalism.FeudalismException;
import feudalism.object.Realm;

public class SelectCommands {
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
            Realm realm = (Realm) data.get(0);
            realm.removeOwner();
            Chat.sendMessage(sender, String.format("Successfully abandoned realm %s", realm.getName()));
        }
    
        @Override
        public List<String> onTabComplete(CommandSender sender, String alias, String[] args, List<Object> data) throws FeudalismException {
            return new ArrayList<>();
        }
        
    }
}
