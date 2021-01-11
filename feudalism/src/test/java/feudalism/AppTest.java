package feudalism;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Test;

import feudalism.object.GridCoord;
import feudalism.object.Realm;
import feudalism.object.Siege;
import feudalism.object.SiegeGoal;
import ca.uqac.lif.azrael.PrintException;
import ca.uqac.lif.azrael.ReadException;
import ca.uqac.lif.azrael.json.JsonPrinter;
import ca.uqac.lif.azrael.json.JsonReader;
import ca.uqac.lif.json.JsonElement;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

public class AppTest {
    @Test
    public void overlordSubjectLinkingTest() throws FeudalismException {
        Realm r1 = new Realm();
        Realm r2 = new Realm();
        r2.setOverlord(r1);
        assertEquals(true, r2.getOverlord() == r1);
        assertEquals(true, r1.getSubjects().get(0) == r2);
        r2.removeOverlord();
        assertEquals(false, r2.hasOverlord());
        assertEquals(false, r1.getSubjects().size() > 0);
        Registry.resetInstance();
    }

    @Test
    public void azraelSerializationTest() throws PrintException, ReadException, FeudalismException {
        Realm realm = new Realm();
        realm.setName("Test");
        realm.setOwner(UUID.randomUUID());
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
        assertEquals(true, realm.getOwner().equals(realmReconstruct.getOwner()));
        assertEquals(true, realm.getClaims().size() == realmReconstruct.getClaims().size());
        Registry.resetInstance();
    }

    @Test
    public void azraelGridCoordTest() throws PrintException, ReadException, FeudalismException {
        GridCoord coord = GridCoord.getFromGridPosition(100, 100);
        JsonPrinter printer = new JsonPrinter();
        JsonReader reader = new JsonReader();
        JsonElement elem = printer.print(coord);
        GridCoord coordR = (GridCoord) reader.read(elem);
        assertEquals(true, coord.getGridX() == coordR.getGridX());
        assertEquals(true, coord.getGridZ() == coordR.getGridZ());
        Registry.resetInstance();
    }

    @Test
    public void luaTest() throws FeudalismException {
        Globals globals = JsePlatform.standardGlobals();
        LuaValue val = globals.load("return \"Test string\"").call();
        assertEquals(true, val.equals(LuaValue.valueOf("Test string")));
        Registry.resetInstance();
    }

    @Test
    public void gridCoordTest() throws FeudalismException {
        Realm realm = new Realm();
        realm.addClaimFromWorldPosition(0, 0);
        realm.addClaimFromWorldPosition(0, 1);
        realm.addClaimFromWorldPosition(1, 1);
        realm.addClaimFromWorldPosition(1, 1);
        assertEquals(true, realm.getClaims().size() == 1);
        int size = Registry.getInstance().getRealms().size();
        realm.removeClaimFromGridPosition(0, 0);
        assertEquals(true, Registry.getInstance().getRealms().size() == size - 1);
        Registry.resetInstance();
    }

    @Test
    public void siegeTest() throws FeudalismException {
        assertEquals(true, Registry.getInstance().hasSiegeGoal("subjugate"));
        Realm r1 = new Realm();
        Realm r2 = new Realm();
        Siege siege = new Siege(r1, r2, Registry.getInstance().getSiegeGoal("subjugate"));
        try {
            r2.setOverlord(r1);
            assertEquals(true, false);
        } catch (FeudalismException e) {}
        assertEquals(false, r2.hasOverlord());
        siege.win(r1, new ArrayList<>());
        assertEquals(true, r2.getOverlord() == r1);
        Registry.resetInstance();
    }

    @Test
    public void configTest() throws FeudalismException {
        assertEquals(true, Config.getInt("#siege.goals") == 3);
        Registry.resetInstance();
    }

    @Test
    public void siegeGoalTest() throws FeudalismException {
        assertEquals(true, Registry.getInstance().hasSiegeGoal("subjugate"));
        SiegeGoal goal = Registry.getInstance().getSiegeGoal("subjugate");
        Realm r1 = new Realm();
        Realm r2 = new Realm();
        goal.execute(r1, r2, new ArrayList<>());
        assertEquals(true, r2.getOverlord() == r1);
        Registry.resetInstance();
    }

    @Test
    public void siegeSerializationTest() throws FeudalismException, ReadException, PrintException {
        assertEquals(true, Registry.getInstance().hasSiegeGoal("subjugate"));
        Realm r1 = new Realm();
        r1.setName("R1");
        Realm r2 = new Realm();
        r2.setName("R2");
        Realm ally = new Realm();
        Siege siege = new Siege(r1, r2, Registry.getInstance().getSiegeGoal("subjugate"));
        siege.addAlly(r1, ally);
        JsonPrinter printer = new JsonPrinter();
        JsonReader reader = new JsonReader();
        JsonElement elem = printer.print(siege);
        Siege siegeRec = (Siege) reader.read(elem);
        assertEquals(true, siegeRec.getAttacker() == siege.getAttacker());
        assertEquals(true, siegeRec.getDefender() == siege.getDefender());
        assertEquals(true, siegeRec.getAttackers().size() == siege.getAttackers().size());
        assertEquals(true, siegeRec.getDefenders().size() == siege.getDefenders().size());
        assertEquals(true, siegeRec.getGoal() == siege.getGoal());
        Registry.resetInstance();
    }

    @Test
    public void directBorderTest() throws FeudalismException {
        Realm realm = new Realm();
        realm.addClaimFromGridPosition(0, 0);
        GridCoord coord = GridCoord.getFromGridPosition(1, 0);
        GridCoord coordNot = GridCoord.getFromGridPosition(2, 0);
        assertEquals(true, realm.hasDirectBorder(coord));
        assertEquals(false, realm.hasDirectBorder(coordNot));
        Registry.resetInstance();
    }

    // @Test
    // public void configTest() throws FeudalismException, IOException {
    //     File configFile = new File("config.lua");
    //     if (!configFile.exists()) {
    //         FileWriter writer = new FileWriter("config.lua");
    //         writer.write(Config.generate());
    //         writer.close();
    //     }
    //     Config.loadFile("config.lua");
    //     System.out.println(Config.getInt("realm.coord_grid_size"));
    // }

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
