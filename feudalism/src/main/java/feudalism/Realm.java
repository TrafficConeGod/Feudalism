package feudalism;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Realm {
    private UUID uuid;

    private boolean hasOwner = false;
    private UUID owner;

    private boolean hasOverlord = false;
    private Realm overlord;

    private List<Realm> subjects = new ArrayList<>();

    private List<UUID> members = new ArrayList<>();

    private String name = "";

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
        boolean hadOverlord = hasOverlord();
        Realm oldOverlord = getOverlord();
        hasOverlord = true;
        this.overlord = overlord;
        if (hadOverlord) {
            oldOverlord.removeSubject(this);
        }
        if (!overlord.hasSubject(this)) {
            overlord.addSubject(this);
        }
    }

    public List<Realm> getSubjects() {
        return subjects;
    }

    public void addSubject(Realm subject) {
        subjects.add(subject);
        if (!subject.hasOverlord() || subject.getOverlord() != this) {
            subject.setOverlord(this);
        }
    }

    public boolean hasSubject(Realm subject) {
        return subjects.contains(subject);
    }

    public void removeSubject(Realm subject) {
        subjects.remove(subject);
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
}