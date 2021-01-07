package feudalism;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import feudalism.object.Realm;
import ca.uqac.lif.azrael.PrintException;
import ca.uqac.lif.azrael.ReadException;
import ca.uqac.lif.azrael.json.JsonPrinter;
import ca.uqac.lif.azrael.json.JsonReader;
import ca.uqac.lif.json.JsonElement;

public class AppTest {
    @Test
    public void overlordSubjectLinkTest() {
        Realm r1 = new Realm();
        Realm r2 = new Realm();
        r2.setOverlord(r1);
        assertEquals(true, r2.getOverlord() == r1);
        assertEquals(true, r1.getSubjects().get(0) == r2);
    }

    @Test
    public void overlordSubjectDelinkTest() {
        Realm r1 = new Realm();
        Realm r2 = new Realm();
        r2.setOverlord(r1);
        r2.removeOverlord();
        assertEquals(false, r2.hasOverlord());
        assertEquals(false, r1.getSubjects().size() > 0);
    }

    @Test
    public void azraelTest() {
        Realm realm = new Realm();
        realm.setName("Test");
        Realm r2 = new Realm();
        r2.setName("Test2");
        r2.setOverlord(realm);
        JsonPrinter printer = new JsonPrinter();
        JsonReader reader = new JsonReader();
        try {
            JsonElement elem = printer.print(realm);
            Realm realmReconstruct = (Realm) reader.read(elem);
            assertEquals(true, realm.getName() == realmReconstruct.getName());
            assertEquals(true, realm.getSubjects().size() == realmReconstruct.getSubjects().size());
            assertEquals(true, realm.getSubjects().get(0).getName() == realmReconstruct.getSubjects().get(0).getName());
        } catch (PrintException e) {
            e.printStackTrace();
            assertEquals(true, false);
        } catch (ReadException e) {
            e.printStackTrace();
            assertEquals(true, false);
        }
    }
}
