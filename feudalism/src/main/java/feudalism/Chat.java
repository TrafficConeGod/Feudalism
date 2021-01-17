package feudalism;

import org.bukkit.command.CommandSender;

public class Chat {
    public static String insertColorCode(String msg) {
        return msg.replaceAll("&", "\u00a7");
    }

    private static String formatMessage(String msg) {
        return insertColorCode(String.format("&b[Feudalism] &f%s", msg));
    }

    private static String formatStatMessage(String name, String value) {
        return insertColorCode(String.format("&b%s: &e%s", name, value));
    }

    private static String formatErrorMessage(String msg) {
        return insertColorCode(String.format("&b[Feudalism] &c%s", msg));
    }
    
    public static void sendMessage(CommandSender sender, String msg) {
        sender.sendMessage(formatMessage(msg));
    }
    
    public static void sendStatMessage(CommandSender sender, String name, String value) {
        sender.sendMessage(formatStatMessage(name, value));
    }
    
    public static void sendErrorMessage(CommandSender sender, String msg) {
        sender.sendMessage(formatErrorMessage(msg));
    }
}
