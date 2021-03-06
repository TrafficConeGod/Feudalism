package feudalism.object;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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

    public Siege(Realm attacker, Realm defender, SiegeGoal goal) throws FeudalismException {
        if (attacker.hasDescendantSubject(defender) || defender.hasDescendantSubject(attacker)) {
            throw new FeudalismException(String.format("%s and %s are subjects of each other", attacker.getName(), defender.getName()));
        }
        if (!attacker.isWithinBorderRadius(defender)) {
            throw new FeudalismException(String.format("%s is not within border radius distance of %s", attacker.getName(), defender.getName()));
        }
        this.attacker = attacker;
        this.defender = defender;
        this.goal = goal;
        Registry.getInstance().addSiege(this);
    }

    public void destroy() {
        Registry.getInstance().removeSiege(this);
    }

    public Realm getAttacker() {
        return attacker;
    }

    public Realm getDefender() {
        return defender;
    }

    public SiegeGoal getGoal() {
        return goal;
    }

    private List<Realm> getDescendantCombatants(List<Realm> topCombatants) {
        List<Realm> combatants = new ArrayList<>();
        for (Realm combatant : topCombatants) {
            combatants.add(combatant);
            combatants.addAll(combatant.getDescendantSubjects());
        }
        // removes all duplicates from list
        return combatants.stream().distinct().collect(Collectors.toList());
    }

    public List<Realm> getAttackers() {
        List<Realm> topAttackers = new ArrayList<>();
        if (attacker.hasOwner()) {
            try {
                topAttackers.addAll(attacker.getOwner().getOwnedRealms());
            } catch (FeudalismException e) {}
        } else {
            topAttackers.add(attacker);
        }
        topAttackers.addAll(attackerAllies);
        return getDescendantCombatants(topAttackers);
    }

    public List<Realm> getDefenders() {
        List<Realm> topDefenders = new ArrayList<>();
        if (defender.hasOwner()) {
            try {
                topDefenders.addAll(defender.getOwner().getOwnedRealms());
            } catch (FeudalismException e) {}
        } else {
            topDefenders.add(defender);
        }
        topDefenders.addAll(defenderAllies);
        return getDescendantCombatants(topDefenders);
    }

    public List<Realm> getCombatants() {
        List<Realm> attackers = getAttackers();
        List<Realm> defenders = getDefenders();
        attackers.addAll(defenders);
        return attackers;
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
            // if (isSubject(getDefenders(), ally)) {
            //     throw new FeudalismException(String.format("%s is a subject of the opposite side", ally));
            // }
            attackerAllies.add(ally);
        } else {
            // if (isSubject(getAttackers(), ally)) {
            //     throw new FeudalismException(String.format("%s is a subject of the opposite side", ally));
            // }
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

    public String getInfo() {
        return String.format("Goal: %s\nAttacker: %s\nDefender: %s\nAttacker Allies: %s\nDefender Allies: %s",
            getGoal().getDisplayName(),
            getAttacker().getName(),
            getDefender().getName(),
            attackerAllies.size(),
            defenderAllies.size()
        );
    }

    @Override
    public String toString() {
        return getInfo();
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
        list.add(goal.getName());
        return printer.print(list);
    }

    @Override
    public Object read(ObjectReader<?> reader, Object object) throws ReadException {
        List<Object> list = (ArrayList<Object>) reader.read(object);
        try {
            Realm attacker = Registry.getInstance().getRealmByUuid(UUID.fromString((String) list.get(0)));
            Realm defender = Registry.getInstance().getRealmByUuid(UUID.fromString((String) list.get(1)));
            SiegeGoal goal = Registry.getInstance().getSiegeGoal((String) list.get(4));
            Siege siege = new Siege(attacker, defender, goal);
            List<String> attackerAllies = (ArrayList<String>) list.get(2);
            for (String ally : attackerAllies) {
                UUID uuid = UUID.fromString(ally);
                Realm realm = Registry.getInstance().getRealmByUuid(uuid);
                siege.addAlly(attacker, realm);
            }
            List<String> defenderAllies = (ArrayList<String>) list.get(3);
            for (String ally : defenderAllies) {
                UUID uuid = UUID.fromString(ally);
                Realm realm = Registry.getInstance().getRealmByUuid(uuid);
                siege.addAlly(defender, realm);
            }
            return siege;
        } catch (FeudalismException e) {
            e.printStackTrace();
            throw new ReadException(e);
        }
    }
}