package feudalism;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Realm {
    private boolean hasOwner = false;
    private UUID owner;

    private boolean hasOverlord = false;
    private Realm overlord;

    private List<Realm> subjects = new ArrayList<>();

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
}
