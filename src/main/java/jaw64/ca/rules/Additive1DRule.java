package jaw64.ca.rules;

import jaw64.ca.CellGroup;

/**
 * Adds values of immediate neighbor cells.
 * @author jaw64
 */
public final class Additive1DRule extends AbstractRule {

    /**
     * The lowest value for an addition rule.
     */
    private static final int MIN_ADDITION_RULE = 0;

    /**
     * The lowest value for an overflow rule.
     */
    private static final int MIN_OVERFLOW_RULE = 0;

    /**
     * Rule that adds the value of the left neighbor only.
     */
    public static final int LEFT_ONLY = MIN_ADDITION_RULE;

    /**
     * Rule that adds the value of the right neighbor only.
     */
    public static final int RIGHT_ONLY = MIN_ADDITION_RULE + 1;

    /**
     * Rule that adds the value of both surrounding neighbors.
     */
    public static final int BOTH = MIN_ADDITION_RULE + 2;

    /**
     * Rule that wraps values upon over- or underflow.
     */
    public static final int WRAP = MIN_OVERFLOW_RULE;

    /**
     * Rule that clamps values at their minimums / maximums upon over- or underflow.
     */
    public static final int CLAMP = MIN_OVERFLOW_RULE + 1;

    /**
     * The highest value for an addition rule.
     */
    private static final int MAX_ADDITION_RULE = BOTH;

    /**
     * The highest value for an overflow rule.
     */
    private static final int MAX_OVERFLOW_RULE = CLAMP;

    /**
     * The minimum value for adding.
     */
    private final int min;

    /**
     * The maximum value for adding (plus one).
     */
    private final int max;

    /**
     * How much the addition of neighbors should affect a cell.
     */
    private final float mix;

    /**
     * The addition rule.
     */
    private final int additionRule;

    /**
     * The overflow rule.
     */
    private final int overflowRule;

    /**
     * (constructor) mix = 1.0f, additionRule = BOTH, overflowRule = WRAP, see
     * {@link #Additive1DRule(int, int, float, int, int)}
     * @param min the min
     * @param max the max
     */
    public Additive1DRule(int min, int max) {
        this(min, max, 1.0f, BOTH, WRAP);
    }

    /**
     * (constructor) additionRule = BOTH, overflowRule = WRAP, see
     * {@link #Additive1DRule(int, int, float, int, int)}
     * @param min the min
     * @param max the max
     * @param mix the mix
     */
    public Additive1DRule(int min, int max, float mix) {
        this(min, max, mix, BOTH, WRAP);
    }

    /**
     * (constructor) mix = 1.0f, see
     * {@link #Additive1DRule(int, int, float, int, int)}
     * @param min the min
     * @param max the max
     * @param mix the mix
     */
    public Additive1DRule(int min, int max, int additionRule, int overflowRule) {
        this(min, max, 1.0f, additionRule, overflowRule);
    }

    /**
     * (constructor) Creates a 1-dimensional additive rule.
     * @param min the minimum value for this rule
     * @param max the maximum value for this rule (must be greater than minimum)
     * @param mix the strength of addition applied to a cell
     * @param additionRule describes which cells to add
     * @param overflowRule describes what happens when value over or underflows min and max
     */
    public Additive1DRule(int min, int max, float mix, int additionRule, int overflowRule) {
        this.min = min;
        this.max = max + 1;
        this.mix = mix;
        this.additionRule = additionRule;
        this.overflowRule = overflowRule;
        this.runErrorChecks();
    }

    /**
     * Runs error checks for this additive 1D rule.
     */
    private void runErrorChecks() {
        boolean validMinMax = max - 1 > min;
        boolean validAdd = additionRule >= MIN_ADDITION_RULE && additionRule <= MAX_ADDITION_RULE;
        boolean validOver = overflowRule >= MIN_OVERFLOW_RULE && overflowRule <= MAX_OVERFLOW_RULE;
        if (!validMinMax) {
            throw new IllegalArgumentException(
                    "Minimum and maximum bounds invalid (max must be strictly greater than min).");
        }
        if (!validAdd) {
            throw new IllegalArgumentException("Invalid addition rule provided.");
        }
        if (!validOver) {
            throw new IllegalArgumentException("Invalid overflow rule provided.");
        }
    }

    /**
     * Wraps or clamps the specified value based on the overflow rule.
     * @param val the value to wrap / clamp
     * @return the wrapped / clamped value
     */
    private int wrapOrClampValue(int val) {
        switch (overflowRule) {
        case CLAMP: {
            return Math.min(Math.max(val, min), max);
        }
        case WRAP: {
            int mod = max - min; // TODO: cache this value?
            int modded = val % mod;
            return min + modded;
        }
        default:
            throw new RuntimeException("THIS SHOULD NOT BE HAPPENING!");
        }
    }

    @Override
    public int getRequiredNumDimensions() {
        return 1;
    }

    @Override
    protected int getNextCellValueIMPL(CellGroup prevGroup, int... cellIndex) {
        final int currIndex = cellIndex[0];
        final int currVal = getCellFromGroup(prevGroup, currIndex);
        final int leftVal = getCellFromGroup(prevGroup, currIndex - 1);
        final int rightVal = getCellFromGroup(prevGroup, currIndex + 1);
        final int toAdd = (int) ((float) (leftVal + rightVal) * mix);
        final int nextVal = currVal + toAdd;
        return wrapOrClampValue(nextVal);
    }

}
