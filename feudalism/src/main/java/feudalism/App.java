package feudalism;

import java.util.UUID;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
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
