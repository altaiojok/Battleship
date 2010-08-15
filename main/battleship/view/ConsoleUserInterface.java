package battleship.view;

import battleship.controller.Game;
import battleship.model.Grid;

import java.io.IOException;

/**
 * Implementation for standard Java console.
 */
class ConsoleUserInterface implements UserInterface {

    @Override
    public String in() {
        return System.console().readLine();
    }

    @Override
    public void out(final String s) {
        System.out.println(s);
    }

    /**
     * Main entry point for console-based game
     * @param args 0:size of grid
     */
    public static void main(final String[] args) {
        final ConsoleUserInterface ui = new ConsoleUserInterface();

        int gridSize = Grid.DEFAULT_SIZE;

        // user-input grid size
        if(args.length > 0 && args[0] != null) {
            try {
                gridSize = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                ui.out("Invalid argument");
                return;
            }
        }

        new Game(new Grid(gridSize), ui).play();
    }
}
