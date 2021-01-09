package feudalism;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import feudalism.object.Siege;

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

    public static void resetInstance() throws FeudalismException {
        JsonFileFridge fridge = getInstance().getFridge();
        Registry registry = new Registry();
        registry.setFridge(fridge);
        Registry.setInstance(registry);
        if (!Util.isJUnitTest()) {
            Registry.getInstance().initWorld();
        }
    }

    private JsonFileFridge fridge;
    private List<Realm> topRealms = new ArrayList<>();
    private List<Siege> sieges = new ArrayList<>();

    private List<Realm> realms = new ArrayList<>();
    private Map<Integer, Map<Integer, GridCoord>> gridCoordCache = new HashMap<>();
    private World world;

    public void setFridge(JsonFileFridge fridge) {
        this.fridge = fridge;
    }

    public JsonFileFridge getFridge() {
        return fridge;
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

    public void addSiege(Siege siege) {
        sieges.add(siege);
    }

    public void removeSiege(Siege siege) {
        sieges.remove(siege);
    }

    public List<Siege> getSieges() {
        return sieges;
    }

    public Realm getRealmByUuid(UUID uuid) throws FeudalismException {
        for (Realm realm : getRealms()) {
            if (realm.getUuid().equals(uuid)) {
                return realm;
            }
        }
        throw new FeudalismException("No realm with uuid %s");
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
    
    // TODO: Particularly inefficient function
    public boolean isInConflict(Realm realm1, Realm realm2) {
        for (Siege siege : getSieges()) {
            List<Realm> attackers = siege.getAttackers();
            List<Realm> defenders = siege.getDefenders();
            if ((attackers.contains(realm1) && defenders.contains(realm2)) || (attackers.contains(realm1) && defenders.contains(realm2))) {
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
        list.add(getSieges());
        return printer.print(list);
    }

    @Override
    public Object read(ObjectReader<?> reader, Object object) throws ReadException {
        List<Object> list = (ArrayList<Object>) reader.read(object);
        Registry registry = new Registry();
        for (Realm realm : (ArrayList<Realm>) list.get(0)) {
            registry.addTopRealm(realm);
        }
        for (Siege siege : (ArrayList<Siege>) list.get(1)) {
            registry.addSiege(siege);
        }
        return registry;
    }

}
