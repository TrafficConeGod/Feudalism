package feudalism.object;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.Location;

import ca.uqac.lif.azrael.ObjectPrinter;
import ca.uqac.lif.azrael.ObjectReader;
import ca.uqac.lif.azrael.PrintException;
import ca.uqac.lif.azrael.Printable;
import ca.uqac.lif.azrael.Readable;
import feudalism.Config;
import feudalism.FeudalismException;
import feudalism.Registry;
import ca.uqac.lif.azrael.ReadException;

public class Realm implements Printable, Readable {
    private UUID uuid;

    private boolean hasOwner = false;
    private UUID owner;

    private boolean hasOverlord = false;
    private Realm overlord;

    private List<Realm> subjects = new ArrayList<>();

    private List<UUID> members = new ArrayList<>();

    private String name = "RealmName";

    private List<GridCoord> claims = new ArrayList<>();

    public Realm() {
        uuid = UUID.randomUUID();
        Registry.getInstance().addRealm(this);
        Registry.getInstance().addTopRealm(this);
    }

    public Realm(UUID uuid) {
        this.uuid = uuid;
        Registry.getInstance().addRealm(this);
        Registry.getInstance().addTopRealm(this);
    }

    public Realm(UUID owner, String name) {
        uuid = UUID.randomUUID();
        setOwner(owner);
        setName(name);
        Registry.getInstance().addRealm(this);
        Registry.getInstance().addTopRealm(this);
    }

    public Realm(UUID owner, String name, Realm overlord) {
        uuid = UUID.randomUUID();
        setOwner(owner);
        setName(name);
        setOverlord(overlord);
        Registry.getInstance().addRealm(this);
    }

    public UUID getUuid() {
        return uuid;
    }

    @Override
    public String toString() {
        return getName();
    }

    public boolean hasOwner() {
        return hasOwner;
    }

    public void removeOwner() {
        hasOwner = false;
    }

    public UUID getOwner() {
        return owner;
    }

    public Player getOwnerPlayer() {
        return Bukkit.getPlayer(getOwner());
    }

    public void setOwner(UUID owner) {
        hasOwner = true;
        this.owner = owner;
    }

    public boolean hasOverlord() {
        return hasOverlord;
    }

    public void removeOverlord() {
        boolean hadOverlord = hasOverlord();
        hasOverlord = false;
        // if the subject had an overlord then remove the subject from the overlords
        // subject list
        if (hadOverlord) {
            Realm overlord = getOverlord();
            if (overlord.hasSubject(this)) {
                overlord.removeSubject(this);
            }
        }
        Registry.getInstance().addTopRealm(this);
    }

    public Realm getOverlord() {
        return overlord;
    }

    public void setOverlord(Realm overlord) {
        if (getDescendantSubjects().contains(overlord)) {
            System.out.println("Can not set overlord if already overlord");
            return;
        }
        boolean hadOverlord = hasOverlord();
        Realm oldOverlord = getOverlord();
        hasOverlord = true;
        this.overlord = overlord;
        // if the subject had an overlord then remove the subject from the overlords
        // subject list
        if (hadOverlord) {
            oldOverlord.removeSubject(this);
        }
        // if the subject has an overlord now then add the subject to the overlords
        // subject list
        if (!overlord.hasSubject(this)) {
            overlord.addSubject(this);
        }
        Registry.getInstance().removeTopRealm(this);
    }

    public List<Realm> getSubjects() {
        return subjects;
    }

    public void addSubject(Realm subject) {
        if (getDescendantSubjects().contains(subject) || subject.getDescendantSubjects().contains(this)) {
            System.out.println("Can not add subject if it already is a descendnat subject or is a subject of this already");
            return;
        }
        subjects.add(subject);
        // if the subject doesn't have this overlord then set it's overlord to this
        if (!subject.hasOverlord() || subject.getOverlord() != this) {
            subject.setOverlord(this);
        }
    }

    public boolean hasSubject(Realm subject) {
        return subjects.contains(subject);
    }

    public boolean hasDescendantSubject(Realm subject) {
        return getDescendantSubjects().contains(subject);
    }

    public void removeSubject(Realm subject) {
        if (!hasSubject(subject)) {
            System.out.println("Can not remove subject that is not a subject of this");
            return;
        }
        subjects.remove(subject);
        // if the subject does have an overlord then remove it
        if (subject.hasOverlord() && subject.getOverlord() == this) {
            subject.removeOverlord();
        }
    }

    public void addMember(UUID member) {
        members.add(uuid);
    }

    public boolean hasMember(UUID member) {
        return members.contains(member);
    }

    public void removeMember(UUID member) {
        members.remove(member);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Realm> getDescendantSubjects() {
        List<Realm> descendants = new ArrayList<>();
        descendants.addAll(getSubjects());
        for (Realm desc : descendants) {
            descendants.addAll(desc.getDescendantSubjects());
        }
        return descendants;
    }

    public void addClaimFromGridPosition(int x, int z) {
        GridCoord coord = GridCoord.getFromGridPosition(x, z);
        if (!hasClaim(coord)) {
            addClaim(coord);
        }
    }

    public void removeClaimFromGridPosition(int x, int z) {
        GridCoord coord = GridCoord.getFromGridPosition(x, z);
        removeClaim(coord);
    }

    public void addClaimFromWorldPosition(int x, int z) {
        GridCoord coord = GridCoord.getFromWorldPosition(x, z);
        if (!hasClaim(coord)) {
            addClaim(coord);
        }
    }

    public void removeClaimFromWorldPosition(int x, int z) {
        GridCoord coord = GridCoord.getFromWorldPosition(x, z);
        removeClaim(coord);
    }

    public List<GridCoord> getClaims() {
        return claims;
    }

    public void addClaim(GridCoord coord) {
        claims.add(coord);
        if (!coord.hasOwner() || coord.getOwner() != this) {
            coord.setOwner(this);
        }
    }

    public void removeClaim(GridCoord coord) {
        claims.remove(coord);
        if (claims.size() == 0) {
            destroy();
        }
    }

    public boolean hasClaim(GridCoord coord) {
        return claims.contains(coord);
    }

    public void destroy() {
        for (GridCoord coord : getClaims()) {
            coord.destroy();
        }
        Registry.getInstance().removeTopRealm(this);
        Registry.getInstance().removeRealm(this);
    }

    public void destroyTree() {
        for (Realm subject : getSubjects()) {
            subject.destroyTree();
        }
        subjects = new ArrayList<>();
        destroy();
    }

    public String getProps() {
        return String.format("UUID: %s\nName: %s\nOwner: %s\nOverlord: %s\nSubjects: %s\nMembers: %s\nClaims: %s",
            getUuid().toString(),
            getName(),
            hasOwner() ? getOwnerPlayer().getName() : "None",
            hasOverlord() ? getOverlord().getName() : "None",
            getSubjects().size(),
            members.size(),
            claims.size()
        );
    }

    public Object print(ObjectPrinter<?> printer) throws PrintException {
        List<Object> list = new ArrayList<>();
        list.add(uuid.toString());
        list.add(subjects);
        List<String> members = new ArrayList<>();
        for (UUID member : this.members) {
            members.add(member.toString());
        }
        list.add(members);
        list.add(name);
        list.add(claims);
        list.add(hasOwner());
        if (hasOwner()) {
            list.add(getOwner().toString());
        }
        return printer.print(list);
    }

    public Object read(ObjectReader<?> reader, Object object) throws ReadException {
        List<Object> list = (ArrayList<Object>) reader.read(object);
        Realm realm = new Realm(UUID.fromString((String)list.get(0)));
        realm.setName((String)list.get(3));
        List<Realm> subjects = (ArrayList<Realm>) list.get(1);
        for (Realm subject : subjects) {
            realm.addSubject(subject);
        }
        List<String> members = (ArrayList<String>) list.get(2);
        for (String member : members) {
            realm.addMember(UUID.fromString(member));
        }
        List<GridCoord> claims = (ArrayList<GridCoord>) list.get(4);
        for (GridCoord coord : claims) {
            realm.addClaim(coord);
        }
        if ((boolean) list.get(5)) {
            realm.setOwner(UUID.fromString((String) list.get(6)));
        }
        return realm;
    }
}