package feudalism;
import java.util.UUID;
import org.bukkit.plugin.java.JavaPlugin;

public class App extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("Hello, SpigotMC!");
    }

    @Override
    public void onDisable() {
        getLogger().info("See you again, SpigotMC!");
    }
}

// public class App 
// {
//     public static void main( String[] args )
//     {
//         System.out.println( "Hello World!" );
//         Realm realm = new Realm();
//         realm.setOwner(UUID.randomUUID());
//         Realm r2 = new Realm();
//         r2.setOverlord(realm);
//         r2.removeOverlord();
//         // realm.removeSubject(r2);
//         System.out.println(realm.getSubjects());
//         System.out.println(r2.hasOverlord());
//         System.out.println(r2.getOverlord());
//     }
// }
