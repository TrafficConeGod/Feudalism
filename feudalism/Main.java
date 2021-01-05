package feudalism;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        Realm realm = new Realm();
        realm.setOwner(UUID.randomUUID());
        Realm r2 = new Realm();
        r2.setOverlord(realm);
        r2.removeOverlord();
        // realm.removeSubject(r2);
        System.out.println(realm.getSubjects());
        System.out.println(r2.hasOverlord());
        System.out.println(r2.getOverlord());
    }
}