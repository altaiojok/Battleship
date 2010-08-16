package battleship.model;

import junit.framework.TestCase;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class CellIteratorTest extends TestCase {

    public void testNextThrowsNoSuchElementException() throws Exception {
        final Grid grid = new Grid();
        final Iterator<Cell> iterator = grid.iterator();

        while (iterator.hasNext()) {
            iterator.next();
        }

        try {
            iterator.next();
            fail();
        } catch (NoSuchElementException e) {
            // expected.
        }
    }

    public void testNext() throws Exception {
        final Grid grid = new Grid();
        final Ship ship = Ship.SUBMARINE;
        grid.place(ship, new Coordinate(2, 0));

        final Iterator<Cell> iterator = grid.iterator();

        GridTest.assertNull(iterator.next().getShip());             // 0,0
        GridTest.assertNull(iterator.next().getShip());             // 1,0
        for (int s = 0; s < ship.length; s++) {
            GridTest.assertEquals(ship, iterator.next().getShip()); // 2~5,0
        }
        GridTest.assertNull(iterator.next().getShip());             // 6,0
    }

    public void testRemove() throws Exception {
        final Grid grid = new Grid();
        final Iterator<Cell> iterator = grid.iterator();

        try {
            iterator.remove();
            fail();
        } catch (UnsupportedOperationException e) {
            // expected.
        }
    }
}