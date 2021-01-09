package feudalism.object;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ca.uqac.lif.azrael.ObjectPrinter;
import ca.uqac.lif.azrael.ObjectReader;
import ca.uqac.lif.azrael.PrintException;
import ca.uqac.lif.azrael.Printable;
import ca.uqac.lif.azrael.ReadException;
import ca.uqac.lif.azrael.Readable;
import feudalism.FeudalismException;
import feudalism.Registry;

public class Siege implements Printable, Readable {
    private Realm attacker;
    private Realm defender;
    private List<Realm> attackerAllies = new ArrayList<Realm>();
    private List<Realm> defenderAllies = new ArrayList<Realm>();
    private SiegeGoal goal;

    // please azrael
    public Siege() {

    }

    public Siege(Realm attacker, Realm defender, SiegeGoal goal) {
        this.attacker = attacker;
        this.defender = defender;
        this.goal = goal;
        Registry.getInstance().addSiege(this);
    }

    public void destroy() {
        Registry.getInstance().removeSiege(this);
    }

    public List<Realm> getAttackers() {
        List<Realm> attackers = new ArrayList<>();
        attackers.add(attacker);
        attackers.addAll(attacker.getDescendantSubjects());
        attackers.addAll(attackerAllies);
        for (Realm realm : attackerAllies) {
            attackers.addAll(realm.getDescendantSubjects());
        }
        return attackers;
    }

    public List<Realm> getDefenders() {
        List<Realm> defenders = new ArrayList<>();
        defenders.add(defender);
        defenders.addAll(defender.getDescendantSubjects());
        defenders.addAll(defenderAllies);
        for (Realm realm : defenderAllies) {
            defenders.addAll(realm.getDescendantSubjects());
        }
        return defenders;
    }

    private boolean isSubject(List<Realm> possibleOverlords, Realm possibleSubject) {
        for (Realm realm : possibleOverlords) {
            if (realm.hasDescendantSubject(possibleSubject)) {
                return true;
            }
        }
        return false;
    }

    public void addAlly(Realm side, Realm ally) throws FeudalismException {
        // towny.newwar flashbacks
        if (isInvolved(ally)) {
            throw new FeudalismException(String.format("%s is already involved in the siege", ally));
        }
        if (side == attacker) {
            if (isSubject(getDefenders(), ally)) {
                throw new FeudalismException(String.format("%s is a subject of the opposite side", ally));
            }
            attackerAllies.add(ally);
        } else {
            if (isSubject(getAttackers(), ally)) {
                throw new FeudalismException(String.format("%s is a subject of the opposite side", ally));
            }
            defenderAllies.add(ally);
        }
    }

    public boolean isInvolved(Realm realm) {
        return getAttackers().contains(realm) || getDefenders().contains(realm);
    }

    public void win(Realm side, List<String> propValues) throws FeudalismException {
        destroy();
        if (side == attacker) {
            goal.execute(attacker, defender, propValues);
        } else {
            // nothing here yet
        }
    }

    @Override
    public Object print(ObjectPrinter<?> printer) throws PrintException {
        List<Object> list = new ArrayList<>();
        list.add(attacker.getUuid().toString());
        list.add(defender.getUuid().toString());
        List<String> attackerAllies = new ArrayList<>();
        for (Realm ally : this.attackerAllies) {
            attackerAllies.add(ally.getUuid().toString());
        }
        list.add(attackerAllies);
        List<String> defenderAllies = new ArrayList<>();
        for (Realm ally : this.defenderAllies) {
            defenderAllies.add(ally.getUuid().toString());
        }
        list.add(defenderAllies);
        // TODO: Add other siege properties
        return printer.print(list);
    }

    @Override
    public Object read(ObjectReader<?> reader, Object object) throws ReadException {
        List<Object> list = (ArrayList<Object>) reader.read(object);
        try {
            Realm attacker = Registry.getInstance().getRealmByUuid(UUID.fromString((String) list.get(0)));
            Realm defender = Registry.getInstance().getRealmByUuid(UUID.fromString((String) list.get(1)));
            // Siege siege = new Siege(attacker, defender);
            // TODO: Add other siege properties
            // return siege;
            return null;
        } catch (FeudalismException e) {
            e.printStackTrace();
            throw new ReadException(e);
        }
    }
}