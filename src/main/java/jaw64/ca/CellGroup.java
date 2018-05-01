package jaw64.ca;

import java.util.Arrays;

/**
 * Class representing an individual group of cells with n dimensions
 * (n >= 1).
 * @author jaw64
 */
public class CellGroup {

    /**
     * The dimensions of this cell group.
     */
    protected final CellGroupDimensions dimensions;

    /**
     * The individual cells themselves.
     */
    protected final int[] cells;

    /**
     * (constructor) Creates a new cell group with the specified dimensions.
     * @param dims the sizes of each of the dimensions
     */
    public CellGroup(int... dims) {
        this(new CellGroupDimensions(dims));
    }

    /**
     * (constructor) Creates a new cell group with the specified dimensions.
     * @param cd the cell group dimensions
     */
    public CellGroup(CellGroupDimensions cd) {
        this.dimensions = cd;
        this.cells = new int[cd.getNumElements()];
    }

    /**
     * Converts an arbitrary cell index to the index used by the 1D array
     * representation.
     * @param index the index of the cell
     * @return the 1D converted index
     */
    private int convertTo1DIndex(int... index) {
        switch (index.length) {
        case 1: {
            return index[0];
        }
        case 2: {
            return index[1] + index[0] * dimensions.getDimensionSize(1);
        }
        default: {
            int ret = 0;
            int numDims = dimensions.getNumDimensions();
            for (int i = 0; i < numDims; i++) {
                int subsize = 1;
                for (int j = i + 1; j < numDims; j++) {
                    subsize *= dimensions.getDimensionSize(j);
                }
                ret += subsize * index[i];
            }
            return ret;
        }
        }
    }

    /**
     * Gets the dimensions of this cell group.
     * @return the dimensions of this cell group
     */
    public CellGroupDimensions getDimensions() {
        return dimensions;
    }

    /**
     * Gets the number of dimensions of this cell group.
     * @return the number of dimensions of the cell group
     */
    public int getNumDimensions() {
        return dimensions.getNumDimensions();
    }

    /**
     * Gets the number of elements in this cell group.
     * @return the number of elements in the group
     */
    public int getNumElements() {
        return dimensions.getNumElements();
    }

    /**
     * Gets the value of the cell at the specified index.
     * @param index the index of the cell
     * @return the value of that cell
     */
    public int getValue(int... index) {
        int trueIndex = convertTo1DIndex(index);
        return cells[trueIndex];
    }

    /**
     * Sets the value of the cell at the specified index.
     * @param value the value to set
     * @param index the index of the cell to set
     */
    public void setValue(int value, int... index) {
        int trueIndex = convertTo1DIndex(index);
        cells[trueIndex] = value;
    }

    /**
     * Fills the entire cell group with the specified value.
     * @param value the value to set each cell to
     */
    public void fill(int value) {
        Arrays.fill(cells, value);
    }
}
