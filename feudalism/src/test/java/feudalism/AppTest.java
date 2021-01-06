package feudalism;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

import java.util.UUID;

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
        UUID uuid = UUID.randomUUID();
        Realm realm = new Realm(uuid);
        realm.setName("Test");
        JsonPrinter printer = new JsonPrinter();
        JsonReader reader = new JsonReader();
        try {
            JsonElement elem = printer.print(realm);
            Realm realmReconstruct = (Realm) reader.read(elem);
            assertEquals(true, realm.getName() == realmReconstruct.getName());
        } catch (PrintException e) {
            e.printStackTrace();
            assertSame(true, false);
        } catch (ReadException e) {
            e.printStackTrace();
            assertSame(true, false);
        }
    }
}
