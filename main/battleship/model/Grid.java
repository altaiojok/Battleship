package battleship.model;

import java.util.*;

/**
 * A standard battleship game grid of specified size. If no size is provided,
 * grid will default to DEFAULT_SIZE. Each cell on the Grid is populated
 * with a Cell instance, which maintains the state of ship placement and and hits.
 */
public class Grid implements Iterable<Cell> {
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
     * Create a square grid of a specified size.
     *
     * @param size of each side
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
        final CellIterator iterator = iterator();
        while(iterator.hasNext()) {
            iterator.next();
            iterator.apply(new Transformation<Cell>() {
                @Override
                public Cell transform(Cell cell) {
                    return new Cell();
                }
            });
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

            if (cell.getStatus() != battleship.model.CellStatus.EMPTY) {
                rollback(coordsPlaced);
                throw new OverlappingException();
            }

            cell.setShip(ship);
            cell.setStatus(battleship.model.CellStatus.PLACED);
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

        final boolean isNewHit = cell.getStatus() == battleship.model.CellStatus.PLACED;
        cell.setStatus(isNewHit || cell.getStatus() == battleship.model.CellStatus.HIT ?
                       battleship.model.CellStatus.HIT : battleship.model.CellStatus.MISSED);

        if (isNewHit && !isShipAfloat(cell.getShip())) {
            sunkenShips.add(cell.getShip());
        }

        return isNewHit;
    }

    /**
     * Iterates over this grid to determine if the specified ship is still alive.
     *
     * @param ship to find on grid
     * @return true if any unsunk ships exist
     */
    private boolean isShipAfloat(final Ship ship) {
        return matchingCellExists(new Matcher<Cell>() {
            @Override
            public boolean match(Cell cell) {
                return cell.getShip() == ship && cell.getStatus() == battleship.model.CellStatus.PLACED;

            }
        });
    }

    /**
     * Iterates over this grid to determine if any ships are still afloat.
     *
     * @return true if any unsunk ships exist
     */
    public boolean areAnyShipsAfloat() {
        return matchingCellExists(new Matcher<Cell>() {
            @Override
            public boolean match(final Cell cell) {
                return cell.getStatus() == battleship.model.CellStatus.PLACED;

            }
        });
    }

    /**
     * Returns the cell at the given coordinate.
     */
    Cell getCell(Coordinate coord) {
        return cells[coord.getX()][coord.getY()];
    }

    private boolean matchingCellExists(Matcher<Cell> matcher) {
        for (Cell c : this) {
            if (matcher.match(c)) {
                return true;
            }
        }
        return false;
    }

    public String display(boolean mask) {
        final StringBuilder sb = new StringBuilder();

        int colNum = 0;
        for (Cell c : this) {
            sb.append(c.getStatus().display(mask));
            if(colNum++ == this.size) {
                sb.append("\n");
                colNum = 0;
            }
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

    @Override
    public CellIterator iterator() {
        return new CellIterator();
    }

    /**
     * Iterator to cycle over the cells on this grid. Starts at (0,0),
     * moves across first row, then to subsequent rows, until completing all cells.
     * e.g. (1,0), (2,0), (...,0), (1,1), (2,1), ..., (size - 1, size - 1)
     */
    private class CellIterator implements Iterator<Cell> {
        private int x = -1;
        private int y = -1;

        @Override
        public boolean hasNext() {
            return (x + 1 < size) || (y + 1 < size);
        }

        @Override
        public Cell next() {
            if(!hasNext()) {
                throw new NoSuchElementException();
            }
            
            x = (x + 1 < size) ? x + 1 : 0;
            y = (y + 1 < size) && (x == 0) ? y + 1 : y;

            return cells[x][y];
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        /**
         * Apply the given transformation to the current cell on the grid
         * @param transformation
         */
        private void apply(Transformation<Cell> transformation) {
            cells[x][y] = transformation.transform(cells[x][y]);
        }
    }
}
