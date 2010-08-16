package battleship.model;

/**
 * A transformation to apply to type T
 *
 * @param <T> type to transform
 */
interface Transformation<T> {

    /**
     * Applies a transformation to t and returns the result.
     *
     * @param t virgin t
     * @return transformed t
     */
    T transform(T t);
}
