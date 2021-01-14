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
import feudalism.object.PermType;
import feudalism.object.Perms;
import feudalism.object.Realm;
import feudalism.object.Siege;
import feudalism.object.SiegeGoal;
import feudalism.object.User;
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
    public void realmsTest() throws FeudalismException {
        float upkeepFactor = Config.getFloat("realm.upkeep_factor");
        User user = new User();
        Realm r1 = new Realm();
        r1.setName("First");
        r1.addClaim(GridCoord.getFromGridPosition(0, 0));
        r1.addClaim(GridCoord.getFromGridPosition(0, 1));
        r1.setOwner(user);
        Realm r2 = new Realm();
        r2.setName("Second");
        r2.addClaim(GridCoord.getFromGridPosition(1, 0));
        r2.addClaim(GridCoord.getFromGridPosition(1, 1));
        r2.setOwner(user);
        r2.setOverlord(r1);
        assertEquals(true, r2.getOverlord() == r1);
        assertEquals(true, r1.getSubjects().get(0) == r2);
        r2.removeOverlord();
        assertEquals(false, r2.hasOverlord());
        assertEquals(false, r1.getSubjects().size() > 0);
        assertEquals(true, user.getOwnedRealms().size() == 2);
        assertEquals(true, user.getUpkeep() == upkeepFactor * 4);
        Registry.resetInstance();
    }

    @Test
    public void descendantSubjectsTest() throws FeudalismException {
        Realm tr = new Realm();
        Realm mr = new Realm();
        Realm br = new Realm();
        br.setOverlord(mr);
        mr.setOverlord(tr);
        assertEquals(true, tr.getDescendantSubjects().size() == 2);
        mr.removeOverlord();
        assertEquals(true, tr.getDescendantSubjects().size() == 0);
        Registry.resetInstance();
    }

    @Test
    public void azraelRealmTest() throws PrintException, ReadException, FeudalismException {
        User user = User.get(UUID.randomUUID());
        User member = User.get(UUID.randomUUID());
        Realm realm = new Realm(user, "Test", GridCoord.getFromGridPosition(0, 10));
        realm.addMember(member);
        PermType blockPlace = Registry.getInstance().getPermType("block_place");
        assertEquals(true, realm.hasPerm(user, blockPlace));
        assertEquals(false, realm.hasPerm(member, blockPlace));
        realm.getMemberPerms().set(blockPlace, true);
        assertEquals(true, realm.hasPerm(member, blockPlace));
        Realm r2 = new Realm();
        r2.setName("Test2");
        r2.setOverlord(realm);
        JsonPrinter printer = new JsonPrinter();
        JsonReader reader = new JsonReader();
        JsonElement elem = printer.print(realm);
        Realm realmReconstruct = (Realm) reader.read(elem);
        assertEquals(true, (realm.getName() + "_").equals(realmReconstruct.getName()));
        assertEquals(true, realm.getUuid().equals(realmReconstruct.getUuid()));
        assertEquals(true, realm.getSubjects().size() == realmReconstruct.getSubjects().size());
        assertEquals(true, (realm.getSubjects().get(0).getName() + "_").equals(realmReconstruct.getSubjects().get(0).getName()));
        assertEquals(true, realm.getOwner().equals(realmReconstruct.getOwner()));
        assertEquals(true, realm.getClaims().size() == realmReconstruct.getClaims().size());
        assertEquals(true, user == realmReconstruct.getOwner());
        assertEquals(true, realmReconstruct.getMembers().size() == 1);
        assertEquals(true, realmReconstruct.getMembers().get(0) == member);
        assertEquals(true, realmReconstruct.hasPerm(user, blockPlace));
        assertEquals(true, realmReconstruct.hasPerm(member, blockPlace));
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
    public void azraelUserTest() throws PrintException, ReadException, FeudalismException {
        User user = User.get(UUID.randomUUID());
        JsonPrinter printer = new JsonPrinter();
        JsonReader reader = new JsonReader();
        JsonElement elem = printer.print(user);
        User userR = (User) reader.read(elem);
        assertEquals(true, user.getUuid().equals(userR.getUuid()));
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
        realm.addClaim(GridCoord.getFromWorldPosition(0, 0));
        realm.addClaim(GridCoord.getFromWorldPosition(0, 1));
        realm.addClaim(GridCoord.getFromWorldPosition(1, 1));
        realm.addClaim(GridCoord.getFromWorldPosition(1, 1));
        realm.addClaim(GridCoord.getFromGridPosition(1, 10));
        assertEquals(true, realm.getClaims().size() == 1);
        int size = Registry.getInstance().getRealms().size();
        realm.removeClaim(GridCoord.getFromGridPosition(0, 0));
        assertEquals(true, Registry.getInstance().getRealms().size() == size - 1);
        Registry.resetInstance();
    }

    @Test
    public void siegeTest() throws FeudalismException {
        assertEquals(true, Registry.getInstance().hasSiegeGoal("subjugate"));
        User user1 = new User();
        Realm r1 = new Realm(user1, "Realm1");
        Realm subject1 = new Realm(user1, "Subject/PU1", r1);
        Realm r2 = new Realm();
        r2.setName("Realm2");
        Realm subject2 = new Realm();
        subject2.setName("Subject2");
        subject2.setOverlord(r2);
        Realm ally1 = new Realm();
        ally1.setName("Ally1");
        Realm ally2 = new Realm();
        ally2.setName("Ally2");
        Siege siege = new Siege(r1, r2, Registry.getInstance().getSiegeGoal("subjugate"));
        siege.addAlly(r1, ally1);
        siege.addAlly(r2, ally2);
        try {
            r2.setOverlord(r1);
            assertEquals(true, false);
        } catch (FeudalismException e) {
        }
        try {
            siege.addAlly(r2, ally1);
            assertEquals(true, false);
        } catch (FeudalismException e) {}
        assertEquals(false, r2.hasOverlord());
        assertEquals(true, siege.getAttackers().size() == 3);
        assertEquals(true, siege.getDefenders().size() == 3);
        assertEquals(true, siege.getCombatants().size() == 6);
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
        realm.addClaim(GridCoord.getFromGridPosition(1, 0));
        GridCoord coord = GridCoord.getFromGridPosition(2, 0);
        GridCoord coordNot = GridCoord.getFromGridPosition(3, 0);
        assertEquals(true, realm.hasDirectBorder(coord));
        assertEquals(false, realm.hasDirectBorder(coordNot));
        Registry.resetInstance();
    }

    @Test
    public void borderRadiusTest() throws FeudalismException {
        Realm realm = new Realm();
        realm.addClaim(GridCoord.getFromGridPosition(1, 0));
        GridCoord coord = GridCoord.getFromGridPosition(5, 0);
        GridCoord coordNot = GridCoord.getFromGridPosition(12, 0);
        assertEquals(true, realm.isWithinBorderRadius(coord));
        assertEquals(false, realm.isWithinBorderRadius(coordNot));
        Registry.resetInstance();
    }

    @Test
    public void siegeBorderRadiusTest() throws FeudalismException {
        assertEquals(true, Registry.getInstance().hasSiegeGoal("subjugate"));
        Realm r1 = new Realm();
        r1.addClaim(GridCoord.getFromGridPosition(0, 0));
        Realm r2 = new Realm();
        r2.addClaim(GridCoord.getFromGridPosition(50, 10));
        try {
            new Siege(r1, r2, Registry.getInstance().getSiegeGoal("subjugate"));
            assertEquals(true, false);
        } catch (FeudalismException e) {
            assertEquals(true, true);
        }
        Registry.resetInstance();
    }

    @Test
    public void permsTest() throws FeudalismException {
        assertEquals(true, Registry.getInstance().hasPermType("block_place"));
        Perms perms = new Perms();
        assertEquals(true, perms.get(Registry.getInstance().getPermType("block_place")) == false);
        perms.set(Registry.getInstance().getPermType("block_place"), true);
        assertEquals(true, perms.get(Registry.getInstance().getPermType("block_place")) == true);
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
