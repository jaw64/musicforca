package jaw64.ca;

/**
 * Wrapper for CellGroup class which does not allow for updating
 * of individual cells within the group. Create an immutable cell group
 * using {@link CellularAutomaton#immutableCopy(CellGroup)}.
 * @author jaw64
 */
class ImmutableCellGroup extends CellGroup {

    /**
     * (constructor) Creates a new immutable cell group based on an existing cell group.
     * @param dims the sizes of each of the dimensions
     */
    public ImmutableCellGroup(CellGroup cellGroup) {
        super(cellGroup.getDimensions());
        this.copyCellValues(cellGroup);
    }

    /**
     * Copies cell values from one cell group to another.
     * @param group the group to copy from
     */
    private void copyCellValues(CellGroup group) {
        for (int i = 0; i < group.getNumElements(); i++) {
            this.cells[i] = group.cells[i];
        }
    }

    /*
     * (non-Javadoc)
     * @see jaw64.ca.CellGroup#setValue(int, int[])
     */
    @Override
    public void setValue(int value, int... index) {
        throw new UnsupportedOperationException(
                "This cell group is immutable; cannot call \"setValue(int, int...)\" method.");
    }

    /*
     * (non-Javadoc)
     * @see jaw64.ca.CellGroup#fill(int)
     */
    @Override
    public void fill(int value) {
        throw new UnsupportedOperationException(
                "This cell group is immutable; cannot call \"fill(int)\" method.");
    }
}
