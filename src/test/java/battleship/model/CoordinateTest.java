package battleship.model;

import junit.framework.TestCase;

public class CoordinateTest extends TestCase {

    public void testEquals() throws Exception {
        final Coordinate coordinate = new Coordinate(0, 0);
        assertTrue(coordinate.equals(coordinate));
        assertTrue(new Coordinate(0,0).equals(new Coordinate(0,0)));
        assertFalse(new Coordinate(1,0).equals(new Coordinate(0,0)));
        assertFalse(new Coordinate(0,1).equals(new Coordinate(0,0)));
        assertFalse(new Coordinate(0,0).equals(null));
        assertFalse(new Coordinate(0,0).equals(new Object()));
    }

    public void testToString() throws Exception {
        assertEquals("(1,2)", new Coordinate(1,2).toString());
    }

}
