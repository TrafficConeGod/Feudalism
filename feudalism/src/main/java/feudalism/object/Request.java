package feudalism.object;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import feudalism.Chat;
import feudalism.FeudalismException;
import feudalism.Registry;

public class Request {
    private CommandSender sender;
    private Lambda acceptLambda;
    private Lambda denyLambda;

    public Request(CommandSender sender, Lambda acceptLambda, Lambda denyLambda) {
        this.sender = sender;
        this.acceptLambda = acceptLambda;
        this.denyLambda = denyLambda;
        Registry.getInstance().addOrReplaceRequest(this);
        sender.sendMessage(Chat.insertColorCode("&a/accept &rto accept"));
        sender.sendMessage(Chat.insertColorCode("&c/deny &rto deny"));
    }

    public CommandSender getSender() {
        return sender;
    }

    public void accept() {
        try {
            acceptLambda.run();
        } catch (FeudalismException e) {
            Chat.sendErrorMessage(sender, e.getMessage());
        }
        destroy();
    }

    public void deny() {
        try {
            denyLambda.run();
        } catch (FeudalismException e) {
            Chat.sendErrorMessage(sender, e.getMessage());
        }
        destroy();
    }

    private void destroy() {
        Registry.getInstance().removeRequest(this);
    }
}
