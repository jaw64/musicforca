package jaw64.ca;

/**
 * Dimensions class used by cellular automata to define the dimensions for any
 * individual cell group.
 * @author jaw64
 */
public final class CellGroupDimensions {

    /**
     * Represents the sizes of each dimension.
     */
    private int[] dimensions;

    /**
     * The number of elements allowed in a cell group of the specified dimensions.
     */
    private int numElements;

    /**
     * (constructor) Creates a dimensions object for a cell group.
     * @param dims the sizes of each dimension
     */
    public CellGroupDimensions(int... dims) {
        this.dimensions = dims;
        this.numElements = 0;
        this.setup();
    }

    /**
     * Does post-initialization pre-processing / error checking.
     */
    private void setup() {
        numElements = 1;
        for (int i = 0; i < dimensions.length; i++) {
            int size = dimensions[i];
            if (size < 1) {
                throw new IllegalArgumentException(String.format(
                        "Cell group " + "size for dimension \"%d\" is invalid. Size given: %d", i,
                        size));
            }
            numElements *= size;
        }
    }

    /**
     * Gets the maximum number of elements allowed in a cell specified by these
     * dimensions.
     * @return the number of elements allowed for a cell with these dimensions
     */
    public int getNumElements() {
        return numElements;
    }

    /**
     * Gets the number of dimensions used for a particular cellular automata.
     * @return the number of dimensions
     */
    public int getNumDimensions() {
        return dimensions.length;
    }

    /**
     * Gets the size of the specified dimension.
     * @param dim the zero-indexed dimension
     * @return the size of the specified dimension
     */
    public int getDimensionSize(int dim) {
        return dimensions[dim];
    }
}
