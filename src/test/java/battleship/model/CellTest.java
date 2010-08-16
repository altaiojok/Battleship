package battleship.model;

import junit.framework.TestCase;
import net.nelz.jectu.Jectu;


public class CellTest extends TestCase {

    public void testEquals() throws Exception {
        new Jectu(Cell.class)
            .addEffectiveField("status", CellStatus.EMPTY, CellStatus.HIT, CellStatus.EMPTY)
            .addEffectiveField("ship", Ship.DESTROYER, Ship.PATROL, Ship.DESTROYER)
            .execute();

    }

    public void testToString() throws Exception {
        assertEquals("Cell{status=EMPTY, ship=null}", new Cell().toString());
    }
}
