package feudalism;

import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.azrael.fridge.FridgeException;
import ca.uqac.lif.azrael.json.JsonFileFridge;
import feudalism.object.Realm;

public class Registry {
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

    public void save() {
        try {
            fridge.store(this);
        } catch (FridgeException e) {
            e.printStackTrace();
        }
    }
}
