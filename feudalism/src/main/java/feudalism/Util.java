package feudalism;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import feudalism.object.User;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class Util {
    public static UUID getPlayerUuidByName(String name) throws FeudalismException {
        if (!isJUnitTest()) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(name); // oh ffs this is just for a command im not using it for data storage (what do you think my goal here is?????)
            if (player.hasPlayedBefore() || player.isOnline()) {
                return player.getUniqueId();
            } else {
                throw new FeudalismException("No player with name " + name);
            }
        }
        return UUID.randomUUID();
    }
    
    public static boolean isValidPlayerUuid(UUID uuid) {
        if (!isJUnitTest()) {
            if (Bukkit.getPlayer(uuid) != null) {
                return true;
            }
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            return player.hasPlayedBefore();
        }
        return true;
    }

    public static boolean isJUnitTest() {  
        for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
            if (element.getClassName().startsWith("org.junit.")) {
                return true;
            }           
        }
        return false;
    }

    public static String repeatString(String str, int count) {
        return new String(new char[count]).replace("\0", str);
    }

    public static String[] trimArgs(String[] args, int amount) {
        String[] trimmed = new String[args.length - amount];
        for (int i = amount; i < args.length; i++) {
            trimmed[i - amount] = args[i];
        }
        return trimmed;
    }

    public static void checkPlayer(CommandSender sender) throws FeudalismException {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.isOnline()) {
                throw new FeudalismException("Sender must be an online player");
            }
        } else {
            throw new FeudalismException("Sender must be an online player");
        }
    }

    public static int intFromString(String str) throws FeudalismException {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            throw new FeudalismException(String.format("%s is not a valid number", str));
        }
    } 

    public static void sendActionBarMessage(Player player, String msg) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(msg));
    }

    public static User getUserByNameAndCheckOnline(String name) throws FeudalismException {
        UUID uuid = Util.getPlayerUuidByName(name);
        User user = User.get(uuid);
        OfflinePlayer offlinePlayer = user.getOfflinePlayer();
        if (!offlinePlayer.isOnline()) {
            throw new FeudalismException(String.format("%s must be online to do this", name));
        }
        return user;
    }
}
