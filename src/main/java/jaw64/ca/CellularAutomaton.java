package jaw64.ca;

import jaw64.ca.rules.Rule;

/**
 * Contract for an individual cellular automaton.
 * @author jaw64
 */
public interface CellularAutomaton {

    /**
     * Creates an immutable copy of the specified cell group.
     * @param group the cell group to make immutable
     * @return a wrapper around the provided cell group
     */
    public static ImmutableCellGroup immutableCopy(CellGroup group) {
        return new ImmutableCellGroup(group);
    }

    /**
     * Gets the dimensions of the cell groups used by this cellular automaton.
     * @return the dimensions of an individual cell
     */
    public CellGroupDimensions getDimensions();

    /**
     * Gets the rule used to generate iterations of cell groups.
     * @return the rule
     */
    public Rule getRule();

    /**
     * Gets the initial condition from which all iterations of cell groups
     * are generated.
     * @return a(n ideally unmodifiable) cell group
     */
    public CellGroup getInitialCellGroup();

    /**
     * Gets the cell group at the specified iteration.
     * @param i the iteration
     * @return the cell at the specified iteration
     */
    public CellGroup getIteration(int i);
}
