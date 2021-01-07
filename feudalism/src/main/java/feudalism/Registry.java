package feudalism;

import java.io.IOException;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.azrael.ObjectPrinter;
import ca.uqac.lif.azrael.ObjectReader;
import ca.uqac.lif.azrael.PrintException;
import ca.uqac.lif.azrael.Printable;
import ca.uqac.lif.azrael.ReadException;
import ca.uqac.lif.azrael.Readable;
import ca.uqac.lif.azrael.fridge.FridgeException;
import ca.uqac.lif.azrael.json.JsonFileFridge;
import feudalism.object.Realm;

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

    public void setFridge(JsonFileFridge fridge) {
        this.fridge = fridge;
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
