package feudalism;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.uqac.lif.azrael.ObjectPrinter;
import ca.uqac.lif.azrael.ObjectReader;
import ca.uqac.lif.azrael.PrintException;
import ca.uqac.lif.azrael.Printable;
import ca.uqac.lif.azrael.ReadException;
import ca.uqac.lif.azrael.Readable;
import ca.uqac.lif.azrael.fridge.FridgeException;
import ca.uqac.lif.azrael.json.JsonFileFridge;
import feudalism.object.GridCoord;
import feudalism.object.Realm;

import org.bukkit.Bukkit;
import org.bukkit.World;

public class Registry implements Printable, Readable {
    private static Registry instance = new Registry();

    public static Registry getInstance() {
        return instance;
    }

    public static void setInstance(Registry inst) {
        instance = inst;
    }

    private JsonFileFridge fridge;
    private List<Realm> topRealms = new ArrayList<>();
    private List<Realm> realms = new ArrayList<>();
    private Map<Integer, Map<Integer, GridCoord>> gridCoordCache = new HashMap<>();
    private World world;

    public void setFridge(JsonFileFridge fridge) {
        this.fridge = fridge;
    }

    public void initWorld() throws FeudalismException {
        String worldName = Config.getString("realm.world");
        world = Bukkit.getWorld(worldName);
        if (world == null) {
            throw new FeudalismException(String.format("World %s does not exist", worldName));
        }
    }

    public void addTopRealm(Realm realm) {
        if (realm.hasOverlord()) {
            System.out.println("Can not add top realm that has an overlord");
            return;
        }
        topRealms.add(realm);
    }

    public void removeTopRealm(Realm realm) {
        topRealms.remove(realm);
    }

    public List<Realm> getTopRealms() {
        return topRealms;
    }

    public void addRealm(Realm realm) {
        realms.add(realm);
    }

    public void removeRealm(Realm realm) {
        realms.remove(realm);
    }

    public List<Realm> getRealms() {
        return realms;
    }

    public boolean hasGridCoord(int x, int z) {
        return gridCoordCache.containsKey(x) && gridCoordCache.get(x).containsKey(z);
    }

    public boolean hasGridCoord(GridCoord gridCoord) {
        return hasGridCoord(gridCoord.getGridX(), gridCoord.getGridZ());
    }

    public GridCoord getGridCoord(int x, int z) {
        return gridCoordCache.get(x).get(z);
    }

    public void addGridCoord(GridCoord gridCoord) {
        if (!gridCoordCache.containsKey(gridCoord.getGridX())) {
            Map<Integer, GridCoord> map = new HashMap<>();
            gridCoordCache.put(gridCoord.getGridX(), map);
        }
        Map<Integer, GridCoord> zMap = gridCoordCache.get(gridCoord.getGridX());
        if (!zMap.containsKey(gridCoord.getGridZ())) {
            zMap.put(gridCoord.getGridZ(), gridCoord);
        }
    }

    public void removeGridCoord(GridCoord gridCoord) {
        if (hasGridCoord(gridCoord)) {
            gridCoordCache.get(gridCoord.getGridX()).remove(gridCoord.getGridZ());
        }
    }

    public World getWorld() {
        return world;
    }

    public static boolean isJUnitTest() {  
        for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
            if (element.getClassName().startsWith("org.junit.")) {
                return true;
            }           
        }
        return false;
    }      

    public void save() {
        try {
            fridge.store(this);
        } catch (FridgeException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object print(ObjectPrinter<?> printer) throws PrintException {
        List<Object> list = new ArrayList<>();
        list.add(getTopRealms());
        return printer.print(list);
    }

    @Override
    public Object read(ObjectReader<?> reader, Object object) throws ReadException {
        List<Object> list = (ArrayList<Object>) reader.read(object);
        Registry registry = new Registry();
        for (Realm realm : (ArrayList<Realm>) list.get(0)) {
            registry.addTopRealm(realm);
        }
        return registry;
    }

}
