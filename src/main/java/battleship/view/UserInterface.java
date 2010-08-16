package battleship.view;

/**
 * Interface with the user to control textual input and output.
 */
public interface UserInterface {

    /**
     * Read a string from the user.
     * @return Most recent line of user input.
     */
    String in();

    /**
     * Display a string to the user.
     * @param s
     */
    void out(String s);
}
