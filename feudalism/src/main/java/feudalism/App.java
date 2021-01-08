package feudalism;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;
import org.bukkit.plugin.java.JavaPlugin;

import ca.uqac.lif.azrael.PrintException;
import ca.uqac.lif.azrael.fridge.FridgeException;
import ca.uqac.lif.azrael.json.JsonFileFridge;
import ca.uqac.lif.azrael.json.JsonPrinter;
import ca.uqac.lif.json.JsonElement;
import feudalism.command.realm.RealmCommand;
import feudalism.object.Realm;

public class App extends JavaPlugin {
    @Override
    public void onEnable() {
        try {
            initFilesystem();
        } catch (FeudalismException e) {
            e.printStackTrace();
            return;
        }
        initCommands();
        for (Realm realm : Registry.getInstance().getTopRealms()) {
            System.out.println(realm);
        }
    }

    private void initFilesystem() throws FeudalismException {
        File dir = new File("plugins/feudalism");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // config setup
        File configFile = new File("plugins/feudalism/config.lua");
        if (!configFile.exists()) {
            try {
                FileWriter writer = new FileWriter("plugins/feudalism/config.lua");
                writer.write(Config.generate());
                writer.close();
            } catch (IOException e) {
                configFile.delete();
                throw new FeudalismException("IOException: " + e.getMessage());
            }
        }
        Config.loadFile("plugins/feudalism/config.lua");
        // registry and fridge setup
        File fridgeFile = new File("plugins/feudalism/fridge.json");
        if (!fridgeFile.exists()) {
            try {
                fridgeFile.createNewFile();
                FileWriter writer = new FileWriter("plugins/feudalism/fridge.json");
                JsonPrinter printer = new JsonPrinter();
                JsonElement elem = printer.print(Registry.getInstance());
                writer.write(elem.toString());
                writer.close();
            } catch (IOException e) {
                fridgeFile.delete();
                throw new FeudalismException("IOException: " + e.getMessage());
            } catch (PrintException e) {
                fridgeFile.delete();
                throw new FeudalismException("PrintException: " + e.getMessage());
            }
        }
        JsonFileFridge fridge = new JsonFileFridge("plugins/feudalism/fridge.json");
        try {
            Registry registry = (Registry) fridge.fetch();
            registry.setFridge(fridge);
            Registry.setInstance(registry);
            Registry.getInstance().initWorld();
        } catch (FridgeException e) {
            throw new FeudalismException("FridgeException: " + e.getMessage());
        }
    }

    private void initCommands() {
        this.getCommand("realm").setExecutor(new RealmCommand());
    }

    @Override
    public void onDisable() {
        Registry.getInstance().save();
    }
}
