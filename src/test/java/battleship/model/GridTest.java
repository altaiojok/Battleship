package battleship.model;

import junit.framework.TestCase;
import java.util.EnumSet;
import java.util.Iterator;

public class GridTest extends TestCase {

    public void testBattleshipSimplePlaceAndStrike() throws Exception {
        final Grid grid = new Grid();

        // place a ship at the origin
        final Coordinate origin = new Coordinate(0, 0);
        grid.place(Ship.PATROL, origin);
        assertTrue(grid.areAnyShipsAfloat());
        assertEquals(EnumSet.noneOf(Ship.class), grid.getSunkenShips());

        // miss the ship
        final Coordinate xBoundary = new Coordinate(2, 0);
        assertFalse(grid.strike(xBoundary));
        assertEquals(CellStatus.MISSED, grid.getCell(xBoundary).getStatus());

        final Coordinate yBoundary = new Coordinate(0, 1);
        assertFalse(grid.strike(yBoundary));
        assertEquals(CellStatus.MISSED, grid.getCell(yBoundary).getStatus());

        assertTrue(grid.areAnyShipsAfloat());
        assertEquals(EnumSet.noneOf(Ship.class), grid.getSunkenShips());

        // hit the ship
        assertEquals(CellStatus.PLACED, grid.getCell(origin).getStatus());
        assertTrue(grid.strike(origin));
        assertEquals(CellStatus.HIT, grid.getCell(origin).getStatus());

        // finish it off
        assertEquals(2, Ship.PATROL.length);
        assertTrue(grid.strike(new Coordinate(origin.getX() + 1, 0)));
        assertFalse(grid.areAnyShipsAfloat());
        assertEquals(EnumSet.of(Ship.PATROL), grid.getSunkenShips());

        // re-hit the ship
        assertFalse(grid.strike(origin));
        assertEquals(CellStatus.HIT, grid.getCell(origin).getStatus());
    }

    public void testBattleshipPlacementWithOverlapAndRollback() throws Exception {
        final Grid grid = new Grid(5);
        grid.place(Ship.PATROL, new Coordinate(3, 0));       // 000SS

        try {
            grid.place(Ship.DESTROYER, new Coordinate(0, 0)); // DDDD0
            fail("Should not be allowed to overlap ships");
        } catch (OverlappingException e) {
            // expected. check for proper rollback:
            assertEquals(new Cell(), grid.getCell(new Coordinate(0, 0)));
            assertEquals(new Cell(), grid.getCell(new Coordinate(1, 0)));
            assertEquals(new Cell(), grid.getCell(new Coordinate(2, 0)));
            assertEquals(Ship.PATROL,    grid.getCell(new Coordinate(3, 0)).getShip());
            assertEquals(Ship.PATROL,    grid.getCell(new Coordinate(4, 0)).getShip());
        }
    }

    public void testBattleshipPlacementWithOriginOutOfBounds() throws Exception {
        final Grid grid = new Grid();
        try {
            grid.place(Ship.DESTROYER, new Coordinate(0, Grid.DEFAULT_SIZE + 1));
            fail();
        } catch (OffTheGridException e) {
            // expected
        }
    }

    public void testBattleshipPlacementHangingOutOfBounds() throws Exception {
        final Grid grid = new Grid();
        final Coordinate origin = new Coordinate(Grid.DEFAULT_SIZE - Ship.DESTROYER.length + 1, 0);
        try {
            grid.place(Ship.DESTROYER, origin);
            fail();
        } catch (OffTheGridException e) {
            // expected. make sure the cells on the board were rolled back:
            assertEquals(new Cell(), grid.getCell(origin));
        }
    }

    public void testStrikingOutOfBounds() throws Exception {
        final Grid grid = new Grid();
        try {
            grid.strike(new Coordinate(0, Grid.DEFAULT_SIZE + 1));
            fail();
        } catch (OffTheGridException e) {
            // expected
        }
    }

    public void testGridIterator() throws Exception {
        final Grid grid = new Grid();
        final Ship ship = Ship.SUBMARINE;
        grid.place(ship, new Coordinate(2,0));

        final Iterator<Cell> iterator = grid.iterator();

        assertNull(iterator.next().getShip());             // 0,0
        assertNull(iterator.next().getShip());             // 1,0
        for(int s = 0; s < ship.length; s++) {
            assertEquals(ship, iterator.next().getShip()); // 2~5,0
        }
        assertNull(iterator.next().getShip());             // 6,0

        try {
            iterator.remove();
            fail();
        } catch (UnsupportedOperationException e) {}
    }
}
