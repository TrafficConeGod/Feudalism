package feudalism;

import org.bukkit.command.CommandSender;

public class Chat {
    private static String insertColorCode(String msg) {
        return msg.replaceAll("&", "\u00a7");
    }

    private static String formatMessage(String msg) {
        return insertColorCode(String.format("&3[Feudalism]: &2%s", msg));
    }

    private static String formatErrorMessage(String msg) {
        return insertColorCode(String.format("&3[Feudalism]: &4%s", msg));
    }
    
    public static void sendMessage(CommandSender sender, String msg) {
        sender.sendMessage(formatMessage(msg));
    }
    
    public static void sendErrorMessage(CommandSender sender, String msg) {
        sender.sendMessage(formatErrorMessage(msg));
    }
}
