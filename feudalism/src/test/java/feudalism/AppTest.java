package feudalism;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import feudalism.object.Realm;

public class AppTest 
{
    @Test
    public void overlordSubjectLinkTest()
    {
        Realm r1 = new Realm();
        Realm r2 = new Realm();
        r2.setOverlord(r1);
        assertEquals(true, r2.getOverlord() == r1);
        assertEquals(true, r1.getSubjects().get(0) == r2);
    }

    @Test
    public void overlordSubjectDelinkTest() {
        Realm r1 = new Realm();
        Realm r2 = new Realm();
        r2.setOverlord(r1);
        r2.removeOverlord();
        assertEquals(false, r2.hasOverlord());
        assertEquals(false, r1.getSubjects().size() > 0);
    }
}
