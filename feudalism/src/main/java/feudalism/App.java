package feudalism;
import java.util.UUID;
import org.bukkit.plugin.java.JavaPlugin;
// import ca.uqac.lif.azrael.json.JsonPrinter;
// import ca.uqac.lif.json.JsonElement;

import feudalism.command.realm.RealmCommand;
import feudalism.object.Realm;

public class App extends JavaPlugin {
    @Override
    public void onEnable() {
        this.getCommand("realm").setExecutor(new RealmCommand());
    }

    @Override
    public void onDisable() {
    }
}
