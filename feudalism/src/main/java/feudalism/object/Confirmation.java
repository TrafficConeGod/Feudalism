package feudalism.object;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import feudalism.Chat;
import feudalism.FeudalismException;
import feudalism.Registry;

public class Confirmation {
    private CommandSender sender;
    private ConfirmationInterface confInterface;

    public Confirmation(CommandSender sender, ConfirmationInterface confInterface) {
        this.sender = sender;
        this.confInterface = confInterface;
        Registry.getInstance().addOrReplaceConfirmation(this);
        sender.sendMessage(Chat.insertColorCode("&a/confirm &rto confirm"));
        sender.sendMessage(Chat.insertColorCode("&c/cancel &rto cancel"));
    }

    public CommandSender getSender() {
        return sender;
    }

    public void confirm() {
        try {
            confInterface.onConfirm();
        } catch (FeudalismException e) {
            Chat.sendErrorMessage(sender, e.getMessage());
        }
        destroy();
    }

    public void cancel() {
        Chat.sendMessage(sender, "Successfully cancelled action");
        destroy();
    }

    private void destroy() {
        Registry.getInstance().removeConfirmation(this);
    }
}
