package feudalism.object;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ca.uqac.lif.azrael.ObjectPrinter;
import ca.uqac.lif.azrael.ObjectReader;
import ca.uqac.lif.azrael.PrintException;
import ca.uqac.lif.azrael.Printable;
import ca.uqac.lif.azrael.ReadException;
import ca.uqac.lif.azrael.Readable;

public class Perms implements Printable, Readable {
    private Map<String, Boolean> enabledTypes = new HashMap<>();

    public Perms() {

    }

    public Perms(Map<String, Boolean> enabledTypes) {
        this.enabledTypes = enabledTypes;
    }

    public void set(PermType type, boolean status) {
        enabledTypes.put(type.getName(), status);
    }

    public boolean get(PermType type) {
        if (enabledTypes.containsKey(type.getName())) {
            return enabledTypes.get(type.getName());
        }
        return type.getDefaultStatus();
    }

    public Object print(ObjectPrinter<?> printer) throws PrintException {
        return printer.print(enabledTypes);
    }

    public Object read(ObjectReader<?> reader, Object object) throws ReadException {
        return new Perms((Map<String, Boolean>) reader.read(object));
    }
}
