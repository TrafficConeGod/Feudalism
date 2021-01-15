package feudalism.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.swing.plaf.synth.SynthEditorPaneUI;

import org.bukkit.command.CommandSender;

import feudalism.Chat;
import feudalism.FeudalismException;

public interface CommandElement {
    abstract public String[] getAliases();
    abstract public void onExecute(CommandSender sender, String alias, String[] args, List<Object> data) throws FeudalismException;
    abstract public List<String> onTabComplete(CommandSender sender, String alias, String[] args, List<Object> data) throws FeudalismException;
    abstract public int getRequiredArgs();

    abstract public CommandElement[] getSubelements();

    public default void execute(CommandSender sender, String alias, String[] args, List<Object> data) {
        if (args.length < getRequiredArgs()) {
            Chat.sendErrorMessage(sender, "Not enough arguments for command");
            return;
        }
        try {
            onExecute(sender, alias, args, data);
        } catch (FeudalismException e) {
            Chat.sendErrorMessage(sender, e.getMessage());
        }
    }

    public default List<String> getTabComplete(CommandSender sender, String alias, String[] args, List<Object> data) {
        try {
            List<String> tabComplete = onTabComplete(sender, alias, args, data);
            return tabComplete;
        } catch (FeudalismException e) {
            return new ArrayList<>();
        }
    }

    public default boolean hasAlias(String check) {
        return Arrays.asList(getAliases()).contains(check.toLowerCase());
    }

    public default CommandElement getSubelementWithAlias(String alias) throws FeudalismException {
        for (CommandElement element : getSubelements()) {
            if (element.hasAlias(alias)) {
                return element;
            }
        }
        throw new FeudalismException(String.format("%s is an invalid name", alias));
    }

    public default List<String> getSubaliases() {
        List<String> subaliases = new ArrayList<>();
        for (CommandElement element : getSubelements()) {
            subaliases.addAll(Arrays.asList(element.getAliases()));
        }
        return subaliases;
    }
}
