package feudalism.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import feudalism.Chat;
import feudalism.FeudalismException;
import feudalism.Registry;
import feudalism.object.Request;

public class DenyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            Request request = Registry.getInstance().getRequest(sender);
            request.deny();
        } catch (FeudalismException e) {
            Chat.sendErrorMessage(sender, e.getMessage());
        }
        return true;
    }
    
}
