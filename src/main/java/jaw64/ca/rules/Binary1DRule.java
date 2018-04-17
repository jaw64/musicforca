package jaw64.ca.rules;

import jaw64.ca.CellGroup;

/**
 * Implementation of Wolfram's 1-dimensional binary rules for CA.
 * @author jaw64
 */
public class Binary1DRule extends AbstractRule {

    /**
     * The rule number.
     */
    private int ruleNo;

    /**
     * (constructor) Creates a new 1-dimensional binary rule based on the
     * provided Wolfram rule number ({@code ruleNo >= 0 and < 256}).
     * @param ruleNo the rule number between [0, 256)
     */
    public Binary1DRule(int ruleNo) {
        this.ruleNo = ruleNo;
        if (ruleNo < 0 || ruleNo > 255) {
            throw new IllegalArgumentException("1-dimensional binary rules range from [0, 255]");
        }
    }

    @Override
    public int getRequiredNumDimensions() {
        return 1;
    }

    @Override
    protected int getNextCellValueIMPL(CellGroup prevGroup, int... cellIndex) {
        int currIndex = cellIndex[0];
        int currVal = getCellFromGroup(prevGroup, currIndex);
        int leftVal = getCellFromGroup(prevGroup, currIndex - 1);
        int rightVal = getCellFromGroup(prevGroup, currIndex + 1);
        int index = 4 * leftVal + 2 * currVal + rightVal;
        int nextVal = (ruleNo >> index) % 2;
        return nextVal;
    }

}
