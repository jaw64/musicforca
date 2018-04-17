package jaw64.ca;

import java.util.ArrayList;
import java.util.List;
import jaw64.ca.rules.Rule;

/**
 * Default implementation of cellular automata.
 * @author jaw64
 */
public class DefaultCellularAutomaton implements CellularAutomaton {

    /**
     * The maximum capacity for the cell group iteration cache.
     */
    public static final int MAX_CACHE_CAPACITY = 256;

    /**
     * The cell group used for the first iteration (iteration 0).
     */
    private CellGroup initialGroup;

    /**
     * The rule used to generate subsequent iterations.
     */
    private Rule rule;

    /**
     * Cache of cells.
     */
    private List<CellGroup> cache;

    /**
     * Iteration offset for cache from the first cell group.
     */
    private int cacheOffset;

    /**
     * (constructor) Creates a default cellular automaton with the specified
     * initial condition and generating rule.
     * @param initialGroup the initial cell group used for iteration 0
     * @param rule the rule used to generate iterations
     */
    public DefaultCellularAutomaton(CellGroup initialGroup, Rule rule) {
        this.initialGroup = initialGroup;
        this.rule = rule;
        this.cache = new ArrayList<>(MAX_CACHE_CAPACITY);
        this.cacheOffset = 0;
        this.setup();
    }

    /**
     * Does any post-initialization setup / preprocessing.
     */
    private void setup() {
        if (initialGroup.getNumDimensions() != rule.getRequiredNumDimensions()) {
            throw new IllegalArgumentException(String.format(
                    "The rule for this cellular automaton "
                            + "requires a(n) %d-dimensional cell group.",
                    rule.getRequiredNumDimensions()));
        }
        cache.add(initialGroup);
    }

    /**
     * Increments the current index, carrying when any subindex reaches its
     * corresponding limit as specified by {@code dimSizes}.
     * @param currIndex the current index for each dimension
     * @param dimSizes the number of values allowed for each dimensions
     */
    private static void incrementIndex(int[] currIndex, int[] dimSizes) {
        boolean carry = true;
        for (int i = currIndex.length - 1; i >= 0 && carry; i--) {
            carry = false;
            currIndex[i]++;
            if (currIndex[i] == dimSizes[i]) {
                currIndex[i] = 0;
                carry = true;
            }
        }
    }

    /**
     * Generates a single iteration given the previous iteration.
     * @param prev the previous iteration
     * @return the subsequent iteration
     */
    private CellGroup generateIteration(CellGroup prev) {
        final CellGroupDimensions dims = getDimensions();
        final CellGroup ret = new CellGroup(dims);
        final int numDims = dims.getNumDimensions();
        final int totalElements = dims.getNumElements();
        final int[] dimSizes = new int[numDims];
        final int[] currIndex = new int[numDims];
        for (int i = 0; i < numDims; i++) {
            dimSizes[i] = dims.getDimensionSize(i);
        }
        int elementsUpdated = 0;
        while (elementsUpdated < totalElements) {
            int newValue = rule.getNextCellValue(prev, currIndex);
            ret.setValue(newValue, currIndex);
            elementsUpdated++;
            incrementIndex(currIndex, dimSizes);
        }
        return ret;
    }

    /**
     * Fills the cache such that the last element in the cache is the cell group
     * at the provided iteration (unless the iteration is already in the cache, in
     * which case nothing happens, because it is impossible to backtrack without
     * resetting the entire cache).
     * @param iteration the iteration to fill the cache up to
     */
    private void fillCacheToIteration(final int iteration) {
        if (iteration <= 0) {
            throw new IllegalArgumentException(String
                    .format("Cannot fill to iteration %d. Valid iterations are > 0.", iteration));
        }
        int minCacheIteration = cacheOffset;
        int maxCacheIteration = cacheOffset + Math.min(cache.size(), MAX_CACHE_CAPACITY) - 1;
        if (iteration > maxCacheIteration) {
            final int groupsToGenerate = iteration - maxCacheIteration; // required generations
            CellGroup latestGroup = cache.get(cache.size() - 1);
            List<CellGroup> generated = new ArrayList<>(groupsToGenerate);
            for (int i = 0; i < groupsToGenerate; i++) {
                CellGroup next = generateIteration(latestGroup);
                generated.add(next);
                latestGroup = next;
            }
            cache.addAll(generated);
            if (cache.size() > MAX_CACHE_CAPACITY) {
                minCacheIteration = iteration - MAX_CACHE_CAPACITY + 1;
                cache.subList(0, minCacheIteration - cacheOffset).clear();
                cacheOffset = minCacheIteration;
            }
        }
        else if (iteration <= maxCacheIteration && iteration >= minCacheIteration) {
            // Can't do anything, cache already contains this iteration.
        }
        else {
            // TODO: implement cache below
            final int groupsToGenerate = iteration;
            System.err.println("NOOOO");
        }
    }

    /*
     * (non-Javadoc)
     * @see jaw64.ca.CellularAutomaton#getDimensions()
     */
    @Override
    public CellGroupDimensions getDimensions() {
        return initialGroup.getDimensions();
    }

    /*
     * (non-Javadoc)
     * @see jaw64.ca.CellularAutomaton#getRule()
     */
    @Override
    public Rule getRule() {
        return rule;
    }

    /*
     * (non-Javadoc)
     * @see jaw64.ca.CellularAutomaton#getInitialCellGroup()
     */
    @Override
    public CellGroup getInitialCellGroup() {
        return initialGroup;
    }

    /*
     * (non-Javadoc)
     * @see jaw64.ca.CellularAutomaton#getIteration(int)
     */
    @Override
    public CellGroup getIteration(int iteration) {
        if (iteration < 0) {
            throw new IllegalArgumentException(String.format(
                    "Cellular automaton cannot get iteration %d. Valid iterations are >= 0.",
                    iteration));
        }
        else if (iteration == 0) {
            return initialGroup;
        }
        else {
            fillCacheToIteration(iteration);
            return cache.get(cache.size() - 1);
        }
    }
}
