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
import feudalism.object.Confirmation;
import feudalism.object.GridCoord;
import feudalism.object.PermType;
import feudalism.object.Realm;
import feudalism.object.Request;
import feudalism.object.Siege;
import feudalism.object.SiegeGoal;
import feudalism.object.User;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Registry implements Printable, Readable {
    public Registry() {
        {
            int size = Config.getInt("#siege.goals");
            for (int i = 1; i <= size; i++) {
                siegeGoals.add(new SiegeGoal(i));
            }
        }
        {
            int size = Config.getInt("#realm.perms");
            for (int i = 1; i <= size; i++) {
                permTypes.add(new PermType(i));
            }
        }
    }

    private static Registry instance = new Registry();

    public static Registry getInstance() {
        return instance;
    }

    public static void setInstance(Registry inst) {
        instance = inst;
    }

    public static void resetInstance() throws FeudalismException {
        JsonFileFridge fridge = getInstance().getFridge();
        Registry oldRegistry = Registry.getInstance();
        World world = oldRegistry.getWorld();
        Economy economy = oldRegistry.getEconomy();
        Registry registry = new Registry();
        registry.setFridge(fridge);
        registry.setWorld(world);
        registry.setEconomy(economy);
        Registry.setInstance(registry);
        if (!Util.isJUnitTest()) {
            Registry.getInstance().initWorld();
        }
    }

    private JsonFileFridge fridge;
    private List<User> users = new ArrayList<>();
    private List<Realm> topRealms = new ArrayList<>();
    private List<Siege> sieges = new ArrayList<>();

    private List<Realm> realms = new ArrayList<>();
    private Map<Integer, Map<Integer, GridCoord>> gridCoordCache = new HashMap<>();
    private List<SiegeGoal> siegeGoals = new ArrayList<>();
    private List<PermType> permTypes = new ArrayList<>();
    private List<Confirmation> confirmations = new ArrayList<>();
    private List<Request> requests = new ArrayList<>();
    private World world;
    private Economy economy;

    public void setFridge(JsonFileFridge fridge) {
        this.fridge = fridge;
    }

    public JsonFileFridge getFridge() {
        return fridge;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public void setEconomy(Economy economy) {
        this.economy = economy;
    }

    public void initWorld() throws FeudalismException {
        String worldName = Config.getString("realm.world");
        world = Bukkit.getWorld(worldName);
        if (world == null) {
            throw new FeudalismException(String.format("World %s does not exist", worldName));
        }
    }

    public void initEconomy() throws FeudalismException {
        if (App.getPlugin().getServer().getPluginManager().getPlugin("Vault") == null) {
            throw new FeudalismException("Vault plugin is not installed");
        }
        RegisteredServiceProvider<Economy> rsp = App.getPlugin().getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null || rsp.getProvider() == null) {
            throw new FeudalismException("Error while setting up economy service");
        }
        economy = rsp.getProvider();
    }

    public void addTopRealm(Realm realm) throws FeudalismException {
        if (realm.hasOverlord()) {
            throw new FeudalismException("Can not add top realm that has an overlord");
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
        throw new FeudalismException(String.format("No realm with uuid %s", uuid.toString()));
    }

    public Realm getRealmByName(String name) throws FeudalismException {
        for (Realm realm : getRealms()) {
            if (realm.getName().equals(name)) {
                return realm;
            }
        }
        throw new FeudalismException(String.format("No realm with name %s", name));
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

    public Economy getEconomy() {
        return economy;
    }

    public boolean hasSiegeGoal(String name) {
        for (SiegeGoal goal : siegeGoals) {
            if (goal.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public SiegeGoal getSiegeGoal(String name) throws FeudalismException {
        for (SiegeGoal goal : siegeGoals) {
            if (goal.getName().equals(name)) {
                return goal;
            }
        }
        throw new FeudalismException(String.format("No siege goal type with name %s", name));
    }

    public boolean hasPermType(String name) {
        for (PermType type : permTypes) {
            if (type.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public PermType getPermType(String name) throws FeudalismException {
        for (PermType type : permTypes) {
            if (type.getName().equals(name)) {
                return type;
            }
        }
        throw new FeudalismException(String.format("No perm type with name %s", name));
    }

    public List<PermType> getPermTypes() {
        return permTypes;
    }

    public PermType getPermTypeByDisplayName(String displayName) throws FeudalismException {
        for (PermType type : permTypes) {
            if (type.getDisplayName().equals(displayName)) {
                return type;
            }
        }
        throw new FeudalismException(String.format("No perm type with name %s", displayName));
    }

    public boolean isInConflict(Realm realm1, Realm realm2) {
        for (Siege siege : getSieges()) {
            List<Realm> attackers = siege.getAttackers();
            List<Realm> defenders = siege.getDefenders();
            if ((attackers.contains(realm1) && defenders.contains(realm2))
                    || (attackers.contains(realm1) && defenders.contains(realm2))) {
                return true;
            }
        }
        return false;
    }

    public void addUser(User user) {
        users.add(user);
    }

    public void removeUser(User user) {
        users.remove(user);
    }

    public User getUserByUuid(UUID uuid) throws FeudalismException {
        for (User user : users) {
            if (user.getUuid().equals(uuid)) {
                return user;
            }
        }
        throw new FeudalismException(String.format("No user with uuid %s", uuid.toString()));
    }

    public boolean hasRealmWithName(String name) {
        for (Realm realm : realms) {
            if (realm.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public String getChunkVisualization(GridCoord coord, int size) {
        String output = "";
        int gridX = coord.getGridX();
        int gridZ = coord.getGridZ();
        int topX = gridX - size;
        int bottomX = gridX + size;
        int topZ = gridZ - size;
        int bottomZ = gridZ + size;
        for (int z = topZ; z <= bottomZ; z++) {
            for (int x = topX; x <= bottomX; x++) {
                String val = "-";
                GridCoord checkCoord = GridCoord.getFromGridPosition(x, z);
                if (checkCoord == coord) {
                    val = "O";
                } else if (checkCoord.hasOwner()) {
                    String name = checkCoord.getOwnerOrNull().getName();
                    val = String.valueOf(name.charAt(0));
                }
                checkCoord.clean();
                output += val;
                if (x + 1 > bottomX) {
                    output += "\n";
                }
            }
        }
        return output;
    }

    private PermType getPermTypeWithEvent(String eventType) throws FeudalismException {
        for (PermType type : permTypes) {
            if (type.hasEvent(eventType)) {
                return type;
            }
        }
        throw new FeudalismException(String.format("No perm type with event %s", eventType));
    }

    public boolean getEventStatus(String eventType, Player player, GridCoord coord) {
        try {
            // if (player.isOp()) {
            //     return true;
            // }
            if (!coord.hasOwner()) {
                coord.clean();
                return false;
            }
            PermType type = getPermTypeWithEvent(eventType);
            User user = User.get(player);
            boolean status = !user.hasPerm(coord.getOwner(), type);
            if (status) {
                Chat.sendErrorMessage(player, String.format("You can't %s here!", type.getDisplayName()));
            }
            return status;
        } catch (FeudalismException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void addOrReplaceConfirmation(Confirmation confirmation) {
        for (Confirmation check : confirmations) {
            if (check.getSender() == confirmation.getSender()) {
                confirmations.remove(check);
                break;
            }
        }
        confirmations.add(confirmation);
    }

    public void removeConfirmation(Confirmation confirmation) {
        confirmations.remove(confirmation);
    }

    public Confirmation getConfirmation(CommandSender sender) throws FeudalismException {
        for (Confirmation confirmation : confirmations) {
            if (confirmation.getSender() == sender) {
                return confirmation;
            }
        }
        throw new FeudalismException(String.format("You have nothing to confirm or cancel"));
    }

    public List<Confirmation> getConfirmations() {
        return confirmations;
    }

    public void addOrReplaceRequest(Request request) {
        for (Request check : requests) {
            if (check.getSender() == request.getSender()) {
                requests.remove(check);
                break;
            }
        }
        requests.add(request);
    }

    public void removeRequest(Request request) {
        requests.remove(request);
    }

    public Request getRequest(CommandSender sender) throws FeudalismException {
        for (Request request : requests) {
            if (request.getSender() == sender) {
                return request;
            }
        }
        throw new FeudalismException(String.format("You have nothing to accept or deny"));
    }

    public List<Request> getRequests() {
        return requests;
    }

    public List<String> getPlayerNames() {
        List<String> names = new ArrayList<>();
        for (Player player : world.getPlayers()) {
            names.add(player.getDisplayName());
        }
        return names;
    }

    public List<String> getRealmNames() {
        List<String> names = new ArrayList<>();
        for (Realm realm : getRealms()) {
            names.add(realm.getName());
        }
        return names;
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
        list.add(users);
        list.add(getTopRealms());
        list.add(getSieges());
        return printer.print(list);
    }

    @Override
    public Object read(ObjectReader<?> reader, Object object) throws ReadException {
        try {
            List<Object> list = (ArrayList<Object>) reader.read(object);
            Registry registry = new Registry();
            for (User user : (ArrayList<User>) list.get(0)) {
                registry.addUser(user);
            }
            for (Realm realm : (ArrayList<Realm>) list.get(1)) {
                registry.addTopRealm(realm);
            }
            for (Siege siege : (ArrayList<Siege>) list.get(2)) {
                registry.addSiege(siege);
            }
            return registry;
        } catch (FeudalismException e) {
            throw new ReadException(e);
        }
    }

    // this is hacky code that has no reason to exist but i set up this structure in a dumbass way so here i am writing this
    public void loadFrom(Registry oldRegistry) {
        realms = oldRegistry.realms;
        gridCoordCache = oldRegistry.gridCoordCache;
    }

}
