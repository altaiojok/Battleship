package battleship.model;

public enum Ship {

    PATROL("Ship_Patrol", 2), SUBMARINE("Ship_Submarine", 3), DESTROYER("Ship_Destroyer", 4);

    final int length;
    private String labelKey;

    Ship(String labelKey, final int length) {
        this.labelKey = labelKey;
        this.length = length;
    }

    public String getLabelKey() {
        return labelKey;
    }

}
