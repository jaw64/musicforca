package jaw64.ca.rules;

import jaw64.ca.CellGroup;

/**
 * A partial implementation for a rule which verifies that the number of
 * dimensions of a previous cell group matches the dimensions required by
 * the rule. Also defines a default cell value of 0.
 * @author jaw64
 */
public abstract class AbstractRule implements Rule {

    /**
     * Implementation of {@link #getNextCellValue(CellGroup, int...)}.
     * @param prevGroup the cell group from the previous iteration
     * @param cellIndex the index of the cell to be updated
     * @return the new value of the cell at {@code cellIndex}
     */
    protected abstract int getNextCellValueIMPL(CellGroup prevGroup, int... cellIndex);

    /**
     * Get a cell from a group, or return the default cell value if no such cell exists.
     * TODO: Maybe move this out of Rule class and into CellGroup class?
     * @param group the group of cells
     * @param cellIndex the index of the cell
     * @return the cell value
     */
    public int getCellFromGroup(CellGroup group, int... cellIndex) {
        try {
            return group.getValue(cellIndex);
        } catch (Exception e) {
            return getDefaultCellValue();
        }
    }

    /*
     * (non-Javadoc)
     * @see jaw64.ca.Rule#getNextCellValue(jaw64.ca.CellGroup, int[])
     */
    @Override
    public int getNextCellValue(CellGroup prevGroup, int... cellIndex) {
        if (prevGroup.getNumDimensions() == getRequiredNumDimensions()) {
            return getNextCellValueIMPL(prevGroup, cellIndex);
        }
        else {
            throw new UnsupportedOperationException();
        }
    }

    /*
     * (non-Javadoc)
     * @see jaw64.ca.Rule#getDefaultCellValue()
     */
    @Override
    public int getDefaultCellValue() {
        return 0;
    }

}
