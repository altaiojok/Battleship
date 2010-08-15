package battleship.controller;

import battleship.model.Coordinate;
import battleship.model.Grid;
import battleship.model.OffTheGridException;
import battleship.model.Ship;
import battleship.view.UserInterface;

import java.util.EnumSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Main controller of a battleship game.
 */
public class Game {

    private static final Logger log = Logger.getLogger(Game.class.getName());

    private final Grid grid;
    private final UserInterface ui;
    private final Set<Ship> reportedSinkings;
    private int attempts;

    /**
     * Create a new game with a given grid and user interface
     *
     * @param grid
     * @param ui
     */
    public Game(final Grid grid, final UserInterface ui) {
        this.ui = ui;
        this.grid = grid;
        grid.placeAllShipsRandomly();
        reportedSinkings = EnumSet.noneOf(Ship.class);
    }

    /**
     * Start this game and continue until win. No way to lose.
     */
    public void play() {
        ui.out(Labels.get("Welcome"));

        while (grid.areAnyShipsAfloat()) {
            playOneRound();
        }

        ui.out(Labels.get("YouWon"));
    }

    /**
     * Play one round. Exposed for testing only.
     */
    void playOneRound() {
        log.fine("\n" + grid.toString());
        ui.out("\n" + grid.display(true));

        final Coordinate coord = getNextCoordinate();
        try {
            ui.out(grid.strike(new Coordinate(coord.getX(), coord.getY())) ? Labels.get("Hit") : Labels.get("Miss"));
            ui.out(Labels.get("Attempts", ++attempts));
        } catch (OffTheGridException e) {
            ui.out(Labels.get("OutOfBoundsError"));
        }

        final Set<Ship> sunkenShips = grid.getSunkenShips();
        if (!reportedSinkings.containsAll(sunkenShips)) {
            sunkenShips.removeAll(reportedSinkings);
            assert sunkenShips.size() == 1;
            final Ship newlySunkenShip = (Ship) sunkenShips.toArray()[0];
            ui.out(Labels.get("SunkShip", Labels.get(newlySunkenShip.getLabelKey())));
            reportedSinkings.addAll(sunkenShips);
        }
    }

    /**
     * Prompt the user for coordinates until received.
     *
     * @return Non-null, properly formatted coordinate from user.
     */
    private Coordinate getNextCoordinate() {
        Coordinate coord = null;
        while (coord == null) {
            coord = promptForCoordinate();
        }
        return coord;
    }

    /**
     * Prompt user for coordinate via the user interface.
     *
     * @return Properly constructed coordinate; else will return null.
     */
    Coordinate promptForCoordinate() {
        ui.out(Labels.get("EnterCoordinates"));
        final String[] rawCoors = ui.in().split(",");

        if (rawCoors.length != 2) {
            ui.out(Labels.get("InvalidNumberOfCoordinates"));
            return null;
        }

        try {
            return new Coordinate(Integer.valueOf(rawCoors[0]), Integer.valueOf(rawCoors[1]));
        } catch (NumberFormatException e) {
            ui.out(Labels.get("InvalidCoordinates"));
        }

        return null;
    }

}
