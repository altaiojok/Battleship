package battleship.model;

/**
 * Types of ship game pieces
 */
public enum Ship {

    PATROL("Ship_Patrol", 2), SUBMARINE("Ship_Submarine", 3), DESTROYER("Ship_Destroyer", 4);

    final int length;
    private final String labelKey;

    Ship(String labelKey, final int length) {
        this.labelKey = labelKey;
        this.length = length;
    }

    public String getLabelKey() {
        return labelKey;
    }

}
