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
import feudalism.command.AdminCommand;
import feudalism.command.CancelCommand;
import feudalism.command.ConfirmCommand;
import feudalism.command.RealmCommand;
import feudalism.listener.BlockListener;
import feudalism.object.GridCoord;
import feudalism.object.Realm;

public class App extends JavaPlugin {
    private boolean isError = false;

    private static JavaPlugin plugin;

    public static JavaPlugin getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;
        try {
            initFilesystem();
        } catch (FeudalismException e) {
            e.printStackTrace();
            isError = true;
            return;
        } catch (FridgeException e) {
            e.printStackTrace();
            isError = true;
            return;
        } catch (IOException e) {
            e.printStackTrace();
            isError = true;
            return;
        } catch (PrintException e) {
            e.printStackTrace();
            isError = true;
            return;
        }
        initCommands();
        initListeners();
        for (Realm realm : Registry.getInstance().getTopRealms()) {
            System.out.println(realm);
        }
    }

    private void initFilesystem() throws FeudalismException, FridgeException, IOException, PrintException {
        File dir = new File("plugins/feudalism");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // config setup
        File configFile = new File("plugins/feudalism/config.lua");
        if (!configFile.exists()) {
            FileWriter writer = new FileWriter("plugins/feudalism/config.lua");
            writer.write(Config.generate());
            writer.close();
        }
        Config.loadFile("plugins/feudalism/config.lua");
        // registry and fridge setup
        File fridgeFile = new File("plugins/feudalism/fridge.json");
        if (!fridgeFile.exists()) {
            fridgeFile.createNewFile();
            FileWriter writer = new FileWriter("plugins/feudalism/fridge.json");
            JsonPrinter printer = new JsonPrinter();
            JsonElement elem = printer.print(Registry.getInstance());
            writer.write(elem.toString());
            writer.close();
        }
        JsonFileFridge fridge = new JsonFileFridge("plugins/feudalism/fridge.json");
        Registry registry = (Registry) fridge.fetch();
        registry.setFridge(fridge);
        Registry oldRegistry = Registry.getInstance();
        registry.loadFrom(oldRegistry);
        Registry.setInstance(registry);
        Registry.getInstance().initWorld();
        Registry.getInstance().initEconomy();
    }

    private void initCommands() {
        this.getCommand("admin").setExecutor(new AdminCommand());
        this.getCommand("realm").setExecutor(new RealmCommand());
        this.getCommand("confirm").setExecutor(new ConfirmCommand());
        this.getCommand("cancel").setExecutor(new CancelCommand());
    }

    private void initListeners() {
        getServer().getPluginManager().registerEvents(new BlockListener(), this);
    }

    @Override
    public void onDisable() {
        if (!isError) {
            Registry.getInstance().save();
        }
    }
}
