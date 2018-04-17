package jaw64.ca.rules;

import jaw64.ca.CellGroup;

/**
 * Defines the contract for a rule to be used with cellular automata.
 * @author jaw64
 */
public interface Rule {

    /**
     * Gets the number of dimensions required from a cell group to use this rule
     * @return number of dimensions required for this rule
     */
    public int getRequiredNumDimensions();

    /**
     * Given a previous cell group and the index of a cell, gets the next
     * value for the cell at the specified index.
     * @param prevGroup the cell group from the previous iteration
     * @param cellIndex the index of the cell to be updated
     * @return the next value for the cell at {@code cellIndex}
     */
    public int getNextCellValue(CellGroup prevGroup, int... cellIndex);

    /**
     * Sets the value that should be used on borders surrounding this
     * particular cellular automaton.
     * @param i the border value
     */
    public int getDefaultCellValue();
}
