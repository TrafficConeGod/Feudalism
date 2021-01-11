package feudalism.object;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

import ca.uqac.lif.azrael.ObjectPrinter;
import ca.uqac.lif.azrael.ObjectReader;
import ca.uqac.lif.azrael.PrintException;
import ca.uqac.lif.azrael.Printable;
import ca.uqac.lif.azrael.ReadException;
import ca.uqac.lif.azrael.Readable;
import feudalism.Config;
import feudalism.FeudalismException;
import feudalism.Registry;

public class GridCoord implements Printable, Readable {
    private int x;
    private int z;
    private boolean hasOwner = false;
    private Realm owner;

    // i have to include this because azrael simply will not work without having a constructor with no args
    public GridCoord() {

    }

    private static int getSize() {
        return Config.getInt("grid_coord.size");
    }

    public GridCoord(int x, int z) {
        this.x = x;
        this.z = z;
        Registry.getInstance().addGridCoord(this);
    }

    public int getGridX() {
        return x;
    }

    public int getGridZ() {
        return z;
    }

    public boolean hasOwner() {
        return hasOwner;
    }

    public void setOwner(Realm owner) {
        boolean hadOwner = hasOwner;
        Realm oldOwner = owner;
        hasOwner = true;
        this.owner = owner;
        
        if (hadOwner && oldOwner.hasClaim(this)) {
            oldOwner.removeClaim(this);
        }
        if (hasOwner && !owner.hasClaim(this)) {
            owner.addClaim(this);
        }
    }

    public Realm getOwner() throws FeudalismException {
        if (!hasOwner()) {
            throw new FeudalismException("GridCoord does not have an owner");
        }
        return owner;
    }

    public Realm getOwnerOrNull() {
        if (hasOwner()) {
            return owner;
        } else {
            return null;
        }
    }

    public Location getLocation() {
        return new Location(Registry.getInstance().getWorld(), x * getSize(), 0, z * getSize());
    }

    public int getWorldX() {
        return x * getSize();
    }

    public int getWorldZ() {
        return z * getSize();
    }

    public static GridCoord getFromGridPosition(int x, int z) {
        if (Registry.getInstance().hasGridCoord(x, z)) {
            return Registry.getInstance().getGridCoord(x, z);
        } else {
            return new GridCoord(x, z);
        }
    }

    public static GridCoord getFromWorldPosition(int x, int z) {
        x = x / getSize();
        z = z / getSize();
        return GridCoord.getFromGridPosition(x, z);
    }

    public static GridCoord getFromLocation(Location location) {
        return getFromWorldPosition((int) location.getX(), (int) location.getZ());
    }

    public static boolean hasInGridPosition(int x, int z) {
        return Registry.getInstance().hasGridCoord(x, z);
    }

    public static boolean hasInWorldPosition(int x, int z) {
        x = x / getSize();
        z = z / getSize();
        return GridCoord.hasInGridPosition(x, z);
    }

    public void destroy() {
        Registry.getInstance().removeGridCoord(this);
    }

    @Override
    public String toString() {
        return x + ", " + z;
    }

    public Object print(ObjectPrinter<?> printer) throws PrintException {
        List<Integer> list = new ArrayList<>();
        list.add(getGridX());
        list.add(getGridZ());
        return printer.print(list);
    }

    public Object read(ObjectReader<?> reader, Object object) throws ReadException {
        List<Integer> list = (List<Integer>) reader.read(object);
        return GridCoord.getFromGridPosition(list.get(0), list.get(1));
    }
}