package feudalism;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Util {
    public static UUID getPlayerUuidByName(String name) throws FeudalismException {
        if (!isJUnitTest()) {
            Player player = Bukkit.getPlayer(name);
            if (player != null && player.isOnline()) {
                return player.getUniqueId();
            } else {
                throw new FeudalismException("No player with name " + name);
            }
        }
        return UUID.randomUUID();
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
