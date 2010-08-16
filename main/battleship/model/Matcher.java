package battleship.model;

/**
 * Matching operations for type t
 *
 * @param <T> type on which to match
 */
public interface Matcher<T> {

    /**
     * Evaluates the matcher for argument t.
     *
     * @param t
     * @return true if the given t matches implemented criterion
     */
    boolean match(T t);
}
