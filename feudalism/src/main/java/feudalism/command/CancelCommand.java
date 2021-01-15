package feudalism.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import feudalism.Chat;
import feudalism.FeudalismException;
import feudalism.Registry;
import feudalism.object.Confirmation;

public class CancelCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            Confirmation confirmation = Registry.getInstance().getConfirmation(sender);
            confirmation.cancel();
        } catch (FeudalismException e) {
            Chat.sendErrorMessage(sender, e.getMessage());
        }
        return true;
    }
    
}
