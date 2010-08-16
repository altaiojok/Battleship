package battleship.model;

/**
 * Maintains the status and ship information of a cell on a grid.
 */
public final class Cell {

    private CellStatus status;
    private Ship ship;

    /**
     * Creates a new empty cell.
     */
    public Cell() {
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
}

