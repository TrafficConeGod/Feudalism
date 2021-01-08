package feudalism.object;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;

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
    private static int size = -1;

    private static void initSize() {
        if (size == -1) {
            try {
                size = Config.getInt("grid_coord.size");
            } catch (FeudalismException e) {
                e.printStackTrace();
            }
        }
    }

    public GridCoord(int x, int z) {
        initSize();
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

    public Location getLocation() {
        return new Location(Registry.getInstance().getWorld(), x * size, 0, z * size);
    }

    public int getWorldX() {
        return x * size;
    }

    public int getWorldZ() {
        return z * size;
    }

    public static GridCoord getFromGridPosition(int x, int z) {
        if (Registry.getInstance().hasGridCoord(x, z)) {
            return Registry.getInstance().getGridCoord(x, z);
        } else {
            return new GridCoord(x, z);
        }
    }

    public static GridCoord getFromWorldPosition(int x, int z) {
        initSize();
        x = (int) x / size;
        z = (int) z / size;
        return GridCoord.getFromGridPosition(x, z);
    }

    @Override
    public Object print(ObjectPrinter<?> printer) throws PrintException {
        List<Integer> list = new ArrayList<>();
        list.add(getGridX());
        list.add(getGridZ());
        return printer.print(list);
    }

    @Override
    public Object read(ObjectReader<?> reader, Object object) throws ReadException {
        List<Integer> list = (List<Integer>) reader.read(object);
        return new GridCoord(list.get(0), list.get(1));
    }
}