package battleship.controller;

import battleship.model.Grid;
import battleship.model.Ship;
import battleship.view.TestUserInterfaceSpy;
import junit.framework.TestCase;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;

public class GameTest extends TestCase {

    public void testPlayNonIterativeOutput() throws Exception {
        final Grid grid = new Grid() {
            @Override
            public boolean areAnyShipsAfloat() {
                return false;
            }
        };

        final TestUserInterfaceSpy uiSpy = new TestUserInterfaceSpy() {
            final Iterator expectedOutputs = Arrays.asList(Labels.get("Welcome"), Labels.get("YouWon")).iterator();

            @Override
            public void out(String output) {
                assertEquals(expectedOutputs.next(), output);
            }
        };

        new Game(grid, uiSpy).play();
    }

    public void testPlayToWinWithBruteForce() throws Exception {
        final int size = 10;
        final Grid grid = new Grid(size);

        // final arrays of one to pass to inner class
        final int[] x = new int[1];
        final int[] y = new int[1];

        final TestUserInterfaceSpy uiSpy = new TestUserInterfaceSpy() {
            @Override
            public String in() {
                while (x[0] < size) {
                    while (y[0] < size) {
                        assertEquals(Labels.get("EnterCoordinates"), getLastOutput());
                        return x[0] + "," + y[0]++;
                    }
                    x[0]++;
                    y[0] = 0;
                }
                throw new IllegalStateException();
            }
        };

        new Game(grid, uiSpy).play();

        assertEquals(Labels.get("YouWon"), uiSpy.getLastOutput());
    }

    public void testShipSinkingMessage() throws Exception {
        final TestUserInterfaceSpy uiSpy = new TestUserInterfaceSpy();
        uiSpy.setNextInput("0,0");

        final Ship expectedSunkenShip = Ship.PATROL;
        final Grid grid = new Grid() {
            @Override
            public Set<Ship> getSunkenShips() {
                return EnumSet.of(expectedSunkenShip);
            }
        };

        new Game(grid, uiSpy).playOneRound();
        assertEquals(Labels.get("SunkShip", Labels.get(expectedSunkenShip.getLabelKey())), uiSpy.getLastOutput());
    }

    public void testStrikingOutOfBoundsErrorMessage() throws Exception {
        final TestUserInterfaceSpy uiSpy = new TestUserInterfaceSpy();
        uiSpy.setNextInput(Grid.DEFAULT_SIZE + 1 + "," + "0");
        new Game(new Grid(), uiSpy).playOneRound();
        assertEquals(Labels.get("OutOfBoundsError"), uiSpy.getLastOutput());
    }

    public void testErrorInvalidNumberOfCoordinates() throws Exception {
        final TestUserInterfaceSpy uiSpy = new TestUserInterfaceSpy();
        uiSpy.setNextInput("0,0,0");
        new Game(new Grid(), uiSpy).promptForCoordinate();
        assertEquals(Labels.get("InvalidNumberOfCoordinates"), uiSpy.getLastOutput());
    }

    public void testErrorInvalidCoordinates() throws Exception {
        final TestUserInterfaceSpy uiSpy = new TestUserInterfaceSpy();
        uiSpy.setNextInput("A,0");
        new Game(new Grid(), uiSpy).promptForCoordinate();
        assertEquals(Labels.get("InvalidCoordinates"), uiSpy.getLastOutput());
    }
}
