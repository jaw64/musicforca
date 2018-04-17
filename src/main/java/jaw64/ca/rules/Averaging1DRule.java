package jaw64.ca.rules;

import jaw64.ca.CellGroup;

/**
 * Mixes / averages values of immediate neighbor cells.
 * @author jaw64
 */
public class Averaging1DRule extends AbstractRule {

    /**
     * The rule number.
     */
    private float mix;

    /**
     * (constructor) Creates a new averaging 1-dimensional binary rule based on t
     * @param the amount to mix towards the averaged colors
     */
    public Averaging1DRule(float mix) {
        this.mix = Math.min(Math.max(mix, 0.0f), 1.0f);
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
        int goalVal = (leftVal + rightVal) / 2;
        int nextVal = (int) (((1.0f - mix) * (float) currVal) + (mix * (float) goalVal));
        return nextVal;
    }

}
