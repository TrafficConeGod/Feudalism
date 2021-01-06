package feudalism;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import feudalism.object.Realm;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void overlordSubjectLinkTest()
    {
        Realm r1 = new Realm();
        Realm r2 = new Realm();
        r2.setOverlord(r1);
        assertEquals(true, r2.getOverlord() == r1);
        assertEquals(true, r1.getSubjects().get(0) == r2);
    }
}
