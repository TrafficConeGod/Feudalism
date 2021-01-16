package feudalism.object;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import feudalism.Chat;
import feudalism.FeudalismException;
import feudalism.Registry;

public class Confirmation {
    private CommandSender sender;
    private Lambda lambda;

    public Confirmation(CommandSender sender, Lambda lambda) {
        this.sender = sender;
        this.lambda = lambda;
        Registry.getInstance().addOrReplaceConfirmation(this);
        sender.sendMessage(Chat.insertColorCode("&a/confirm &rto confirm"));
        sender.sendMessage(Chat.insertColorCode("&c/cancel &rto cancel"));
    }

    public CommandSender getSender() {
        return sender;
    }

    public void confirm() {
        try {
            lambda.run();
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
