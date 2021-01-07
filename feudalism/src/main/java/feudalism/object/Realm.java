package feudalism.object;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ca.uqac.lif.azrael.ObjectPrinter;
import ca.uqac.lif.azrael.ObjectReader;
import ca.uqac.lif.azrael.PrintException;
import ca.uqac.lif.azrael.Printable;
import ca.uqac.lif.azrael.Readable;
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

    public Realm() {
        uuid = UUID.randomUUID();
    }

    public Realm(UUID uuid) {
        this.uuid = uuid;
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

    public Object print(ObjectPrinter<?> printer) throws PrintException {
        List<Object> list = new ArrayList<>();
        list.add(uuid.toString());
        list.add(subjects);
        list.add(members);
        list.add(name);
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
        return realm;
    }
}