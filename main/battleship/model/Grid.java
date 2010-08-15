package battleship.model;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 * A standard battleship game grid of specified size. If no size is provided,
 * grid will default to DEFAULT_SIZE. Each cell on the Grid is populated
 * with a Cell instance, which maintains the state of ship placement and and hits.
 */
public class Grid {
    public static final int DEFAULT_SIZE = 10;

    private final int size;
    private final Cell[][] cells;
    private final Set<Ship> sunkenShips;

    /**
     * Create a Grid of default size.
     */
    public Grid() {
        this(DEFAULT_SIZE);
    }

    /**
     * Create a grid of a specified size.
     *
     * @param size
     */
    public Grid(final int size) {
        for(Ship ship : Ship.values()) {
            if(ship.length > size) {
                throw new IllegalArgumentException("Grid is not large enough to accommodate ships.");
            }
        }

        this.size = size;
        this.cells = new Cell[size][size];
        this.sunkenShips = EnumSet.noneOf(Ship.class);
        reset();
    }

    /**
     * Reset the grid with all new cells.
     */
    void reset() {
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                cells[x][y] = new Cell();
            }
        }

        sunkenShips.clear();
    }

    /**
     * Place all the ships randomly on this grid
     */
    public void placeAllShipsRandomly() {
        final int MAX_RETRIES = 10;
        int retries = 0;

        for (final Ship ship : Ship.values()) {
            boolean successfullyPlaced = false;
            while (!successfullyPlaced) {
                try {
                    final int x = (int) (Math.random() * size);
                    final int y = (int) (Math.random() * size);
                    place(ship, new Coordinate(x, y));
                    successfullyPlaced = true;
                } catch (OverlappingException e) {
                    // we're overlapping another ship. let's try again.
                    if (retries++ > MAX_RETRIES) {
                        reset();
                    }
                } catch (OffTheGridException e) {
                    // we're off the grid. let's try again.
                    if (retries++ > MAX_RETRIES) {
                        reset();
                    }
                }
            }
        }
    }

    /**
     * Place a ship starting at an origin and going in an horizontal orientation on this grid.
     *
     * @param ship   Type of ship to place
     * @param origin Left-most coordinate of this ship
     * @throws OverlappingException Placement of this ship would overlap with another ship. Automatically rolls back partial placement.
     * @throws OffTheGridException  Placement of this ship goes off this grid. Automatically rolls back partial placement.
     */
    public void place(final Ship ship, final Coordinate origin) throws OverlappingException, OffTheGridException {
        int x = origin.getX();
        int y = origin.getY();

        final Set<Coordinate> coordsPlaced = new HashSet<Coordinate>();
        //noinspection ForLoopThatDoesntUseLoopVariable
        for (int xMax = x + ship.length; x < xMax; x++) {
            final Cell cell;
            try {
                cell = cells[x][y];
            } catch (ArrayIndexOutOfBoundsException e) {
                rollback(coordsPlaced);
                throw new OffTheGridException();
            }

            if (cell.getStatus() != Cell.CellStatus.EMPTY) {
                rollback(coordsPlaced);
                throw new OverlappingException();
            }

            cell.setShip(ship);
            cell.setStatus(Cell.CellStatus.PLACED);
            coordsPlaced.add(new Coordinate(x, y));
        }
    }

    private void rollback(Set<Coordinate> coordsPlaced) {
        for (Coordinate coordsToRollback : coordsPlaced) {
            cells[coordsToRollback.getX()][coordsToRollback.getY()] = new Cell();
        }
    }

    /**
     * Attempt to strike a ship at the given coordinate.
     *
     * @param coord strike coordinate
     * @return true if hit is successful
     * @throws OffTheGridException Struck a coordinate not on this Grid
     */
    public boolean strike(final Coordinate coord) throws OffTheGridException {
        final Cell cell;

        try {
            cell = cells[coord.getX()][coord.getY()];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new OffTheGridException();
        }

        final boolean isNewHit = cell.getStatus() == Cell.CellStatus.PLACED;
        cell.setStatus(isNewHit || cell.getStatus() == Cell.CellStatus.HIT ? Cell.CellStatus.HIT : Cell.CellStatus.MISSED);

        if (isNewHit && !isShipAfloat(cell.getShip())) {
            sunkenShips.add(cell.getShip());
        }

        return isNewHit;
    }

    /**
     * Iterates over this grid to determine if the specified ship is still alive.
     *
     * @return true if any unsunk ships exist
     */
    private boolean isShipAfloat(final Ship ship) {
        return matchingCellExists(new CellMatcher() {
            @Override
            public boolean match(Cell cell) {
                return cell.getShip() == ship && cell.getStatus() == Cell.CellStatus.PLACED;

            }
        });
    }

    /**
     * Iterates over this grid to determine if any ships are still afloat.
     *
     * @return true if any unsunk ships exist
     */
    public boolean areAnyShipsAfloat() {
        return matchingCellExists(new CellMatcher() {
            @Override
            public boolean match(final Cell cell) {
                return cell.getStatus() == Cell.CellStatus.PLACED;

            }
        });
    }

    /**
     * Returns the cell at the given coordinate.
     * @param coord
     * @return
     */
    Cell getCell(Coordinate coord) {
        return cells[coord.getX()][coord.getY()];
    }

    static interface CellMatcher {
        boolean match(final Cell cell);
    }

    private boolean matchingCellExists(CellMatcher matcher) {
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if (matcher.match(cells[x][y])) {
                    return true;
                }
            }
        }
        return false;
    }

    public String display(boolean mask) {
        final StringBuilder sb = new StringBuilder();

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                final Cell.CellStatus cellStatus = cells[y][x].getStatus(); //flipped xy for display purposes
                sb.append(cellStatus.display(mask));
//                final Ship ship = cells[y][x].getShip();
//                sb.append(ship != null ? ship.name().substring(0,1) : "#");
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * Returns the status of each cell on the grid.
     */
    @Override
    public String toString() {
        return display(false);
    }

    /**
     * @return a set of sunken ships on this grid.
     */
    public Set<Ship> getSunkenShips() {
        assert sunkenShips != null;
        return EnumSet.copyOf(sunkenShips);
    }

    /**
     * Maintains the status and ship information of a cell on a grid.
     */
    static class Cell {

        private CellStatus status;
        private Ship ship;

        /**
         * Creates a new empty cell.
         */
        Cell() {
            status = CellStatus.EMPTY;
        }

        CellStatus getStatus() {
            return status;
        }

        void setStatus(final CellStatus status) {
            this.status = status;
        }

        Ship getShip() {
            return ship;
        }

        void setShip(final Ship ship) {
            this.ship = ship;
        }

        @Override
        public String toString() {
            return "Cell{" +
                    "status=" + status +
                    ", ship=" + ship +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Cell cell = (Cell) o;

            return ship == cell.ship && status == cell.status;
        }

        @Override
        public int hashCode() {
            int result = status.hashCode();
            result = 31 * result + (ship != null ? ship.hashCode() : 0);
            return result;
        }

        /**
         * The status of a given cell.
         */
        static enum CellStatus {
            /**
             * Virgin cell with no placement or attacks.
             */
            EMPTY('O', false),

            /**
             * A ship is placed on this cell, but not yet attacked.
             */
            PLACED('#', true),

            /**
             * This cell has been attacked. There may or may not have been a ship here.
             */
            HIT('+', false),

            /**
             * This cell has been attacked. There may or may not have been a ship here.
             */
            MISSED('X', false);

            private static final char MASK = EMPTY.display;
            private final char display;
            private final boolean maskable;

            private CellStatus(char display, boolean maskable) {
                this.display = display;
                this.maskable = maskable;
            }

            char display(boolean mask) {
                return mask && maskable ? MASK : display;
            }

        }
    }
}
