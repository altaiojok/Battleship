package battleship.model;

/**
 * Status of a given cell
 */
enum CellStatus {
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