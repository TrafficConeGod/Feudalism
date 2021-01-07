package feudalism;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import feudalism.object.Realm;
import ca.uqac.lif.azrael.PrintException;
import ca.uqac.lif.azrael.ReadException;
import ca.uqac.lif.azrael.fridge.FridgeException;
import ca.uqac.lif.azrael.json.JsonFileFridge;
import ca.uqac.lif.azrael.json.JsonPrinter;
import ca.uqac.lif.azrael.json.JsonReader;
import ca.uqac.lif.json.JsonElement;

public class AppTest {
    @Test
    public void overlordSubjectLinkingTest() {
        Realm r1 = new Realm();
        Realm r2 = new Realm();
        r2.setOverlord(r1);
        assertEquals(true, r2.getOverlord() == r1);
        assertEquals(true, r1.getSubjects().get(0) == r2);
        r2.removeOverlord();
        assertEquals(false, r2.hasOverlord());
        assertEquals(false, r1.getSubjects().size() > 0);
    }

    @Test
    public void azraelSerializationTest() throws PrintException, ReadException {
        Realm realm = new Realm();
        realm.setName("Test");
        Realm r2 = new Realm();
        r2.setName("Test2");
        r2.setOverlord(realm);
        JsonPrinter printer = new JsonPrinter();
        JsonReader reader = new JsonReader();
        JsonElement elem = printer.print(realm);
        Realm realmReconstruct = (Realm) reader.read(elem);
        assertEquals(true, realm.getName() == realmReconstruct.getName());
        assertEquals(true, realm.getUuid().equals(realmReconstruct.getUuid()));
        assertEquals(true, realm.getSubjects().size() == realmReconstruct.getSubjects().size());
        assertEquals(true, realm.getSubjects().get(0).getName() == realmReconstruct.getSubjects().get(0).getName());
    }

    // @Test
    // public void azraelFridgeTest() throws FridgeException {
    //     Realm realm = new Realm();
    //     realm.setName("Test");
    //     JsonFileFridge fridge = new JsonFileFridge("fridge_test.json");
    //     fridge.store(realm);
    //     Realm reconst = (Realm) fridge.fetch();
    //     assertEquals(true, realm.getName().equals(reconst.getName()));
    // }
}
