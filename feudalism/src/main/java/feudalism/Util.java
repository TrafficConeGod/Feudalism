package feudalism;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class Util {
    public static UUID getPlayerUuidByName(String name) throws FeudalismException {
        if (!isJUnitTest()) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(name); // oh ffs this is just for a command im not using it for data storage (what do you think my goal here is?????)
            if (player.hasPlayedBefore()) {
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
}
