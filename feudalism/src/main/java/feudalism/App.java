package feudalism;
import java.io.File;
import java.util.UUID;
import org.bukkit.plugin.java.JavaPlugin;

import ca.uqac.lif.azrael.fridge.FridgeException;
import ca.uqac.lif.azrael.json.JsonFileFridge;
import feudalism.command.realm.RealmCommand;
import feudalism.object.Realm;

public class App extends JavaPlugin {
    @Override
    public void onEnable() {
        initFilesystem();
        initCommands();
    }

    private void initFilesystem() {
        File dir = new File("/plugins/feudalism");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        JsonFileFridge fridge = new JsonFileFridge("/plugins/feudalism/fridge.json");
        try {
            System.out.println(fridge.fetch());
        } catch (FridgeException e) {
            e.printStackTrace();
        }
    }

    private void initCommands() {
        this.getCommand("realm").setExecutor(new RealmCommand());
    }

    @Override
    public void onDisable() {
    }
}
